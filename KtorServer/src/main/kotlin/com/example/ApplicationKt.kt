package com.example

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    Database.connect("jdbc:postgresql://localhost:5432/postgres", driver = "org.postgresql.Driver", user ="postgres", password = "2702")

    transaction {
        SchemaUtils.create (Users)
        SchemaUtils.create (PushUps)
        SchemaUtils.create (Squats)
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
        }
    }.start(wait = true)

}

object Users: Table() {
    val userId = varchar("userId", 255)
}

object PushUps: Table() {
    private val userId = (varchar("userId", 255) references Users.userId)
    override val primaryKey = PrimaryKey(userId)
    private val score = integer("score")
    private val timestamp = datetime("timestamp")
}

object Squats: Table() {
    private val userId = (varchar("userId", 255) references Users.userId)
    override val primaryKey = PrimaryKey(userId)
    private val score = integer("score")
    private val timestamp = datetime("timestamp")
}