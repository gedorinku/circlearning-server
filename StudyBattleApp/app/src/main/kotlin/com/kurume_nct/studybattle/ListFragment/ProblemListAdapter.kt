package com.kurume_nct.studybattle.ListFragment

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.FragmentProblemBinding
import com.kurume_nct.studybattle.view.dummy.DummyContent.DummyItem

class ProblemListAdapter(context: Context, var problems: Problems) : BaseAdapter(){

    val infrater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding : FragmentProblemBinding?
        if(convertView == null){
            binding = DataBindingUtil.inflate(infrater, R.layout.fragment_problem, parent, false)
            binding?.root?.tag = binding
        }else{
            binding = convertView.tag as FragmentProblemBinding
        }
        binding?.item = getItem(position) as Problem
        return binding?.root!!
    }

    override fun getItem(position: Int): Any = problems.problemAt(position)

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = problems.count

}
