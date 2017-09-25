package com.kurume_nct.studybattle.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import org.joda.time.Duration

/**
 * Created by gedorinku on 2017/09/23.
 */
data class LoginResult(@Expose val authenticationKey: String = "")

//TODO 名前とかも取得できるようにします、ごめんなさい
data class Group(@Expose val id: Int = 0)

data class Image(
        @Expose val id: Int = 0,
        @Expose val url: String = "",
        @Expose val fileName: String = "")

//TODO これやめたい
data class IDResponse(@Expose val id: Int)

data class Problem(
        @Expose val id: Int = 0,
        @Expose val title: String = "",
        @Expose val ownerId: Int = 0,
        @Expose val text: String = "",
        @Expose val imageIds: List<Int> = emptyList(),
        @Expose val createdAt: String = "",
        @Expose @SerializedName("startsAt") val rawStartsAt: String = "",
        @Expose val durationMillis: Long = 0L,
        @Expose val point: Int = 0
) {

    val startsAtTime: DateTime by lazy { DateTime.parse(rawStartsAt) }
    val duration: Duration by lazy { Duration.millis(durationMillis) }
}