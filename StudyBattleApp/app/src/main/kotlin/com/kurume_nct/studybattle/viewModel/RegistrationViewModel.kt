package com.kurume_nct.studybattle.viewModel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import java.io.File

/**
 * Created by hanah on 7/30/2017.
 */
class RegistrationViewModel(private val context: Context, private val callback : Callback) : BaseObservable() {

    val REQUEST_CODE = 114
    var iconImageUri: Uri? = null

    companion object {
        @BindingAdapter("loadImage")
        @JvmStatic
        fun setIconImage(view: ImageView, uri: Uri?){
            if(uri == null){
                Glide.with(view).load(R.drawable.icon_gost).into(view)//loadの中にresourceを入れたらtestできる
            }else{
                Glide.with(view).load(uri).into(view)
            }
        }
    }


    @Bindable
    var loginScreenName = R.string.account_registe //既存アカウントのLogin画面も作るかもしれないためBindingしとく

    @Bindable
    var userName = ""
        get
        set(value){
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

    @Bindable
    var imageButtonText = R.string.chenge_image_button_text

    fun onClickLoginButton(view: View){
        if(userName.isEmpty() || userPassword.isEmpty()){
            Toast.makeText(context,context.getString(R.string.errorLoginStatus),Toast.LENGTH_LONG).show()
        }else{
            //login処理
            Log.d("Tag"," displayName = " + displayName +" userName = "+ userName + " password = " + userPassword)
            ServerClient().onRegistration(displayName,userName,userPassword)
            callback.onLogin()
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        //if(requestCode != REQUEST_CODE || resultCode != Activity.RESULT_OK || data?.data == null)return
        if(data?.data == null)return
        //TODO : resize icon here
        iconImageUri = data.data
        imageUri = iconImageUri
    }

    fun onClickChengeIconImage(view: View){
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        callback.startActivityForResult(intent, REQUEST_CODE)
    }

    interface Callback{

        fun onLogin()

        fun startActivityForResult(intent : Intent, requestCode : Int)

    }
}