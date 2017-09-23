package com.kurume_nct.studybattle.client

import com.kurume_nct.studybattle.`object`.*
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.http.*
import java.sql.Time

/**
 * Created by hanah on 7/31/2017.
 */
interface Server {

    @Multipart
    @POST("/register")
    fun register(@Part("displayName") displayName : String, @Part("userName") userName: String, @Part("password") password: String) : Observable<String>

    @Multipart
    @POST("/login")
    fun login(@Part("userName") userName: String, @Part("password") password: String) : Observable<String>

    @Multipart
    @POST("/image/upload")
    fun imageUpload(@Part("authenticationKey") password: RequestBody,@Part("image") image: RequestBody) : Single<Int>
    /*これ以降は企画書のapiのところに合わせて書きなおす*/

    @Multipart
    @POST("hoge/{title}/{contentId}/{lifeTime}")
    fun setProblem(@Path("title") title : String, @Path("contnetId") content : Int, @Path("lifeTime") lifeTime : Time) : Observable<Problems> //Time->LocalDateTime

    //いらない機能？
    @Multipart
    @POST("hoge/{id}/{title}/{contentId}/{text}/{lifeTime}")
    fun repairProblem(@Path("id") id : Int, @Path("title") title: String, @Path("contentId") content: Int, @Path("text") text : String, @Path("lifeTime")lifeTime: Time) : Observable<Problem> //Time->TimeSpan

    @Multipart
    @POST("hoge/{id}")
    fun searchContent(@Path("id") id : Int) : Observable<Content>

    @Multipart
    @POST("hoge/{id}")
    fun searchProblem(@Path("id") id: Int) : Observable<Problems>

    @Multipart
    @POST("hoge/{id}")
    fun getProblemItem(@Path("id") id: Int) : Observable<List<ItemStack>> //問題についているアイテム一覧の取得

    @Multipart
    @POST("hoge/{id}")
    fun getSolution(@Path("id") id: Int) : Observable<Solution>

    @Multipart
    @POST("hoge/{id}") //Comment.id : ProblemIdと思ってる
    fun getComment(@Path("id") id: Int) : Observable<Comment>

    //AuthenticationResult??
}