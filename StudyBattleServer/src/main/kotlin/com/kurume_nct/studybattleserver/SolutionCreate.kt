package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.Content
import com.kurume_nct.studybattleserver.dao.Image
import com.kurume_nct.studybattleserver.dao.Problem
import com.kurume_nct.studybattleserver.dao.Solution
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route
import org.joda.time.DateTime

/**
 * Created by gedorinku on 2017/09/22.
 */
data class SolutionCreateResponse(val id: Int)

fun Route.createSolution() = post<SolutionCreate> {
    val user = verifyCredentials(it.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
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

    val problem = transaction {
        Problem.findById(it.problemId)
    }
    if (problem == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }

    val content = transaction {
        Content.new {
            text = it.text
        }
    }

    content.relateImages(*images.filterNotNull().toTypedArray())

    transaction {
        Solution.new {
            this.author = user
            this.content = content
            this.createdAt = DateTime.now()
            this.problem = problem
        }
    }

    call.respond(Gson().toJson(SolutionCreateResponse(problem.id.value)))
}