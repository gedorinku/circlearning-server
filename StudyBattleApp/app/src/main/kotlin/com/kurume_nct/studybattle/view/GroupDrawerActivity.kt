package com.kurume_nct.studybattle.view

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import com.kurume_nct.studybattle.R
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.support.v4.drawerLayout

/**
 * Created by hanah on 7/24/2017.
 */
class GroupDrawerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GroupDrawerActivityUI().setContentView(this)
    }
}

class GroupDrawerActivityUI : AnkoComponent<GroupDrawerActivity>{
    override fun createView(ui: AnkoContext<GroupDrawerActivity>) = with(ui) {
        drawerLayout {
            setStatusBarBackgroundColor(R.color.colorAccent)
            frameLayout{
            }
            toolbar {
            }
            listView {
            }
        }
    }

}