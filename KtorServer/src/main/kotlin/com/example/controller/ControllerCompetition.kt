package com.example.controller

import com.example.cache.Competition_Exercises
import com.example.cache.Competitions
import com.example.cache.Exercise_Scores
import com.example.domain.model.Competition
import com.example.domain.model.CompetitionScore
import com.example.domain.model.Score

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class ControllerCompetition {

    fun getCompetitions(userId: Int, timestamp: Long): List<Competition> {
        return transaction {
            val competitions = mutableListOf<Competition>()

            Competitions.select {
                (Competitions.userIdOne.eq(userId) or Competitions.userIdTwo.eq(userId)) and Competitions.creationTimestamp.greater(
                    timestamp
                )
            }.forEach { competitionsResultRow ->
                competitions.add(getCompetition(competitionsResultRow[Competitions.id]))
            }

            return@transaction competitions
        }
    }

    fun getCompetition(competitionId: Int): Competition {
        //TODO what happens when competition does not exist -> Exception
       return transaction {
            val competition = Competitions.select { Competitions.id.eq(competitionId) }.firstOrNull()

            val scores = mutableListOf<Score>()
            Competition_Exercises.join(Exercise_Scores, JoinType.INNER, additionalConstraint = {
                Competition_Exercises.scoreId eq Exercise_Scores.id
            }).select { Competition_Exercises.competitionId.eq(competition!![Competitions.id]) }.forEach { scoreResultRow ->
                scores.add(
                    Score(
                        scoreResultRow[Exercise_Scores.id],
                        scoreResultRow[Exercise_Scores.exerciseId],
                        scoreResultRow[Exercise_Scores.timestamp],
                        scoreResultRow[Exercise_Scores.score]
                    )
                )
            }
            return@transaction Competition(
                competition!![Competitions.id],
                competition!![Competitions.userIdOne],
                competition!![Competitions.userIdTwo],
                competition!![Competitions.creationTimestamp],
                scores
            )
        }
    }


    //TODO("check return type -> return id")
    // TODO("what happens when already a competition -> exception")
    fun postCompetitions(inputUserIdOne: Int, inputUserIdTwo: Int) {
        transaction {
            val timestamp = LocalDateTime.now().second

            Competitions.insert {
                it[userIdOne] = inputUserIdOne
                it[userIdTwo] = inputUserIdTwo
                it[creationTimestamp] = timestamp.toLong()
            }
        }

    }

    fun putCompetitionScore(competitionScore: CompetitionScore) {
        transaction {
            Exercise_Scores.insert {
                it[userId] = competitionScore.userId
                it[timestamp] = competitionScore.timestamp
                it[exerciseId] = competitionScore.exerciseId
                it[score] = competitionScore.score
            }

            val createdScoreId = Exercise_Scores.select {
                Exercise_Scores.userId.eq(competitionScore.userId)
                Exercise_Scores.timestamp.eq(competitionScore.timestamp)
                Exercise_Scores.exerciseId.eq(competitionScore.exerciseId)
                Exercise_Scores.score.eq(competitionScore.score)
            }.first()[Exercise_Scores.id]



            Competition_Exercises.insert {
                it[competitionId] = competitionScore.competitionId
                it[scoreId] = createdScoreId
            }
        }
    }



}