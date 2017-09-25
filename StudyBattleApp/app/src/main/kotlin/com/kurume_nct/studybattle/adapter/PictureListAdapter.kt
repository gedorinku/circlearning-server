package com.kurume_nct.studybattle.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.model.Person_Group

/**
 * Created by hanah on 9/22/2017.
 */
class PictureListAdapter(context: Context, val list: MutableList<Person_Group>, val callbacks : (Int) -> Unit)
    : RecyclerView.Adapter<PictureListAdapter.GroupListHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupListHolder{
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.group_object,parent,false)
        val holder = GroupListHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            callbacks(position)
        }
        return holder
    }

    override fun onBindViewHolder(holder: GroupListHolder, position: Int) {
        holder.binding.setVariable(BR.Group, list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class GroupListHolder(view : View) : RecyclerView.ViewHolder(view){
            val binding : ViewDataBinding = DataBindingUtil.bind(view)
        }
}