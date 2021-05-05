package com.example


import com.example.handler.HandlerCompetition
import com.example.handler.HandlerScore
import com.example.handler.HandlerUser
import com.example.routing.Competitions
import com.example.routing.Scores
import com.example.routing.Users
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json


fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module(){
    install(Locations)
    val handlerUser = HandlerUser()
    val handlerScore = HandlerScore()
    val handlerCompetition = HandlerCompetition()
//    val hw = HelloWorldTest()

    routing {

        get<Scores>{ scores ->
            handlerScore.getScores()
            call.respondText("Given userId: ${scores.userId}")
        }
        put<Scores>{ scores ->
            val params = call.receiveParameters()
            val userId = params["userId"]
            val timestamp = params["timestamp"]
            val exerciseId = params["exerciseId"]
            val score = params["score"]
            call.respondText("Given userId: $userId")
        }
        get<Users>{ users ->
            call.respondText("Given userId: ${users.ids}")
        }
        post<Users>{
            val params = call.receiveParameters()
            val name = params["name"]
            call.respondText("Given name $name")
        }
        get<Competitions>{ competitions ->
            call.respondText("Given userId: ${competitions.userId}")

        }
        post<Competitions>{

        }
        get<Competitions.CompetitionId>{ competitionsId ->
            call.respondText("Given competitionId: ${competitionsId.id}")
        }

    }
}
