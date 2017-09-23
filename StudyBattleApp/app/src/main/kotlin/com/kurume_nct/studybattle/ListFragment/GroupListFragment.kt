package com.kurume_nct.studybattle.ListFragment

import android.content.Context
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kurume_nct.studybattle.`object`.Person_Group
import com.kurume_nct.studybattle.adapter.PictureListAdapter
import com.kurume_nct.studybattle.databinding.GroupListBinding



class GroupListFragment : Fragment() {

    private lateinit var binding : GroupListBinding
    lateinit var grouplist : MutableList<Person_Group>
    lateinit var listAdapter : PictureListAdapter
    private var activityId = 0


    fun newInstance(id : Int): GroupListFragment {
        val fragment = GroupListFragment()
        val args = Bundle()
        args.putInt("actId",id)
        fragment.arguments = args
        return fragment
    }

    lateinit var mContext : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activityId = arguments.getInt("actId")
        binding = GroupListBinding.inflate(inflater,container,false)
        grouplist = mutableListOf(Person_Group())
        grouplist.add(Person_Group("pro"))
        listAdapter = PictureListAdapter(context,grouplist){
            //item
        }
        binding.groupList2.adapter = listAdapter
        binding.groupList2.layoutManager = LinearLayoutManager(binding.groupList2.context)
        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDetach() {
        super.onDetach()
    }
}// Required empty public constructor
