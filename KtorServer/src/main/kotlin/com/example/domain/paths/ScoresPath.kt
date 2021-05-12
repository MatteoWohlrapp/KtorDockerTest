package com.example.domain.paths

import io.ktor.locations.*

@Location("/scores")
data class ScoresPath(
    val userIds: Iterable<Int> = emptyList(),
    val exerciseId: Int = -1,
    val timestamp: Long = 0,
    val highscore: Boolean = true
)