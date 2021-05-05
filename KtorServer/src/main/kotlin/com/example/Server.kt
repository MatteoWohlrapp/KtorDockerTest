package com.example


import io.ktor.application.*
import io.ktor.client.features.websocket.WebSockets.Feature.install
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


fun main(args: Array<String>) {

    Database.connect("jdbc:postgresql://ktorserver_db_1:5432/postgres", driver = "org.postgresql.Driver", user ="postgres", password = "mysecretpassword")


    transaction {
        SchemaUtils.drop(Users)
        SchemaUtils.create(Users)
        Users.insert {
            it[name] = "Matteo"
        }
    }

    embeddedServer(Netty, port = 8080) {
        install(Locations)

        routing {
            get("/") {
                transaction {
                    val query = Users.selectAll()
                    GlobalScope.launch {
                        call.respondText("Test: $query")
                    }
                }
            }
        }
    }.start(wait = true)
}

object Users: Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)
}