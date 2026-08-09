package org.example.globalconsole.di

import org.example.globalconsole.juegosPcsx2.domain.usecase.DeleteGameP2UseCase
import org.example.globalconsole.juegosPcsx2.domain.usecase.ExecuteGameP2UseCase
import org.example.globalconsole.juegosPcsx2.domain.usecase.GetGamesP2UseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetGamesP2UseCase(repository = get()) }
    factory { ExecuteGameP2UseCase(repository = get()) }
    factory { DeleteGameP2UseCase(repository = get()) }
}
