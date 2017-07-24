package com.kurume_nct.studybattle.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import com.kurume_nct.studybattle.R
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.linearLayoutCompat

/**
 * A login screen that offers login via email/password.
 */
class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_registrarion)
        RegistrationActivityUI().setContentView(this)
    }

}

class RegistrationActivityUI : AnkoComponent<RegistrationActivity>{
    override fun createView(ui: AnkoContext<RegistrationActivity>) = with(ui) {
        linearLayout {
            orientation = LinearLayout.VERTICAL

            val name = editText {
                hint = "UserName"
            }
            val password = editText{
                hint = "Password"
            }

            button("登録"){
                onClick {
                    if(name == null || password != null){
                        //toast("入力に不備があるようです.")
                    }
                    else{
                        //startActivity<>()
                    }
                }
            }.lparams{
                gravity = Gravity.CENTER
            }
        }
    }

}

