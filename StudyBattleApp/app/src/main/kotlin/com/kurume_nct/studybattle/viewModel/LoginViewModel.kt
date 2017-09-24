package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.view.View
import android.widget.Toast
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R

/**
 * Created by hanah on 8/11/2017.
 */
class LoginViewModel(private val context: Context, private val callback : Callback) : BaseObservable(){

    @Bindable
    var name = ""
    get
    set(value) {
        field = value
        notifyPropertyChanged(BR.name)
    }

    @Bindable
    var password = ""
    get
    set(value) {
        field = value
        notifyPropertyChanged(BR.password)
    }

    fun onClickLogin(view: View){
        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.errorLoginStatus), Toast.LENGTH_LONG).show()
        }else{
            callback.onLogin(name,password)
        }
    }

    fun onClickToRegister(view : View){
        callback.toRegusterActivity()
    }

    interface Callback{
        fun onLogin(name : String, password : String)
        fun toRegusterActivity()
    }
}