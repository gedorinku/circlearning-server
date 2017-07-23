package com.kurume_nct.studybattleserver

import com.kurume_nct.studybattleserver.dao.User
import com.kurume_nct.studybattleserver.dao.Users
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.Route
import java.security.SecureRandom
import javax.xml.bind.DatatypeConverter

/**
 * Created by gedorinku on 2017/07/23.
 */
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
                //TODO keyを保存
                call.respondText(key)
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
