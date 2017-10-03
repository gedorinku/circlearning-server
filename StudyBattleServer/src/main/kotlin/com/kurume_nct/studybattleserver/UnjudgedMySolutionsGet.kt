package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.JudgingState
import com.kurume_nct.studybattleserver.dao.Solution
import com.kurume_nct.studybattleserver.dao.Solutions
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/10/03.
 */
fun Route.getUnjudgedMySolutions() = get<UnjudgedMySolutionsGet> {
    val authenticationKey = call.request.queryParameters["authenticationKey"].orEmpty()
    val user = verifyCredentials(authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@get
    }

    val solutions = transaction {
        Solution
                .find {
                    Solutions.author.eq(user.id) and
                            (Solutions.judgingState.eq(JudgingState.Solved) or
                                    Solutions.judgingState.eq(JudgingState.WaitingForJudge))
                }
                .toList()
                .map { SolutionGetResponse.fromSolution(it) }
    }

    call.respond(Gson().toJson(solutions))
}