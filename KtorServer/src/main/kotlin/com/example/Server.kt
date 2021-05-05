package com.example


import com.example.cache.*
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
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


fun main(args: Array<String>) {
    // TODO: gehört verbinden zur Datenbank hier hin?
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
    "insert into competitions (user_id_one, user_id_two, creation_timestamp) values (1,2,0);".exec()
    "insert into exercise_scores (user_id, timestamp, exercise, score) values (1,0,0,30);".exec()
    "insert into competition_exercises (competition_id, score_id) values (1,1);".exec();
    "insert into exercise_scores (user_id, timestamp, exercise, score) values (2,0,0,1);".exec()
    "insert into competition_exercises (competition_id, score_id) values (1,2);".exec();
    "insert into exercise_scores (user_id, timestamp, exercise, score) values (1,0,1,30);".exec()
    "insert into competition_exercises (competition_id, score_id) values (1,3);".exec();
    "insert into exercise_scores (user_id, timestamp, exercise, score) values (2,0,1,0);".exec()
    "insert into competition_exercises (competition_id, score_id) values (1,4);".exec();
}

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
