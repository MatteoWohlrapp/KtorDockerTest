package com.example.controller

import com.example.model.Score
import com.example.paths.ScoresPath
import io.ktor.application.*

class ControllerScore {
    fun getScores(userId: Int, exerciseId: Int, timestamp: Long, highscore: Boolean) : List<Score> {
        //TODO(not implemented yet)
        return emptyList()
    }

    fun putScores(score: Score) : Boolean {
        //TODO(not implemented yet)
        return false
    }
}