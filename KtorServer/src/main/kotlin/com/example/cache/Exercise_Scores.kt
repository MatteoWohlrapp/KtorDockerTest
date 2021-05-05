package com.example.cache

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Exercise_Scores: Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val timestamp = long("timestamp")
    val exercise = integer("exercise")
    val score = integer("score")
    override val primaryKey = PrimaryKey(id)
}