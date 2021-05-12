package com.example


import com.example.cache.*
import com.example.di.serverModule
import com.example.domain.exceptions.MissingBodyParameterException
import com.example.domain.exceptions.MissingPathParameterException
import com.example.domain.exceptions.ParsingException
import com.example.handler.HandlerCompetition
import com.example.handler.HandlerScore
import com.example.handler.HandlerUser
import com.example.domain.paths.CompetitionsPath
import com.example.domain.paths.ScoresPath
import com.example.domain.paths.UsersPath
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import java.lang.NumberFormatException


fun main(args: Array<String>) {

    DBInitializer.initialize()
    EngineMain.main(args)
}

fun Application.module(){


    install(Locations)
    install(Koin) {
        modules(serverModule)
    }
    install(StatusPages){
        exception<MissingPathParameterException>{ cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message.toString())
        }
        exception<MissingBodyParameterException>{ cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message.toString())
        }
        exception<ParsingException>{cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message.toString())
        }
        exception<NumberFormatException>{ cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message.toString())
        }
        exception<Throwable>{ cause ->
            call.respond(HttpStatusCode.InternalServerError, cause.message.toString())
        }
    }
    val handlerUser by inject<HandlerUser>()
    val handlerScore by inject<HandlerScore>()
    val handlerCompetition by inject<HandlerCompetition>()

    routing {

        get<ScoresPath>{ scores ->
            handlerScore.getScores(call, scores)
        }
        put("/scores"){
            handlerScore.putScores(call)
        }
        get<UsersPath>{ users ->
            handlerUser.getUsers(call, users)
        }
        post("/users"){
            handlerUser.postUsers(call)
        }
        get<CompetitionsPath>{ competitions ->
            handlerCompetition.getCompetitions(call, competitions)
        }
        get<CompetitionsPath.CompetitionId>{ competitionsId ->
            handlerCompetition.getCompetition(call, competitionsId)
        }
        post("/competitions"){
            handlerCompetition.postCompetitions(call)
        }
        put("/competition-score"){
            handlerCompetition.putCompetitionScore(call)
        }
    }
}


