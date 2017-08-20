package com.kurume_nct.studybattleserver

import com.kurume_nct.studybattleserver.dao.Content
import com.kurume_nct.studybattleserver.dao.Problem
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route
import org.joda.time.DateTime

/**
 * Created by gedorinku on 2017/08/11.
 */
data class ProblemCreateResponse(val id: Int)

fun Route.createProblem() = post<ProblemCreate> {
    val user = verifyCredentials(it.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    val content = transaction {
        Content.new {
            text = it.text
        }
    }
    val problem = transaction {
        Problem.new {
            title = it.title
            owner = user
            this.content = content
            createdAt = DateTime.now()
            durationMillis = it.durationMillis
        }
    }

    call.respond(ProblemCreateResponse(problem.id.value))
}