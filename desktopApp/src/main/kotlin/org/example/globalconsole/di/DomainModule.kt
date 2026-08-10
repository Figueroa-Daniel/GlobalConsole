package org.example.globalconsole.di

import org.example.globalconsole.juegosPcsx2.domain.usecase.DeleteGameP2UseCase
import org.example.globalconsole.juegosPcsx2.domain.usecase.ExecuteGameP2UseCase
import org.example.globalconsole.juegosPcsx2.domain.usecase.GetGamesP2UseCase
import org.example.globalconsole.settings.domain.usecase.GetEmulatorPathUseCase
import org.example.globalconsole.settings.domain.usecase.SaveEmulatorPathUseCase
import org.koin.dsl.module

val domainModule = module {
    // UseCases de configuración
    factory { GetEmulatorPathUseCase(repository = get()) }
    factory { SaveEmulatorPathUseCase(repository = get()) }

    // UseCases de PCSX2
    factory { GetGamesP2UseCase(repository = get()) }
    factory { ExecuteGameP2UseCase(repository = get()) }
    factory { DeleteGameP2UseCase(repository = get()) }
}
