package com.kurume_nct.studybattle.client

import android.content.Context
import android.net.Uri
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.kurume_nct.studybattle.model.Group
import com.kurume_nct.studybattle.model.Image
import com.kurume_nct.studybattle.model.Problem
import com.kurume_nct.studybattle.model.Solution
import io.reactivex.Observable
import okhttp3.*
import org.joda.time.DateTime
import org.joda.time.Duration
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
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

    fun register(displayName: String, userName: String, password: String): Observable<Unit>
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

    fun uploadImage(inputStream: InputStream, type: String): Observable<Image> {
        val bytes = inputStream.use {
            val buffer = mutableListOf<Byte>()
            while (true) {
                val temp = it.read()
                if (temp == -1) {
                    break
                }
                buffer.add(temp.toByte())
            }
            buffer.toByteArray()
        }

        val authenticationKeyPart = MultipartBody.Part.create(
                Headers.of(mapOf("Content-Disposition" to "form-data; name=\"authenticationKey\"")),
                RequestBody.create(
                        MediaType.parse(type),
                        authenticationKey
                )
        )
        val fileExtension = type.substring("image/".length)
        val imagePart = MultipartBody.Part.create(
                Headers.of(mapOf("Content-Disposition" to "form-data; name=\"image\"; filename=\"hoge.$fileExtension\"")),
                RequestBody.create(
                        MediaType.parse(type),
                        bytes
                )
        )

        return server.uploadImage(authenticationKeyPart, imagePart)
    }

    fun uploadImage(uri: Uri, context: Context): Observable<Image> {
        val contentResolver = context.contentResolver
        return uploadImage(contentResolver.openInputStream(uri), contentResolver.getType(uri))
    }

    fun createProblem(
            title: String, text: String, imageIds: List<Int>, startsAt: DateTime, duration: Duration
    ): Observable<Problem> =
            server
                    .createProblem(
                            authenticationKey,
                            title,
                            text,
                            imageIds.toIntArray(),
                            startsAt.toString(),
                            duration.millis
                    )
                    .flatMap {
                        getProblem(it.id)
                    }

    fun getProblem(id: Int): Observable<Problem> = server.getProblem(authenticationKey, id)

    fun createSolution(
            text: String, problem: Problem, imageIds: List<Int>
    ): Observable<Solution> = createSolution(text, problem.id, imageIds)

    fun createSolution(
            text: String, problemId: Int, imageIds: List<Int>
    ): Observable<Solution> =
            server
                    .createSolution(authenticationKey, text, problemId, imageIds.toIntArray())
                    .flatMap {
                        server.getSolution(authenticationKey, it.id)
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