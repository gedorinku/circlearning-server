package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.Group
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/09/30.
 */
data class GroupGetResponse(val id: Int, val name: String, val owner: UserGetResponse)

fun Route.getGroup() = post<GroupGet> {
    val user = verifyCredentials(it.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    val group = transaction {
        Group.findById(it.id)
    }
    if (group == null) {
        call.respond(HttpStatusCode.NotFound)
        return@post
    }

    val response = transaction {
        val owner = group.owner
        GroupGetResponse(
                group.id.value,
                group.name,
                UserGetResponse.fromUser(owner))
    }

    call.respond(Gson().toJson(response))
}