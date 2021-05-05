package com.example.controller

import com.example.model.Competition
import com.example.model.CompetitionScore
import com.example.model.Score
import com.example.paths.CompetitionsPath
import io.ktor.application.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction

class ControllerCompetition {

    fun getCompetitions(userId: Int, timestamp: Long): List<Competition> {
//        var queryPartDependingOnParameters = ""
//        if (userId != null) {
//            queryPartDependingOnParameters += "WHERE c.user_id_one = $userId OR c.user_id_two = $userId"
//            if (timestamp != null) {
//                queryPartDependingOnParameters += " AND c.creation_timestamp >= $timestamp"
//            }
//        } else {
//            if (timestamp != null) {
//                queryPartDependingOnParameters += "WHERE c.creation_timestamp >= $timestamp"
//            }
//        }
//        queryPartDependingOnParameters += ";"
//        val query = "SELECT c.id as competition_id," +
//                " c.user_id_one," +
//                " c.user_id_two," +
//                " c.creation_timestamp," +
//                " es.userId," +
//                " es.timestamp," +
//                " es.exercise as exercise_id," +
//                " es.score\n"
//        "FROM competitions c JOIN competition_exercises ce ON c.id = ce.competition_id\n" +
//                "JOIN exercise_scores es ON ce.score_id = es.id\n" +
//                queryPartDependingOnParameters
//        transaction {
//            query.execAndReturn { rs ->
//                val competitions = HashMap<Int, Competition>()
//                while (rs.next() != null) {
//                    val columnNumber = rs.metaData.columnCount
//                    var count = 1
//                    while (count < columnNumber) {
//                        println("Column at index $count is ${rs.metaData.getColumnName(count++)}")
//                    }
//                    val competitionId = rs.getInt("competition_id")
//                    // only insert the competition
//                    if (!competitions.containsKey(competitionId)) {
//                        competitions[competitionId] = Competition(
//                            competitionId,
//                            rs.getInt("user_id_one"),
//                            rs.getInt("user_id_two"),
//                            rs.getLong("creation_timestamp"),
//                            arrayListOf()
//                        )
//                    }
//                    val score = Score(
//                        rs.getInt("user_id"),
//                        rs.getLong("timestamp"),
//                        rs.getInt("exercise_id"),
//                        rs.getInt("score")
//                    )
//                    val currentCompetitionScores = competitions[competitionId]!!.scores
//                    currentCompetitionScores.add(score)
//                    competitions[competitionId] = competitions[competitionId]!!.copy(scores = currentCompetitionScores)
//                }
//                // done with retrieving all the data from the query, convert Map to List
//                val comps: List<Competition> = competitions.toList().map { (_, value) -> value }
//                GlobalScope.launch {
//                    call.respondText(Json.encodeToString(comps))
//                }
//            }
//            return emptyList()
//        }
//    }
       return emptyList()
    }

    fun getCompetition(competitionId: Int): Competition {
        // TODO("not implemented yet")
        return Competition(0, 0, 0, 0, emptyList())
    }

    fun postCompetitions(userIdOne: Int, userIdTwo: Int): Boolean {
        // TODO("not implemented yet")
        return false
    }

    fun putCompetitionScore(score: CompetitionScore): Boolean {
        // TODO("not implemented yet")
        return false
    }

}