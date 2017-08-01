package com.kurume_nct.studybattle.viewModel

import android.content.Context
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
import java.io.File

/**
 * Created by hanah on 7/30/2017.
 */
class RegistrationViewModel(private val context: Context, private val callback : Callback) : BaseObservable() {

    var iconImageUri: Uri? = null

    companion object {
        @BindingAdapter("loadImage")
        @JvmStatic
        fun setIconImage(view: ImageView, uri: Uri?){
            if(uri == null){
                Glide.with(view).load(R.drawable.icon_gost).into(view)//loadの中にresourceを入れたらtestできる
            }else{
                Glide.with(view).load(File(uri.path)).into(view)//loadの中にresourceを入れたらtestできる
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
            callback.onLogin()
        }
    }

    fun onClickChengeIconImage(view: View){
        imageUri = iconImageUri
    }

    interface Callback{
        fun onLogin()
    }
}