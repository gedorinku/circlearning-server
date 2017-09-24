package com.kurume_nct.studybattle.client

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
}