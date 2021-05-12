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
    // TODO: gehört verbinden zur Datenbank hier hin?
    Thread.sleep(3000)
    Database.connect("jdbc:postgresql://ktorserver_db_1:5432/postgres", driver = "org.postgresql.Driver", user ="postgres", password = "mysecretpassword")
    // DEBUG
    transaction {
        SchemaUtils.drop(Competition_Exercises)
        SchemaUtils.drop(Competitions)
        SchemaUtils.drop(Exercise_Scores)
        SchemaUtils.drop(Users)
        SchemaUtils.create(Users)
        SchemaUtils.create(Exercise_Scores)
        SchemaUtils.create(Competitions)
        SchemaUtils.create(Competition_Exercises)
        insertFirstValues()
    }
    EngineMain.main(args)
}

fun insertFirstValues() {
    "insert into users (name) values ('Max');".exec()
    "insert into users (name) values ('Matteo');".exec()
    "insert into competitions (user_id_one, user_id_two, creation_timestamp) values (1,2,54783567);".exec()
    "insert into exercise_scores (user_id, timestamp, exercise, score) values (1,347856473,0,30);".exec()
    "insert into competition_exercises (competition_id, score_id) values (1,1);".exec();
    "insert into exercise_scores (user_id, timestamp, exercise, score) values (2,53454732875,0,1);".exec()
    "insert into competition_exercises (competition_id, score_id) values (1,2);".exec();
    "insert into exercise_scores (user_id, timestamp, exercise, score) values (1,43589743892,1,30);".exec()
    "insert into competition_exercises (competition_id, score_id) values (1,3);".exec();
    "insert into exercise_scores (user_id, timestamp, exercise, score) values (2,8758782,1,0);".exec()
    "insert into competition_exercises (competition_id, score_id) values (1,4);".exec();
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


