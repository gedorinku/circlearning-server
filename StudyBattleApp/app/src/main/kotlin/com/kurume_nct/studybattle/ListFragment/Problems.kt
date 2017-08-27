package com.kurume_nct.studybattle.ListFragment

import android.support.v7.widget.DialogTitle

/**
 * Created by hanah on 8/18/2017.
 */
data class Problem(val title: String = "問題", val detail: String = "詳細")

class Problems(private val problems: MutableList<Problem>){
    val count: Int = problems.count()
    fun problemAt(index: Int) : Problem = problems[index]
}