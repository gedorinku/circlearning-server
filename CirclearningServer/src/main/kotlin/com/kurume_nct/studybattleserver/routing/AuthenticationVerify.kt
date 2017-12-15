package com.kurume_nct.studybattleserver.routing

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.AuthenticationVerify
import com.kurume_nct.studybattleserver.routing.user.UserGetResponse
import com.kurume_nct.studybattleserver.verifyCredentials
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/10/02.
 */
fun Route.verifyAuthentication() = post<AuthenticationVerify> { _ ->
    val authenticationVerify = AuthenticationVerify.create(call.receive())
    if (authenticationVerify == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }
    val user = verifyCredentials(authenticationVerify.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    val response = UserGetResponse.fromUser(user)
    call.respond(Gson().toJson(response))
}