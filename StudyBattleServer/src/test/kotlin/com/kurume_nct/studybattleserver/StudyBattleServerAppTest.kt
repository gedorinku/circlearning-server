package com.kurume_nct.studybattleserver

import com.kurume_nct.studybattleserver.dao.User
import com.kurume_nct.studybattleserver.dao.Users
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.http.formUrlEncode
import org.jetbrains.ktor.testing.TestApplicationCall
import org.jetbrains.ktor.testing.handleRequest
import org.jetbrains.ktor.testing.withTestApplication
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.BeforeClass
import org.junit.Test
import java.security.MessageDigest
import java.security.SecureRandom
import javax.xml.bind.DatatypeConverter

/**
 * Created by gedorinku on 2017/07/23.
 */
class StudyBattleServerAppTest {

    companion object {

        val userName = "test"
        val displayName = "Test"
        val password = "hogehoge"

        @BeforeClass
        @JvmStatic
        fun insertTestUser() {
            connectDataBase()

            val random = SecureRandom()
            val salt = generateSalt(random)
            val passwordHash = hashWithSalt(password, salt)

            transaction {
                val testUser = User.find { Users.userName.eq(userName) }
                        .takeIf { !it.empty() }
                        ?.first()
                if (testUser == null) {
                    User.new {
                        this.userName = StudyBattleServerAppTest.userName
                        this.displayName = StudyBattleServerAppTest.displayName
                        this.hashSalt = salt
                        this.passwordHash = passwordHash
                    }
                } else {
                    testUser.displayName = displayName
                    testUser.hashSalt = salt
                    testUser.passwordHash = passwordHash
                }
            }
        }
    }

    @Test
    fun loginTest() = withTestApplication(Application::studyBattleServerApp) {
        val login: (String, String, TestApplicationCall.() -> Unit) -> Unit = {
            userName, password, test ->
            val values = listOf("userName" to "test", "password" to "hogehoge")
            test(handleRequest(HttpMethod.Post, "/login") {
                addHeader(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
                body = values.formUrlEncode()
            })
        }

        //valid
        login("test", "hogehoge") {
            println(response.content)
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(response.content.orEmpty().length, 64)
        }

        //invalid
        login("test", "piyopiyo") {
            println(response.content)
            assertEquals(HttpStatusCode.Unauthorized, response.status())
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