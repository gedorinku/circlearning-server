package com.kurume_nct.studybattle.viewModel

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.file.FileResource
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.BufferedInputStream
import java.io.File
import java.io.FileReader
import java.util.concurrent.CountDownLatch

/**
 * Created by hanah on 7/30/2017.
 */
class RegistrationViewModel(private val context: Context, private val callback: Callback) : BaseObservable() {

    val REQUEST_CODE = 114
    var iconImageUri: Uri
    var iconId: Int
    var imageBitmap: Bitmap

    init {
        iconImageUri = convertUrlFromDrawableResId(context, R.drawable.icon_gost)!!
        imageBitmap = ImageCustom().onUriToBitmap(context, iconImageUri)
        iconId = 0
    }

    companion object {
        @BindingAdapter("loadImage")
        @JvmStatic
        fun setIconImage(view: ImageView, uri: Uri?) {

            if (uri == null) {
                Glide.with(view).load(R.drawable.icon_gost).into(view)//loadの中にresourceを入れたらtestできる
            } else {
                Glide.with(view).load(uri).into(view)
            }
        }
    }

    @Bindable
    var userName = ""
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.userName)
        }

    @Bindable
    var userPassword = ""
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.userPassword)
        }

    @Bindable
    var displayName = ""
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.displayName)
        }

    @Bindable
    var loginButtonText = "登録"

    @Bindable
    var imageUri = iconImageUri
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.imageUri)
        }

    fun onClicktoLoginButton(view: View) {
        callback.toLoginActivity()
    }

    fun onClickLoginButton(view: View) {
        if (userName.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.errorLoginStatus), Toast.LENGTH_LONG).show()
        } else if (!userName.matches(Regex("[^a-zA-z0-9]"))) {
            Toast.makeText(context, "ユーザー名に不適切な文字列が含まれています.", Toast.LENGTH_LONG).show()
            userName = ""
        } else {
            //sever処理
            val countDown: CountDownLatch = CountDownLatch(2)
            var countSuccess = 0
            ServerClient().register(displayName, userName, userPassword)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                    }, {
                        Toast.makeText(context, context.getString(R.string.usedUserNameAlart), Toast.LENGTH_LONG).show()
                        countDown.countDown()
                    }, {
                        countSuccess++
                        countDown.countDown()
                    })
            ServerClient().uploadImage(imageUri, context)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        iconId = it.id
                        countDown.countDown()
                        countSuccess++
                    }, {
                        Toast.makeText(context, context.getString(R.string.fail_image_upload), Toast.LENGTH_LONG)
                        countDown.countDown()
                    })
            countDown.await()
            if (countSuccess == 2) {
                callback.onLogin()
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //if(requestCode != REQUEST_CODE || resultCode != Activity.RESULT_OK || data?.data == null)return
        if (data?.data == null) return
        //TODO : resize icon here
        iconImageUri = data.data
        imageUri = iconImageUri
        imageBitmap = ImageCustom().onUriToBitmap(context,iconImageUri)
    }

    fun onClickChengeIconImage(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        callback.startActivityForResult(intent, REQUEST_CODE)
    }

    fun changeImageSize(uri: Uri): Bitmap {
        val stream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(BufferedInputStream(stream))
        return Bitmap.createScaledBitmap(bitmap, 100, 100, false)
    }

    fun convertUrlFromDrawableResId(context: Context, drawableResId: Int): Uri? {
        val sb = StringBuilder()
        sb.append(ContentResolver.SCHEME_ANDROID_RESOURCE)
        sb.append("://")
        sb.append(context.resources.getResourcePackageName(drawableResId))
        sb.append("/")
        sb.append(context.resources.getResourceTypeName(drawableResId))
        sb.append("/")
        sb.append(context.resources.getResourceEntryName(drawableResId))
        return Uri.parse(sb.toString())
    }

    interface Callback {

        fun toLoginActivity()

        fun onLogin()

        fun startActivityForResult(intent: Intent, requestCode: Int)

    }

}