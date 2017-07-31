package com.kurume_nct.studybattle.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.view.view.RegistrationActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivity(Intent(this, RegistrationActivity::class.java))
    }
}
