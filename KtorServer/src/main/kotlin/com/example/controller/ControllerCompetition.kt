package com.example.controller

import com.example.model.Competition
import com.example.model.Score
import com.example.paths.CompetitionsPath
import io.ktor.application.*

class ControllerCompetition {

    fun getCompetitions(userId: Int, timestamp: Long) : List<Competition> {
        // TODO("not implemented yet")
        return emptyList()
    }

    fun getCompetition(competitionId: Int) : Competition {
        // TODO("not implemented yet")
        return Competition(0,0,0,0, emptyList())
    }

    fun postCompetitions(userIdOne : Int, userIdTwo: Int) : Boolean {
        // TODO("not implemented yet")
        return false
    }

    fun putCompetitionScore(score: Score): Boolean{
        // TODO("not implemented yet")
        return false
    }

}