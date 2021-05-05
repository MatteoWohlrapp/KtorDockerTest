package com.example.controller

import com.example.model.User
import kotlinx.coroutines.sync.Semaphore
import org.jetbrains.exposed.sql.transactions.transaction

class ControllerUser {

    suspend fun getUsers(ids: List<Int>): List<User> {
        // TODO: find a more elegant way to pass the values from execAndReturn on to the outside
        val users = arrayListOf<User>()
        val semaphore = Semaphore(1)
        transaction {
            ("SELECT id, name FROM users;").execAndReturn { rs ->
                while (rs.next()) {
                    val user = User(rs.getInt("id"), rs.getString("name"))
                    users.add(user)
                }
            }
        }
        semaphore.acquire()
        return users
    }

    fun postUsers(name: String) : Int {
        if (name != null) {
            transaction {
                "INSERT INTO users (name) VALUES ('$name');".exec()
            }
        }
        return -1
    }
}