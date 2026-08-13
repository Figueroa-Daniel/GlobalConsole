package org.example.globalconsole.di

import org.example.globalconsole.HeroicGames.domain.usecase.EnableHGLauncherUseCase
import org.example.globalconsole.HeroicGames.domain.usecase.ExecuteHGLauncherUseCase
import org.example.globalconsole.HeroicGames.domain.usecase.FindHGLauncherUseCase
import org.example.globalconsole.HeroicGames.domain.usecase.HideHGLauncherUseCase
import org.example.globalconsole.HeroicGames.domain.usecase.ShowHGLauncherUseCase
import org.example.globalconsole.juegosPcsx2.domain.usecase.DeleteGameP2UseCase
import org.example.globalconsole.juegosPcsx2.domain.usecase.ExecuteGameP2UseCase
import org.example.globalconsole.juegosPcsx2.domain.usecase.GetGamesP2UseCase
import org.example.globalconsole.settings.domain.usecase.GetEmulatorPathUseCase
import org.example.globalconsole.settings.domain.usecase.GetMouseSensitivityUseCase
import org.example.globalconsole.settings.domain.usecase.SaveEmulatorPathUseCase
import org.example.globalconsole.settings.domain.usecase.SaveMouseSensitivityUseCase
import org.koin.dsl.module

val domainModule = module {
    // UseCases de configuración (rutas de emuladores)
    factory { GetEmulatorPathUseCase(repository = get()) }
    factory { SaveEmulatorPathUseCase(repository = get()) }
    factory { GetMouseSensitivityUseCase(repository = get()) }
    factory { SaveMouseSensitivityUseCase(repository = get()) }

    // UseCases de PCSX2
    factory { GetGamesP2UseCase(repository = get()) }
    factory { ExecuteGameP2UseCase(repository = get()) }
    factory { DeleteGameP2UseCase(repository = get()) }

    // UseCases de Heroic Games Launcher — ejecución
    factory { ExecuteHGLauncherUseCase(adapter = get()) }

    // UseCase de Heroic Games Launcher — obtener datos del launcher como entidad de dominio
    factory { ShowHGLauncherUseCase(repository = get()) }

    // UseCases de Heroic Games Launcher — gestión de visibilidad en biblioteca
    factory { FindHGLauncherUseCase(repository = get()) }
    factory { EnableHGLauncherUseCase(repository = get()) }
    factory { HideHGLauncherUseCase(repository = get()) }
}
