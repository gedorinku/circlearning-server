package com.kurume_nct.studybattle.client

/**
 * Created by gedorinku on 2017/09/23.
 */
data class LoginResult(val authenticationKey: String)

//TODO 名前とかも取得できるようにします、ごめんなさい
data class Group(val id: Int)

data class Image(var id: Int, var url: String, var fileName: String)
