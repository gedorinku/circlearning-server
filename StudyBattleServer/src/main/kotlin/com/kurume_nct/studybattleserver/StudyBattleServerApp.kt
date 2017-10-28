package com.kurume_nct.studybattleserver

import com.google.gson.FieldNamingPolicy
import com.kurume_nct.studybattleserver.daemon.ChallengePhaseObsever
import com.kurume_nct.studybattleserver.daemon.DaemonManager
import com.kurume_nct.studybattleserver.daemon.ProblemAssignmentObserver
import com.kurume_nct.studybattleserver.daemon.ProblemDurationObserver
import com.kurume_nct.studybattleserver.dao.*
import com.kurume_nct.studybattleserver.item.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.features.CallLogging
import org.jetbrains.ktor.features.Compression
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.gson.GsonSupport
import org.jetbrains.ktor.locations.Locations
import org.jetbrains.ktor.locations.location
import org.jetbrains.ktor.routing.Routing
import org.jetbrains.ktor.util.ValuesMap
import java.io.File
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import javax.xml.bind.DatatypeConverter

/**
 * Created by gedorinku on 2017/07/23.
 */

@location("/login")
data class Login(val userName: String = "", val password: String = "") {

    companion object {

        fun create(values: ValuesMap): Login? {
            val userName = values["userName"] ?: return null
            val password = values["password"] ?: return null
            return Login(userName, password)
        }
    }
}

@location("/register")
data class Register(val displayName: String = "",
                    val userName: String = "",
                    val password: String = "",
                    val iconImageId: Int = NO_ICON) {
    companion object {
        const val NO_ICON = -1

        fun create(values: ValuesMap): Register? {
            val displayName = values["displayName"] ?: return null
            val userName = values["userName"] ?: return null
            val password = values["password"] ?: return null
            val iconImageId = values["iconImageId"].let {
                if (it == null) {
                    NO_ICON
                } else {
                    it.toIntOrNull() ?: return null
                }
            }
            return Register(displayName, userName, password, iconImageId)
        }
    }
}

@location("/verify_authentication")
data class AuthenticationVerify(val authenticationKey: String = "") {

    companion object {

        fun create(values: ValuesMap): AuthenticationVerify? {
            val authenticationKey = values["authenticationKey"] ?: return null
            return AuthenticationVerify(authenticationKey)
        }
    }
}

@location("/user/by_id/{id}")
data class UserGetById(val id: Int = 0)

/**
 * GET
 * query: String
 */
@location("/user/search")
class UsersSearch

@location("/group/new")
data class GroupCreate(val authenticationKey: String = "", val name: String = "") {

    companion object {

        fun create(values: ValuesMap): GroupCreate? {
            val authenticationKey = values["authenticationKey"] ?: return null
            val name = values["name"] ?: return null
            return GroupCreate(authenticationKey, name)
        }
    }
}

@location("/group/join")
data class GroupJoin(val authenticationKey: String = "", val groupId: Int = 0) {

    companion object {

        fun create(values: ValuesMap): GroupJoin? {
            val authenticationKey = values["authenticationKey"] ?: return null
            val groupId = values["groupId"]?.toIntOrNull() ?: return null
            return GroupJoin(authenticationKey, groupId)
        }
    }
}

@location("/group/leave")
class GroupLeave

@location("/group/attach")
data class GroupAttach(val authenticationKey: String = "", val groupId: Int = 0, val userId: Int = 0) {

    companion object {

        fun create(values: ValuesMap): GroupAttach? {
            val authenticationKey = values["authenticationKey"] ?: return null
            val groupId = values["groupId"]?.toIntOrNull() ?: return null
            val userId = values["userId"]?.toIntOrNull() ?: return null
            return GroupAttach(authenticationKey, groupId, userId)
        }
    }
}

@location("/group/{id}")
data class GroupGet(val authenticationKey: String = "", val id: Int = 0) {

    companion object {

        fun create(values: ValuesMap, id: Int): GroupGet? {
            val authenticationKey = values["authenticationKey"] ?: return null
            return GroupGet(authenticationKey, id)
        }
    }
}

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
                      val id: Int = -1) {

    companion object {

        fun create(values: ValuesMap, id: Int): ProblemGet? {
            val authenticationKey = values["authenticationKey"] ?: return null
            return ProblemGet(authenticationKey, id)
        }
    }
}

@location("/problem/assigned")
data class AssignedProblemsGet(val authenticationKey: String = "", val groupId: Int = 0) {

    companion object {

        fun create(values: ValuesMap): AssignedProblemsGet? {
            val authenticationKey = values["authenticationKey"] ?: return null
            val groupId = values["groupId"]?.toIntOrNull() ?: return null
            return AssignedProblemsGet(authenticationKey, groupId)
        }
    }
}


/**
 * GET
 * authenticationKey
 * problemId
 */
@location("/problem/pass")
class ProblemPass

/**
 * GET
 * authenticationKey
 * problemId
 */
@location("/problem/open")
class ProblemOpen

/**
 * GET
 * authenticationKey
 * groupId
 */
@location("/problem/judged")
class MyJudgedProblemsGet

/**
 * GET
 * authenticationKey
 * groupId
 */
@location("/problem/judging")
class MyJudgingProblemsGet

/**
 * GET
 * authenticationKey
 * groupId
 */
@location("/problem/collecting")
class MyCollectingProblemsGet

@location("/problem/request_new")
data class ProblemRequest(val authenticationKey: String = "", val groupId: Int = 0) {

    companion object {

        fun create(values: ValuesMap): ProblemRequest? {
            val authenticationKey = values["authenticationKey"] ?: return null
            val groupId = values["groupId"]?.toIntOrNull() ?: return null
            return ProblemRequest(authenticationKey, groupId)
        }
    }
}

@location("/solution/create")
data class SolutionCreate(val authenticationKey: String = "",
                          val text: String = "",
                          val problemId: Int = -1,
                          val imageIds: List<Int> = emptyList(),
                          val attachedItemId: Int = 0) {

    companion object {

        fun create(values: ValuesMap): SolutionCreate? {
            val authenticationKey = values["authenticationKey"] ?: return null
            val text = values["text"] ?: return null
            val problemId = values["problemId"]?.toIntOrNull() ?: return null
            val imageIds = mutableListOf<Int?>()
            val attachedItemId = values["attachedItemId"]?.toIntOrNull() ?: return null

            values.forEach { s, list ->
                if (s == "imageIds") {
                    imageIds.addAll(list.map {
                        it.toIntOrNull()
                    })
                }
            }
            imageIds.forEach {
                it ?: return null
            }

            return SolutionCreate(authenticationKey, text, problemId, imageIds.filterNotNull(), attachedItemId)
        }
    }
}

@location("/solution/{id}")
data class SolutionGet(val authenticationKey: String = "",
                       val id: Int = -1) {

    companion object {

        fun create(values: ValuesMap, id: Int): SolutionGet? {
            val authenticationKey = values["authenticationKey"] ?: return null
            return SolutionGet(authenticationKey, id)
        }
    }
}

@location("/solution/judge")
data class SolutionJudge(val authenticationKey: String = "",
                         val id: Int = -1,
                         val isAccepted: Boolean? = null) {

    companion object {

        fun create(values: ValuesMap): SolutionJudge? {
            val authenticationKey = values["authenticationKey"] ?: return null
            val id = values["id"]?.toIntOrNull() ?: return null
            val isAccepted = values["isAccepted"]?.toBoolean() ?: return null
            return SolutionJudge(authenticationKey, id, isAccepted)
        }
    }
}

@location("/my_solution/judged")
class JudgedMySolutionsGet

@location("/my_solution/unjudged")
class UnjudgedMySolutionsGet

@location("/my_items")
class MyItemsGet

/**
 * GET
 * authenticationKey
 * groupId
 */
@location("/ranking")
class Ranking

@location("/comment/create")
data class CommentCreate(val authenticationKey: String = "",
                         val solutionId: Int = 0,
                         val text: String = "",
                         val imageIds: List<Int> = emptyList(),
                         val replyTo: Int = 0) {

    companion object {

        fun create(values: ValuesMap): CommentCreate? {
            val authenticationKey = values["authenticationKey"] ?: return null
            val text = values["text"] ?: return null
            val solutionId = values["solutionId"]?.toIntOrNull() ?: return null
            val imageIds = mutableListOf<Int?>()
            val replyTo = values["replyTo"]?.toIntOrNull() ?: 0

            values.forEach { s, list ->
                if (s == "imageIds") {
                    imageIds.addAll(list.map {
                        it.toIntOrNull()
                    })
                }
            }
            imageIds.forEach {
                it ?: return null
            }

            return CommentCreate(authenticationKey, solutionId, text, imageIds.filterNotNull(), replyTo)
        }
    }
}

private val random = SecureRandom()

fun Application.studyBattleServerApp() {
    connectDataBase()

    registerItems()

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
        searchUsers()
        getUserById()
        createGroup()
        joinGroup()
        leaveGroup()
        getGroup()
        getJoinedGroups()
        uploadImage()
        getImage()
        getImageById()
        createProblem()
        getProblem()
        openProblem()
        getMyJudgedProblems()
        getMyJudgingProblems()
        getMyCollectingProblems()
        getAssignedProblems()
        requestProblem()
        passProblem()
        createSolution()
        getSolution()
        getJudgedMySolutions()
        getUnjudgedMySolutions()
        judgeSolution()
        attachToGroup()
        getMyItems()
        getRanking()
        createComment()
    }

    startDaemons()
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
                AssumedSolutionRelations,
                ProblemAssignments,
                ItemStacks,
                ScoreHistories,
                Comments
              )
    }
}

fun registerItems() = ItemRegistry.apply {
    register(Air)
    register(Bomb)
    register(Shield)
    register(DoubleScoreCard)
    register(MagicHand)
}

fun startDaemons() = DaemonManager.apply {
    register(ProblemAssignmentObserver)
    register(ProblemDurationObserver)
    register(ChallengePhaseObsever)
}.startAsync()

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
