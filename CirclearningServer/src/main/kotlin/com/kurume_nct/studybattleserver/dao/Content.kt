package com.kurume_nct.studybattleserver.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Created by gedorinku on 2017/08/10.
 */
object Contents : IntIdTable() {

    val text = text("text")
}

class Content(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Content>(Contents)

    var text by Contents.text

    fun relateImages(vararg images: Image) {
        val self = this
        images.requireNoNulls().forEach {
            transaction {
                ContentImageRelation.new {
                    this.content = self
                    this.image = it
                }
            }
        }
    }

    fun fetchRelatedImages(): List<Image> = transaction {
        ContentImageRelation
                .find { ContentImageRelations.content.eq(this@Content.id) }
                .map { it.image }
                .toList()
    }
}
