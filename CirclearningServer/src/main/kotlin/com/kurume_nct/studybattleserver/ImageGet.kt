package com.kurume_nct.studybattleserver

import com.kurume_nct.studybattleserver.dao.Image
import com.kurume_nct.studybattleserver.dao.Images
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.content.LocalFileContent
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route
import java.io.File

/**
 * Created by gedorinku on 2017/08/09.
 */
fun Route.getImage() = get<ImageGet> {
    val exists = transaction {
        !Image.find { Images.fileName.eq(it.fileName) }.empty()
    }
    if (!exists) {
        call.respond(HttpStatusCode.NotFound)
        return@get
    }

    call.respond(LocalFileContent(File("images/${it.fileName}")))
}