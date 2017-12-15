package com.kurume_nct.studybattleserver.routing.group

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.GroupCreate
import com.kurume_nct.studybattleserver.dao.Group
import com.kurume_nct.studybattleserver.isValidDisplayName
import com.kurume_nct.studybattleserver.routing.user.UserGetResponse
import com.kurume_nct.studybattleserver.verifyCredentials
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/07/30.
 */
fun Route.createGroup() = post<GroupCreate> { _ ->
    val groupCreate = GroupCreate.create(call.receive())
    if (groupCreate == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }
    val user = verifyCredentials(groupCreate.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    if (!isValidDisplayName(groupCreate.name)) {
        val description = "グループ名は2文字以上20文字以下である必要があります。"
        call.respond(HttpStatusCode(HttpStatusCode.BadRequest.value, description))
        return@post
    }

    val group = transaction {
        Group.new {
            name = groupCreate.name
            owner = user
        }
    }
    group.attachUser(user)

    val owner = UserGetResponse.fromUser(user)
    val response = GroupGetResponse.fromGroup(group)
    val json = Gson().toJson(response)
    call.respond(json)
}