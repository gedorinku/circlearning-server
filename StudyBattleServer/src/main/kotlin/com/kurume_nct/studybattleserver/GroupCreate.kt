package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.Group
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/07/30.
 */
fun Route.createGroup() = post<GroupCreate> {
    val user = verifyCredentials(it.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    if (!isValidDisplayName(it.name)) {
        val description = "グループ名は2文字以上20文字以下である必要があります。"
        call.respond(HttpStatusCode(HttpStatusCode.BadRequest.value, description))
        return@post
    }

    val group = transaction {
        Group.new {
            name = it.name
            owner = user
        }
    }
    group.attachUser(user)

    val owner = UserGetResponse.fromUser(user)
    val response = transaction {
        GroupGetResponse(group.id.value, group.name, owner)
    }
    val json = Gson().toJson(response)
    call.respond(json)
}