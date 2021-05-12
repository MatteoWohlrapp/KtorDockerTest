package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Score (
    val userId: Int,
    val exerciseId: Int,
    val timestamp: Long,
    val score: Int
    )