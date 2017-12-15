package com.kurume_nct.studybattleserver.routing.problem

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.AssignedProblemsGet
import com.kurume_nct.studybattleserver.dao.Problem
import com.kurume_nct.studybattleserver.dao.Problems
import com.kurume_nct.studybattleserver.verifyCredentials
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/09/29.
 */
fun Route.getAssignedProblems() = post<AssignedProblemsGet> { _ ->
    val assignedProblemsGet = AssignedProblemsGet.create(call.receive())
    if (assignedProblemsGet == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }
    val user = verifyCredentials(assignedProblemsGet.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    val problems = transaction {
        Problem
                .find {
                    Problems.assignedUser.eq(user.id) and Problems.group.eq(assignedProblemsGet.groupId)
                }
                .toList()
                .map {
                    ProblemGetResponse.fromProblem(it)
                }
    }

    call.respond(Gson().toJson(problems))
}