package com.example.handler

import com.example.controller.ControllerScore
import com.example.domain.exceptions.MissingBodyParameterException
import com.example.domain.exceptions.MissingPathParameterException
import com.example.domain.exceptions.ParsingException
import com.example.domain.model.Score
import com.example.domain.paths.ScoresPath
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.postgresql.util.PSQLException
import java.lang.Exception
import java.lang.NullPointerException
import java.lang.NumberFormatException

class HandlerScore(val controller: ControllerScore) {

    suspend fun getScores(applicationCall: ApplicationCall, scoresPath: ScoresPath) {
        if (scoresPath.exerciseId < 0)
            throw MissingPathParameterException() //returns code 400 Bad request

        try {
            val scores =
                controller.getScores(
                    scoresPath.userIds,
                    scoresPath.exerciseId,
                    scoresPath.timestamp,
                    scoresPath.highscore
                )
            applicationCall.respond(HttpStatusCode.OK, Json.encodeToString(scores))

            //TODO add custom exceptions for getScores in Controller
        } catch (e: PSQLException) {
            applicationCall.respond(HttpStatusCode.InternalServerError, "Sql Exception")
        }
    }

    suspend fun putScores(applicationCall: ApplicationCall) {
        try {
            val params = applicationCall.receiveParameters()

            val score = Score(
                params["userId"]!!.toInt(),
                params["exerciseId"]!!.toInt(),
                params["timestamp"]!!.toLong(),
                params["score"]!!.toInt()
            )

            controller.putScores(score)
            applicationCall.respond(HttpStatusCode.OK, "Score added successfully.")

            //TODO add custom exceptions for putScores in Controller
        } catch (e: NumberFormatException) {
            throw ParsingException()
        } catch (e: NullPointerException) {
            throw MissingBodyParameterException()
        } catch (e: PSQLException) {
            applicationCall.respond(HttpStatusCode.InternalServerError, "Sql Exception")
        }

    }
}