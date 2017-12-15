package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.Group
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
fun Route.getJudgedMySolutions() = get<JudgedMySolutionsGet> {
    val queryParameters = call.request.queryParameters
    val authenticationKey = queryParameters["authenticationKey"].orEmpty()
    val user = verifyCredentials(authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@get
    }

    val groupId = queryParameters["groupId"].orEmpty().toIntOrNull() ?: 0
    val group = transaction {
        Group.findById(groupId)
    }
    if (group == null) {
        call.respond(HttpStatusCode.NotFound)
        return@get
    }

    val solutions = transaction {
        Solution
                .find {
                    Solutions.author.eq(user.id) and
                            (Solutions.judgingState.eq(JudgingState.Accepted) or
                                    Solutions.judgingState.eq(JudgingState.WrongAnswer))
                }
                .filter {
                    it.problem.owner.id != user.id && it.problem.group.id == group.id
                }
                .toList()
                .map { SolutionGetResponse.fromSolution(it) }
    }

    call.respond(Gson().toJson(solutions))
}