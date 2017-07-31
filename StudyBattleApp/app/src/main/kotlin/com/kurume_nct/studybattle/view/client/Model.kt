package com.kurume_nct.studybattle.view.client

import android.graphics.Picture
import io.reactivex.internal.operators.maybe.MaybeDoAfterSuccess
import twitter4j.util.TimeSpanConverter
import java.net.URI
import java.sql.RowIdLifetime
import java.sql.Time
import java.sql.Timestamp

/**
 * Created by hanah on 7/31/2017.
 */
data class ProblemItem(val title : String = "", val timer : String = "00:00")

data class ProblemInfo(val picture: Picture, val sententce : String = "")

data class User(val id : Int, val userName : String = "", val displayName : String = "", val iconImageURL: String, val scores : Map<Int,Int>)

data class Group(val id : Int, val name: String, val ownerId : Int, val getOwner : User){
    fun getOwner(){}//User
}

data class Content(val id: Int, val text : String, val createdAt : Time)//Time->LocalDateTime

data class Comment(val id: Int, val content : Int, val replyToId : Int, val createdAt : Time){
    fun getReplyToComment() : Comment = this //?
}//Time->LocalDateTime

data class Problem(val id: Int, val title: String, val contentLd : Int, val createdAt : Time, val startedAt : Time, val lifetime: Int){
    fun getContent()/*: Content*/{}//return Content
} //Time->LocalDateTime, lifetime->?

data class ImageUrl(val imageUrl: String)

data class ItemStack(val itemLd : Int, val count : Int)

data class Solution(val id: Int, val userId : Int, val cintentId : Int, val createdAt: Time){
    fun getUser(){}//User
}//Time->LocalDateTime

data class AuthenticationResult(val message : String, val authenticationKey: String, val succeeded : Boolean)


