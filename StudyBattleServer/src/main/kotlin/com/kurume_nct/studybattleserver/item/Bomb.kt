package com.kurume_nct.studybattleserver.item

import com.kurume_nct.studybattleserver.dao.ItemStack
import com.kurume_nct.studybattleserver.dao.ItemStacks
import com.kurume_nct.studybattleserver.dao.Problem
import com.kurume_nct.studybattleserver.dao.User
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Created by gedorinku on 2017/10/12.
 */
object Bomb : Item() {

    override fun onOpenProblem(problem: Problem, user: User): OpenAction
            = transaction {
        ItemStack
                .find {
                    ItemStacks.user.eq(user.id) and ItemStacks.itemId.eq(Shield.id)
                }
                .firstOrNull()
                ?.let {
                    val defended = Shield.onExplode(it, problem, user)
                    if (defended) {
                        OpenAction.DEFENDED
                    } else {
                        OpenAction.EXPLODED
                    }
                } ?: OpenAction.EXPLODED
    }
}