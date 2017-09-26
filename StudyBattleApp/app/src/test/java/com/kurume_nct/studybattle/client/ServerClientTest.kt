package com.kurume_nct.studybattle.client

import org.joda.time.DateTime
import org.joda.time.Duration
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import java.io.InputStream
import java.net.URL
import java.security.MessageDigest
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

        private fun generateTestUserName(): String {
            val prefix = "chie_"
            return prefix + printHex(generateRandomBytes(4))
        }

        private fun generateTestUserPassword(): String = printHex(generateRandomBytes(16))

        private fun generateRandomBytes(size: Int): ByteArray {
            val randomBytes = ByteArray(size)
            random.nextBytes(randomBytes)
            return randomBytes
        }

        private fun printHex(bytes: ByteArray): String
                = bytes.map { String.format("%02x", it) }.joinToString("")

        private fun hashContent(inputStream: InputStream): String {
            val md5 = MessageDigest.getInstance("MD5")
            inputStream.use {
                while (true) {
                    val temp = it.read()
                    if (temp == -1) {
                        break
                    }
                    md5.update(temp.toByte())
                }
            }

            return printHex(md5.digest())
        }
    }


    @Test
    fun createAndJoinGroupTest() {
        val groupName = "kamesan_" + printHex(generateRandomBytes(4))
        client.createGroup(groupName)
                .flatMap { client.joinGroup(it.id) }
                .blockingSubscribe()
    }

    @Test
    fun uploadImageTest() {
        val fileName = "icon.png"
        val classLoader = javaClass.classLoader
        val testSubscriber = client
                .uploadImage(classLoader.getResourceAsStream(fileName), "image/png")
                .test()

        testSubscriber.awaitTerminalEvent()
        val url = testSubscriber
                .assertNoErrors()
                .assertNoTimeout()
                .values()[0]
                .url

        val origin = hashContent(classLoader.getResourceAsStream(fileName))
        val upload = hashContent(URL(url).openStream())
        assertEquals(origin, upload)
    }

    @Test
    fun createProblemAndSolutionTest() {
        val problem = {
            val problemTitle = "hoge"
            val problemText = "うぇい\nそいい\nabc"
            val startsAt = DateTime.now() - Duration.standardMinutes(1)
            val duration = Duration.standardHours(1)

            val testSubscriber = client
                    .createProblem(problemTitle, problemText, emptyList(), startsAt, duration)
                    .test()

            testSubscriber.awaitTerminalEvent()
            val problem = testSubscriber
                    .assertNoErrors()
                    .assertNoTimeout()
                    .values()[0]

            assertEquals(problemTitle, problem.title)
            assertEquals(problemText, problem.text)
            assertEquals(startsAt.millis, problem.startsAtTime.millis)
            assertEquals(duration, problem.duration)

            problem
        }()

        val solutionText = "英語は世界中の多くの人間によって作られる\n" +
                "スポーツ研究会だ。"

        val testSubscriber = client
                .createSolution(solutionText, problem, emptyList())
                .test()
        testSubscriber.awaitTerminalEvent()

        val solution = testSubscriber
                .assertNoErrors()
                .assertNoTimeout()
                .values()[0]

        assertEquals(solutionText, solution.text)
        assertEquals(problem.id, solution.problemId)
    }
}