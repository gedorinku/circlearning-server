package com.kurume_nct.studybattleserver.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Created by gedorinku on 2017/07/30.
 */
object Groups : IntIdTable() {

    val name = varchar("name", 22)
    val owner = reference("owner", Users)
}

class Group(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Group>(Groups)

    var name by Groups.name
    var owner by User referencedOn Groups.owner

    /**
     * グループにユーザーを参加させます。すでに参加している場合は何もしません。
     */
    fun attachUser(user: User) = transaction {
        val joined = !Belonging.find {
            Belongings.user.eq(user.id) and Belongings.group.eq(this@Group.id)
        }.empty()
        if (joined) {
            return@transaction
        }

        Belonging.new {
            this.user = user
            this.group = this@Group
        }
    }

    /**
     * グループにユーザーを参加させます。すでに参加している場合は何もしません。
     */
    fun attachUser(userId: Int) {
        val user = transaction {
            User.findById(userId)
        } ?: throw IllegalArgumentException()
        attachUser(user)
    }

    fun fetchUsers(): List<User> = transaction {
        Belonging.find { Belongings.group.eq(this@Group.id) }
                .map { it.user }
                .toList()
    }

    fun countOfUsers(): Int = transaction {
        Belonging.find { Belongings.group.eq(this@Group.id) }
                .count()
    }
}
