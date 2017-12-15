package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/10/03.
 */
fun Route.getJoinedGroups() = get<JoinedGroupsGet> {
    val authenticationKey = call.request.queryParameters["authenticationKey"].orEmpty()
    val user = verifyCredentials(authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@get
    }

    val groups = transaction {
        Belonging
                .find { Belongings.user.eq(user.id) }
                .toList()
                .map { GroupGetResponse.fromGroup(it.group) }
    }

    call.respond(Gson().toJson(groups))
}