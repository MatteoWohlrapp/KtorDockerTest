package com.example.handler

import com.example.controller.ControllerCompetition
import com.example.model.CompetitionScore
import com.example.model.Score
import com.example.paths.CompetitionsPath
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HandlerCompetition {
    private val controller = ControllerCompetition()

    suspend fun getCompetitions(applicationCall: ApplicationCall, competitionsPath: CompetitionsPath) {
        val competitions =
            controller.getCompetitions(competitionsPath.userId, competitionsPath.timestamp)

        applicationCall.respondText("From Controller: ${Json.encodeToString(competitions)}")
    }

    suspend fun getCompetition(applicationCall: ApplicationCall, competitionsIdPath: CompetitionsPath.CompetitionId) {
        applicationCall.respondText("Given userId: ${competitionsIdPath.id}")

        val competition =
            controller.getCompetition(competitionsIdPath.id)

        applicationCall.respondText("From Controller: ${Json.encodeToString(competition)}")
    }

    suspend fun postCompetitions(applicationCall: ApplicationCall) {
        val params = applicationCall.receiveParameters()
        val userIdOne = params["userIdOne"]!!.toInt()
        val userIdTwo = params["userIdTwo"]!!.toInt()


        applicationCall.respondText("Given userIdOne: $userIdOne")

        val result = controller.postCompetitions(userIdOne, userIdTwo)

        applicationCall.respondText("From Controller: $result")
    }

    suspend fun putCompetitionScore(applicationCall: ApplicationCall){
        val params = applicationCall.receiveParameters()
        val score = CompetitionScore(
            params["competitionId"]!!.toInt(),
            params["userId"]!!.toInt(),
            params["exerciseId"]!!.toInt(),
            params["timestamp"]!!.toLong(),
            params["score"]!!.toInt()
        )

        applicationCall.respondText("Given userId: ${score.userId}")

        val result = controller.putCompetitionScore(score)

        applicationCall.respondText("From Controller: $result")
    }
}