package com.kurume_nct.studybattle

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log

import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TabHost
import android.widget.Toast
import com.kurume_nct.studybattle.ListFragment.MainListFragment
import com.kurume_nct.studybattle.view.RegistrationActivity
import android.widget.TabHost.TabContentFactory
import com.kurume_nct.studybattle.adapter.MainPagerAdapter


class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var tabHost: TabHost
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        val viewPaper : ViewPager = findViewById(R.id.pager) as ViewPager
        val tabLayout : TabLayout = findViewById(R.id.tabs) as TabLayout

        var mAdapter = MainPagerAdapter(supportFragmentManager)
        viewPaper.adapter = mAdapter
        viewPaper.offscreenPageLimit = mAdapter.count
        tabLayout.setupWithViewPager(viewPaper)

    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun onClickRankingButton(view: View) {
        Toast.makeText(this, "Ranking...", Toast.LENGTH_SHORT).show()
    }

    fun onClickItemButton(view: View) {
        Toast.makeText(this, "Items...", Toast.LENGTH_SHORT).show()
    }

    fun onClickNewProblemButton(view: View) {

    }

    override fun onStop() {
        super.onStop()
    }

}


