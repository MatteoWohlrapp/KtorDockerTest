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

// TODO: make sure that all the queries comply with required/optional parameter conventions defined in swagger.yaml
class Controller {

}

fun main() {
    Database.connect("jdbc:postgresql://ktorserver_db_1:5432/postgres", driver = "org.postgresql.Driver", user ="postgres", password = "mysecretpassword")

    transaction {
        SchemaUtils.drop(Competition_Exercises)
        SchemaUtils.drop(Competitions)
        SchemaUtils.drop(Exercise_Scores)
        SchemaUtils.drop(Users)
        SchemaUtils.create(Users)
        SchemaUtils.create(Exercise_Scores)
        SchemaUtils.create(Competitions)
        SchemaUtils.create(Competition_Exercises)
        insertFirstValues()
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
                        ("INSERT INTO scores (user_id, timestamp, exercise, score) VALUES ($userId, $timestamp, $exerciseId, $score);").exec()
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
            // TODO: delete, will use the approach listOf(ids) -> listOf(names) instead; update swagger!
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
                var queryPartDependingOnParameters = ""
                if (userId != null) {
                    queryPartDependingOnParameters += "WHERE c.user_id_one = $userId OR c.user_id_two = $userId"
                    if(timestamp != null) {
                        queryPartDependingOnParameters += " AND c.creation_timestamp >= $timestamp"
                    }
                }
                else {
                    if(timestamp != null) {
                        queryPartDependingOnParameters += "WHERE c.creation_timestamp >= $timestamp"
                    }
                }
                queryPartDependingOnParameters += ";"
                val query = "SELECT c.id as competition_id," +
                        " c.user_id_one," +
                        " c.user_id_two," +
                        " c.creation_timestamp," +
                        " es.userId," +
                        " es.timestamp," +
                        " es.exercise as exercise_id," +
                        " es.score\n"
                        "FROM competitions c JOIN competition_exercises ce ON c.id = ce.competition_id\n" +
                        "JOIN exercise_scores es ON ce.score_id = es.id\n" +
                        queryPartDependingOnParameters
                transaction {
                    query.execAndReturn { rs ->
                        val competitions = HashMap<Int, Competition>()
                        while(rs.next() != null) {
                            val columnNumber = rs.metaData.columnCount
                            var count = 1
                            while (count < columnNumber) {
                                println("Column at index $count is ${rs.metaData.getColumnName(count++)}")
                            }
                            val competitionId = rs.getInt("competition_id")
                            // only insert the competition
                            if(!competitions.containsKey(competitionId)) {
                                competitions[competitionId] = Competition (
                                    competitionId,
                                    rs.getInt("user_id_one"),
                                    rs.getInt("user_id_two"),
                                    rs.getLong("creation_timestamp"),
                                    arrayListOf()
                                )
                            }
                            val score = Score (
                                rs.getInt("user_id"),
                                rs.getLong("timestamp"),
                                rs.getInt("exercise_id"),
                                rs.getInt("score")
                            )
                            val currentCompetitionScores = competitions[competitionId]!!.scores
                            currentCompetitionScores.add(score)
                            competitions[competitionId] = competitions[competitionId]!!.copy(scores = currentCompetitionScores)
                        }
                        // done with retrieving all the data from the query, convert Map to List
                        val comps : List<Competition> = competitions.toList().map {(_, value) -> value}
                        GlobalScope.launch {
                            call.respondText(Json.encodeToString(comps))
                        }
                    }
                }
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

fun insertFirstValues() {
    "insert into users (name) values ('Max');".exec()
    "insert into users (name) values ('Matteo');".exec()
    "insert into competitions (user_id_one, user_id_two, creation_timestamp) values (1,2,0);".exec()
    "insert into exercise_scores (user_id, timestamp, exercise, score) values (1,0,0,30);".exec()
    "insert into competition_exercises (competition_id, score_id) values (1,1);".exec();
    "insert into exercise_scores (user_id, timestamp, exercise, score) values (2,0,0,1);".exec()
    "insert into competition_exercises (competition_id, score_id) values (1,2);".exec();
    "insert into exercise_scores (user_id, timestamp, exercise, score) values (1,0,1,30);".exec()
    "insert into competition_exercises (competition_id, score_id) values (1,3);".exec();
    "insert into exercise_scores (user_id, timestamp, exercise, score) values (2,0,1,0);".exec()
    "insert into competition_exercises (competition_id, score_id) values (1,4);".exec();
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
