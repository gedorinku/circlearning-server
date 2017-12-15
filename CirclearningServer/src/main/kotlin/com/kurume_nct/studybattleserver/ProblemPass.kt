package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.Problem
import com.kurume_nct.studybattleserver.dao.ProblemAssignment
import com.kurume_nct.studybattleserver.dao.ProblemAssignments
import com.kurume_nct.studybattleserver.dao.User
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/10/12.
 */
fun Route.passProblem() = get<ProblemPass> {
    val queryParameters = call.request.queryParameters
    val authenticationKey = queryParameters["authenticationKey"].orEmpty()
    val problemId = queryParameters["problemId"].orEmpty().toIntOrNull()
    val user = verifyCredentials(authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@get
    }
    if (problemId == null) {
        call.respond(HttpStatusCode(HttpStatusCode.BadRequest.value, "problem id"))
        return@get
    }

    val statusCode = tryPassProblem(user, problemId)
    call.response.status(statusCode)
    call.respond(Gson().toJson(statusCode))
}

private fun tryPassProblem(user: User, problemId: Int): HttpStatusCode = transaction {
    val problem = Problem.findById(problemId)
            ?: return@transaction HttpStatusCode(404, "problem not found")
    if (problem.assignedUser?.id?.value != user.id.value) {
        return@transaction HttpStatusCode(HttpStatusCode.BadRequest.value, "not assigned")
    }

    val assignment = ProblemAssignment
            .find { ProblemAssignments.problem.eq(problem.id) }
            .firstOrNull()
            ?: return@transaction HttpStatusCode(HttpStatusCode.InternalServerError.value,
                                                 "assigned, but ProblemAssignment not found")
    assignment.delete()
    problem.assignedUser = null
    problem.flush()

    HttpStatusCode.OK
}