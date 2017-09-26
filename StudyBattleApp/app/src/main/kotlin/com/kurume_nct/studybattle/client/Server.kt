package com.kurume_nct.studybattle.client

import com.kurume_nct.studybattle.model.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Created by hanah on 7/31/2017.
 */
interface Server {

    @FormUrlEncoded
    @POST("/register")
    fun register(
            @Field("displayName") displayName: String,
            @Field("userName") userName: String,
            @Field("password") password: String
    ): Observable<Unit>

    @FormUrlEncoded
    @POST("/login")
    fun login(
            @Field("userName") userName: String,
            @Field("password") password: String
    ): Observable<LoginResult>

    @FormUrlEncoded
    @POST("/group/new")
    fun createGroup(
            @Field("authenticationKey") authenticationKey: String,
            @Field("name") name: String
    ): Observable<Group>

    @FormUrlEncoded
    @POST("/group/join")
    fun joinGroup(
            @Field("authenticationKey") authenticationKey: String,
            @Field("groupId") groupId: Int
    ): Observable<Unit>

    @Multipart
    @POST("/image/upload")
    fun uploadImage(
            @Part() authenticationKey: MultipartBody.Part,
            @Part() image: MultipartBody.Part
    ): Observable<Image>

    @FormUrlEncoded
    @POST("/problem/create")
    fun createProblem(
            @Field("authenticationKey") authenticationKey: String,
            @Field("title") title: String,
            @Field("text") text: String,
            @Field("imageIds[]") imageIds: IntArray,
            @Field("startsAt") startsAt: String,
            @Field("durationMillis") durationMillis: Long
    ): Observable<IDResponse>

    @FormUrlEncoded
    @POST("/problem/{id}")
    fun getProblem(
            @Field("authenticationKey") authenticationKey: String,
            @Path("id") id: Int
    ): Observable<Problem>

    @FormUrlEncoded
    @POST("/solution/create")
    fun createSolution(
            @Field("authenticationKey") authenticationKey: String,
            @Field("text") text: String,
            @Field("problemId") problemId: Int,
            @Field("imageIds[]") imageIds: IntArray
    ): Observable<IDResponse>

    @FormUrlEncoded
    @POST("/solution/{id}")
    fun getSolution(
            @Field("authenticationKey") authenticationKey: String,
            @Path("id") id: Int
    ): Observable<Solution>
}