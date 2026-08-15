package org.example.globalconsole.di

import org.example.globalconsole.HeroicGames.data.database.LauncherHeroicGamesAdapter
import org.example.globalconsole.HeroicGames.data.repository.HGLauncherRepository
import org.example.globalconsole.HeroicGames.data.repositoryImpl.HGLauncherRepositoryImpl
import org.example.globalconsole.juegosPcsx2.data.database.GameP2FileSystemAdapter
import org.example.globalconsole.juegosPcsx2.data.database.GamePCSX2Adapter
import org.example.globalconsole.juegosPcsx2.data.repository.GameP2Repository
import org.example.globalconsole.juegosPcsx2.data.repositoryImpl.GameP2RepositoryImpl
import org.example.globalconsole.settings.data.SettingsRepositoryImpl
import org.example.globalconsole.settings.domain.SettingsRepository
import org.koin.dsl.module

val dataModule = module {
    // Repositorio de configuración expuesto por su interfaz de dominio
    single<SettingsRepository> { SettingsRepositoryImpl() }

    // El adaptador de archivos obtiene la ruta desde el UseCase de configuración
    single { GameP2FileSystemAdapter(getEmulatorPathUseCase = get()) }
    single { GamePCSX2Adapter() }

    // El repositorio de juegos se expone por su interfaz, inyectando los adaptadores requeridos
    single<GameP2Repository> { GameP2RepositoryImpl(get(), get()) }

    // Adaptador de Heroic Games Launcher para detección y ejecución nativa del proceso
    single { LauncherHeroicGamesAdapter() }

    // Repositorio de Heroic Games Launcher expuesto por su interfaz de dominio
    single<HGLauncherRepository> { HGLauncherRepositoryImpl(adapter = get()) }

    // Adaptadores de Melon DS
    single { org.example.globalconsole.melonDS.data.database.GameMelonDSAdapter() }
    single { org.example.globalconsole.melonDS.data.database.LauncherMelonDSAdapter() }
    single { org.example.globalconsole.melonDS.data.database.GameDSFileSystemAdapter(getEmulatorPathUseCase = get()) }

    // Repositorios de Melon DS
    single<org.example.globalconsole.melonDS.data.repository.GameDSRepository> { 
        org.example.globalconsole.melonDS.data.repositoryImpl.GameDSRepositoryImpl(
            dataSourceFile = get(),
            dataSourceDs = get()
        ) 
    }
    single<org.example.globalconsole.melonDS.data.repository.MelonDSRepository> { 
        org.example.globalconsole.melonDS.data.repositoryImpl.MelonDSRepositoryImpl(
            launcherAdapter = get(),
            gameAdapter = get()
        ) 
    }
}
