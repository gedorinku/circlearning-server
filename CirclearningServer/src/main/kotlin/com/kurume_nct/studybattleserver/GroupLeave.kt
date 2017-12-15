package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.Belonging
import com.kurume_nct.studybattleserver.dao.Belongings
import com.kurume_nct.studybattleserver.dao.Group
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/10/09.
 */
fun Route.leaveGroup() = get<GroupLeave> {
    val queryParameters = call.request.queryParameters
    val authenticationKey = queryParameters["authenticationKey"].orEmpty()
    val user = verifyCredentials(authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@get
    }

    val groupId = queryParameters["groupId"].orEmpty().toIntOrNull()
    val group = if (groupId == null) {
        null
    } else transaction {
        Group.findById(groupId)
    }
    if (group == null) {
        call.respond(HttpStatusCode(404, "group not found"))
        return@get
    }

    transaction {
        Belonging.find { Belongings.user.eq(user.id) and Belongings.group.eq(group.id) }
                .map { it.delete() }
    }

    call.respond(Gson().toJson(HttpStatusCode.OK))
}