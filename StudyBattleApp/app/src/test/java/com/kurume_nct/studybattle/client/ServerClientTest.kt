package com.kurume_nct.studybattle.client

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import org.junit.BeforeClass
import org.junit.Test
import java.security.SecureRandom

/**
 * Created by gedorinku on 2017/09/23.
 */
class ServerClientTest {

    companion object {

        val random = SecureRandom()
        val displayName = "里中チエ"
        val userName = generateTestUserName()
        val password = generateTestUserPassword()
        val client = ServerClient()

        @JvmStatic
        @BeforeClass
        fun registerTestUser() {
            client
                    .register(displayName, userName, password)
                    .flatMap { client.login(userName, password) }
                    .blockingSubscribe()
        }

        fun generateTestUserName(): String {
            val prefix = "chie_"
            return prefix + printHex(generateRandomBytes(4))
        }

        fun generateTestUserPassword(): String = printHex(generateRandomBytes(16))

        fun generateRandomBytes(size: Int): ByteArray {
            val randomBytes = ByteArray(size)
            random.nextBytes(randomBytes)
            return randomBytes
        }

        fun printHex(bytes: ByteArray): String
                = bytes.map { String.format("%02x", it) }.joinToString("")
    }


    @Test
    fun createAndJoinGroupTest() {
        val groupName = "kamesan_" + printHex(generateRandomBytes(4))
        client.createGroup(groupName)
                .flatMap { client.joinGroup(it.id) }
                .blockingSubscribe()
    }
}