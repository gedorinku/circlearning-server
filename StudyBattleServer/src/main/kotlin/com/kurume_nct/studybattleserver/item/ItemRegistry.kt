package com.kurume_nct.studybattleserver.item

/**
 * Created by gedorinku on 2017/10/12.
 */
object ItemRegistry {
    private val items = mutableMapOf<Int, Item>()

    val registeredItems
        get() = items.toMap()

    fun register(item: Item): Int {
        if (item.id != -1) {
            return item.id
        }
        val id = items.size
        items.put(id, item)
        item.onRegisteredItem(id)
        return id
    }
}