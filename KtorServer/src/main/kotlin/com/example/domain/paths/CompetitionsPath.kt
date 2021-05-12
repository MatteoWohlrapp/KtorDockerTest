package com.example.domain.paths

import io.ktor.locations.*

@Location("/competitions")
data class CompetitionsPath(
    val userId: Int = -1,
    val timestamp: Long = 0,
) {
    @Location("/{id}")
    data class CompetitionId(private val parent: CompetitionsPath, val id: Int = -1)
}