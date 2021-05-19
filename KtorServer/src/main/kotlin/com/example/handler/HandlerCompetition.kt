package com.example.handler

import com.example.controller.ControllerCompetition
import com.example.domain.exceptions.MissingBodyParameterException
import com.example.domain.exceptions.MissingPathParameterException
import com.example.domain.exceptions.NoSuchCompetitionException
import com.example.domain.exceptions.ParsingException
import com.example.domain.model.CompetitionScore
import com.example.domain.paths.CompetitionsPath
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.Exception
import java.lang.NullPointerException
import java.lang.NumberFormatException

class HandlerCompetition(val controller: ControllerCompetition) {

    suspend fun getCompetitions(applicationCall: ApplicationCall, competitionsPath: CompetitionsPath) {
        if (competitionsPath.userId < 0)
            throw MissingPathParameterException()

        try {
            val competitions =
                controller.getCompetitions(
                    competitionsPath.userId,
                    competitionsPath.timestamp
                )
            applicationCall.respond(HttpStatusCode.OK, Json.encodeToString(competitions))

        } catch (e: NoSuchCompetitionException) {
            applicationCall.respond(HttpStatusCode.BadRequest, e.toString())
        }
    }

    suspend fun getCompetition(applicationCall: ApplicationCall, competitionsIdPath: CompetitionsPath.CompetitionId) {
        //no parameter checking necessary, since it does not end up at this method otherwise
        val competition =
            controller.getCompetition(competitionsIdPath.id)

        try {
            val competition =
                controller.getCompetition(competitionsIdPath.id)
            applicationCall.respond(HttpStatusCode.OK, Json.encodeToString(competition))

            //TODO add custom exceptions for getCompetition in Controller
        } catch (e: Exception) {
            applicationCall.respond(HttpStatusCode.InternalServerError, e.toString())
        }
    }

    suspend fun postCompetitions(applicationCall: ApplicationCall) {
        try {
            val params = applicationCall.receiveParameters()
            val userIdOne = params["userIdOne"]!!.toInt()
            val userIdTwo = params["userIdTwo"]!!.toInt()

            val competitionId = controller.postCompetitions(userIdOne, userIdTwo)
            applicationCall.respond(HttpStatusCode.OK, competitionId)

            //TODO add custom exceptions for postCompetitions in Controller
        } catch (e: NumberFormatException) {
            throw ParsingException()
        } catch (e: NullPointerException) {
            throw MissingBodyParameterException()
        } catch (e: Throwable) {
            applicationCall.respond(HttpStatusCode.InternalServerError, e.toString())
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
            applicationCall.respond(HttpStatusCode.OK, "Competition score added successfully.")

            //TODO add custom exceptions for putCompetitionScores in Controller
        } catch (e: NumberFormatException) {
            throw ParsingException()
        } catch (e: NullPointerException) {
            throw MissingBodyParameterException()
        } catch (e: Throwable) {
            applicationCall.respond(HttpStatusCode.InternalServerError, e.toString())
        }
    }
}