package com.kurume_nct.studybattleserver.routing.problem

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.ProblemGet
import com.kurume_nct.studybattleserver.dao.AssumedSolutionRelation
import com.kurume_nct.studybattleserver.dao.AssumedSolutionRelations
import com.kurume_nct.studybattleserver.dao.Problem
import com.kurume_nct.studybattleserver.routing.solution.SolutionGetResponse
import com.kurume_nct.studybattleserver.routing.user.UserGetResponse
import com.kurume_nct.studybattleserver.verifyCredentials
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route
import org.joda.time.DateTime

/**
 * Created by gedorinku on 2017/09/23.
 */
data class ProblemGetResponse(
        val id: Int,
        val title: String,
        val ownerId: Int,
        val text: String,
        val imageIds: List<Int>,
        val createdAt: String,
        val startsAt: String,
        val durationMillis: Long,
        val point: Int,
        val solutions: List<SolutionGetResponse>,
        val assignedUser: UserGetResponse?,
        val assignedAt: String,
        val durationPerUserMillis: Long,
        val state: String,
        val assumedSolution: SolutionGetResponse
                             ) {

    companion object {

        fun fromProblem(problem: Problem): ProblemGetResponse = transaction {
            val content = transaction { problem.content }
            val started = transaction { problem.startedAt <= DateTime.now() }

            val imageIds = if (started) {
                content
                        .fetchRelatedImages()
                        .map { it.id.value }
            } else {
                emptyList()
            }
            val text = if (started) {
                content.text
            } else {
                ""
            }
            val solutions = problem
                    .fetchSubmittedSolutions()
                    .map { SolutionGetResponse.fromSolution(it) }

            val assignedUser = problem.assignedUser
            val user = if (assignedUser == null) {
                null
            } else {
                UserGetResponse.fromUser(assignedUser)
            }
            val assumedSolution = AssumedSolutionRelation
                    .find { AssumedSolutionRelations.problem.eq(problem.id) }
                    .firstOrNull()!!
                    .assumedSolution

            ProblemGetResponse(
                    problem.id.value,
                    problem.title,
                    problem.owner.id.value,
                    text,
                    imageIds,
                    problem.createdAt.toString(),
                    problem.startedAt.toString(),
                    problem.durationMillis,
                    problem.point,
                    solutions,
                    user,
                    problem.assignedAt.toString(),
                    problem.durationPerUserMillis,
                    problem.state.toString(),
                    SolutionGetResponse.fromSolution(assumedSolution))
        }
    }
}

fun Route.getProblem() = post<ProblemGet> {
    val problemGet = ProblemGet.create(call.receive(), it.id)
    if (problemGet == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }
    val user = verifyCredentials(problemGet.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    val problem = transaction {
        Problem.findById(problemGet.id)
    }
    if (problem == null) {
        call.respond(HttpStatusCode.NotFound)
        return@post
    }

    call.respond(Gson().toJson(ProblemGetResponse.fromProblem(problem)))
}