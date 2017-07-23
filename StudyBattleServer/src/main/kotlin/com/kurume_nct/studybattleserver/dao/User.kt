package com.kurume_nct.studybattleserver.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by gedorinku on 2017/07/23.
 */
object Users : IntIdTable() {

    val userName = varchar("user_name", 20).uniqueIndex()
    val displayName = varchar("screen_name", 20)
    val hashSalt = varchar("hash_salt", 127)
    val passwordHash = varchar("password_hash", 127)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var userName by Users.userName
    var displayName by Users.displayName
    var hashSalt by Users.hashSalt
    var passwordHash by Users.passwordHash
}