package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.Image
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/10/02.
 */
fun Route.getImageById() = get<ImageGetById> {
    val image = transaction {
        Image.findById(it.id)
    }
    if (image == null) {
        call.respond(HttpStatusCode.NotFound)
        return@get
    }

    val response = ImageUploadResponse.fromImage(image)
    call.respond(Gson().toJson(response))
}