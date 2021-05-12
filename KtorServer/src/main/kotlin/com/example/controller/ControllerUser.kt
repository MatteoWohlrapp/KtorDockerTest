package com.example.controller

import com.example.cache.DBInitializer
import com.example.cache.Users
import com.example.domain.exceptions.UserNameAlreadyExistsException
import com.example.domain.model.User
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class ControllerUser {

    fun getUsers(ids: List<Int>): List<User> {
        val users = mutableListOf<User>()
        for (id in ids) {
            transaction {
                Users.select { Users.id.eq(id) }.forEach {
                    users.add(
                        User(
                            it[Users.id],
                            it[Users.name]
                        )
                    )
                }
            }

        }
        return users
    }

    fun postUsers(inputName: String): User {
        return transaction {
            if(Users.select { Users.name.eq(inputName) }.none()) {
                Users.insert {
                    it[name] = inputName
                }
                val resultRow = Users.slice(Users.id, Users.name).select { Users.name.eq(inputName) }.first()
                return@transaction User(resultRow[Users.id], resultRow[Users.name])
            } else {
                throw UserNameAlreadyExistsException()
            }
        }
    }

}