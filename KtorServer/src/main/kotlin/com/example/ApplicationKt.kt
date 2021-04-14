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

fun main() {
    Database.connect("jdbc:postgresql://localhost:5432/postgres", driver = "org.postgresql.Driver", user ="postgres", password = "2702")

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
        }
    }.start(wait = true)

}

object names: IntIdTable() {
    val name = varchar("name", 255)
}