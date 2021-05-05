package com.example.cache

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Competition_Exercises: Table() {
    val competitionId = integer("competition_id").references(Competitions.id, onDelete = ReferenceOption.CASCADE)
    val scoreId = integer("score_id").references(Exercise_Scores.id, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(competitionId, scoreId)
}