package com.kurume_nct.studybattle.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout

import com.kurume_nct.studybattle.R
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
   //     setContentView(R.layout.activity_main)
        MainActivityUI().setContentView(this)
    }
}

class MainActivityUI : AnkoComponent<MainActivity>{
    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        linearLayout {
            orientation = LinearLayout.VERTICAL

            button("Next"){
                onClick {
                    startActivity<RegistrationActivity>()
                }
            }.lparams {
                gravity = Gravity.CENTER
            }
        }
    }

}