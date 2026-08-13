package org.example.globalconsole.di

import org.example.globalconsole.presesentation.viewModel.home.HomeViewModel
import org.example.globalconsole.presesentation.viewModel.settings.SettingsViewModel
import org.koin.dsl.module

/**
 * Módulo de inyección de dependencias de la capa de presentación.
 * Provee los ViewModels de la aplicación con todas sus dependencias inyectadas por Koin.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-10
 */
val presentationModule = module {
    factory {
        HomeViewModel(
            getGamesP2UseCase = get(),
            executeGameP2UseCase = get(),
            deleteGameP2UseCase = get(),
            executeHGLauncherUseCase = get(),
            findHGLauncherUseCase = get(),
            showHGLauncherUseCase = get()
        )
    }
    factory {
        SettingsViewModel(
            saveEmulatorPathUseCase = get(),
            getEmulatorPathUseCase = get(),
            findHGLauncherUseCase = get(),
            enableHGLauncherUseCase = get(),
            hideHGLauncherUseCase = get(),
            getMouseSensitivityUseCase = get(),
            saveMouseSensitivityUseCase = get()
        )
    }
}
