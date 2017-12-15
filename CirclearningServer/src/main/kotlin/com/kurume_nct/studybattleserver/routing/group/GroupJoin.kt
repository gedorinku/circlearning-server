package com.kurume_nct.studybattleserver.routing.group

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.GroupJoin
import com.kurume_nct.studybattleserver.dao.Group
import com.kurume_nct.studybattleserver.verifyCredentials
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/08/03.
 */
fun Route.joinGroup() = post<GroupJoin> { _ ->
    val groupJoin = GroupJoin.create(call.receive())
    if (groupJoin == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }
    val user = verifyCredentials(groupJoin.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    val group = transaction {
        Group.findById(groupJoin.groupId)
    }
    if (group == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }

    group.attachUser(user)

    call.response.status(HttpStatusCode.OK)
    call.respond(Gson().toJson(HttpStatusCode.OK))
}