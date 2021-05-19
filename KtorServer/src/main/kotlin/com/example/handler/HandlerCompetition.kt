package com.example.handler

import com.example.controller.ControllerCompetition
import com.example.domain.exceptions.MissingBodyParameterException
import com.example.domain.exceptions.MissingPathParameterException
import com.example.domain.model.CompetitionScore
import com.example.domain.model.EmptyJson
import com.example.domain.paths.CompetitionsPath
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.NullPointerException

class HandlerCompetition(private val controller: ControllerCompetition) {

    suspend fun getCompetitions(applicationCall: ApplicationCall, competitionsPath: CompetitionsPath) {
        if (competitionsPath.userId < 0)
            throw MissingPathParameterException()

        val competitions =
            controller.getCompetitions(
                competitionsPath.userId,
                competitionsPath.timestamp
            )

        applicationCall.respond(HttpStatusCode.OK, Json.encodeToString(competitions))
    }

    suspend fun getCompetition(applicationCall: ApplicationCall, competitionsIdPath: CompetitionsPath.CompetitionId) {
        //no parameter check necessary
        val competition =
            controller.getCompetition(competitionsIdPath.id)

        if (competition == null)
            applicationCall.respond(HttpStatusCode.OK, Json.encodeToString(EmptyJson()))
        else
            applicationCall.respond(HttpStatusCode.OK, Json.encodeToString(competition))
    }

    suspend fun postCompetitions(applicationCall: ApplicationCall) {
        try {
            val params = applicationCall.receiveParameters()
            val userIdOne = params["userIdOne"]!!.toInt()
            val userIdTwo = params["userIdTwo"]!!.toInt()

            val competition = controller.postCompetitions(userIdOne, userIdTwo)
            applicationCall.respond(HttpStatusCode.Created, competition)

        } catch (e: NullPointerException) {
            throw MissingBodyParameterException()
        }
    }

    suspend fun putCompetitionScore(applicationCall: ApplicationCall) {
        try {
            val params = applicationCall.receiveParameters()
            val score = CompetitionScore(
                params["competitionId"]!!.toInt(),
                params["userId"]!!.toInt(),
                params["exerciseId"]!!.toInt(),
                params["timestamp"]!!.toLong(),
                params["score"]!!.toInt()
            )

            controller.putCompetitionScore(score)
            applicationCall.respond(HttpStatusCode.Created, "Competition score added successfully.")

        }  catch (e: NullPointerException) {
            throw MissingBodyParameterException()
        }
    }
}