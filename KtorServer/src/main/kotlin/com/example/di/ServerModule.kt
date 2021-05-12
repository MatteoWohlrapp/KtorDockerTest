package com.example.di

import com.example.controller.ControllerCompetition
import com.example.controller.ControllerScore
import com.example.controller.ControllerUser
import com.example.handler.HandlerCompetition
import com.example.handler.HandlerScore
import com.example.handler.HandlerUser
import org.koin.dsl.module

val serverModule = module {
    single { HandlerScore(get()) }

    single { HandlerUser(get()) }

    single { HandlerCompetition(get()) }

    single { ControllerScore() }

    single { ControllerUser() }

    single { ControllerCompetition() }
}