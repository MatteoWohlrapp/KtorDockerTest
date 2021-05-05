package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Score (
    private val userId: Int,
    private val timestamp: Long,
    private val exerciseId: Int,
    private val score: Int
    )