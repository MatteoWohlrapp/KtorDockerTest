package com.example.controller

import com.example.model.Score
import com.example.paths.ScoresPath
import io.ktor.application.*

class ControllerScore {
    fun getScores(userId: Int, exerciseId: Int, timestamp: Long, highscore: Boolean) : List<Score> {
        return emptyList()
    }

    fun putScores(score: Score) : Boolean {
//        val parameters = call.receiveParameters()
//        val userId = parameters["userId"]
//        val timestamp = parameters["timestamp"]
//        val exerciseId = parameters["exerciseId"]
//        val score = parameters["score"]
//        if (listOf(userId, timestamp, exerciseId, score).none { parameter -> parameter == null }) {
//            transaction {
//                ("INSERT INTO scores (user_id, timestamp, exercise, score) VALUES ($userId, $timestamp, $exerciseId, $score);").exec()
//            }
//        } else {
//            // return some error to handler
//        }
//        return emptyList()
        return false
    }
}