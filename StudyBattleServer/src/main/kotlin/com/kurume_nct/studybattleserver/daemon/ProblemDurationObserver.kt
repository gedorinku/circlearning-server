package com.kurume_nct.studybattleserver.daemon

import com.kurume_nct.studybattleserver.dao.*
import org.jetbrains.exposed.sql.and
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
        fetchProblems()
    }

    private fun fetchProblems() = transaction {
        val problems = Problem
                .find {
                    Problems.id.greater(sinceId)
                }
                .map { it.toCache() }
                .toList()
        sinceId = problems.lastOrNull()?.id ?: sinceId
        problemCloseQueue.addAll(problems)
    }

    private fun closeProblemsIfOutOfDuration() {
        if (problemCloseQueue.isEmpty()) {
            return
        }

        val now = DateTime.now()

        while (problemCloseQueue.isNotEmpty()) {
            val first = problemCloseQueue.peek()
            if (now < first.closeAt) {
                break
            }

            transaction {
                problemCloseQueue.poll()
                val problem = Problem.findById(first.id) ?: return@transaction
                if (problem.state != ProblemState.Opening) {
                    return@transaction
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
    }

    private data class ProblemCache(val id: Int, val closeAt: DateTime) : Comparable<ProblemCache> {

        override fun compareTo(other: ProblemCache): Int = closeAt.compareTo(other.closeAt)
    }

    private fun Problem.toCache() = transaction {
        //Transactionに同名のメンバーがあったため。
        val self = this@toCache
        ProblemCache(self.id.value, self.createdAt + self.duration)
    }
}