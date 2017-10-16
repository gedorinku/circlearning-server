package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.Problem
import com.kurume_nct.studybattleserver.item.Air
import com.kurume_nct.studybattleserver.item.Item
import com.kurume_nct.studybattleserver.item.ItemRegistry
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/10/13.
 */
class ProblemOpenResponse(openAction: Item.OpenAction) {

    val happened: String = openAction.toString()
}

fun Route.openProblem() = get<ProblemOpen> {
    val queryParameters = call.request.queryParameters

    val authenticationKey = queryParameters["authenticationKey"].orEmpty()
    val user = verifyCredentials(authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@get
    }

    val problemId = queryParameters["problemId"].orEmpty().toIntOrNull()
    if (problemId == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@get
    }

    val problem = transaction {
        Problem.findById(problemId)
    }
    if (problem == null) {
        call.respond(HttpStatusCode(404, "problem not found"))
        return@get
    }

    if (problem.assignedUser?.id != user.id) {
        call.respond(HttpStatusCode(HttpStatusCode.BadRequest.value, "not assigned"))
        return@get
    }

    val itemId = problem.attachedItemId
    val action = if (itemId != Air.id) {
        val item = ItemRegistry.registeredItems[itemId]
        if (item == null) {
            call.respond(HttpStatusCode(HttpStatusCode.InternalServerError.value, "item not registered"))
            return@get
        }
        item.onOpenProblem(problem, user)
    } else {
        Item.OpenAction.NONE
    }

    transaction {
        problem.attachedItemId = Air.id
        problem.flush()
    }

    val response = Gson().toJson(ProblemOpenResponse(action))
    call.respond(response)
}