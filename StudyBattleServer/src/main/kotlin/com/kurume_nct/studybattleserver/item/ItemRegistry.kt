package com.kurume_nct.studybattleserver.item

/**
 * Created by gedorinku on 2017/10/12.
 */
object ItemRegistry {
    private val items = mutableMapOf<Int, Item>()

    val registeredItems
        get() = items.toMap()

    fun register(item: Item): Int {
        val id = items.size
        items.put(id, item)
        item.onRegisteredItem(id)
        return id
    }
}