package com.example.controller

import com.example.cache.exec
import com.example.cache.execAndReturn
import com.example.domain.model.Score
import io.ktor.application.*
import org.jetbrains.exposed.sql.transactions.transaction

class ControllerScore {
    fun getScores(userId: Int, exerciseId: Int, timestamp: Long, highscore: Boolean) : List<Score> {
        val scores = arrayListOf<Score>()
        val middlePartOfSQLStatement = " FROM exercise_scores " +
                "WHERE user_id = $userId AND exercise = $exerciseId AND timestamp > $timestamp "
        var query: String
        if (highscore) {
            query = "SELECT user_id, exercise, timestamp, max(score) AS score" +
                    middlePartOfSQLStatement +
                    "GROUP BY user_id, exercise, timestamp;"
        }
        else {
            query = "SELECT user_id, exercise, timestamp, score " +
                    middlePartOfSQLStatement +
                    ";"
        }
        transaction {
            query.execAndReturn { rs ->
                while(rs.next()) {
                    val score = Score(rs.getInt("user_id"), rs.getInt("exercise"), rs.getLong("timestamp"), rs.getInt("score"))
                    scores.add(score)
                }
            }
        }
        // TODO: remove later, just for debugging purposes
        if(highscore && scores.size > 1) {
            throw RuntimeException("There cannot be more than one highscore!")
        }
        return scores
    }

    // TODO: deal with idempotence, don't trash database
    fun putScores(score: Score) {
        transaction {
            "INSERT INTO exercise_scores (user_id, timestamp, exercise, score) VALUES (${score.userId}, ${score.timestamp}, ${score.exerciseId}, ${score.score});".exec()
        }
    }
}