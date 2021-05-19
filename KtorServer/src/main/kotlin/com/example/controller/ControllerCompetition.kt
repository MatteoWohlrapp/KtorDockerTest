package com.example.controller

import com.example.cache.Competition_Exercises
import com.example.cache.Competitions
import com.example.cache.Exercise_Scores
import com.example.domain.exceptions.CompetitionAlreadyActiveException
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
                val competition = getCompetition(competitionsResultRow[Competitions.id])
                if (competition != null )
                    competitions.add(competition)
            }

            return@transaction competitions
        }
    }

    fun getCompetition(competitionId: Int): Competition? {
        return transaction {
            val competition = Competitions.select { Competitions.id.eq(competitionId) }.firstOrNull()
                ?: return@transaction null
            val scores = mutableListOf<Score>()
            Competition_Exercises.join(Exercise_Scores, JoinType.INNER, additionalConstraint = {
                Competition_Exercises.scoreId eq Exercise_Scores.id
            }).select { Competition_Exercises.competitionId.eq(competition!![Competitions.id]) }
                .forEach { scoreResultRow ->
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


    fun postCompetitions(inputUserIdOne: Int, inputUserIdTwo: Int): Competition {
        return transaction {
            // get unix timestamp
            val timestamp = System.currentTimeMillis() / 1000L;

            val openCompetitions =
                Competitions.select {
                    (Competitions.userIdOne.eq(inputUserIdOne) and Competitions.userIdTwo.eq(
                        inputUserIdTwo
                    )) or (Competitions.userIdOne.eq(inputUserIdTwo) and Competitions.userIdTwo.eq(inputUserIdOne))
                }
                    .any { resultRow ->
                        val competition = getCompetition(resultRow[Competitions.id])
                        if (competition != null) {
                            if (competition.scores.size % 2 != 0)
                                true

                            var scoreOne = 0
                            var scoreTwo = 0

                            for (scorePlayerOne in competition.scores) {
                                if (scorePlayerOne.userId == inputUserIdOne) {
                                    val scorePlayerTwo = competition.scores.find { score ->
                                        score.exerciseId == scorePlayerOne.exerciseId && score.userId != scorePlayerOne.exerciseId
                                    }

                                    if (scorePlayerOne.score > scorePlayerTwo!!.score)
                                        scoreOne++
                                    else
                                        scoreTwo++
                                }
                            }

                            // one player has to win at least 2 rounds to win.
                            (scoreOne <= 1 && scoreTwo <= 1)
                        }
                        false
                    }

            if (openCompetitions)
                throw CompetitionAlreadyActiveException()

            Competitions.insert {
                it[userIdOne] = inputUserIdOne
                it[userIdTwo] = inputUserIdTwo
                it[creationTimestamp] = timestamp
            }
            return@transaction getCompetitions(inputUserIdOne, timestamp).first()
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