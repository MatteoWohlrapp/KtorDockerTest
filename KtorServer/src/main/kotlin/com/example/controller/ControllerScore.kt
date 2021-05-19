package com.example.controller

import com.example.cache.Exercise_Scores
import com.example.cache.Users
import com.example.domain.model.Score
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ControllerScore {

    fun getScores(inputUserIds: Iterable<Int>, exerciseId: Int, timestamp: Long, highscore: Boolean): List<Score> {
        return transaction {
            val scores = mutableListOf<Score>()
            var userIds = inputUserIds
            if (inputUserIds.none()) {
                userIds = Users.selectAll().map { resultRow -> resultRow[Users.id] }
            }

            for (userId in userIds) {
                val resultRows = mutableListOf<ResultRow>()
                var query =
                    Exercise_Scores.select {
                        Exercise_Scores.userId.eq(userId) and Exercise_Scores.exerciseId.eq(exerciseId) and Exercise_Scores.timestamp.greater(
                            timestamp
                        )
                    }
                if (highscore) {
                    val resultRow = query.maxWithOrNull { rR1, rR2 ->
                        rR1[Exercise_Scores.score] - rR2[Exercise_Scores.score]
                    }

                    if (resultRow != null) {
                        resultRows.add(resultRow)
                    }
                } else {
                    query.iterator().forEach { resultRows.add(it) }
                }
                for (resultRow in resultRows) {
                    if (resultRow != null) {
                        val score = Score(
                            resultRow[Exercise_Scores.userId],
                            resultRow[Exercise_Scores.exerciseId],
                            resultRow[Exercise_Scores.timestamp],
                            resultRow[Exercise_Scores.score]
                        )
                        scores.add(score)
                    }
                }
            }
            return@transaction scores
        }
    }


    fun putScores(inputScore: Score) {
        transaction {
            Exercise_Scores.insert {
                it[userId] = inputScore.userId
                it[timestamp] = inputScore.timestamp
                it[exerciseId] = inputScore.exerciseId
                it[score] = inputScore.score
            }
        }
    }
}