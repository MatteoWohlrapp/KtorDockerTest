package com.example.controller

import com.example.cache.exec
import com.example.cache.execAndReturn
import com.example.model.User
import kotlinx.coroutines.sync.Semaphore
import org.jetbrains.exposed.sql.transactions.transaction

class ControllerUser {

    suspend fun getUsers(ids: List<Int>): List<User> {
        val users = arrayListOf<User>()
        // TODO: validate with more tests that semaphores are not necessary
//        val semaphore = Semaphore(1,1)
        transaction {
            ("SELECT id, name FROM users;").execAndReturn { rs ->
                while (rs.next()) {
                    val user = User(rs.getInt("id"), rs.getString("name"))
                    users.add(user)
                }
//                semaphore.release()
            }
        }
//        semaphore.acquire()
        return users
    }

    fun postUsers(name: String) : Int {
        // TODO: get id and return it
        transaction {
                "INSERT INTO users (name) VALUES ('$name');".exec()
        }
        return -1
    }
}