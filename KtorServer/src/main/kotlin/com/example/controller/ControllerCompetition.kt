package com.example.controller

import com.example.cache.Competition_Exercises
import com.example.cache.Competitions
import com.example.cache.Exercise_Scores
import com.example.cache.execAndReturn
import com.example.domain.model.Competition
import com.example.domain.model.CompetitionScore
import com.example.domain.model.Score

import io.ktor.application.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ControllerCompetition {

    fun getCompetitions(userId: Int, timestamp: Long): List<Competition> {
        var comps: List<Competition> = arrayListOf()
        val query = "SELECT c.id AS competition_id," +
                " c.user_id_one," +
                " c.user_id_two," +
                " c.creation_timestamp," +
                " es.user_id," +
                " es.timestamp," +
                " es.exercise as exercise_id," +
                " es.score\n" +
        "FROM competitions AS c JOIN competition_exercises AS ce ON c.id = ce.competition_id\n" +
                "JOIN exercise_scores AS es ON ce.score_id = es.id\n" +
                "WHERE c.user_id_one = $userId OR c.user_id_two = $userId AND c.creation_timestamp > $timestamp"
        transaction {
            query.execAndReturn { rs ->
                val competitions = HashMap<Int, Competition>()
                while (rs.next()) {
                    val columnNumber = rs.metaData.columnCount
                    var count = 1
                    while (count <= columnNumber) {
                        println("Column at index $count is ${rs.metaData.getColumnName(count++)}")
                    }
                    val competitionId = rs.getInt("competition_id")
                    // only insert the competition if it's not already in the HashMap
                    if (!competitions.containsKey(competitionId)) {
                        competitions[competitionId] = Competition(
                            competitionId,
                            rs.getInt("user_id_one"),
                            rs.getInt("user_id_two"),
                            rs.getLong("creation_timestamp"),
                            arrayListOf()
                        )
                    }
                    val score = Score(
                        rs.getInt("user_id"),
                        rs.getInt("exercise_id"),
                        rs.getLong("timestamp"),
                        rs.getInt("score")
                    )
                    val currentCompetitionScores = competitions[competitionId]!!.scores as ArrayList<Score>
                    currentCompetitionScores.add(score)
                    competitions[competitionId] = competitions[competitionId]!!.copy(scores = currentCompetitionScores)
                }
                // done with retrieving all the data from the query, convert Map to List
                comps = competitions.toList().map { (_, value) -> value }
            }
        }
        return comps
    }

//    fun getCompetitions(userId: Int, timestamp: Long): List<Competition> {
//        var comps: List<Competition> = arrayListOf()
//        val query = "SELECT c.id AS competition_id," +
//                " c.user_id_one," +
//                " c.user_id_two," +
//                " c.creation_timestamp," +
//                " es.user_id," +
//                " es.timestamp," +
//                " es.exercise as exercise_id," +
//                " es.score\n" +
//                "FROM competitions AS c JOIN competition_exercises AS ce ON c.id = ce.competition_id\n" +
//                "JOIN exercise_scores AS es ON ce.score_id = es.id\n" +
//                "WHERE c.user_id_one = $userId OR c.user_id_two = $userId AND c.creation_timestamp > $timestamp"
//        transaction {
//            query.execAndReturn { rs ->
//                val competitions = HashMap<Int, Competition>()
//                while (rs.next()) {
//                    val columnNumber = rs.metaData.columnCount
//                    var count = 1
//                    while (count <= columnNumber) {
//                        println("Column at index $count is ${rs.metaData.getColumnName(count++)}")
//                    }
//                    val competitionId = rs.getInt("competition_id")
//                    // only insert the competition if it's not already in the HashMap
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
//                        rs.getInt("exercise_id"),
//                        rs.getLong("timestamp"),
//                        rs.getInt("score")
//                    )
//                    val currentCompetitionScores = competitions[competitionId]!!.scores as ArrayList<Score>
//                    currentCompetitionScores.add(score)
//                    competitions[competitionId] = competitions[competitionId]!!.copy(scores = currentCompetitionScores)
//                }
//                // done with retrieving all the data from the query, convert Map to List
//                comps = competitions.toList().map { (_, value) -> value }
//            }
//        }
//        val competitions = mutableListOf<Competitions>()
//
//        (Competitions.join(Competition_Exercises, JoinType.INNER, additionalConstraint = {
//            Competitions.id eq Competition_Exercises.competitionId
//            (Competitions.userIdOne eq userId) or (Competitions.userIdTwo eq userId)
//            Competitions.creationTimestamp greater timestamp
//        })).join(Exercise_Scores, JoinType.INNER, additionalConstraint = {
//            Competition_Exercises.scoreId eq Exercise_Scores.id
//        }).selectAll().forEach {
//
//        }
//
//        (Competitions.join(Competition_Exercises, JoinType.INNER, additionalConstraint = {
//            Competitions.id eq Competition_Exercises.competitionId
//            (Competitions.userIdOne eq userId) or (Competitions.userIdTwo eq userId)
//            Competitions.creationTimestamp greater timestamp
//        }))
//
//
//
//
//        return comps
//    }

    fun getCompetition(competitionId: Int): Competition {
        // TODO("not implemented yet")
        return Competition(0, 0, 0, 0, emptyList())
    }

    fun postCompetitions(userIdOne: Int, userIdTwo: Int) {
        // TODO("not implemented yet")
    }

    fun putCompetitionScore(score: CompetitionScore) {
        // TODO("not implemented yet")
    }

}