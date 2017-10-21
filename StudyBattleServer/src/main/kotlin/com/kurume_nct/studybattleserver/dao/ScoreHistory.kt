package com.kurume_nct.studybattleserver.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by gedorinku on 2017/10/16.
 */
object ScoreHistories : IntIdTable() {

    val score = integer("score")
    val user = reference("user", Users)
    val group = reference("group", Groups)
    val createdAt = datetime("created_at")
}

class ScoreHistory(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ScoreHistory>(ScoreHistories)

    var score by ScoreHistories.score
    var user by User referencedOn ScoreHistories.user
    var group by Group referencedOn ScoreHistories.group
    var createdAt by ScoreHistories.createdAt
}