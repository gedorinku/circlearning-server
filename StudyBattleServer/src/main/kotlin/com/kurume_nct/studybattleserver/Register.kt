package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.Image
import com.kurume_nct.studybattleserver.dao.User
import com.kurume_nct.studybattleserver.dao.Users
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route
import java.security.SecureRandom

/**
 * Created by gedorinku on 2017/07/29.
 */
fun Route.register(random: SecureRandom) = post<Register> { _ ->
    val register = Register.create(call.receive())
    if (register == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }
    if (!isValidUserName(register.userName)) {
        val description = "ユーザー名は2文字以上20文字以下で、半角英数字と_のみ使用可能です。"
        call.respond(HttpStatusCode(HttpStatusCode.BadRequest.value, description))
        return@post
    }

    if (!isValidDisplayName(register.displayName)) {
        val description = "表示名は2文字以上20文字以下である必要があります。"
        call.respond(HttpStatusCode(HttpStatusCode.BadRequest.value, description))
        return@post
    }

    if (transaction {
        !User.find { Users.userName.eq(register.userName) }.empty()
    }) {
        val description = "ユーザー名はすでに使用されています。"
        call.respond(HttpStatusCode(HttpStatusCode.BadRequest.value, description))
        return@post
    }

    val icon = transaction {
        Image.findById(register.iconImageId)
    }

    if (icon == null && register.iconImageId != Register.NO_ICON) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }

    transaction {
        val salt = generateSalt(random)

        User.new {
            userName = register.userName
            displayName = register.displayName
            hashSalt = salt
            passwordHash = hashWithSalt(register.password, salt)
            this.icon = icon
        }
    }

    call.response.status(HttpStatusCode.OK)
    call.respond(Gson().toJson(HttpStatusCode.OK))
}