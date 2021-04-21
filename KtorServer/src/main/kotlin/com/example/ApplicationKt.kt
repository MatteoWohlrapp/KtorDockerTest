package com.example

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.format.DateTimeFormatter
import java.util.*

fun main() {
    Database.connect("jdbc:postgresql://localhost:5432/postgres", driver = "org.postgresql.Driver", user ="postgres", password = "mysecretpassword")

    transaction {
        SchemaUtils.create(Users)
        SchemaUtils.create(PushUps)
        SchemaUtils.create(Squats)
        println(Users.selectAll())
    }

    embeddedServer(Netty, port = 8000) {
        routing {
            get("/") {
                call.respondText("Gradle sucks!")
            }
            get("/boobs"){
                call.respondText("Nice!")
            }
            get("/names") {
                transaction {
                    SchemaUtils.create(Users)
                    transaction {
                        Users.insert {
                            it[userId] = "Matteo"
                        }
                        SchemaUtils.create(Users)
                        val query = Users.selectAll()
                        val builder = StringBuilder()
                        query.forEach {
                            builder.append("$it, ")
                        }

                        GlobalScope.launch {
                            call.respondText("Names: $builder")
                        }
                    }
                }
            }
            get("/highscores"){

            }
            post("/register"){
                val userId = call.receiveParameters()["userId"]
                transaction {

                }
                call.respondText("User is: $userId")
            }
            post("/score"){
                val userId = call.receiveParameters()["userId"]
                val score = call.receiveParameters()["score"]
                val exercise = call.receiveParameters()["exerciseName"]
                transaction {

                }
                call.respondText("User: $userId, Score: $score, Exercise: $exercise")
            }
        }
    }.start(wait = true)

}

object Users: Table() {
    val userId = varchar("userId", 255)
    override val primaryKey = PrimaryKey(userId)
}

object PushUps: Table() {
    private val userId = (varchar("id", 255) references Users.userId)
    override val primaryKey = PrimaryKey(userId)
    private val score = integer("score")
    private val timestamp = datetime("timestamp")
}

object Squats: Table() {
    private val userId = (varchar("id", 255) references Users.userId)
    override val primaryKey = PrimaryKey(userId)
    private val score = integer("score")
    private val timestamp = datetime("timestamp")
}


/*
client.get<HighscoreContainer>("$URL/highscores")
client.post<HttpResponse>("$URL/score?userId=$userId&score=$score&exerciseName=$exerciseName") -> bekommt boolean zurück
client.post<HttpResponse>("$URL/register?userId=$userId") -> bekommt boolean zurück
 */
