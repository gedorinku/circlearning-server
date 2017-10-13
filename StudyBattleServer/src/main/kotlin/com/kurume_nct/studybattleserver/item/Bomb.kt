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

    override fun onOpenProblem(itemStack: ItemStack, problem: Problem, user: User): Unit
            = transaction {
        ItemStack
                .find {
                    ItemStacks.user.eq(user.id) and ItemStacks.itemId.eq(this@Bomb.id)
                }
                .firstOrNull()
                ?.let {
                    TODO("盾")
                }
    }
}