package com.example


import com.example.handler.HandlerCompetition
import com.example.handler.HandlerScore
import com.example.handler.HandlerUser
import com.example.paths.CompetitionsPath
import com.example.paths.CompetitionsScorePath
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
            handlerScore.getScores(call, scores)
        }
        put<ScoresPath>{
            handlerScore.putScores(call)
        }
        get<UsersPath>{ users ->
            handlerUser.getUsers(call, users)
        }
        post<UsersPath>{
            handlerUser.postUsers(call)
        }
        get<CompetitionsPath>{ competitions ->
            handlerCompetition.getCompetitions(call, competitions)
        }
        get<CompetitionsPath.CompetitionId>{ competitionsId ->
            handlerCompetition.getCompetition(call, competitionsId)
        }
        post<CompetitionsPath>{
            handlerCompetition.postCompetitions(call)
        }
        put<CompetitionsScorePath>{
            handlerCompetition.putCompetitionScore(call)
        }
    }
}
