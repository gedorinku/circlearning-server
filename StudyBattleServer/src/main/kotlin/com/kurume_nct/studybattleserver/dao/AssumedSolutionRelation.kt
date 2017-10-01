package com.kurume_nct.studybattleserver.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by gedorinku on 2017/10/01.
 */
object AssumedSolutionRelations : IntIdTable() {

    val problem = reference("problem", Problems).uniqueIndex()
    val assumedSolution = reference("assumed_solution", Solutions).uniqueIndex()
}

class AssumedSolutionRelation(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AssumedSolutionRelation>(AssumedSolutionRelations)

    var problem by Problem referencedOn AssumedSolutionRelations.problem
    var assumedSolution by Solution referencedOn AssumedSolutionRelations.assumedSolution
}