package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.Content
import com.kurume_nct.studybattleserver.dao.ContentImageRelation
import com.kurume_nct.studybattleserver.dao.Image
import com.kurume_nct.studybattleserver.dao.Problem
import org.jetbrains.exposed.dao.EntityID
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

    val parsedStartsAt = DateTime.parse(it.startsAt)
    if (parsedStartsAt == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }

    val images = it.imageIds.map {
        transaction {
            Image.findById(it)
        }
    }
    if (images.contains(null)) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }

    val content = transaction {
        Content.new {
            text = it.text
        }
    }
    images.requireNoNulls().forEach {
        transaction {
            ContentImageRelation.new {
                this.content = content
                this.image = it
            }
        }
    }
    val problem = transaction {
        Problem.new {
            title = it.title
            owner = user
            this.content = content
            createdAt = DateTime.now()
            startedAt = parsedStartsAt
            durationMillis = it.durationMillis
            point = 0
        }
    }

    val json = Gson().toJson(ProblemCreateResponse(problem.id.value))
    call.respond(json)
}