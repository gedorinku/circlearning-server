package com.kurume_nct.studybattle.view

import android.content.Intent
import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import com.kurume_nct.studybattle.Main2Activity

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.ActivityLoginBinding
import com.kurume_nct.studybattle.viewModel.LoginViewModel

class LoginActivity : AppCompatActivity(), LoginViewModel.Callback {

    var used = false
    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login)
        binding.userLogin = LoginViewModel(this,this)
        if(used){
            toMain2Activity("つちのこ")
        }
    }

    fun toMain2Activity(name: String){
        val intent = Intent(this,Main2Activity::class.java)
        //add photo and userName
        intent.putExtra("userName",name)
        startActivity(intent)
        finish()
    }

    override fun onLogin(name: String, password: String) {
        toMain2Activity(name)
    }

    override fun toRegusterActivity() {
        val intent = Intent(this,RegistrationActivity::class.java)
        startActivity(intent)
    }
}
