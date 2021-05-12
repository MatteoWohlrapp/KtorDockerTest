package com.example.handler

import com.example.controller.ControllerUser
import com.example.model.Score
import com.example.paths.UsersPath
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HandlerUser {
    private val controller = ControllerUser()

    suspend fun getUsers(applicationCall: ApplicationCall, usersPath: UsersPath) {
        val users =
            controller.getUsers(usersPath.ids.toList())
        applicationCall.respondText("From Controller: ${Json.encodeToString(users)}")
    }

    suspend fun postUsers(applicationCall: ApplicationCall) {
        val params = applicationCall.receiveParameters()
        val name = params["name"]
        val result = controller.postUsers(name!!)
        applicationCall.respondText("From Controller: $result")
    }
}