package org.example.globalconsole.di

import org.example.globalconsole.presesentation.viewModel.home.HomeViewModel
import org.example.globalconsole.presesentation.viewModel.settings.SettingsViewModel
import org.koin.dsl.module

val presentationModule = module {
    factory {
        HomeViewModel(
            getGamesP2UseCase = get(),
            executeGameP2UseCase = get(),
            deleteGameP2UseCase = get()
        )
    }
    factory {
        SettingsViewModel(
            saveEmulatorPathUseCase = get(),
            getEmulatorPathUseCase = get()
        )
    }
}
