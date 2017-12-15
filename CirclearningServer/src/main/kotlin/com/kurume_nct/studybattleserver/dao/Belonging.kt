package com.kurume_nct.studybattleserver.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by gedorinku on 2017/08/03.
 */
object Belongings : IntIdTable() {

    val user = reference("user", Users)
    val group = reference("group", Groups)
}

class Belonging(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Belonging>(Belongings)

    var user by User referencedOn Belongings.user
    var group by Group referencedOn Belongings.group
}