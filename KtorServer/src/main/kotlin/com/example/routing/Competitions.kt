package com.example.routing

import io.ktor.locations.*

@Location("/scores")
data class Scores(
    val userId: Int = -1,
    val exerciseId: Int = -1,
    val timestamp: Int = 0,
    val highscore: Boolean = false
)