package com.kurume_nct.studybattleserver.dao

import org.jetbrains.exposed.dao.*

/**
 * Created by gedorinku on 2017/10/23.
 */
object Comments : IntIdTable() {

    val replyTo = reference("reply_to", Users).nullable()
    val createdAt = datetime("created_at")
    val body = reference("body", Contents)
}

class Comment(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Comment>(Comments)

    var replyTo by User optionalReferencedOn Comments.replyTo
    var createdAt by Comments.createdAt
    var body by Content referencedOn Comments.body
}