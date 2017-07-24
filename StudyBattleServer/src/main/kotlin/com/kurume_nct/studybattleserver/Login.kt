package com.kurume_nct.studybattleserver

import com.kurume_nct.studybattleserver.dao.AuthenticationKey
import com.kurume_nct.studybattleserver.dao.User
import com.kurume_nct.studybattleserver.dao.Users
import com.sun.xml.internal.ws.api.ha.StickyFeature
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.util.valuesOf
import org.joda.time.DateTime
import java.io.Serializable
import java.security.SecureRandom
import javax.xml.bind.DatatypeConverter

/**
 * Created by gedorinku on 2017/07/23.
 */
data class LoginResponse(val authenticationKey: String)

fun Route.login(random: SecureRandom) {
    post<Login> {
        val result = transaction {
            User.find { Users.userName.eq(it.userName) }.toList()
        }

        if (!result.isEmpty()) {
            val user = result.first()
            val hash = hashWithSalt(it.password, user.hashSalt)
            if (hash == user.passwordHash) {
                val key = generateAuthenticationKey(random)
                transaction {
                    AuthenticationKey.new {
                        this.keyHash = hashWithSalt(key, user.hashSalt)
                        this.user = user
                        createdAt = DateTime.now()
                    }
                }
                //call.respondText(key)
                call.respond(LoginResponse(key))
            }
        }

        call.respond(HttpStatusCode.Unauthorized)
    }
}

fun generateAuthenticationKey(random: SecureRandom): String {
    val key = ByteArray(32, { 0 })
    random.nextBytes(key)
    return DatatypeConverter.printHexBinary(key)
}
