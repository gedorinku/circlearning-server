package com.kurume_nct.studybattle.client

import android.view.Display
import io.reactivex.Observable
import io.reactivex.Observer
import retrofit2.http.*
import java.sql.Time

/**
 * Created by hanah on 7/31/2017.
 */
interface Server {

    @Multipart
    @POST("/register")
    fun register(@Part("displayName") displayName : String, @Part("userName") userName: String, @Part("password") password: String) : Observable<String>

    @POST("/login")
    fun login(@Part("userName") userName: String, @Part("password") password: String) : Observable<String>

    @POST("hoge/{title}/{contentId}/{lifeTime}")
    fun setProblem(@Path("title") title : String, @Path("contnetId") content : Int, @Path("lifeTime") lifeTime : Time) : Observable<Problem> //Time->LocalDateTime

    //いらない機能？
    @POST("hoge/{id}/{title}/{contentId}/{text}/{lifeTime}")
    fun refactorProblem(@Path("id") id : Int, @Path("title") title: String, @Path("contentId") content: Int, @Path("text") text : String, @Path("lifeTime")lifeTime: Time) : Observable<Problem> //Time->TimeSpan

    @GET("hoge/{id}")
    fun searchContent(@Path("id") id : Int) : Observable<Content>

    @GET("hoge/{id}")
    fun searchProblem(@Path("id") id: Int) : Observable<Problem>

    @GET("hoge/{id}")
    fun getProblemItem(@Path("id") id: Int) : Observable<List<ItemStack>> //問題についているアイテム一覧の取得

    @GET("hoge/{id}")
    fun getSolution(@Path("id") id: Int) : Observable<Solution>

    @GET("hoge/{id}") //Comment.id : ProblemIdと思ってる
    fun getComment(@Path("id") id: Int) : Observable<Comment>

    //AuthenticationResult??
}