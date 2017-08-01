package com.kurume_nct.studybattle.client

import android.view.Display
import io.reactivex.Observer
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import java.sql.Time

/**
 * Created by hanah on 7/31/2017.
 */
interface Server {

    @POST("/register/{displayName}/{userName}/{password}")
    fun register(@Part("displayName") displayName : String, @Part("userName") userName: String, @Part("password") password: String) : Observer<Unit>

    @POST("/login/{userName}/{password}")
    fun login(@Part("userName") userName: String, @Part("password") password: String) : Observer<Unit>

    @POST("hoge/{title}/{contentId}/{lifeTime}")
    fun setProblem(@Part("title") title : String, @Part("contnetId") content : Int, @Part("lifeTime") lifeTime : Time) : Observer<Problem> //Time->LocalDateTime

    //いらない機能？
    @POST("hoge/{id}/{title}/{contentId}/{text}/{lifeTime}")
    fun refactorProblem(@Part("id") id : Int, @Part("title") title: String, @Part("contentId") content: Int, @Part("text") text : String, @Part("lifeTime")lifeTime: Time) : Observer<Problem> //Time->TimeSpan

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