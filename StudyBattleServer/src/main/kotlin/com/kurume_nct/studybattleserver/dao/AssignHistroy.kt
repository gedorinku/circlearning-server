package com.kurume_nct.studybattleserver.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by gedorinku on 2017/09/28.
 */
object AssignHistories : IntIdTable() {

    val user = reference("user", Users)
    val problem = reference("problem", Problems)
}

class AssignHistroy(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AssignHistroy>(AssignHistories)

    var user by User referencedOn AssignHistories.user
    var problem by Problem referencedOn AssignHistories.problem
}