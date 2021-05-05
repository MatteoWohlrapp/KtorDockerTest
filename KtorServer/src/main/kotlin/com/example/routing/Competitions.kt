package com.example.routing

import io.ktor.locations.*

@Location("/competitions")
data class Competitions(
    val userId: Int = -1,
    val timestamp: Int = 0,
) {
    @Location("/{id}")
    data class CompetitionId(private val parent: Competitions, val id: Int = -1)
}