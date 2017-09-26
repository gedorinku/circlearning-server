package com.kurume_nct.studybattle.model

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import org.joda.time.Duration

/**
 * Created by gedorinku on 2017/09/23.
 */
data class LoginResult(val authenticationKey: String = "")

//TODO 名前とかも取得できるようにします、ごめんなさい
data class Group(val id: Int = 0)

data class Image(
        val id: Int = 0,
        val url: String = "",
        val fileName: String = "")

//TODO これやめたい
data class IDResponse(val id: Int)

data class Problem(
        val id: Int = 0,
        val title: String = "",
        val ownerId: Int = 0,
        val text: String = "",
        val imageIds: List<Int> = emptyList(),
        val createdAt: String = "",
        @SerializedName("startsAt") val rawStartsAt: String = "",
        val durationMillis: Long = 0L,
        val point: Int = 0
) {

    val startsAtTime: DateTime by lazy { DateTime.parse(rawStartsAt) }
    val duration: Duration by lazy { Duration.millis(durationMillis) }
}

data class Solution(
        val id: Int = 0,
        val text: String = "",
        val authorId: Int = 0,
        val problemId: Int = 0,
        val imageCount: Int = 0,
        val imageIds: List<Int> = emptyList()
)