package com.kurume_nct.studybattleserver

import com.kurume_nct.studybattleserver.dao.AuthenticationKeys
import com.kurume_nct.studybattleserver.dao.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.features.Compression
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.gson.GsonSupport
import org.jetbrains.ktor.locations.Locations
import org.jetbrains.ktor.locations.location
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.routing.Routing
import java.io.File
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import javax.xml.bind.DatatypeConverter

/**
 * Created by gedorinku on 2017/07/23.
 */

@location("/login")
data class Login(val userName: String = "", val password: String = "")

private val random = SecureRandom()

fun Application.studyBattleServerApp() {
    connectDataBase()

    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(Locations)
    install(GsonSupport) {
        setPrettyPrinting()
    }

    install(Routing) {
        login(random)
    }
}

fun connectDataBase() {
    val properties = Properties()
    File("database.properties").reader().use {
        properties.load(it)
    }

    Database.connect(
            url = properties.getProperty("url"),
            driver = properties.getProperty("driver"),
            user = properties.getProperty("user"),
            password = properties.getProperty("password")
    )

    transaction {
        create(Users, AuthenticationKeys)
    }
}

fun hashWithSalt(password: String, salt: String): String {
    val sha256 = MessageDigest.getInstance("SHA-256")
    sha256.update(password.toByteArray(Charsets.UTF_8))
    sha256.update(salt.toByteArray(Charsets.UTF_8))
    return DatatypeConverter.printHexBinary(sha256.digest())
}

fun generateSalt(random: SecureRandom): String {
    val salt = ByteArray(32, { 0 })
    random.nextBytes(salt)
    return DatatypeConverter.printHexBinary(salt)
}