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
            showHGLauncherUseCase = get(),
            executeGameMelonDSUseCase = get(),
            executeLauncherMelonDSUseCase = get(),
            findMelonDSLauncherUseCase = get(),
            showMelonDSLauncherUseCase = get(),
            getGamesDSUseCase = get(),
            closeGameP2UseCase = get(),
            closeGameDSUseCase = get(),
            closeLauncherMelonDSUseCase = get(),
            closeHGLauncherUseCase = get(),
            getGamesDolphinUseCase = get(),
            executeGameDolphinUseCase = get(),
            closeGameDolphinUseCase = get(),
            executeLauncherDolphinUseCase = get(),
            closeLauncherDolphinUseCase = get(),
            findDolphinLauncherUseCase = get(),
            showDolphinLauncherUseCase = get(),
            executePS3LauncherUseCase = get(),
            findPS3LauncherUseCase = get(),
            showPS3LauncherUseCase = get(),
            closePS3LauncherUseCase = get()
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
            saveMouseSensitivityUseCase = get(),
            findMelonDSLauncherUseCase = get(),
            enableMelonDSLauncherUseCase = get(),
            hideMelonDSLauncherUseCase = get(),
            findDolphinLauncherUseCase = get(),
            enableDolphinLauncherUseCase = get(),
            hideDolphinLauncherUseCase = get(),
            findPS3LauncherUseCase = get(),
            enablePS3LauncherUseCase = get(),
            hidePS3LauncherUseCase = get()
        )
    }
}
