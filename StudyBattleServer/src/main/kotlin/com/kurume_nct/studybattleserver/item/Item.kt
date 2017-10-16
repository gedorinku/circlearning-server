package com.kurume_nct.studybattleserver.item

import com.kurume_nct.studybattleserver.dao.ItemStack
import com.kurume_nct.studybattleserver.dao.Problem
import com.kurume_nct.studybattleserver.dao.User

/**
 * Created by gedorinku on 2017/10/12.
 */
abstract class Item {
    var id: Int = -1
        private set

    open fun onOpenProblem(problem: Problem, user: User): OpenAction = OpenAction.NONE

    open fun onExplode(itemStack: ItemStack, problem: Problem, user: User): Boolean = false

    open fun onScore(itemStack: ItemStack, user: User, score: Double): Double = score

    fun onRegisteredItem(id: Int) {
        if (this.id != -1) {
            throw IllegalStateException("already registered")
        }
        this.id = id
    }

    enum class OpenAction {

        NONE,
        EXPLODED,
        DEFENDED
    }
}