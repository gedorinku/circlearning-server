package com.kurume_nct.studybattle

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jakewharton.threetenabp.AndroidThreeTen

import com.kurume_nct.studybattle.view.RegistrationActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivity(Intent(this, RegistrationActivity::class.java))

        AndroidThreeTen.init(this)

    }
}
