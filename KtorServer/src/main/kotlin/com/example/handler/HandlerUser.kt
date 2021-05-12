package com.example.handler

import com.example.controller.ControllerUser
import com.example.domain.exceptions.MissingBodyParameterException
import com.example.domain.exceptions.MissingPathParameterException
import com.example.domain.paths.UsersPath
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.Exception
import java.lang.NullPointerException

class HandlerUser(val controller: ControllerUser) {

    suspend fun getUsers(applicationCall: ApplicationCall, usersPath: UsersPath) {
        if (!usersPath.ids.any { true })
            throw MissingPathParameterException()

        try {
            val users =
                controller.getUsers(usersPath.ids.toList())
            applicationCall.respondText(Json.encodeToString(users))

            //TODO add custom exceptions for getUsers in Controller
        } catch (e: Exception) {
            applicationCall.respond(HttpStatusCode.InternalServerError, e.toString())
        }
    }

    suspend fun postUsers(applicationCall: ApplicationCall) {
        try {
            val params = applicationCall.receiveParameters()
            val name = params["name"]
            applicationCall.respondText("Name is: $name")

//            val userId = controller.postUsers(name!!)
//            val user = User(userId, name)
//            applicationCall.respond(HttpStatusCode.OK, Json.encodeToString(user))
            //TODO add custom exception for postUsers in Controller
            //TODO check if user already exists
        } catch (e: NullPointerException) {
            throw MissingBodyParameterException()
        } catch (e: Throwable) {
            applicationCall.respondText(e.toString())
        }
    }
}