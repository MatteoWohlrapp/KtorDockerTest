package com.example.paths

import io.ktor.locations.*

@Location("/users")
data class UsersPath(val ids: Iterable<Int>)