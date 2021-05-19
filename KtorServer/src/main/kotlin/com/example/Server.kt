package com.example


import com.example.cache.*
import com.example.di.serverModule
import com.example.domain.exceptions.CompetitionAlreadyActiveException
import com.example.domain.exceptions.MissingBodyParameterException
import com.example.domain.exceptions.MissingPathParameterException
import com.example.domain.exceptions.UserNameAlreadyExistsException
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
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.postgresql.util.PSQLException
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
        exception<UserNameAlreadyExistsException>{ cause ->
            call.respond(HttpStatusCode.Conflict, cause.message.toString())
        }
        exception<CompetitionAlreadyActiveException>{ cause ->
            call.respond(HttpStatusCode.Conflict, cause.message.toString())
        }
        exception<PSQLException> { cause ->
            call.respond(HttpStatusCode.InternalServerError, cause.message.toString())
        }
        // somehow it doesnt catch Number format exceptions as such
        exception<Throwable>{ cause ->
            if(cause.cause != null && cause.cause!!::class == NumberFormatException::class)
                call.respond(HttpStatusCode.BadRequest, cause.message.toString())
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


