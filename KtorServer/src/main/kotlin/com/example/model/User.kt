package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class User (
    private val id: Int,
    private val name: String
    )