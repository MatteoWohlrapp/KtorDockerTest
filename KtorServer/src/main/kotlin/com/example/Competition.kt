package com.example

import kotlinx.serialization.Serializable
import java.util.ArrayList

@Serializable
data class Competition (private val id: Int, private val userIdOne: Int, private val userIdTwo: Int, private val scores: ArrayList<Score>)