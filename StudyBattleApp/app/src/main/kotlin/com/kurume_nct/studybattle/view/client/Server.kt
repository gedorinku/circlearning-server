package com.kurume_nct.studybattle.view.client

import android.widget.ImageView
import android.widget.TimePicker
import io.reactivex.Observer
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Url
import java.sql.Time

/**
 * Created by hanah on 7/31/2017.
 */
interface Server {

    @POST("hoge/{userName}/{displayName}/{iconImage}")
    fun postUserInfo(@Part("userName") userName: String, @Part("displayName") password: String, @Part("iconImage") iconImageUrl: ImageUrl): Observer<User>

    @POST("hoge/{title}/{contentId}/{lifeTime}")
    fun setProblem(@Part("title") title : String, @Part("contnetId") content : Int, @Part("lifeTime") lifeTime : Time) : Observer<Problem> //Time->LocalDateTime

    //いらない機能？
    @POST("hoge/{id}/{title}/{contentId}/{text}/{lifeTime}")
    fun refacterProblem(@Part("id") id : Int, @Part("title") title: String, @Part("contentId") content: Int, @Part("text") text : String, @Part("lifeTime")lifeTime: Time) : Observer<Problem> //Time->TimeSpan

    @GET("hoge/{id}")
    fun searchContent(@Part("id") id : Int) : Observer<Content>

    @GET("hoge/{id}")
    fun searchProblem(@Part("id") id: Int) : Observer<Problem>

    @GET("hoge/{id}")
    fun getProblemItem(@Part("id") id: Int) : Observer<List<ItemStack>> //問題についているアイテム一覧の取得

    @GET("hoge/{id}")
    fun getSolution(@Part("id") id: Int) : Observer<Solution>

    @GET("hoge/{id}") //Comment.id : ProblemIdと思ってる
    fun getComment(@Part("id") id: Int) : Observer<Comment>

    //AuthenticationResult??<-ask him.
}