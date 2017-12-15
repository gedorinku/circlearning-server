package com.kurume_nct.studybattleserver.routing.user

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.UsersSearch
import com.kurume_nct.studybattleserver.dao.User
import com.kurume_nct.studybattleserver.dao.Users
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/10/08.
 */
fun Route.searchUsers() = get<UsersSearch> {
    val pattern = "^[a-zA-Z0-9_]*".toRegex()
    val query = call.request.queryParameters["query"].orEmpty()
    if (!query.matches(pattern)) {
        call.respond(HttpStatusCode.BadRequest)
        return@get
    }
    val users = transaction {
        User.find { Users.userName.like("%$query%") }
                .toList()
                .map { UserGetResponse.fromUser(it) }
    }
    call.respond(Gson().toJson(users))
}
