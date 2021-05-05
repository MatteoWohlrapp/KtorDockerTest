package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class CompetitionScore (
    val competitionId: Int,
    val userId: Int,
    val exerciseId: Int,
    val timestamp: Long,
    val score: Int
    )