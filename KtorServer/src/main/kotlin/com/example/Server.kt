package com.example

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*


fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText("Gradle sucks!")

            }
        }
    }.start(wait = true)
}


//val userId = call.receiveParameters()["userId"]
////                val score = call.receiveParameters()["score"]
////                val exercise = call.receiveParameters()["exerciseName"]
