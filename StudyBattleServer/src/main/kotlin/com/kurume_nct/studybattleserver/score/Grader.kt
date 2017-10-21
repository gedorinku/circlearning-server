package com.kurume_nct.studybattleserver.score

import com.kurume_nct.studybattleserver.dao.*
import com.kurume_nct.studybattleserver.item.ItemRegistry
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Created by gedorinku on 2017/10/16.
 */
object Grader {

    fun scoreSolutions(problem: Problem) = transaction {
        val solutions = problem.fetchSubmittedSolutions()
        if (solutions.any { !it.isJudged }) {
            throw IllegalStateException("no judged yet")
        }

        val assignedUsers = problem.fetchAssignedUsers()

        if (assignedUsers.isEmpty()) {
            //誰も提出してない問題何もしなくていいことに気がついた(あほ)
            return@transaction
        }

        val correctCount = solutions.count { it.judgingState == JudgingState.Accepted }
        val correctRate = correctCount / assignedUsers.size.toDouble()
        val defaultScore = getScore(correctRate)
        val firstAcceptScore = getFirstAcceptScore(defaultScore)

        getFirstAcceptedUser(solutions)?.addScore(firstAcceptScore)

        problem.owner.addScore(firstAcceptScore)

        val registeredItems = ItemRegistry.registeredItems

        solutions
                .filter { it.judgingState == JudgingState.Accepted }
                .forEach {
                    val item = registeredItems[it.attachedItemId]
                    val score = item?.onScore(it, defaultScore) ?: defaultScore
                    it.author.addScore(score)
                }
    }

    private const val EPS = 1.0e-6

    private fun getFirstAcceptedUser(solutions: List<Solution>) = transaction {
        solutions
                .filter { it.judgingState == JudgingState.Accepted }
                .sortedBy { it.id.value }
                .firstOrNull()
                ?.author
    }

    private fun getScore(correctRate: Double) = (100.0 * (1.0 - minOf(correctRate, 0.9)) + EPS).toInt()

    private fun getFirstAcceptScore(score: Int) = (score + 10.0 + EPS).toInt()
}