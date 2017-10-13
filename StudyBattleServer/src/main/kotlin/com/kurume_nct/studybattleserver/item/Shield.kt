package com.kurume_nct.studybattleserver.item

import com.kurume_nct.studybattleserver.dao.ItemStack
import com.kurume_nct.studybattleserver.dao.Problem
import com.kurume_nct.studybattleserver.dao.User
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Created by gedorinku on 2017/10/12.
 */
object Shield : Item() {

    override fun onExplode(itemStack: ItemStack, problem: Problem, user: User): Boolean
        = transaction {
        itemStack.discard()
        true
    }
}