package com.example.routing

import io.ktor.locations.*

@Location("/users")
data class Users(val ids: Iterable<Integer>)