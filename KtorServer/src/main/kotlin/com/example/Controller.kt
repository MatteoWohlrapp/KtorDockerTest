package com.example

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.*


class Controller {

}

fun main() {
    Database.connect("jdbc:postgresql://localhost:5432/postgres", driver = "org.postgresql.Driver", user ="postgres", password = "mysecretpassword")

    transaction {
        SchemaUtils.drop(CompetitionExercises)
        SchemaUtils.drop(Competitions)
        SchemaUtils.drop(ExerciseScores)
        SchemaUtils.drop(Users)
        SchemaUtils.create(Users)
        SchemaUtils.create(ExerciseScores)
        SchemaUtils.create(Competitions)
        SchemaUtils.create(CompetitionExercises)
    }

    embeddedServer(Netty, port = 8000) {
        routing {
            get("/scores") {
                // TODO: how do we handle 16 possible parameter combinations? restrict API? what about optional parameters?

            }
            post("/scores") {
                val parameters = call.receiveParameters()
                val userId = parameters["userId"]
                val timestamp = parameters["timestamp"]
                val exerciseId = parameters["exerciseId"]
                val score = parameters["score"]
                if(listOf(userId, timestamp, exerciseId, score).none{parameter -> parameter == null}) {
                    transaction {
                        ("INSERT INTO scores (userId, timestamp, exercise, score) VALUES ($userId, $timestamp, $exerciseId, $score);").exec()
                    }
                }
                else {
                    // return some error to handler
                }


            }
            get("/users") {
                val closure : (ResultSet) -> Unit = { rs ->
                    val users = ArrayList<User>()
                    while(rs.next()) {
                        val user = User(rs.getInt("id"), rs.getString("name"))
                        users.add(user)
                    }
                    GlobalScope.launch {
                        call.respondText(Json.encodeToString(users))
                    }
                }
                transaction {
                    // TODO: think of a different response when nothing comes back? or probably just pass empty array to handler and handler has to think of something
                    ("SELECT id, name FROM users;").execAndReturn(closure)
                }
            }
            post("/users") {
                val userName = call.receiveParameters()["name"]
                if (userName != null) {
                    transaction {
                        "INSERT INTO users (name) VALUES ('$userName');".exec()
                    }
                }
            }
            get("/users/{id}") {
                val id : String? = call.parameters["id"]
                val closure : (ResultSet) -> Unit = { rs ->
                    val users = ArrayList<User>()
                    while(rs.next()) {
                        val user = User(rs.getInt("id"), rs.getString("name"))
                        users.add(user)
                    }
                    GlobalScope.launch {
                        call.respondText(Json.encodeToString(users))
                    }
                }
                if(id != null) {
                    transaction {
                        ("SELECT id, name FROM users WHERE id = $id;").execAndReturn(closure)
                    }
                }
                else {
                    // return an error back to the handler
                }
            }
            get("/competitions") {
                val parameters = call.request.queryParameters
                val userId = parameters["userId"]
                val timestamp = parameters["timestamp"]
                // TODO
            }
            post("/competitions") {
                // TODO
            }
            get("/competitions/{id}") {
                // TODO
            }
        }
    }.start(wait = true)

}

//object Users: Table() {
//    val id = integer("id").autoIncrement()
//    val name = varchar("name", 255)
//    override val primaryKey = PrimaryKey(id)
//}
object ExerciseScores: Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("userId").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val timestamp = long("timestamp")
    val exercise = integer("exercise")
    val score = integer("score")
    override val primaryKey = PrimaryKey(id)
}
object Competitions: Table() {
    val id = integer("id").autoIncrement()
    val userIdOne = integer("userIdOne").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val userIdTwo = integer("userIdTwo").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val timestamp = long("timestamp")
    override val primaryKey = PrimaryKey(id)
}
object CompetitionExercises: Table() {
    val competitionId = integer("competitionId").references(Competitions.id, onDelete = ReferenceOption.CASCADE)
    val scoreId = integer("scoreId").references(ExerciseScores.id, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(competitionId, scoreId)
}




// taken from https://github.com/JetBrains/Exposed/issues/118
//fun <T:Any> String.execAndMap(transform : (ResultSet) -> T) : List<T> {
//    val result = arrayListOf<T>()
//    TransactionManager.current().exec(this) { rs ->
//        while (rs.next()) {
//            result += transform(rs)
//        }
//    }
//    return result
//}

// useful for queries that expect an answer
fun String.execAndReturn(closure: (ResultSet) -> Unit) {
    TransactionManager.current().exec(this) { rs ->
        closure(rs)
    }
}

// just executes sql query as a string, useful for queries that do not expect an answer
fun String.exec() {
    TransactionManager.current().exec(this)
}

// might protect a little more against sql injection attacks?
//fun Transaction.exec(sql: String, body: PreparedStatement.() -> Unit) : ResultSet? {
//    return connection.prepareStatement(sql).apply(body).run {
//        if (sql.toLowerCase().startsWith("select "))
//            sql.execAndMap { rs ->
//                // TODO
//                rs.getString(...)
//            }
//        else {
//            null
//        }
//    }
//}

//                val query = Users.selectAll()
//                val builder = StringBuilder()
//                query.forEach {
//                    builder.append("$it\n")
//                }
//                GlobalScope.launch {
//                    call.respondText("Names:\n$builder")
//                }
