package com.kurume_nct.studybattle.ListFragment

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.support.v4.app.ListFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListAdapter
import android.widget.Toast
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.FragmentProblemListBinding

class MainListFragment : Fragment() {

    lateinit var binding : FragmentProblemListBinding
    var tabId : Int = 0
    lateinit var problemList : MutableList<Problem>
    //lateinit var problems : Problems

    lateinit var listAdapter : ProblemListAdapter
    fun newInstance(id: Int) : MainListFragment{
        val fragment = MainListFragment()
        val args = Bundle()
        args.putInt("id",id)
        fragment.arguments = args
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_problem_list,container,false)
        binding = FragmentProblemListBinding.inflate(inflater, container, false)
        problemList = mutableListOf(Problem("hoge","hoge"))
        listAdapter = ProblemListAdapter(context, problemList)
        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(binding.list.context)
        setList()
        return binding.root
    }

    fun setList(){
        listAdapter.notifyItemRangeRemoved(0,problemList.size)
        problemList.clear()
        //readProblemList from serve
        (0..11).forEach { problemList.add(Problem("hoge","gedorin",it))}
        listAdapter.notifyItemRangeInserted(0,problemList.size)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    fun changeList(id : Int){
        listAdapter.notifyItemRangeRemoved(0,problemList.size)
        //problemList.clear()
        when(id){
            0->{
                //call server
                problemList.add(Problem("0",":;(∩´﹏`∩);:"))
            }
            1->{
                problemList.add(Problem("1",":;(∩´﹏`∩);:"))
            }
            2->{
                problemList.add(Problem("2",":;(∩´﹏`∩);:"))
            }
            3->{
                problemList.add(Problem("3",":;(∩´﹏`∩);:"))
            }
        }
        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(binding.list.context)
        listAdapter.notifyItemRangeInserted(0,problemList.size)
        Log.d(problemList.size.toString(), tabId.toString())
    }

    fun finish(){
        fragmentManager.beginTransaction().remove(this).commit()
    }

}
