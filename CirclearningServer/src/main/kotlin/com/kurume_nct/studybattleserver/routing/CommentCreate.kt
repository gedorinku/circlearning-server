package com.kurume_nct.studybattleserver.routing

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.CommentCreate
import com.kurume_nct.studybattleserver.dao.*
import com.kurume_nct.studybattleserver.verifyCredentials
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.request.receiveStream
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route
import org.joda.time.DateTime

/**
 * Created by gedorinku on 2017/10/28.
 */
data class CommentCreateResponse(val id: Int)

fun Route.createComment() = post<CommentCreate> { _ ->
    val requestJson = call.receiveStream().use {
        val buffer = mutableListOf<Byte>()
        var temp = it.read()
        while (temp != -1) {
            buffer.add(temp.toByte())
            temp = it.read()
        }
        String(buffer.toByteArray(), Charsets.UTF_8)
    }
    val gson = Gson()
    val request = gson.fromJson(requestJson, CommentCreate::class.java)
    if (request == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }

    val user = verifyCredentials(request.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    val images = transaction {
        request.imageIds.map {
            Image.findById(it)
        }
    }
    if (images.contains(null)) {
        call.respond(HttpStatusCode(404, "image not found"))
        return@post
    }

    val replyTo = if (0 < request.replyTo) {
        val temp = transaction {
            User.findById(request.replyTo)
        }
        if (temp == null) {
            call.respond(HttpStatusCode(404, "user not found: replyTo"))
            return@post
        }
        temp
    } else {
        null
    }

    val solution = transaction {
        Solution.findById(request.solutionId)
    }
    if (solution == null) {
        call.respond(HttpStatusCode(404, "solution not found"))
        return@post
    }

    val content = transaction {
        Content.new {
            text = request.text
        }.apply {
            relateImages(*images.filterNotNull().toTypedArray())
        }
    }

    val commentId = transaction {
        Comment.new {
            this.author = user
            this.replyTo = replyTo
            this.body = content
            this.createdAt = DateTime.now()
            this.solution = solution
        }.id.value
    }

    val response = CommentCreateResponse(commentId)
    call.respond(Gson().toJson(response))
}