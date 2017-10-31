package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.*
import com.kurume_nct.studybattleserver.item.Air
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.request.receiveStream
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/09/22.
 */
data class SolutionCreateResponse(val id: Int, val receivedItemId: Int)

fun Route.createSolution() = post<SolutionCreate> { _ ->
    val requestJson = call.receiveStream().use {
        val buffer = mutableListOf<Byte>()
        var temp = it.read()
        while (temp != -1) {
            buffer.add(temp.toByte())
            temp = it.read()
        }
        String(buffer.toByteArray(), Charsets.UTF_8)
    }
    val gson = Gson()
    val solutionCreate = gson.fromJson(requestJson, SolutionCreate::class.java)

    val user = verifyCredentials(solutionCreate.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    val result = Solution.fromRequest(solutionCreate, user)
    if (result.second != HttpStatusCode.OK) {
        call.respond(result.second)
        return@post
    }

    val solution = result.first
    if (solution == null) {
        call.respond(HttpStatusCode.InternalServerError)
        return@post
    }

    val receivedItem = transaction {
        val problem = solution.problem
        problem.assignedUser = null
        problem.attachedItemId = solutionCreate.attachedItemId
        problem.flush()
        ProblemAssignment.find { ProblemAssignments.problem.eq(problem.id) }
                .map { it.delete() }
        val receivedItem = Lottery.getRandomItem()
        if (receivedItem.id != Air.id) {
            user.giveItem(receivedItem, 1, problem.group)
        }

        closeProblemIfAllUsersSubmitted(problem, problem.group)

        receivedItem
    }

    call.respond(Gson().toJson(SolutionCreateResponse(solution.id.value, receivedItem.id)))
}

private fun closeProblemIfAllUsersSubmitted(problem: Problem, group: Group) = transaction {
    if (group.fetchUsers().size != problem.fetchSubmittedSolutions().size) {
        return@transaction
    }

    problem.state = ProblemState.Judging
    problem.flush()
}