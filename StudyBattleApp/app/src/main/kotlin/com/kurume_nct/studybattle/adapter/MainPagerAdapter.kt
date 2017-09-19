package com.kurume_nct.studybattle.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.kurume_nct.studybattle.ListFragment.MainListFragment

/**
 * Created by hanah on 9/18/2017.
 */
class MainPagerAdapter(mf: FragmentManager) : FragmentPagerAdapter(mf){

    private val mFragment = ArrayList<Fragment>()

    init {
        mFragment.add(MainListFragment())
        mFragment.add(MainListFragment())
        mFragment.add(MainListFragment())
        mFragment.add(MainListFragment())
    }

    override fun getItem(position: Int): Fragment {
        //transration de layout to connect do.
        val fragment = mFragment[position]
        return fragment
    }

    fun addFragment(fragment: Fragment){
        mFragment.add(fragment)
    }

    override fun getCount(): Int = mFragment.size

}