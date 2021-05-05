package com.example.routing

import io.ktor.locations.*

@Location("/scores") data class Scores(val ids: Iterable<Integer>)