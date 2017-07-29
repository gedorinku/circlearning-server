package com.kurume_nct.studybattle.view

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Gravity
import android.widget.*
import com.kurume_nct.studybattle.R
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.linearLayoutCompat

/**
 * A login screen that offers login via email/password.
 */
class RegistrationActivity : AppCompatActivity() {

    var userName : String = ""
    var userPassword : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RegistrationActivityUI().setContentView(this)
    }

    fun setNamePassword(name : String, password : String){
        userName = name
        userPassword = password
        Log.d(userName,userPassword)
    }

    fun sendUserData(){
        if(userName.isNotEmpty() && userPassword.isNotEmpty()){

        }
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
                    if("${name.text}".isEmpty() || "${password.text}".isEmpty()){
                        toast("入力に不備があるようです.")
                    }
                    else{
                        ui.owner.setNamePassword("${name.text}","${password.text}")
                        toast("${password.text},${name.text}で登録完了しました")
                        startActivity<GroupDrawerActivity>()
                    }
                }
            }.lparams{
                gravity = Gravity.CENTER
            }
        }
    }

}

