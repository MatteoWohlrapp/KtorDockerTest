package com.example

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Competitions: Table() {
    val id = integer("id").autoIncrement()
    val userIdOne = integer("user_id_one").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val userIdTwo = integer("user_id_two").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val creationTimestamp = long("creation_timestamp")
    override val primaryKey = PrimaryKey(id)
}