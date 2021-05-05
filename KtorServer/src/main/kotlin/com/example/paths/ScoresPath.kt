package com.example.paths

import io.ktor.locations.*

@Location("/scores")
data class ScoresPath(
    val userId: Int = -1,
    val exerciseId: Int = -1,
    val timestamp: Int = 0,
    val highscore: Boolean = false
)