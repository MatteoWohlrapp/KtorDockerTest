package com.example.cache

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Exercise_Scores: Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val timestamp = long("timestamp")
    val exerciseId = integer("exercise_id")
    val score = integer("score")
    override val primaryKey = PrimaryKey(id)
}