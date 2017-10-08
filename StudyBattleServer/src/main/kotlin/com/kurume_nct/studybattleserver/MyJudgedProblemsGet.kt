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
fun Route.getMyJudgedProblems() = get<MyJudgedProblemsGet> {
    val queryParameters = call.request.queryParameters
    val authenticationKey = queryParameters["authenticationKey"].orEmpty()
    val groupId = queryParameters["groupId"].orEmpty().toIntOrNull() ?: 0
    val result = Problems.getUserProblems(authenticationKey, groupId, ProblemState.Judged)

    if (result.first == null) {
        call.respond(result.second)
        return@get
    }

    call.respond(Gson().toJson(result.first))
}