package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.Group
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/08/03.
 */
fun Route.joinGroup() = post<GroupJoin> {
    val user = verifyCredentials(it.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    val group = transaction {
        Group.findById(it.groupId)
    }
    if (group == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }

    group.attachUser(user)

    call.response.status(HttpStatusCode.OK)
    call.respond(Gson().toJson(HttpStatusCode.OK))
}