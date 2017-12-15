package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.ProblemState
import com.kurume_nct.studybattleserver.dao.Problems
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/10/07.
 */
fun Route.getMyCollectingProblems() = get<MyCollectingProblemsGet> {
    val queryParameters = call.request.queryParameters
    val authenticationKey = queryParameters["authenticationKey"].orEmpty()
    val groupId = queryParameters["groupId"].orEmpty().toIntOrNull() ?: 0
    val result = Problems.getUserProblems(authenticationKey, groupId, ProblemState.Opening)
    val problems = result.first

    if (problems == null) {
        call.respond(result.second)
        return@get
    }

    val response = problems.map { ProblemGetResponse.fromProblem(it) }
    call.respond(Gson().toJson(response))
}