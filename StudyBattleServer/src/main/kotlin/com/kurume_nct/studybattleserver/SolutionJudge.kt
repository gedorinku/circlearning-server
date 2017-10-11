package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.JudgingState
import com.kurume_nct.studybattleserver.dao.Solution
import com.kurume_nct.studybattleserver.dao.Solutions
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/10/03.
 */
fun Route.judgeSolution() = post<SolutionJudge> { _ ->
    val solutionJudge = SolutionJudge.create(call.receive())
    if (solutionJudge == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }
    val user = verifyCredentials(solutionJudge.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    val solution = transaction {
        Solution.findById(solutionJudge.id)
    }
    if (solution == null) {
        call.respond(HttpStatusCode.NotFound)
        return@post
    }

    val problemOwnerId = transaction {
        solution.problem.owner.id.value
    }

    val userId = transaction {
        user.id.value
    }
    if (userId != problemOwnerId) {
        call.respond(HttpStatusCode.Forbidden)
        return@post
    }

    if (solution.judgingState == JudgingState.Accepted || solution.judgingState == JudgingState.WrongAnswer) {
        call.respond(HttpStatusCode(400, "already judged"))
        return@post
    }

    if (solutionJudge.isAccepted == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }

    val judge = if (solutionJudge.isAccepted) {
        JudgingState.Accepted
    } else {
        JudgingState.WrongAnswer
    }

    transaction {
        Solutions.update({ Solutions.id.eq(solution.id) }) {
            it[Solutions.judgingState] = judge
        }
    }

    call.respond(Gson().toJson(HttpStatusCode.OK))
}