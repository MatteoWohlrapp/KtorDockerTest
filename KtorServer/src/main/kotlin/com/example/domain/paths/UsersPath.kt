package com.example.domain.paths

import io.ktor.locations.*

@Location("/users")
data class UsersPath(val ids: Iterable<Int>)