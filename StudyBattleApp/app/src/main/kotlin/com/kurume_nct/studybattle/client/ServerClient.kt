package com.kurume_nct.studybattle.client

import android.net.Uri
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import java.lang.reflect.Type


/**
 * Created by hanah on 7/31/2017.
 */
class ServerClient(authenticationKey: String = "") {

    private val server: Server

    var authenticationKey: String = authenticationKey
        private set

    init {
        val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create()
        val retrofit = Retrofit.Builder()
                .baseUrl("http://studybattle.dip.jp:8080")
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        server = retrofit.create(Server::class.java)
    }

    fun register(displayName: String, userName: String, password: String): Observable<String>
            = server.register(displayName, userName, password)

    fun login(userName: String, password: String)
            = server
            .login(userName, password)
            .map {
                authenticationKey = it.authenticationKey
                it
            }!!

    fun createGroup(name: String) = server.createGroup(authenticationKey, name)

    fun joinGroup(id: Int) = server.joinGroup(authenticationKey, id)

    fun joinGroup(group: Group) = joinGroup(group.id)

    fun uploadImage(uri: Uri): Observable<Int> {
        throw NotImplementedError()
    }
}

private class StringConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        if (String::class.java == type) {
            return Converter<ResponseBody, String> { value -> value.string() }
        }
        return null
    }

    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>, methodAnnotations: Array<Annotation>, retrofit: Retrofit): Converter<*, RequestBody>? {
        if (String::class.java == type) {
            return Converter<String, RequestBody> { value -> RequestBody.create(MEDIA_TYPE, value) }
        }

        return null
    }

    companion object {
        private val MEDIA_TYPE = MediaType.parse("text/plain")

        fun create(): StringConverterFactory {
            return StringConverterFactory()
        }
    }

}