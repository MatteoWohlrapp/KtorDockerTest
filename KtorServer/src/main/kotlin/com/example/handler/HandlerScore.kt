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
import java.lang.Exception
import java.lang.NullPointerException
import java.lang.NumberFormatException

class HandlerScore(val controller: ControllerScore) {

    suspend fun getScores(applicationCall: ApplicationCall, scoresPath: ScoresPath) {
        if (!scoresPath.userIds.any { true } || scoresPath.exerciseId < 0)
            throw MissingPathParameterException() //returns code 400 Bad request

        applicationCall.respondText("Given userId: ${scoresPath.userIds}")

        try {
//            val scores =
//                controller.getScores(
//                    scoresPath.userIds,
//                    scoresPath.exerciseId,
//                    scoresPath.timestamp,
//                    scoresPath.highscore
//                )
//            applicationCall.respond(HttpStatusCode.OK, Json.encodeToString(scores))

            //TODO add custom exceptions for getScores in Controller
        } catch (e: Exception) {
            applicationCall.respond(HttpStatusCode.InternalServerError, e.toString())
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
            applicationCall.respondText("Given userId: ${score.userId}")


//            controller.putScores(score)
//            applicationCall.respond(HttpStatusCode.OK, "Score added successfully.")

            //TODO add custom exceptions for putScores in Controller
        } catch (e: NumberFormatException) {
            throw ParsingException()
        } catch (e: NullPointerException) {
            throw MissingBodyParameterException()
        } catch (e: Throwable) {
            applicationCall.respond(HttpStatusCode.InternalServerError, e.toString())
        }

    }
}