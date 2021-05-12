package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Competition (
    private val id: Int,
    private val userIdOne: Int,
    private val userIdTwo: Int,
    private val timestamp: Long,
    val scores: List<Score>
    )