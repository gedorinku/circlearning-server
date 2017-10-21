package com.kurume_nct.studybattleserver.item

import com.kurume_nct.studybattleserver.dao.Problem
import com.kurume_nct.studybattleserver.dao.User

/**
 * Created by gedorinku on 2017/10/22.
 */
object MagicHand : Item() {

    override fun onOpenProblem(problem: Problem, user: User): OpenAction {
        user.addScore(-10)
        return super.onOpenProblem(problem, user)
    }
}