package com.kurume_nct.studybattleserver.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by gedorinku on 2017/09/03.
 */
object ContentImageRelations : IntIdTable() {

    val content = reference("content", Contents)
    val image = reference("image", Images)
}

class ContentImageRelation(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ContentImageRelation>(ContentImageRelations)

    var content by Content referencedOn ContentImageRelations.content
    var image by Image referencedOn ContentImageRelations.image
}