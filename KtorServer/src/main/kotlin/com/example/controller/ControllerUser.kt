package com.example.controller

import com.example.cache.Users
import com.example.domain.exceptions.UserNameAlreadyExistsException
import com.example.domain.model.User
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class ControllerUser {

//    fun getUsers(ids: List<Int>): List<User> {
//        val users = arrayListOf<User>()
//        // TODO: validate with more tests that semaphores are not necessary
////        val semaphore = Semaphore(1,1)
//        var queryPartDependingOnParameters = "WHERE id = ${ids.first()}"
//        // for every other id just add another check
//        ids.drop(1).forEach { id ->
//            queryPartDependingOnParameters += " OR id = $id"
//        }
//        transaction {
//            ("SELECT id, name FROM users $queryPartDependingOnParameters;").execAndReturn { rs ->
//                while (rs.next()) {
//                    val user = User(rs.getInt("id"), rs.getString("name"))
//                    users.add(user)
//                }
////                semaphore.release()
//            }
//        }
////        semaphore.acquire()
//        return users
//    }

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

    // TODO: complains about curl -X POST -d 'name=Boss' localhost:8080/users because there is no column boss
//    fun postUsers(name: String) : Int {
//        val ids = arrayListOf<Int>()
//        transaction {
//            "INSERT INTO users (name) VALUES ('$name');".exec()
//            "SELECT id FROM users WHERE name = $name;".execAndReturn { rs ->
//                while (rs.next()) {
//                    val id = rs.getInt("id")
//                    ids.add(id)
//                }
//            }
//        }
//        if (ids.count() > 1) {
//            // TODO: think of something better because if that is the case then we messed up big time; maybe throw execption? but should not be possible anyway
//            return -1
//        }
//        // TODO: remove else if branch, just for debugging purposes
//        else if (ids.count() == 0) {
//            throw RuntimeException("No userId created for the just posted user.")
//        }
//        return ids.first()
//    }

    fun postUsers(inputName: String): Int {
        if(Users.select { Users.name.eq(inputName) }.none()) {
            Users.insert {
                it[name] = inputName
            }
            val resultRow = Users.slice(Users.id).select { Users.name.eq(inputName) }.first()
            return resultRow[Users.id]
        } else {
            throw UserNameAlreadyExistsException()
        }
    }
}