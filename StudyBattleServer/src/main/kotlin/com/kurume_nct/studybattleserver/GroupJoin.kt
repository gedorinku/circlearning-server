package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.Belonging
import com.kurume_nct.studybattleserver.dao.Belongings
import com.kurume_nct.studybattleserver.dao.Group
import org.jetbrains.exposed.sql.and
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

    val belongings = transaction {
        Belonging.find {
            Belongings.user.eq(user.id) and Belongings.group.eq(group.id)
        }.toList()
    }
    if (belongings.isEmpty()) {
        transaction {
            Belonging.new {
                this.user = user
                this.group = group
            }
        }
    }

    call.response.status(HttpStatusCode.OK)
    call.respond(Gson().toJson(HttpStatusCode.OK))
}