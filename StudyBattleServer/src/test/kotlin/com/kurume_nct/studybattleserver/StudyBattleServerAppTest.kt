package com.kurume_nct.studybattleserver

import io.netty.handler.codec.http.multipart.InterfaceHttpData
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.http.formUrlEncode
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.testing.handleRequest
import org.jetbrains.ktor.testing.withTestApplication
import org.jetbrains.ktor.util.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.security.MessageDigest
import java.security.SecureRandom
import javax.xml.bind.DatatypeConverter

/**
 * Created by gedorinku on 2017/07/23.
 */
class StudyBattleServerAppTest {

    @Test
    fun loginTest() = withTestApplication(Application::studyBattleServerApp) {
        val values = listOf("userName" to "test", "password" to "hogehoge")
        with (handleRequest(HttpMethod.Post, "/login") {
            addHeader(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
            body = values.formUrlEncode()
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    @Test
    fun hashWithSaltTest() {
        val password = "password123"
        val sha256 = MessageDigest.getInstance("SHA-256")
        sha256.update(password.toByteArray(Charsets.UTF_8))
        val noSaltHash = DatatypeConverter.printHexBinary(sha256.digest())

        val random = SecureRandom()
        val salt = generateSalt(random)
        val hash = hashWithSalt(password, salt)
        assertEquals(hash.length, 64)
        assertNotEquals(noSaltHash, hash)
    }

    @Test
    fun generateSaltTest() {
        val random = SecureRandom()
        val salt = generateSalt(random)
        assertEquals(salt.length, 64)
    }

}