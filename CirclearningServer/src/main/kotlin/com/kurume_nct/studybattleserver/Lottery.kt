package com.kurume_nct.studybattleserver

import com.kurume_nct.studybattleserver.item.Item
import com.kurume_nct.studybattleserver.item.ItemRegistry
import java.util.*

/**
 * Created by gedorinku on 2017/10/15.
 */
object Lottery {

    private val random = Random()

    fun getRandomItem(): Item {
        val registeredItems = ItemRegistry.registeredItems
        val size = registeredItems.size
        return registeredItems[random.nextInt(size)]!!
    }
}