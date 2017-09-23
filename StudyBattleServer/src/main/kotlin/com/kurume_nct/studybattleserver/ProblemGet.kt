package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.Problem
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
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
        val point: Int
)

fun Route.getProblem() = post<ProblemGet> {
    val user = verifyCredentials(it.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    val problem = transaction {
        Problem.findById(it.id)
    }
    if (problem == null) {
        call.respond(HttpStatusCode.NotFound)
        return@post
    }

    val content = transaction { problem.content }
    val started = transaction { problem.startedAt <= DateTime.now() }

    val imageIds = if (started) {
        transaction {
            content
                    .fetchRelatedImages()
                    .map { it.id.value }
        }
    } else {
        emptyList()
    }
    val text = if (started) {
        content.text
    } else {
        ""
    }

    val response = transaction {
        ProblemGetResponse(
                problem.id.value,
                problem.title,
                problem.owner.id.value,
                text,
                imageIds,
                problem.createdAt.toString(),
                problem.startedAt.toString(),
                problem.durationMillis,
                problem.point
        )
    }
    call.respond(Gson().toJson(response))
}