package com.kurume_nct.studybattleserver.routing.solution

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.SolutionJudge
import com.kurume_nct.studybattleserver.dao.*
import com.kurume_nct.studybattleserver.verifyCredentials
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

    val problem = transaction {
        solution.problem
    }

    val problemOwnerId = transaction {
        problem.owner.id.value
    }

    val userId = transaction {
        user.id.value
    }
    if (userId != problemOwnerId) {
        call.respond(HttpStatusCode.Forbidden)
        return@post
    }

    if (solutionJudge.isAccepted == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }

    if (problem.state != ProblemState.Judging && problem.state != ProblemState.ChallengePhase) {
        call.respond(HttpStatusCode(HttpStatusCode.BadRequest.value, "out of duration"))
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

        switchChallengePhaseIfAllSolutionsJudged(solution.problem)
    }

    call.respond(Gson().toJson(HttpStatusCode.OK))
}

private fun switchChallengePhaseIfAllSolutionsJudged(problem: Problem) = transaction {
    if (problem.state == ProblemState.Judging && problem.fetchSubmittedSolutions().all { it.isJudged }) {
        problem.switchChallengePhase()
    }
}