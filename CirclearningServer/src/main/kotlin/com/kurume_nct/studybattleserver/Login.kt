package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.AuthenticationKey
import com.kurume_nct.studybattleserver.dao.User
import com.kurume_nct.studybattleserver.dao.Users
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.util.ValuesMap
import org.joda.time.DateTime
import java.security.SecureRandom
import javax.xml.bind.DatatypeConverter

/**
 * Created by gedorinku on 2017/07/23.
 */
data class LoginResponse(val authenticationKey: String)

fun Route.login(random: SecureRandom) = post<Login> { _ ->
    val login = Login.create(call.receive())
    if (login == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }
    if (!isValidUserName(login.userName)) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }
    val result = transaction {
        User.find { Users.userName.eq(login.userName) }.toList()
    }

    if (!result.isEmpty()) {
        val user = result.first()
        val hash = hashWithSalt(login.password, user.hashSalt)
        if (hash == user.passwordHash) {
            val key = generateAuthenticationKey(random)
            transaction {
                AuthenticationKey.new {
                    this.keyHash = hashWithSalt(key, "")
                    this.user = user
                    createdAt = DateTime.now()
                }
            }
            val gson = Gson()
            val json = gson.toJson(LoginResponse(key))
            call.respond(json)

            return@post
        }
    }

    call.respond(HttpStatusCode.Unauthorized)
}

fun generateAuthenticationKey(random: SecureRandom): String {
    val key = ByteArray(32, { 0 })
    random.nextBytes(key)
    return DatatypeConverter.printHexBinary(key)
}
