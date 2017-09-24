package com.kurume_nct.studybattleserver

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.dao.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.http.formUrlEncode
import org.jetbrains.ktor.request.PartData
import org.jetbrains.ktor.testing.TestApplicationCall
import org.jetbrains.ktor.testing.TestApplicationHost
import org.jetbrains.ktor.testing.handleRequest
import org.jetbrains.ktor.testing.withTestApplication
import org.jetbrains.ktor.util.ValuesMap
import org.joda.time.DateTime
import org.joda.time.Duration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.BeforeClass
import org.junit.Test
import java.io.File
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import javax.xml.bind.DatatypeConverter

/**
 * Created by gedorinku on 2017/07/23.
 */
class StudyBattleServerAppTest {

    companion object {

        val testUserName = "test"
        val testDisplayName = "Test"
        lateinit var testPassword: String
        val random = SecureRandom()

        @BeforeClass
        @JvmStatic
        fun insertTestUser() {
            connectDataBase()

            testPassword = randomPassword(random)
            val salt = generateSalt(random)
            val passwordHash = hashWithSalt(testPassword, salt)

            transaction {
                val testUser = User.find { Users.userName.eq(testUserName) }
                        .takeIf { !it.empty() }
                        ?.first()
                if (testUser == null) {
                    User.new {
                        this.userName = StudyBattleServerAppTest.testUserName
                        this.displayName = StudyBattleServerAppTest.testDisplayName
                        this.hashSalt = salt
                        this.passwordHash = passwordHash
                    }
                } else {
                    testUser.displayName = testDisplayName
                    testUser.hashSalt = salt
                    testUser.passwordHash = passwordHash
                }
            }
        }

        fun randomPassword(random: SecureRandom): String {
            val password = ByteArray(32, { 0 })
            random.nextBytes(password)
            return DatatypeConverter.printHexBinary(password)
        }
    }

    private fun TestApplicationHost.login(userName: String,
                                          password: String,
                                          test: TestApplicationCall.() -> Unit = {}): String? {
        val values = listOf("userName" to userName, "password" to password)
        val call = handleRequest(HttpMethod.Post, "/login") {
            addHeader(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
            body = values.formUrlEncode()
        }
        test(call)
        return Gson().fromJson(call.response.content.orEmpty(), LoginResponse::class.java)
                ?.authenticationKey
    }

    @Test
    fun loginTest() = withTestApplication(Application::studyBattleServerApp) {
        //valid
        login(testUserName, testPassword) {
            assertEquals(HttpStatusCode.OK, response.status())
            val gson = Gson()
            val key = gson.fromJson(response.content.orEmpty(), LoginResponse::class.java)
            assertEquals(key.authenticationKey.length, 64)
        }

        //invalid
        login(testUserName, "piyopiyo") {
            assertEquals(HttpStatusCode.Unauthorized, response.status())
            assertEquals(response.content, null)
        }

        Unit
    }

    @Test
    fun registerTest() = withTestApplication(Application::studyBattleServerApp) {
        val register: (String, String, String, TestApplicationCall.() -> Unit) -> Unit = { userName, displayName, password, test ->
            transaction {
                User.find { Users.userName.eq(userName) }
                        .forEach { it.delete() }
            }
            val values = listOf("userName" to userName, "displayName" to displayName, "password" to password)
            test(
                    handleRequest(HttpMethod.Post, "/register") {
                        addHeader(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
                        body = values.formUrlEncode()
                    }
                )
        }

        //valid
        val test2UserName = "test2"
        val test2DisplayName = "テスト2"
        val test2Password = randomPassword(random)
        register(test2UserName, test2DisplayName, test2Password) {
            assertEquals(HttpStatusCode.OK, response.status())
            val user = transaction {
                val result = User.find { Users.userName.eq(test2UserName) }
                assertEquals(1, result.count())
                result.first()
            }
            assertEquals(test2DisplayName, user.displayName)
            assertEquals(hashWithSalt(test2Password, user.hashSalt), user.passwordHash)
        }

        //invalid
        register("a", "Test2", randomPassword(random)) {
            assertEquals(HttpStatusCode.BadRequest.value, response.status()?.value)
        }

        //invalid
        register("hoge;", "Test2", randomPassword(random)) {
            assertEquals(HttpStatusCode.BadRequest.value, response.status()?.value)
        }

        //invalid
        register("a", "Test2", randomPassword(random)) {
            assertEquals(HttpStatusCode.BadRequest.value, response.status()?.value)
        }

        //invalid
        register("test2", "あ", randomPassword(random)) {
            assertEquals(HttpStatusCode.BadRequest.value, response.status()?.value)
        }

        //invalid
        register("test2", "test2;", randomPassword(random)) {
            assertEquals(HttpStatusCode.BadRequest.value, response.status()?.value)
        }
    }

    @Test
    fun createGroupTest() = withTestApplication(Application::studyBattleServerApp) {
        val createGroup: (String, String, TestApplicationCall.() -> Unit) -> Unit = { authenticationKey, groupName, test ->
            val user = com.kurume_nct.studybattleserver.verifyCredentials(authenticationKey)
                    ?: throw IllegalStateException("Unauthorized")
            transaction {
                Group.find { Groups.owner.eq(user.id) and Groups.name.eq(groupName) }
                        .forEach { it.delete() }
            }

            val values = listOf("authenticationKey" to authenticationKey, "name" to groupName)
            test(
                    handleRequest(HttpMethod.Post, "/group/new") {
                        addHeader(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
                        body = values.formUrlEncode()
                    }
                )
        }

        val authKey = login(testUserName, testPassword)!!
        val groupName = "piyopiuo"
        createGroup(authKey, groupName) {
            assertEquals(HttpStatusCode.OK, response.status())
            val user = verifyCredentials(authKey)!!
            val count = transaction {
                Group.find { Groups.owner.eq(user.id) and Groups.name.eq(groupName) }.count()
            }
            assertEquals(1, count)
        }
    }

    @Test
    fun joinGroupTest() = withTestApplication(Application::studyBattleServerApp) {
        val joinGroup: (String, EntityID<Int>, TestApplicationCall.() -> Unit) -> Unit = { authenticationKey, groupId, test ->
            val values = listOf(
                    "authenticationKey" to authenticationKey,
                    "groupId" to groupId.toString()
                               )
            test(
                    handleRequest(HttpMethod.Post, "/group/join") {
                        addHeader(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
                        body = values.formUrlEncode()
                    }
                )
        }

        val authenticationKey = login(testUserName, testPassword)!!
        val user = verifyCredentials(authenticationKey)!!
        val random = Random()
        val groupName = "test" + random.nextLong().toString(16)
        val group = transaction {
            Group.new {
                name = groupName
                owner = user
            }
        }

        joinGroup(authenticationKey, group.id) {
            assertEquals(HttpStatusCode.OK, response.status())
            val result = transaction {
                Belonging.find {
                    Belongings.user.eq(user.id) and
                            Belongings.group.eq(group.id)
                }.count()
            }

            assertEquals(1, result)
        }
    }

    @Test
    fun uploadImageTest() = withTestApplication(Application::studyBattleServerApp) {
        val uploadImage: (String, File, TestApplicationCall.() -> Unit) -> Unit = { authenticationKey, image, test ->
            test(
                    handleRequest(HttpMethod.Post, "image/upload") {
                        addHeader(HttpHeaders.ContentType, "multipart/form-data")
                        val authPart = PartData.FormItem(
                                authenticationKey, {},
                                ValuesMap.build {
                                    append(HttpHeaders.ContentDisposition, "form-data; name=\"authenticationKey\"")
                                }
                                                        )
                        val imagePart = PartData.FileItem(
                                { image.inputStream() }, {},
                                ValuesMap.build {
                                    append(HttpHeaders.ContentDisposition, "form-data; name=\"image\"")
                                }
                                                         )
                        multiPartEntries = listOf(authPart, imagePart)
                    }
                )
        }

        val authenticationKey = login(testUserName, testPassword)!!
        uploadImage(authenticationKey, File("assets/worry.png")) {
            assertEquals(HttpStatusCode.OK, response.status())
            val id = Gson()
                    .fromJson(response.content.orEmpty(), ImageUploadResponse::class.java)
                    .id
            assert(0 < id)
        }

        uploadImage(authenticationKey, File("assets/worry.jpg")) {
            val originHash = {
                val sha256 = MessageDigest.getInstance("SHA-256")
                val bytes = File("assets/worry.jpg").inputStream().use { it.readBytes() }
                DatatypeConverter.printHexBinary(sha256.digest(bytes))
            }()

            assertEquals(HttpStatusCode.OK, response.status())
            val uploadResponse = Gson()
                    .fromJson(response.content.orEmpty(), ImageUploadResponse::class.java)
            assert(0 < uploadResponse.id)

            handleRequest(HttpMethod.Get, "image/${uploadResponse.fileName}").apply {
                val sha256 = MessageDigest.getInstance("SHA-256")
                val hash = DatatypeConverter.printHexBinary(sha256.digest(response.byteContent))
                assertEquals(originHash, hash)
            }
        }

        uploadImage(authenticationKey, File("assets/worry.pdf")) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }

    @Test
    fun createProblemAndSolutionTest() = withTestApplication(Application::studyBattleServerApp) {
        val createProblem: (ProblemCreate, TestApplicationCall.() -> Unit) -> Unit
                = { (authenticationKey, title, text, imageIds, startsAt, durationMillis), test ->
            val imageIdsEncoded = imageIds
                    .mapIndexed { index, id -> "imageIds" to id.toString() }
            val values = mutableListOf(
                    "authenticationKey" to authenticationKey,
                    "title" to title,
                    "text" to text,
                    "startsAt" to startsAt,
                    "durationMillis" to durationMillis.toString()
                                      )
            values.addAll(imageIdsEncoded)

            test(
                    handleRequest(HttpMethod.Post, "/problem/create") {
                        addHeader(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
                        body = values.formUrlEncode()
                    }
                )
        }

        val getProblem: (ProblemGet, TestApplicationCall.() -> Unit) -> Unit
                = { (authenticationKey, id), test ->
            val values = listOf("authenticationKey" to authenticationKey)

            test(
                    handleRequest(HttpMethod.Post, "problem/$id") {
                        addHeader(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
                        body = values.formUrlEncode()
                    }
                )
        }

        val createSolution: (SolutionCreate, TestApplicationCall.() -> Unit) -> Unit
                = { (authenticationKey, text, problemId, imageIds), test ->
            val imageIdsEncoded = imageIds
                    .mapIndexed { index, id -> "imageIds" to id.toString() }
            val values = mutableListOf(
                    "authenticationKey" to authenticationKey,
                    "text" to text,
                    "problemId" to problemId.toString()
                                      )
            values.addAll(imageIdsEncoded)

            test(
                    handleRequest(HttpMethod.Post, "/solution/create") {
                        addHeader(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
                        body = values.formUrlEncode()
                    }
                )
        }

        val getSolution: (SolutionGet, TestApplicationCall.() -> Unit) -> Unit
                = { (authenticationKey, id), test ->
            val values = listOf("authenticationKey" to authenticationKey)

            test(
                    handleRequest(HttpMethod.Post, "/solution/$id") {
                        addHeader(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
                        body = values.formUrlEncode()
                    }
                )
        }

        val authenticationKey = login(testUserName, testPassword)!!
        val problemTitle = "hoge"
        val problemText =
                "うぇい\n" +
                        "ほげほげ\n" +
                        "abc"
        val startsAt = DateTime.now()
        val duration = Duration.standardHours(1)
        createProblem(
                ProblemCreate(
                        authenticationKey,
                        problemTitle,
                        problemText,
                        emptyList(),
                        startsAt.toString(),
                        duration.millis
                             )
                     ) {
            assertEquals(HttpStatusCode.OK, response.status())
            val problemId = Gson()
                    .fromJson(
                            response.content.orEmpty(),
                            ProblemCreateResponse::class.java
                             )
                    .id
            assert(0 < problemId)

            getProblem(ProblemGet(authenticationKey, problemId)) {
                val problem = Gson()
                        .fromJson(
                                response.content.orEmpty(),
                                ProblemGetResponse::class.java
                                 )

                assertEquals(problemId, problem.id)
                assertEquals(problemTitle, problem.title)
                assertEquals(problemText, problem.text)
                assertEquals(startsAt, DateTime(problem.startsAt))
                assertEquals(duration, Duration.millis(problem.durationMillis))
            }

            val solutionText =
                    "そい\n" +
                            "そおお"

            createSolution(
                    SolutionCreate(
                            authenticationKey,
                            solutionText,
                            problemId,
                            emptyList()
                                  )
                          ) {
                assertEquals(HttpStatusCode.OK, response.status())
                val solutionId = Gson()
                        .fromJson(
                                response.content.orEmpty(),
                                SolutionCreateResponse::class.java
                                 )
                        .id
                assert(0 < solutionId)
                println(solutionId)

                getSolution(SolutionGet(authenticationKey, solutionId)) {
                    assertEquals(HttpStatusCode.OK, response.status())
                    val solution = Gson()
                            .fromJson(
                                    response.content.orEmpty(),
                                    SolutionGetResponse::class.java
                                     )
                    assertEquals(solutionText, solution.text)
                    assertEquals(problemId, solution.problemId)
                }
            }
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