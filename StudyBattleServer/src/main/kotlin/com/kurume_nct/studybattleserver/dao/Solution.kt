package com.kurume_nct.studybattleserver.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by gedorinku on 2017/09/22.
 */
object Solutions : IntIdTable() {

    val author = reference("author", Users)
    val content = reference("content", Contents)
    val problem = reference("problem", Problems)
    val createdAt = datetime("created_at")
}

class Solution(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Solution>(Solutions)

    var author by User referencedOn Solutions.author
    var content by Content referencedOn Solutions.content
    var problem by Problem referencedOn Solutions.problem
    var createdAt by Solutions.createdAt
}