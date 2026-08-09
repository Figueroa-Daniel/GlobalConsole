package org.example.globalconsole.di

import org.example.globalconsole.juegosPcsx2.data.database.GameP2FileSystemAdapter
import org.example.globalconsole.juegosPcsx2.data.database.GamePCSX2Adapter
import org.example.globalconsole.juegosPcsx2.data.repository.GameP2Repository
import org.example.globalconsole.juegosPcsx2.data.repositoryImpl.GameP2RepositoryImpl
import org.koin.dsl.module

val dataModule = module {
    single { GameP2FileSystemAdapter() }
    single { GamePCSX2Adapter() }
    
    // El repositorio se expone por su interfaz, inyectando los adaptadores requeridos
    single<GameP2Repository> { GameP2RepositoryImpl(get(), get()) }
}
