package com.kurume_nct.studybattleserver.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by gedorinku on 2017/07/30.
 */
object Groups : IntIdTable() {

    val name = varchar("name", 22)
    val owner = reference("owner", Users)
}

class Group(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Group>(Groups)

    var name by Groups.name
    var owner by User referencedOn Groups.owner
}
