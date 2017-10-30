package com.kurume_nct.studybattleserver.daemon

import com.kurume_nct.studybattleserver.dao.Problem
import com.kurume_nct.studybattleserver.dao.ProblemState
import com.kurume_nct.studybattleserver.dao.Problems
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.util.*

/**
 * Created by gedorinku on 2017/10/28.
 */
object ChallengePhaseObsever : Daemon {

    private val phaseCloseQueue = PriorityQueue<ChallengePhaseCache>()
    private var sinceId = 0

    override fun onFastUpdate() {
        closeChallengePhaseIfOutOfDuration()
    }

    override fun onSlowUpdate() {
        fetchProblems()
    }

    private fun fetchProblems() = transaction {
        val problems = Problem
                .find {
                    Problems.id.greater(sinceId) and
                            Problems.state.eq(ProblemState.ChallengePhase)
                }
                .map { it.toCache() }
                .toList()
        sinceId = problems.lastOrNull()?.id ?: sinceId
        phaseCloseQueue.addAll(problems)
    }

    private fun closeChallengePhaseIfOutOfDuration() {
        if (phaseCloseQueue.isEmpty()) {
            return
        }

        val now = DateTime.now()

        while (phaseCloseQueue.isNotEmpty()) {
            val first = phaseCloseQueue.peek()
            if (now < first.phaseEndsAt) {
                break
            }

            transaction {
                phaseCloseQueue.poll()
                val problem = Problem.findById(first.id) ?: return@transaction
                problem.state = ProblemState.Judged
            }
        }
    }

    private data class ChallengePhaseCache(val id: Int, val phaseEndsAt: DateTime) : Comparable<ChallengePhaseCache> {
        override fun compareTo(other: ChallengePhaseCache): Int
                = phaseEndsAt.compareTo(other.phaseEndsAt)

    }

    private fun Problem.toCache() = transaction {
        val self = this@toCache
        ChallengePhaseCache(self.id.value, self.challengePhaseStartsAt + Problems.challengePahseDuration)
    }
}