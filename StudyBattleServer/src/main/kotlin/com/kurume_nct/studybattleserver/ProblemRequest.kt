package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.Group
import com.kurume_nct.studybattleserver.dao.Problem
import com.kurume_nct.studybattleserver.dao.Problems
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route
import org.joda.time.DateTime
import java.util.*

/**
 * Created by gedorinku on 2017/09/29.
 */
data class ProblemRequestResponse(
        val accepted: Boolean,
        val message: String,
        val problem: ProblemGetResponse?)

fun Route.requestProblem() = post<ProblemRequest> { _ ->
    val problemRequest = ProblemRequest.create(call.receive())
    if (problemRequest == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }
    val user = verifyCredentials(problemRequest.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    val group = transaction {
        Group.findById(problemRequest.groupId)
    }
    if (group == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }

    val availableProblems = transaction {
        Problem.find {
            Problems.group.eq(group.id) and
                    Problems.assignedUser.isNull() and
                    Problems.startedAt.lessEq(DateTime.now()) and
                    Problems.owner.neq(user.id)
        }.toList()
    }
    val count = user.countAssignedProblems(group)
    val (accepted, message) = when {
        count < 3 -> Pair(true, "ok")
        availableProblems.isEmpty() -> Pair(false, "取得できる問題がありません。")
        else -> Pair(false, "割り当てられる問題の上限に達しています。")
    }

    val random = Random()
    val problem = if (accepted) {
        availableProblems[random.nextInt(availableProblems.size)]
                .run {
                    transaction {
                        assignUser(user)
                        ProblemGetResponse.fromProblem(this@run)
                    }
                }
    } else {
        null
    }
    val response = ProblemRequestResponse(accepted, message, problem)
    call.respond(Gson().toJson(response))
}