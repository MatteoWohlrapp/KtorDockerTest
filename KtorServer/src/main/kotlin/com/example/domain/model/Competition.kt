package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Competition (
    val id: Int,
    val userIdOne: Int,
    val userIdTwo: Int,
    private val timestamp: Long,
    val scores: List<Score>
    )