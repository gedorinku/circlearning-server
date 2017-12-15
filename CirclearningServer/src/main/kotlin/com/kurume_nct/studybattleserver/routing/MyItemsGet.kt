package com.kurume_nct.studybattleserver.routing

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.MyItemsGet
import com.kurume_nct.studybattleserver.verifyCredentials
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/10/15.
 */
data class ItemStackResponse(val id: Int, val count: Int)

fun Route.getMyItems() = get<MyItemsGet> {
    val queryParameters = call.request.queryParameters
    val authenticationKey = queryParameters["authenticationKey"].orEmpty()
    val groupId = queryParameters["groupId"].orEmpty().toIntOrNull() ?: 0

    val user = verifyCredentials(authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@get
    }

    val result = user.getItemStacks(groupId)
    if (result.second != HttpStatusCode.OK) {
        call.respond(result.second)
        return@get
    }

    val itemStacks = result.first
    if (itemStacks == null) {
        call.respond(HttpStatusCode.InternalServerError)
        return@get
    }

    val response = transaction {
        Gson().toJson(itemStacks.map { ItemStackResponse(it.itemId, it.count) })
    }
    call.respond(response)
}