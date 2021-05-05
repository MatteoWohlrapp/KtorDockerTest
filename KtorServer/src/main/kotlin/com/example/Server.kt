package com.example


import com.example.handler.HandlerCompetition
import com.example.handler.HandlerScore
import com.example.handler.HandlerUser
import com.example.paths.CompetitionsPath
import com.example.paths.ScoresPath
import com.example.paths.UsersPath
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.netty.*



fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module(){
    install(Locations)
    val handlerUser = HandlerUser()
    val handlerScore = HandlerScore()
    val handlerCompetition = HandlerCompetition()

    routing {

        get<ScoresPath>{ scores ->
            call.respondText("Given userId: ${scores.userId}")
        }
        put<ScoresPath>{ scores ->
            val params = call.receiveParameters()
            val userId = params["userId"]
            val timestamp = params["timestamp"]
            val exerciseId = params["exerciseId"]
            val score = params["score"]
            call.respondText("Given userId: $userId")
        }
        get<UsersPath>{ users ->
            call.respondText("Given userId: ${users.ids}")
        }
        post<UsersPath>{
            val params = call.receiveParameters()
            val name = params["name"]
            call.respondText("Given name $name")
        }
        get<CompetitionsPath>{ competitions ->
            call.respondText("Given userId: ${competitions.userId}")

        }
        post<CompetitionsPath>{

        }
        get<CompetitionsPath.CompetitionId>{ competitionsId ->
            call.respondText("Given competitionId: ${competitionsId.id}")
        }

    }
}
