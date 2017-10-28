package com.kurume_nct.studybattleserver.dao

import com.kurume_nct.studybattleserver.SolutionCreate
import com.kurume_nct.studybattleserver.item.ItemRegistry
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
enum class JudgingState {
    Solved,
    WaitingForJudge,
    Accepted,
    WrongAnswer
}

object Solutions : IntIdTable() {

    val author = reference("author", Users)
    val content = reference("content", Contents)
    val problem = reference("problem", Problems)
    val createdAt = datetime("created_at")
    val judgingState = enumeration("judging_state", JudgingState::class.java).default(JudgingState.Solved)
    val attachedItemId = integer("attached_item_id")
}

class Solution(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Solution>(Solutions)

    var author by User referencedOn Solutions.author
    var content by Content referencedOn Solutions.content
    var problem by Problem referencedOn Solutions.problem
    var createdAt by Solutions.createdAt
    var judgingState by Solutions.judgingState
    var attachedItemId by Solutions.attachedItemId

    val isJudged
        get() = transaction {
            author.id == problem.owner.id ||
                    judgingState == JudgingState.Accepted ||
                    judgingState == JudgingState.WrongAnswer
        }

    fun fetchComments() = transaction {
        Comment.find { Comments.solution.eq(this@Solution.id) }
                .toList()
    }
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

    if (request.text.isEmpty() && images.isEmpty()) {
        return Pair(null, HttpStatusCode.BadRequest)
    }

    ItemRegistry.registeredItems[request.attachedItemId]
            ?: return Pair(null, HttpStatusCode(HttpStatusCode.BadRequest.value, "item id is out of range"))

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
            this.attachedItemId = request.attachedItemId
        }
    }

    return Pair(solution, HttpStatusCode.OK)
}