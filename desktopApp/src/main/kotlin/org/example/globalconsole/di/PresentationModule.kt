package org.example.globalconsole.di

import org.example.globalconsole.presesentation.viewModel.home.HomeViewModel
import org.koin.dsl.module

val presentationModule = module {
    factory { 
        HomeViewModel(
            getGamesP2UseCase = get(),
            executeGameP2UseCase = get(),
            deleteGameP2UseCase = get()
        )
    }
}
