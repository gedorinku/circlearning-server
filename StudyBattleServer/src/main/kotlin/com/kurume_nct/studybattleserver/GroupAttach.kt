package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.Group
import com.kurume_nct.studybattleserver.dao.User
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/10/02.
 */
fun Route.attachToGroup() = post<GroupAttach> {
    val user = verifyCredentials(it.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    val group = transaction {
        Group.findById(it.groupId)
    }
    if (group == null) {
        call.respond(HttpStatusCode(404, "group"))
        return@post
    }

    val target = transaction {
        User.findById(it.userId)
    }
    if (target == null) {
        call.respond(HttpStatusCode(404, "user"))
        return@post
    }

    group.attachUser(user)

    call.respond(Gson().toJson(HttpStatusCode.OK))
}