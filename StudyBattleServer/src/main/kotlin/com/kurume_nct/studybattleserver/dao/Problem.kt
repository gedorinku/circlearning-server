package com.kurume_nct.studybattleserver.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import java.time.Duration

/**
 * Created by gedorinku on 2017/08/10.
 */
object Problems : IntIdTable() {

    val title = varchar("title", 255)
    val owner = reference("owner", Users)
    val content = reference("content", Contents)
    val createdAt = datetime("created_at")
    val startedAt = datetime("started_at")
    val durationMillis = long("duration_millis")
    val point = integer("point")
}

class Problem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Problem>(Problems)

    var title by Problems.title
    var owner by User referencedOn Problems.owner
    var content by Content referencedOn Problems.content
    var createdAt by Problems.createdAt
    var startedAt by Problems.startedAt
    var durationMillis by Problems.durationMillis
    var point by Problems.point

    var duration: Duration
        get() = Duration.ofMillis(durationMillis)
        set(value) {
            durationMillis = value.toMillis()
        }
}
