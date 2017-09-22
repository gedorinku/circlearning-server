package com.kurume_nct.studybattleserver.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by gedorinku on 2017/07/24.
 */
object AuthenticationKeys : IntIdTable() {

    val keyHash = varchar("keyHash", 127)
    val user = reference("user", Users)
    val createdAt = datetime("created_at")
}

class AuthenticationKey(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<AuthenticationKey>(AuthenticationKeys)

    var keyHash by AuthenticationKeys.keyHash
    var user by User referencedOn AuthenticationKeys.user
    var createdAt by AuthenticationKeys.createdAt
}
