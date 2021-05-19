package com.example.handler

import com.example.controller.ControllerUser
import com.example.domain.exceptions.MissingBodyParameterException
import com.example.domain.exceptions.MissingPathParameterException
import com.example.domain.exceptions.UserNameAlreadyExistsException
import com.example.domain.model.User
import com.example.domain.paths.UsersPath
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.postgresql.util.PSQLException
import java.lang.Exception
import java.lang.NullPointerException

class HandlerUser(private val controller: ControllerUser) {

    suspend fun getUsers(applicationCall: ApplicationCall, usersPath: UsersPath) {
        if (usersPath.ids.none())
            throw MissingPathParameterException()

        val users =
            controller.getUsers(usersPath.ids.toList())

        applicationCall.respondText(Json.encodeToString(users))
    }

    suspend fun postUsers(applicationCall: ApplicationCall) {
        try {
            val params = applicationCall.receiveParameters()
            val name = params["name"]

            val user = controller.postUsers(name!!)

            applicationCall.respond(HttpStatusCode.Created, Json.encodeToString(user))

        } catch (e: NullPointerException) {
            throw MissingBodyParameterException()
        }
    }
}