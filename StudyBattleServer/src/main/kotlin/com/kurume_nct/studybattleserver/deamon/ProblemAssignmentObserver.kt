package com.kurume_nct.studybattleserver.deamon

import com.kurume_nct.studybattleserver.dao.ProblemAssignment
import com.kurume_nct.studybattleserver.dao.ProblemAssignments
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.newSingleThreadContext
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by gedorinku on 2017/10/10.
 */
object ProblemAssignmentObserver {
    private val problemCloseQueue = PriorityQueue<AssignmentCache>()
    private var sinceId = 0

    fun startAsync() = async(newSingleThreadContext("${javaClass.simpleName}Thread")) {
        var lastFetchedAt = DateTime(0L)
        val fetchInterval = Duration.ofSeconds(60).toMillis()

        while (true) {
            val now = DateTime.now()
            if (fetchInterval < now.millis - lastFetchedAt.millis) {
                lastFetchedAt = now
                fetchAssignments()
            }
            closeProblemsIfOutOfDuration()

            delay(2, TimeUnit.SECONDS)
        }
    }

    private fun fetchAssignments() = transaction {
        val assignments = ProblemAssignment
                .find { ProblemAssignments.id.greater(sinceId) }
                .map { AssignmentCache.fromDao(it) }
        sinceId = assignments.last().id
        problemCloseQueue.addAll(assignments)
    }

    private fun closeProblemsIfOutOfDuration() = transaction {
        val now = DateTime.now()
        while (problemCloseQueue.isNotEmpty()) {
            val first = problemCloseQueue.peek()
            if (now < first.closeAt) {
                break
            }
            problemCloseQueue.poll()

            val assignment = ProblemAssignment.findById(first.id) ?: continue
            assignment.problem.assignedUser = null
            assignment.problem.flush()
            assignment.delete()
        }
    }


    private data class AssignmentCache(val id: Int, val closeAt: DateTime) : Comparable<AssignmentCache> {

        companion object {

            fun fromDao(dao: ProblemAssignment): AssignmentCache = transaction {
                AssignmentCache(dao.id.value, dao.closeAt)
            }
        }

        override fun compareTo(other: AssignmentCache): Int {
            val millis = closeAt.millis
            val otherMillis = other.closeAt.millis

            if (millis == otherMillis) {
                return 0
            }
            if (millis < otherMillis) {
                return -1
            }
            return 1
        }
    }
}