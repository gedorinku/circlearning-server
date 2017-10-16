package com.kurume_nct.studybattleserver.deamon

import org.joda.time.DateTime

/**
 * Created by gedorinku on 2017/10/16.
 */
object ProblemDurationObserver : Daemon {

    override fun onFastUpdate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSlowUpdate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private data class ProblemCache(val id: Int, val closeAt: DateTime) : Comparable<ProblemCache> {

        override fun compareTo(other: ProblemCache): Int = closeAt.compareTo(other.closeAt)
    }
}