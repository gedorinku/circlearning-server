package com.kurume_nct.studybattle.ListFragment

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.support.v4.app.ListFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.FragmentProblemListBinding

class MainListFragment : Fragment() {

    lateinit var binding : FragmentProblemListBinding
    var tabId : Int = 0
    lateinit var problemList : MutableList<Problem>
    lateinit var problems : Problems
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
        tabId = arguments.getInt("id", 3)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_problem_list,container,false)
        //binding = FragmentProblemListBinding.inflate(inflater, container, false)
        problemList = mutableListOf(Problem())
        when(tabId){
            0->{problemList.add(Problem("0",":;(∩´﹏`∩);:"))}
            1->{problemList.add(Problem("1",":;(∩´﹏`∩);:"))}
            2->{problemList.add(Problem("2",":;(∩´﹏`∩);:"))}
            3->{problemList.add(Problem("3",":;(∩´﹏`∩);:"))}
        }
        problems = Problems(problemList)
        listAdapter = ProblemListAdapter(context, problems)
        binding.list.adapter = listAdapter
        binding.setOnItemClick { parent, view, position, id ->
            //   Toast.makeText(context,"にゃーん",Toast.LENGTH_SHORT).show()
            Log.d(id.toString(),"hoge")
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }
}
