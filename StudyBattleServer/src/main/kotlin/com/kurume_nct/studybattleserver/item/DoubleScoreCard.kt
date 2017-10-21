package com.kurume_nct.studybattleserver.item

import com.kurume_nct.studybattleserver.dao.Solution


/**
 * Created by gedorinku on 2017/10/21.
 */
object DoubleScoreCard : Item() {

    override fun onScore(solution: Solution, score: Int): Int
            = score * 2
}