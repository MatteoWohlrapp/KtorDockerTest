package com.example.controller

import com.example.cache.Competition_Exercises
import com.example.cache.Competitions
import com.example.cache.Exercise_Scores
import com.example.domain.exceptions.NoSuchCompetitionException
import com.example.domain.model.Competition
import com.example.domain.model.CompetitionScore
import com.example.domain.model.Score

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ControllerCompetition {

    fun getCompetitions(userId: Int, timestamp: Long): List<Competition> {
        return transaction {
            val competitions = mutableListOf<Competition>()
            Competitions.select {
                (Competitions.userIdOne.eq(userId) or Competitions.userIdTwo.eq(userId)) and Competitions.creationTimestamp.greaterEq(
                    timestamp
                )
            }.forEach { competitionsResultRow ->
                competitions.add(getCompetition(competitionsResultRow[Competitions.id]))
            }

            return@transaction competitions
        }
    }

    fun getCompetition(competitionId: Int): Competition {
       return transaction {
            val competition = Competitions.select { Competitions.id.eq(competitionId) }.firstOrNull()
                ?: throw NoSuchCompetitionException()
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


    // TODO("what happens when already a competition -> exception (currently we just accept, see below)")
    fun postCompetitions(inputUserIdOne: Int, inputUserIdTwo: Int) : Int {
        return transaction {
            // get unix timestamp
            val timestamp = System.currentTimeMillis() / 1000L;
            // TODO: discuss again, might be way to expensive because it requires finding out if a challenge is still running and we have no easy way to find out
//            // check if competition between those two users already exists
//            val competitionsUserOne = getCompetitions(inputUserIdOne, 0)
//            if(competitionsUserOne.any { competition ->
//                    if(competition.userIdOne == inputUserIdOne) {
//                        competition.userIdTwo == inputUserIdTwo
//                    }
//                    else competition.userIdOne == inputUserIdTwo
//                }) {
//                // a competition between those two users already exists; does not really help because it might be an old one which is alright
//                // ...
//            }
            Competitions.insert {
                it[userIdOne] = inputUserIdOne
                it[userIdTwo] = inputUserIdTwo
                it[creationTimestamp] = timestamp
            }
            val newCompetition = getCompetitions(inputUserIdOne, timestamp).first()
            return@transaction newCompetition.id
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