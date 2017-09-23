package com.kurume_nct.studybattle.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kurume_nct.studybattle.ListFragment.MainListFragment
import com.kurume_nct.studybattle.Main2Activity
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.FragmentProbemMainBinding



class ProbemMainFragment : Fragment() {

    private lateinit var mListener : Main2Activity
    private lateinit var binding : FragmentProbemMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentProbemMainBinding.inflate(inflater,container,false)
        val fragment = MainListFragment().newInstance(0)
        val tranceration = mListener.supportFragmentManager.beginTransaction()
        tranceration.add(R.id.fragment_list,fragment)
        tranceration.commit()
        return binding.root
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as Main2Activity
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        fun newInstance(): ProbemMainFragment {
            val fragment = ProbemMainFragment()
            return fragment
        }
    }
}
