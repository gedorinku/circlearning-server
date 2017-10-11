package com.kurume_nct.studybattleserver.dao

import com.kurume_nct.studybattleserver.verifyCredentials
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.joda.time.DateTime
import java.time.Duration

/**
 * Created by gedorinku on 2017/08/10.
 */
enum class ProblemState {
    Opening,
    Judging,
    Judged
}

object Problems : IntIdTable() {

    val title = varchar("title", 255)
    val owner = reference("owner", Users)
    val content = reference("content", Contents)
    val createdAt = datetime("created_at")
    val startedAt = datetime("started_at")
    val durationMillis = long("duration_millis")
    val group = reference("group", Groups)
    val assignedUser = reference("assigned_user", Users).nullable()
    val assignedAt = datetime("assigned_at").default(DateTime(0))
    val point = integer("point")
    val state = enumeration("status", ProblemState::class.java)
    val durationPerUserMillis = long("duration_per_uer_millis")

    fun getUserProblems(authenticationKey: String, groupId: Int, state: ProblemState)
            : Pair<List<Problem>?, HttpStatusCode> {
        val user = verifyCredentials(authenticationKey) ?: return Pair(null, HttpStatusCode.Unauthorized)

        val group = transaction {
            Group.findById(groupId)
        }
        if (group == null) {
            val statusCode = HttpStatusCode(404, "Group not found.")
            return Pair(null, statusCode)
        }

        val problems = transaction {
            Problem.find { Problems.state.eq(state) and Problems.owner.eq(user.id) }
                    .toList()
        }

        return Pair(problems, HttpStatusCode.OK)
    }
}

class Problem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Problem>(Problems)

    var title by Problems.title
    var owner by User referencedOn Problems.owner
    var content by Content referencedOn Problems.content
    var createdAt by Problems.createdAt
    var startedAt by Problems.startedAt
    var durationMillis by Problems.durationMillis
    var group by Group referencedOn Problems.group
    var assignedUser by User optionalReferencedOn Problems.assignedUser
    var assignedAt by Problems.assignedAt
    var point by Problems.point
    var state by Problems.state
    var durationPerUserMillis by Problems.durationPerUserMillis

    var duration: Duration
        get() = Duration.ofMillis(durationMillis)
        set(value) {
            durationMillis = value.toMillis()
        }

    var durationPerUser: Duration
        get() = Duration.ofMillis(durationPerUserMillis)
        set(value) {
            durationPerUserMillis = value.toMillis()
        }

    fun assignUser(user: User) = transaction {
        val alreadyAssigned =
                !AssignHistroy.find {
                    AssignHistories.user.eq(user.id) and AssignHistories.problem.eq(this@Problem.id)
                }.empty()
        if (alreadyAssigned) {
            throw IllegalStateException("すでに割り当てられたことのある問題です。")
        }

        val now = DateTime.now()

        AssignHistroy.new {
            this.user = user
            this.problem = this@Problem
        }

        ProblemAssignment.new {
            problem = this@Problem
            assignedAt = now
            closeAt = assignedAt + durationPerUserMillis
        }

        assignedUser = user
        assignedAt = now
        this@Problem.flush()
    }

    fun assignUser(userId: Int) {
        val user = transaction {
            User.findById(userId)
        } ?: throw IllegalArgumentException()

        assignUser(user)
    }
}
