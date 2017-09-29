package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.Problem
import com.kurume_nct.studybattleserver.dao.Problems
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/09/29.
 */
data class AssignedProblemsGetResponse(val problems: List<ProblemGetResponse>)

fun Route.getAssignedProblems() = post<AssignedProblemsGet> {
    val user = verifyCredentials(it.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    val problems = transaction {
        Problem
                .find {
                    Problems.assignedUser.eq(user.id)
                }
                .toList()
                .map {
                    ProblemGetResponse(
                            it.id.value,
                            it.title,
                            it.owner.id.value,
                            it.content.text,
                            it.content.fetchRelatedImages().map { it.id.value },
                            it.createdAt.toString(),
                            it.startedAt.toString(),
                            it.durationMillis,
                            it.point)
                }
    }

    val response = AssignedProblemsGetResponse(problems)
    call.respond(Gson().toJson(response))
}