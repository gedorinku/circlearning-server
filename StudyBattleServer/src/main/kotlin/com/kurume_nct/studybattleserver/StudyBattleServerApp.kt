package com.kurume_nct.studybattleserver

import com.google.gson.FieldNamingPolicy
import com.kurume_nct.studybattleserver.dao.*
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

@location("/register")
data class Register(val displayName: String = "",
                    val userName: String = "",
                    val password: String = "",
                    val iconImageId: Int = NO_ICON)  {
    companion object {
        const val NO_ICON = -1
    }
}

@location("/verify_authentication")
data class AuthenticationVerify(val authenticationKey: String = "")

@location("/user/by_id/{id}")
data class UserGetById(val id: Int = 0)

@location("/group/new")
data class GroupCreate(val authenticationKey: String = "", val name: String = "")

@location("/group/join")
data class GroupJoin(val authenticationKey: String = "", val groupId: Int = 0)

@location("/group/attach")
data class GroupAttach(val authenticationKey: String = "", val groupId: Int = 0, val userId: Int = 0)

@location("/group/{id}")
data class GroupGet(val authenticationKey: String = "", val id: Int = 0)

@location("/group/joined")
class JoinedGroupsGet

@location("/image/upload")
class ImageUpload

@location("/image/{fileName}")
data class ImageGet(val fileName: String = "")

@location("/image_by_id/{id}")
data class ImageGetById(val id: Int = 0)

@location("/problem/create")
data class ProblemCreate(val authenticationKey: String = "",
                         val title: String = "",
                         val text: String = "",
                         val imageIds: List<Int> = emptyList(),
                         val startsAt: String = "",
                         val durationMillis: Long = 0,
                         val groupId: Int = 0,
                         val assumedSolution: SolutionCreate = SolutionCreate())

@location("/problem/{id}")
data class ProblemGet(val authenticationKey: String = "",
                      val id: Int = -1)

@location("/problem/assigned")
data class AssignedProblemsGet(val authenticationKey: String = "", val groupId: Int = 0)

@location("/problem/request_new")
data class ProblemRequest(val authenticationKey: String, val groupId: Int = 0)

@location("/solution/create")
data class SolutionCreate(val authenticationKey: String = "",
                          val text: String = "",
                          val problemId: Int = -1,
                          val imageIds: List<Int> = emptyList())

@location("/solution/{id}")
data class SolutionGet(val authenticationKey: String = "",
                       val id: Int = -1)

@location("/solution/judge")
data class SolutionJudge(val authenticationKey: String = "",
                         val id: Int = -1,
                         val isAccepted: Boolean? = null)

@location("/my_solution/judged")
class JudgedMySolutionsGet

@location("/my_solution/unjudged")
class UnjudgedMySolutionsGet

private val random = SecureRandom()

fun Application.studyBattleServerApp() {
    connectDataBase()

    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(GsonSupport) {
        setPrettyPrinting()
        setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
    }
    install(Locations)

    install(Routing) {
        login(random)
        register(random)
        verifyAuthentication()
        getUserById()
        createGroup()
        joinGroup()
        getGroup()
        getJoinedGroups()
        uploadImage()
        getImage()
        getImageById()
        createProblem()
        getProblem()
        getAssignedProblems()
        requestProblem()
        createSolution()
        getSolution()
        getJudgedMySolutions()
        getUnjudgedMySolutions()
        judgeSolution()
        attachToGroup()
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
        create(
                Users,
                AuthenticationKeys,
                Groups,
                Belongings,
                Images,
                Contents,
                Problems,
                ContentImageRelations,
                Solutions,
                AssignHistories,
                AssumedSolutionRelations
              )
    }
}

fun hashWithSalt(password: String, salt: String): String {
    val sha256 = MessageDigest.getInstance("SHA-256")
    val passwordBytes = password.toByteArray(Charsets.UTF_8)
    val saltBytes = salt.toByteArray(Charsets.UTF_8)
    sha256.update(passwordBytes)
    sha256.update(saltBytes)

    (1..stretchCount).forEach {
        sha256.update(sha256.digest())
        sha256.update(passwordBytes)
        sha256.update(saltBytes)
    }

    return DatatypeConverter.printHexBinary(sha256.digest())
}

fun generateSalt(random: SecureRandom): String {
    val salt = ByteArray(32, { 0 })
    random.nextBytes(salt)
    return DatatypeConverter.printHexBinary(salt)
}

fun verifyCredentials(authenticationKey: String): User? {
    if (!authenticationKeyPattern.matches(authenticationKey)) {
        return null
    }

    val keyHash = hashWithSalt(authenticationKey, "")
    return transaction {
        val keyOrEmpty = AuthenticationKey.find { AuthenticationKeys.keyHash.eq(keyHash) }
        if (keyOrEmpty.empty()) {
            null
        } else {
            keyOrEmpty.first().user
        }
    }
}

fun isValidUserName(userName: String): Boolean = userNamePattern.matches(userName)

fun isValidDisplayName(displayName: String): Boolean = displayNamePattern.matches(displayName)

fun getFullUrl(relativePath: String): String = "http://studybattle.dip.jp:8080/$relativePath"

private val userNamePattern = "^[a-zA-Z0-9_]{2,20}".toRegex()
private val displayNamePattern = "^[0-9a-zA-Zぁ-んァ-ヶ一-龠々ー_-]{2,20}".toRegex()
private val authenticationKeyPattern = "[a-zA-Z0-9_]+".toRegex()
private val stretchCount = 10000
