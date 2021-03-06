package com.kurume_nct.studybattleserver.routing.solution

import com.google.gson.Gson
import com.kurume_nct.studybattleserver.SolutionGet
import com.kurume_nct.studybattleserver.dao.Comment
import com.kurume_nct.studybattleserver.dao.Solution
import com.kurume_nct.studybattleserver.routing.user.UserGetResponse
import com.kurume_nct.studybattleserver.verifyCredentials
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route

/**
 * Created by gedorinku on 2017/09/22.
 */
data class CommentGetResponse(val id: Int,
                              val text: String,
                              val authorId: Int,
                              val replyTo: Int,
                              val imageIds: List<Int>,
                              val createdAt: String) {

    companion object {

        fun fromComment(comment: Comment): CommentGetResponse = transaction {
            val images = comment
                    .body
                    .fetchRelatedImages()
                    .map { it.id.value }
            CommentGetResponse(comment.id.value,
                                                                                 comment.body.text,
                                                                                 comment.author.id.value,
                                                                                 comment.replyTo?.id?.value ?: 0,
                                                                                 images,
                                                                                 comment.createdAt.toString())
        }
    }
}

data class SolutionGetResponse(val id: Int,
                               val text: String,
                               val authorId: Int,
                               val author: UserGetResponse,
                               val problemId: Int,
                               val imageCount: Int,
                               val imageIds: List<Int>,
                               val judgingState: String,
                               val comments: List<CommentGetResponse>) {

    companion object {

        fun fromSolution(solution: Solution): SolutionGetResponse {
            val images = transaction {
                solution.content.fetchRelatedImages().map { it.id.value }
            }
            val comments = solution
                    .fetchComments()
                    .map { CommentGetResponse.fromComment(it) }
                    .sortedBy { it.id }

            return transaction {
                SolutionGetResponse(solution.id.value,
                                                                                      solution.content.text,
                                                                                      solution.author.id.value,
                                                                                      UserGetResponse.fromUser(solution.author),
                                                                                      solution.problem.id.value,
                                                                                      images.size,
                                                                                      images,
                                                                                      solution.judgingState.name,
                                                                                      comments)
            }
        }
    }
}

fun Route.getSolution() = post<SolutionGet> {
    val solutionGet = SolutionGet.create(call.receive(), it.id)
    if (solutionGet == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
    }
    val user = verifyCredentials(solutionGet.authenticationKey)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return@post
    }

    val solution = transaction {
        Solution.findById(solutionGet.id)
    }
    if (solution == null) {
        call.respond(HttpStatusCode.NotFound)
        return@post
    }

    val response = SolutionGetResponse.fromSolution(solution)
    call.respond(Gson().toJson(response))
}