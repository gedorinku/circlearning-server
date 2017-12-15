package com.kurume_nct.studybattleserver.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.ktor.host.commandLineEnvironment

/**
 * Created by gedorinku on 2017/10/12.
 */
object ItemStacks : IntIdTable() {

    val itemId = integer("item_id")
    val count = integer("count")
    val user = reference("user", Users)
    val group = reference("group", Groups)
}

class ItemStack(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ItemStack>(ItemStacks)

    var itemId by ItemStacks.itemId
    var count by ItemStacks.count
    var user by User referencedOn ItemStacks.user
    var group by Group referencedOn ItemStacks.group

    fun discard() {
        if (count <= 0) {
            throw IllegalStateException("stack count is $count")
        }
        count--
        if (count == 0) {
            delete()
        }
        flush()
    }
}