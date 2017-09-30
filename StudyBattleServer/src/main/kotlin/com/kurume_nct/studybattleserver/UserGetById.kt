package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.User
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/09/30.
 */
data class UserGetResponse(val id: Int, val userName: String, val displayName: String)

fun Route.getUserById() = get<UserGetById> {
    val user = transaction {
        User.findById(it.id)
    }
    if (user == null) {
        call.respond(HttpStatusCode.NotFound)
        return@get
    }

    val response = transaction {
        UserGetResponse(user.id.value, user.userName, user.displayName)
    }

    call.respond(Gson().toJson(response))
}