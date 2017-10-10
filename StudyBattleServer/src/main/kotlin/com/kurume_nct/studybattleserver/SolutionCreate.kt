package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.ProblemAssignment
import com.kurume_nct.studybattleserver.dao.ProblemAssignments
import com.kurume_nct.studybattleserver.dao.Solution
import com.kurume_nct.studybattleserver.dao.fromRequest
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/09/22.
 */
data class SolutionCreateResponse(val id: Int)

fun Route.createSolution() = post<SolutionCreate> {
    val user = verifyCredentials(it.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    val result = Solution.fromRequest(it, user)
    if (result.second != HttpStatusCode.OK) {
        call.respond(result.second)
        return@post
    }

    val solution = result.first
    if (solution == null) {
        call.respond(HttpStatusCode.InternalServerError)
        return@post
    }

    transaction {
        val problem = solution.problem
        problem.assignedUser = null
        ProblemAssignment.find { ProblemAssignments.problem.eq(problem.id) }
                .map { it.delete() }
    }

    call.respond(Gson().toJson(SolutionCreateResponse(solution.id.value)))
}