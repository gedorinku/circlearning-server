package com.kurume_nct.studybattle

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.jakewharton.threetenabp.AndroidThreeTen

import com.kurume_nct.studybattle.view.RegistrationActivity

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivity(Intent(this, RegistrationActivity::class.java))

        AndroidThreeTen.init(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer : DrawerLayout = findViewById(R.id.drawerLayout) as DrawerLayout
        val toggle : ActionBarDrawerToggle = ActionBarDrawerToggle(
                this,drawer,toolbar,
                R.string.password_toggle_content_description,
                R.string.password_toggle_content_description
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView : NavigationView = findViewById(R.id.navigationView) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawer : DrawerLayout = findViewById(R.id.drawerLayout) as DrawerLayout
        //drawer.closeDrawer(GravityCompat.START) //bug
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
}
