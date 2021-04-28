package com.example

import io.ktor.application.*
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
            post("/users") {
                val userName = call.receiveParameters()["name"]
                if (userName != null) {
                    transaction {
//                        Users.insert {
//                            it[name] = userName
//                        }
                        "INSERT INTO users (name) VALUES ('$userName');".exec()
                    }
                }
            }
            get("/users") {
                transaction {
//                    val query = Users.selectAll()
//                    val builder = StringBuilder()
//                    query.forEach {
//                        builder.append("$it\n")
//                    }
//                    GlobalScope.launch {
//                        call.respondText("Names:\n$builder")
//                    }
                    ("SELECT id, name FROM users;").execAndReturn { rs ->
                        val users = ArrayList<User>()
                        while(rs.next()) {
                            val user = User(rs.getInt("id"), rs.getString("name"))
                            users.add(user)
                        }
                        GlobalScope.launch {
                            call.respondText(Json.encodeToString(users))
                        }
                    }
                }
            }
            get("/") {
                call.respondText("Gradle sucks!")
            }
            get("/boobs"){
                call.respondText("Nice!")
            }
            get("/names") {
                transaction {
                    SchemaUtils.create(Users)
                    transaction {
                        Users.insert {
                            it[name] = "Matteo"
                        }
                        SchemaUtils.create(Users)
                        val query = Users.selectAll()
                        val builder = StringBuilder()
                        query.forEach {
                            builder.append("$it, ")
                        }

                        GlobalScope.launch {
                            call.respondText("Names: $builder")
                        }
                    }
                }
            }
            get("/highscores"){

            }
            post("/register"){
                val userId = call.receiveParameters()["userId"]
                transaction {

                }
                call.respondText("User is: $userId")
            }
            post("/score"){
                val userId = call.receiveParameters()["userId"]
                val score = call.receiveParameters()["score"]
                val exercise = call.receiveParameters()["exerciseName"]
                call.respondText("User: $userId, Score: $score, Exercise: $exercise")
            }
        }
    }.start(wait = true)

}

object Users: Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)
}
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


// API classes
@Serializable
data class User (private val id: Int, private val name: String)
@Serializable
data class Score (private val userId: Int, private val timestamp: Long, private val exerciseId: Int, private val score: Int)
@Serializable
data class Competition (private val id: Int, private val userIdOne: Int, private val userIdTwo: Int, private val scores: ArrayList<Score>)



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


