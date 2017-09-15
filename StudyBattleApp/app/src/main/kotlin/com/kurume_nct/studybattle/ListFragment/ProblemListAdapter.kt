package com.kurume_nct.studybattle.ListFragment

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.FragmentProblemBinding

class ProblemListAdapter(context: Context, val list : MutableList<Problem>)
    : RecyclerView.Adapter<ProblemListAdapter.ProblemListHolder>() {

    override fun onBindViewHolder(holder: ProblemListHolder, position: Int) {
        holder.binding.setVariable(BR.Item, list[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProblemListHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.fragment_problem, parent, false)
        val holder = ProblemListHolder(view)

        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
        }
        return holder
    }

    inner class ProblemListHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding : ViewDataBinding = DataBindingUtil.bind(view)
    }

    override fun getItemCount(): Int = list.size

    fun itemId(position: Int) = list[position].id

}
