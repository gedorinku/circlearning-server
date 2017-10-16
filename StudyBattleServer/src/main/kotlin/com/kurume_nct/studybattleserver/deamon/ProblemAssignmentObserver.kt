package com.kurume_nct.studybattleserver.deamon

import com.kurume_nct.studybattleserver.dao.ProblemAssignment
import com.kurume_nct.studybattleserver.dao.ProblemAssignments
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.util.*

/**
 * Created by gedorinku on 2017/10/10.
 */
object ProblemAssignmentObserver : Daemon {

    private val problemCloseQueue = PriorityQueue<AssignmentCache>()
    private var sinceId = 0

    override fun onFastUpdate() {
        closeProblemsIfOutOfDuration()
    }

    override fun onSlowUpdate() {
        fetchAssignments()
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