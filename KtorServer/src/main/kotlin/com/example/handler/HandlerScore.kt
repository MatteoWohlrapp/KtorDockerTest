package com.example.handler

import com.example.controller.ControllerScore
import com.example.model.Score
import com.example.paths.ScoresPath
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HandlerScore {

    private val controller = ControllerScore()

    suspend fun getScores(applicationCall: ApplicationCall, scoresPath: ScoresPath) {
        applicationCall.respondText("Given userId: ${scoresPath.userId}")
         val scores =
             controller.getScores(scoresPath.userId, scoresPath.exerciseId, scoresPath.timestamp, scoresPath.highscore)

        applicationCall.respondText("From Controller: ${Json.encodeToString(scores)}")
    }

    suspend fun putScores(applicationCall: ApplicationCall) {
        val params = applicationCall.receiveParameters()
        val score = Score(
            params["userId"]!!.toInt(),
            params["exerciseId"]!!.toInt(),
            params["timestamp"]!!.toLong(),
            params["score"]!!.toInt()
        )

        applicationCall.respondText("Given userId: $")

        val result = controller.putScores(score)

        applicationCall.respondText("From Controller: $result")
    }
}