package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/10/15.
 */
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
    if (result.first == null) {
        call.respond(HttpStatusCode.InternalServerError)
        return@get
    }

    val response = Gson().toJson(result.first)
    call.respond(response)
}