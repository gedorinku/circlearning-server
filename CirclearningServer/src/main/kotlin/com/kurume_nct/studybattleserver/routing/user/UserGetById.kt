package com.kurume_nct.studybattleserver.routing.user

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.UserGetById
import com.kurume_nct.studybattleserver.dao.User
import com.kurume_nct.studybattleserver.routing.image.ImageUploadResponse
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/09/30.
 */
data class UserGetResponse(val id: Int, val userName: String, val displayName: String, val icon: ImageUploadResponse?) {
    companion object {

        fun fromUser(user: User): UserGetResponse = transaction {
            val icon = user.icon
            val imageResponse = when(icon) {
                null -> null
                else -> ImageUploadResponse.fromImage(icon)
            }
            UserGetResponse(user.id.value, user.userName, user.displayName, imageResponse)
        }
    }
}

fun Route.getUserById() = get<UserGetById> {
    val user = transaction {
        User.findById(it.id)
    }
    if (user == null) {
        call.respond(HttpStatusCode.NotFound)
        return@get
    }

    val response = UserGetResponse.fromUser(user)

    call.respond(Gson().toJson(response))
}