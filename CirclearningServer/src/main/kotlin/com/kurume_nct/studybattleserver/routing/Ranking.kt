package com.kurume_nct.studybattleserver.routing

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.Ranking
import com.kurume_nct.studybattleserver.dao.*
import com.kurume_nct.studybattleserver.routing.user.UserGetResponse
import com.kurume_nct.studybattleserver.verifyCredentials
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route
import org.joda.time.DateTime
import org.joda.time.Duration

/**
 * Created by gedorinku on 2017/10/19.
 */
data class RankingResponse(val myWeekScore: Int,
                           val myLastWeekScore: Int,
                           val myTotalScore: Int,
                           val ranking: List<Pair<UserGetResponse, Int>>)

fun Route.getRanking() = get<Ranking> {
    val queryParameters = call.request.queryParameters

    val authenticationKey = queryParameters["authenticationKey"].orEmpty()
    val user = verifyCredentials(authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@get
    }

    val groupId = queryParameters["groupId"]?.toIntOrNull()
    if (groupId == null) {
        call.respond(HttpStatusCode(HttpStatusCode.BadRequest.value, "group id"))
        return@get
    }
    val group = transaction {
        Group.findById(groupId)
    }
    if (group == null) {
        call.respond(HttpStatusCode(404, "group not found"))
        return@get
    }

    val now = DateTime.now()
    val since
            = now - Duration.standardDays(now.dayOfWeek - 1L) - Duration.millis(now.millisOfDay.toLong())

    val response = transaction {
        val ranking = getRankingResponse(group, since)
        val myWeekScore = user.getSumOfScore(group, since..now)
        val myLastWeekScore = user.getSumOfScore(group, since.minusDays(7)..since)
        val myTotalScore = user.getSumOfScore(group)
        RankingResponse(myWeekScore, myLastWeekScore, myTotalScore, ranking)
    }
    val json = Gson().toJson(response)
    call.respond(json)
}

private fun getRankingResponse(group: Group, since: DateTime): List<Pair<UserGetResponse, Int>> = transaction {
    val scoreHistories = ScoreHistory
            .find {
                ScoreHistories.createdAt.greaterEq(since) and
                        ScoreHistories.group.eq(group.id)
            }
            .sortedBy {
                it.user.id.value
            }
    val users = Belonging
            .find {
                Belongings.group.eq(group.id)
            }
            .map {
                it.user
            }
    val ranking = mutableMapOf<User, Int>()

    users.forEach {
        ranking[it] = 0
    }
    scoreHistories.forEach {
        ranking[it.user] = ranking[it.user]!! + it.score
    }

    ranking
            .toList()
            .sortedBy { it.second }
            .reversed()
            .take(5)
            .map { UserGetResponse.fromUser(it.first) to it.second }
}
