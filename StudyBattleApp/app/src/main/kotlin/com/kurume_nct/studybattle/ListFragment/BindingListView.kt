package com.kurume_nct.studybattle.ListFragment

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.ListView

/**
 * Created by hanah on 8/18/2017.
 */
class BindingListView(context: Context, attrs: AttributeSet): ListView(context) {

    private lateinit var adapter: ArrayAdapter<Problem>

    fun setList(list: MutableList<Problem>){
        if(adapter == null){
            adapter = ArrayAdapter(
                    context, android.R.layout.simple_list_item_1, list)
            adapter = adapter
        }
        adapter.notifyDataSetChanged()
    }
}