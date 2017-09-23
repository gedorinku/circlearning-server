package com.kurume_nct.studybattle.client

import org.threeten.bp.LocalDateTime
import java.sql.Time

/**
 * Created by hanah on 7/31/2017.
 */
data class User(val id : Int, val userName : String = "", val displayName : String = "", val iconImageURL: String, val scores : Map<Int,Int>)

data class Group(val id : Int, val name: String, val ownerId : Int, val getOwner : User){
    var owner : User? = null
}

data class Content(val id: Int, val text : String, val createdAt : LocalDateTime)

data class Comment(val id: Int, val content : Int, val replyToId : Int, val createdAt : LocalDateTime){
    var replyToComment : Comment? = null
}

data class Problem(val id: Int, val title: String, val contentLd : Int, val createdAt : Time, val startedAt : LocalDateTime, val lifetime: Long){
    var content : Content? = null
} //lifetime->Long is ok?

data class ImageUrl(val imageUrl: String)

data class ItemStack(val itemLd : Int, val count : Int)

data class Solution(val id: Int, val userId : Int, val contentId: Int, val createdAt: LocalDateTime){
    var user : User? = null
}

data class AuthenticationResult(val message : String, val authenticationKey: String, val succeeded : Boolean)


