package com.kurume_nct.studybattleserver.daemon

import com.kurume_nct.studybattleserver.dao.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.util.*

/**
 * Created by gedorinku on 2017/10/16.
 */
object ProblemDurationObserver : Daemon {

    private val problemCloseQueue = PriorityQueue<ProblemCache>()
    private var sinceId = 0

    override fun onFastUpdate() {
        closeProblemsIfOutOfDuration()
    }

    override fun onSlowUpdate() {
        fetchAssignProblems()
    }

    private fun fetchAssignProblems() = transaction {
        val problems = Problem
                .find { Problems.id.greater(sinceId) }
                .map { it.toCache() }
                .toList()
        sinceId = problems.lastOrNull()?.id ?: sinceId
        problemCloseQueue.addAll(problems)
    }

    private fun closeProblemsIfOutOfDuration() = transaction {
        val now = DateTime.now()
        while (problemCloseQueue.isNotEmpty()) {
            val first = problemCloseQueue.peek()
            if (now < first.closeAt) {
                break
            }

            problemCloseQueue.poll()
            val problem = Problem.findById(first.id) ?: continue
            if (problem.state != ProblemState.Opening) {
                continue
            }
            problem.state = ProblemState.Judging
            problem.assignedUser = null
            ProblemAssignment
                    .find { ProblemAssignments.problem.eq(problem.id) }
                    .forEach {
                        it.delete()
                    }
            problem.flush()
        }
    }

    private data class ProblemCache(val id: Int, val closeAt: DateTime) : Comparable<ProblemCache> {

        override fun compareTo(other: ProblemCache): Int = closeAt.compareTo(other.closeAt)
    }

    private fun Problem.toCache() = transaction {
        ProblemCache(id.value, createdAt + duration)
    }
}