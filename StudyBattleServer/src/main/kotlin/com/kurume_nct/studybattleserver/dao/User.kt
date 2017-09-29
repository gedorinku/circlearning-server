package com.kurume_nct.studybattleserver.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Created by gedorinku on 2017/07/23.
 */
object Users : IntIdTable() {

    val userName = varchar("user_name", 20).uniqueIndex()
    val displayName = varchar("display_name", 20)
    val hashSalt = varchar("hash_salt", 127)
    val passwordHash = varchar("password_hash", 127)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var userName by Users.userName
    var displayName by Users.displayName
    var hashSalt by Users.hashSalt
    var passwordHash by Users.passwordHash

    /**
     * グループに参加します。すでに参加している場合は何もしません。
     */
    fun joinGroup(group: Group) {
        group.attachUser(this)
    }

    /**
     * グループに参加します。すでに参加している場合は何もしません。
     */
    fun joinGroup(groupId: Int) = transaction {
        joinGroup(Group.findById(groupId) ?: throw IllegalArgumentException())
    }

    fun countAssignedProblems(group: Group): Int = transaction {
        Problem.find {
            Problems.group.eq(group.id) and Problems.assignedUser.eq(this@User.id)
        }.count()
    }

    fun countAssignedProblems(groupId: Int): Int {
        val group = transaction {
            Group.findById(groupId)
        } ?: throw IllegalArgumentException()
        return countAssignedProblems(group)
    }
}