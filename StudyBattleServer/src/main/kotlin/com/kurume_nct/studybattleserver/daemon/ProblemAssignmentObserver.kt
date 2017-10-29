package com.kurume_nct.studybattleserver.daemon

import com.kurume_nct.studybattleserver.dao.ProblemAssignment
import com.kurume_nct.studybattleserver.dao.ProblemAssignments
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.util.*

/**
 * Created by gedorinku on 2017/10/10.
 */
object ProblemAssignmentObserver : Daemon {

    private val problemWithdrawQueue = PriorityQueue<AssignmentCache>()
    private var sinceId = 0

    override fun onFastUpdate() {
        withdrawProblemsIfOutOfDuration()
    }

    override fun onSlowUpdate() {
        fetchAssignments()
    }

    private fun fetchAssignments() = transaction {
        val assignments = ProblemAssignment
                .find { ProblemAssignments.id.greater(sinceId) }
                .map { it.toCache() }
        sinceId = assignments.lastOrNull()?.id ?: sinceId
        problemWithdrawQueue.addAll(assignments)
    }

    private fun withdrawProblemsIfOutOfDuration() {
        if (problemWithdrawQueue.isEmpty()) {
            return
        }

        val now = DateTime.now()

        while (problemWithdrawQueue.isNotEmpty()) {
            val first = problemWithdrawQueue.peek()
            if (now < first.withdrawAt) {
                break
            }

            transaction {
                problemWithdrawQueue.poll()

                val assignment = ProblemAssignment.findById(first.id) ?: return@transaction
                assignment.problem.assignedUser = null
                assignment.problem.flush()
                assignment.delete()
            }
        }
    }


    private data class AssignmentCache(val id: Int, val withdrawAt: DateTime) : Comparable<AssignmentCache> {

        override fun compareTo(other: AssignmentCache): Int = withdrawAt.compareTo(other.withdrawAt)
    }

    private fun ProblemAssignment.toCache() = transaction {
        AssignmentCache(id.value, closeAt)
    }
}