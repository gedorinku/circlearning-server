package com.kurume_nct.studybattleserver.routing.image

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.ImageUpload
import com.kurume_nct.studybattleserver.dao.Image
import com.kurume_nct.studybattleserver.getFullUrl
import com.kurume_nct.studybattleserver.startsWith
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.request.PartData
import org.jetbrains.ktor.request.receiveMultipart
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route
import java.io.File
import java.nio.ByteBuffer
import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

/**
 * Created by gedorinku on 2017/08/09.
 */
data class ImageUploadResponse(var id: Int = 0, var url: String = "", var fileName: String = "") {

    companion object {

        fun fromImage(image: Image): ImageUploadResponse = transaction {
            ImageUploadResponse(image.id.value, getFullUrl("image/${image.fileName}"), image.fileName)
        }
    }
}

fun Route.uploadImage() = post<ImageUpload> {
    var imageBinary = emptyList<Byte>()

    call.receiveMultipart().parts.forEach {
        if (it is PartData.FileItem && it.partName == "image") {
            imageBinary = it.streamProvider.invoke().use { it.readBytes() }.toList()
        }
    }

    val fileExtension = getFileExtension(imageBinary)
    if (fileExtension.isEmpty()) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }

    val sha256 = MessageDigest.getInstance("SHA-256")
    sha256.update(imageBinary.toByteArray())
    sha256.update(
            ByteBuffer.allocate(java.lang.Long.BYTES).apply {
                putLong(System.currentTimeMillis())
            }.array()
    )

    val fileName = DatatypeConverter.printHexBinary(sha256.digest()) +
            fileExtension

    val baseDir = "images/"
    File(baseDir).mkdir()

    File(baseDir + fileName).outputStream().use {
        stream ->
        stream.write(imageBinary.toByteArray())
    }

    val image = transaction {
        Image.new {
            this.fileName = fileName
        }
    }

    call.respond(Gson().toJson(ImageUploadResponse.fromImage(image)))
}

/***
 * 画像バイナリから拡張子を調べます.
 * @return ".jpeg"もしくは".png"、それ以外のフォーマットの場合は空文字列
 */
fun getFileExtension(image: List<Byte>): String {
    val jpegPrefix = listOf(0xFF.toByte(), 0xD8.toByte())
    if (image.startsWith(jpegPrefix)) {
        return ".jpg"
    }

    val pngPrefix = listOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte())
    if (image.startsWith(pngPrefix)) {
        return ".png"
    }

    //unknown formatヾ(｡>﹏<｡)ﾉﾞ✧*。
    return ""
}
