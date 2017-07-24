package com.kurume_nct.studybattle.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.text.InputType
import android.text.method.PasswordTransformationMethod
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
            val password = editText {
                hint = "Password"
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            button("登録"){
                onClick {
                    if(name.text.isEmpty() || password.text.isEmpty()){
                        toast("入力に不備があるようです.")
                    }
                    else{
                        toast("登録完了しました")
                        startActivity<GroupDrawerActivity>()
                    }
                }
            }.lparams{
                gravity = Gravity.CENTER
            }
        }
    }

}

