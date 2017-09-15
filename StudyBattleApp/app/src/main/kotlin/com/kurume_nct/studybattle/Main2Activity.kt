package com.kurume_nct.studybattle

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
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


class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, TabHost.OnTabChangeListener {

    private lateinit var tabHost: TabHost
    var fragment_exist = false
    var fragment_id = "Problem"
    lateinit var fragment: MainListFragment
    lateinit var transaction: FragmentTransaction
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


        //val mf = MainListFragment()
        if (!fragment_exist) {
         //   onFragmentCreate()
        }
        //TabSetup() //bug here

        /**
         * これを参照しましょう。
         * http://y-anz-m.blogspot.jp/2012/04/android-fragment-fragmenttransaction.html
         */

        if (true) {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

    }

    fun TabSetup() {
        tabHost = findViewById(R.id.tabs) as TabHost
        tabHost.setup()
        val tab1: TabHost.TabSpec = tabHost.newTabSpec("Problem")
        tab1.setIndicator("PROBLEM")
        tab1.setContent(DummyTabFactory(this))
        tabHost.addTab(tab1)
        val tab2: TabHost.TabSpec = tabHost.newTabSpec("Answer")
        tab1.setIndicator("ANSWER")
        tab1.setContent(DummyTabFactory(this))
        tabHost.addTab(tab2)
        val tab3: TabHost.TabSpec = tabHost.newTabSpec("MadeProblem")
        tab1.setIndicator("MADEPROBLEM")
        tab1.setContent(DummyTabFactory(this))
        tabHost.addTab(tab3)
        val tab4: TabHost.TabSpec = tabHost.newTabSpec("Submitted")
        tab1.setIndicator("SUBMITTED")
        tab1.setContent(DummyTabFactory(this))
        tabHost.addTab(tab4)

        tabHost.setOnTabChangedListener(this)
        onTabChanged("Problem")
    }

    override fun onTabChanged(tabId: String?) {
        if(fragment_id != tabId){
            if(tabId.equals("Problem")) {
                transaction.replace(R.id.container, fragment)
                transaction.commit()
            }else if(tabId.equals("Answer")){
                fragment.changeList(2)
            }else if(tabId.equals("ModeProblem")){
                fragment.changeList(3)
            }else if(tabId.equals("Submitted")){
                fragment.changeList(4)
            }
            fragment_id = tabId ?: "Problem"
        }
        Log.d("hoge", tabId.toString())
    }

    fun onFragmentCreate() {
        fragment = MainListFragment().newInstance(0)
        transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.container, fragment)
        transaction.commit()
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
        if (fragment_exist) {
            fragment.finish()
        }

    }

    private class DummyTabFactory internal constructor(
            /* Context */
            private val mContext: Context) : TabContentFactory {

        override fun createTabContent(tag: String): View {
            return View(mContext)
        }
    }

}
