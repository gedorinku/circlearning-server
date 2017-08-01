package com.kurume_nct.studybattle.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.ActivityRegistrationBinding
import com.kurume_nct.studybattle.viewModel.RegistrationViewModel

/**
 * A login screen that offers login via email/password.
 */
class RegistrationActivity : AppCompatActivity() , RegistrationViewModel.Callback {

    lateinit var binding : ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        binding.userEntity = RegistrationViewModel(this, this)

    }

    override fun onLogin() {
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.userEntity.onActivityResult(resultCode, resultCode, data)
    }

}
