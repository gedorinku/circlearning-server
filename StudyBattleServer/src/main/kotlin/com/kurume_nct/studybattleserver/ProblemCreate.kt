package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route
import org.joda.time.DateTime

/**
 * Created by gedorinku on 2017/08/11.
 */
data class ProblemCreateResponse(val id: Int)

fun Route.createProblem() = post<ProblemCreate> {
    val requestJson = call.request.receiveContent().inputStream().bufferedReader().use {
        it.readLines().joinToString("")
    }
    val gson = Gson()
    val request = gson.fromJson(requestJson, ProblemCreate::class.java)

    val user = verifyCredentials(request.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    val parsedStartsAt = DateTime.parse(request.startsAt)
    if (parsedStartsAt == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }

    val images = request.imageIds.map {
        transaction {
            Image.findById(it)
        }
    }
    if (images.contains(null)) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }

    val group = transaction {
        Group.findById(request.groupId)
    }
    if (group == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }

    val content = transaction {
        Content.new {
            text = request.text
        }
    }

    val validationResult = checkValidRequest(request.assumedSolution)
    if (HttpStatusCode.OK != validationResult) {
        call.respond(validationResult)
        return@post
    }

    content.relateImages(*images.filterNotNull().toTypedArray())

    val problem = transaction {
        Problem.new {
            title = request.title
            owner = user
            this.content = content
            createdAt = DateTime.now()
            startedAt = parsedStartsAt
            durationMillis = request.durationMillis
            point = 0
            this.group = group
        }
    }

    val assumedSolution = SolutionCreate(
            "",
            request.assumedSolution.text,
            transaction { problem.id.value },
            request.assumedSolution.imageIds
                                        )
    val solutionResult = Solution.fromRequest(assumedSolution, user)
    if (solutionResult.second != HttpStatusCode.OK) {
        call.respond(solutionResult.second)
        return@post
    }

    val solution = solutionResult.first
    if (solution == null) {
        call.respond(HttpStatusCode.InternalServerError)
        return@post
    }

    transaction {
        AssumedSolutionRelation.new {
            this.problem = problem
            this.assumedSolution = solution
        }
    }

    val json = Gson().toJson(ProblemCreateResponse(problem.id.value))
    call.respond(json)
}

fun checkValidRequest(request: SolutionCreate): HttpStatusCode {
    val images = request.imageIds.map {
        transaction {
            Image.findById(it)
        }
    }
    if (images.contains(null)) {
        return HttpStatusCode.BadRequest
    }

    if (request.text.isEmpty() && images.isEmpty()) {
        return HttpStatusCode.BadRequest
    }

    return HttpStatusCode.OK
}