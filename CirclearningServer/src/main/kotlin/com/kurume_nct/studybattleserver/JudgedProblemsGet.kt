package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.Group
import com.kurume_nct.studybattleserver.dao.Problem
import com.kurume_nct.studybattleserver.dao.ProblemState
import com.kurume_nct.studybattleserver.dao.Problems
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/10/29.
 */
fun Route.getJudgedProblems() = get<JudgedProblemsGet> {
    val queryParameters = call.request.queryParameters

    val authenticationKey = queryParameters["authenticationKey"].orEmpty()
    val user = verifyCredentials(authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@get
    }


    val groupId = queryParameters["groupId"].orEmpty().toIntOrNull()
    if (groupId == null) {
        call.respond(HttpStatusCode(HttpStatusCode.BadRequest.value, "group id"))
        return@get
    }
    val group = transaction {
        Group.findById(groupId)
    }
    if (group == null) {
        call.respond(HttpStatusCode(404, "group not found"))
        return@get
    }

    val problems = transaction {
        Problem
                .find {
                    Problems.state.eq(ProblemState.Judged) and
                            Problems.group.eq(group.id)
                }
                .toList()
                .map {
                    ProblemGetResponse.fromProblem(it)
                }
    }

    call.respond(Gson().toJson(problems))
}