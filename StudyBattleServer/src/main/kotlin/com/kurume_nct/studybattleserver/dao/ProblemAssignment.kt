package com.kurume_nct.studybattleserver.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by gedorinku on 2017/10/10.
 */
object ProblemAssignments : IntIdTable() {

    val problem = reference("problem", Problems)
    val assignedAt = datetime("assigned_at")
    val withdrawAt = datetime("withdraw_at")
}

class ProblemAssignment(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ProblemAssignment>(ProblemAssignments)

    var problem by Problem referencedOn ProblemAssignments.problem
    var assignedAt by ProblemAssignments.assignedAt
    var closeAt by ProblemAssignments.withdrawAt
}