package com.example.controller

import com.example.cache.Exercise_Scores
import com.example.domain.model.Score
import io.ktor.application.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ControllerScore {
//    fun getScores(userIds: Iterable<Int>, exerciseId: Int, timestamp: Long, highscore: Boolean): List<Score> {
//        val scores = arrayListOf<Score>()
//        for (userId in userIds) {
//            val middlePartOfSQLStatement = " FROM exercise_scores " +
//                    "WHERE user_id = $userId AND exercise = $exerciseId AND timestamp > $timestamp "
//            var query: String
//            if (highscore) {
//                query = "SELECT user_id, exercise, timestamp, max(score) AS score" +
//                        middlePartOfSQLStatement +
//                        "GROUP BY user_id, exercise, timestamp;"
//            } else {
//                query = "SELECT user_id, exercise, timestamp, score " +
//                        middlePartOfSQLStatement +
//                        ";"
//            }
//
//            transaction {
//                query.execAndReturn { rs ->
//                    while (rs.next()) {
//                        val score = Score(
//                            rs.getInt("user_id"),
//                            rs.getInt("exercise"),
//                            rs.getLong("timestamp"),
//                            rs.getInt("score")
//                        )
//                        scores.add(score)
//                    }
//                }
//            }
//        }
//
//
//        // TODO: remove later, just for debugging purposes
//        if (highscore && scores.size > 1) {
//            throw RuntimeException("There cannot be more than one highscore!")
//        }
//        return scores
//    }


    fun getScores(userIds: Iterable<Int>, exerciseId: Int, timestamp: Long, highscore: Boolean): List<Score> {
        val scores = arrayListOf<Score>()
        for (userId in userIds) {
            transaction {
                val resultRows = mutableListOf<ResultRow>()
                var query =
                    Exercise_Scores.select {
                            Exercise_Scores.userId.eq(userId) and Exercise_Scores.exercise.eq(exerciseId) and Exercise_Scores.timestamp.greater(
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
                            resultRow[Exercise_Scores.exercise],
                            resultRow[Exercise_Scores.timestamp],
                            resultRow[Exercise_Scores.score]
                        )
                        scores.add(score)
                    }
                }
            }
        }
        return scores
    }


    // TODO: deal with idempotence, don't trash database -> why???
//    fun putScores(score: Score) {
//        transaction {
//            "INSERT INTO exercise_scores (user_id, timestamp, exercise, score) VALUES (${score.userId}, ${score.timestamp}, ${score.exerciseId}, ${score.score});".exec()
//        }
//    }

    fun putScores(inputScore: Score) {
        transaction {
            Exercise_Scores.insert {
                it[userId] = inputScore.userId
                it[timestamp] = inputScore.timestamp
                it[exercise] = inputScore.exerciseId
                it[score] = inputScore.score
            }
        }
    }
}