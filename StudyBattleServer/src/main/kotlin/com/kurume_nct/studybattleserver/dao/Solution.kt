package com.kurume_nct.studybattleserver.dao

import com.kurume_nct.studybattleserver.SolutionCreate
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.joda.time.DateTime

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

fun Solution.Companion.fromRequest(request: SolutionCreate, author: User)
        : Pair<Solution?, HttpStatusCode> {

    val images = request.imageIds.map {
        transaction {
            Image.findById(it)
        }
    }
    if (images.contains(null)) {
        return Pair(null, HttpStatusCode.BadRequest)
    }

    val problem = transaction {
        Problem.findById(request.problemId)
    } ?: return Pair(null, HttpStatusCode.BadRequest)

    val content = transaction {
        Content.new {
            text = request.text
        }
    }

    content.relateImages(*images.filterNotNull().toTypedArray())

    val solution = transaction {
        Solution.new {
            this.author = author
            this.content = content
            this.createdAt = DateTime.now()
            this.problem = problem
        }
    }

    return Pair(solution, HttpStatusCode.OK)
}