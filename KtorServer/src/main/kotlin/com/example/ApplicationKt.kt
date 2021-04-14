package com.example

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.format.DateTimeFormatter
import java.util.*

fun main() {
    Database.connect("jdbc:postgresql://localhost:5432/sportify", driver = "org.postgresql.Driver", user ="envidual", password = "werksstudent")

    transaction {
        SchemaUtils.create (names)

        println(names.selectAll())
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
                    SchemaUtils.create(names)
                    transaction {
                        names.insert {
                            it[name] = "Matteo"
                        }
                        SchemaUtils.create(names)
                        val query = names.selectAll()
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

object names: IntIdTable() {
    val name = varchar("name", 255)
}


/*
client.get<HighscoreContainer>("$URL/highscores")
client.post<HttpResponse>("$URL/score?userId=$userId&score=$score&exerciseName=$exerciseName") -> bekommt boolean zurück
client.post<HttpResponse>("$URL/register?userId=$userId") -> bekommt boolean zurück
 */