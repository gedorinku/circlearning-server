package com.kurume_nct.studybattle.client

import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kurume_nct.studybattle.viewModel.RegistrationViewModel
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.File
import java.io.IOException
import java.lang.reflect.Type


/**
 * Created by hanah on 7/31/2017.
 */
class ServerClient{

    var gson : Gson
    var retrofit : Retrofit
    var server : Server

    init {
        gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
                .create()
        retrofit = Retrofit.Builder()
                .baseUrl("http://studybattle.dip.jp:8080")
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        server = retrofit.create(Server::class.java)
    }

    fun onRegistration(displayName : String, userName: String, password: String)
        = server.register(displayName, userName, password)

    fun onUploadImage(authorityKey : String, url: Uri): Single<Int> {
        val file = File(url.path)
        val keyRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), authorityKey)
        val imageRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        return server.imageUpload(keyRequestBody,imageRequestBody)
    }

}

class StringConverterFactory : Converter.Factory() {

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