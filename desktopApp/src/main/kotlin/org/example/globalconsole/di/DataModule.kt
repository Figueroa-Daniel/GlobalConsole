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

    // Adaptadores de Dolphin
    single { org.example.globalconsole.dolphin.data.database.GameDolphinAdapter() }
    single { org.example.globalconsole.dolphin.data.database.LauncherDolphinAdapter() }
    single { org.example.globalconsole.dolphin.data.database.GameDolphinFileSystemAdapter(getEmulatorPathUseCase = get()) }

    // Repositorios de Dolphin
    single<org.example.globalconsole.dolphin.data.repository.GameDolphinRepository> {
        org.example.globalconsole.dolphin.data.repositoryImpl.GameDolphinRepositoryImpl(
            dataSourceFile = get(),
            dataSourceDolphin = get()
        )
    }
    single<org.example.globalconsole.dolphin.data.repository.DolphinRepository> {
        org.example.globalconsole.dolphin.data.repositoryImpl.DolphinRepositoryImpl(
            launcherAdapter = get(),
            gameAdapter = get()
        )
    }

    // Adaptador de PS3 Launcher
    single { org.example.globalconsole.PS3Launcher.data.database.LauncherPS3Adapter() }

    // Repositorio de PS3 Launcher
    single<org.example.globalconsole.PS3Launcher.data.repository.PS3LauncherRepository> { 
        org.example.globalconsole.PS3Launcher.data.repositoryImpl.PS3LauncherRepositoryImpl(adapter = get()) 
    }

    // Adaptadores de Azahar (Nintendo 3DS)
    single { org.example.globalconsole.azahar.data.database.Game3DSAzaharAdapter() }
    single { org.example.globalconsole.azahar.data.database.LauncherAzaharAdapter() }
    single { org.example.globalconsole.azahar.data.database.Game3DSFileSystemAdapter(getEmulatorPathUseCase = get()) }

    // Repositorios de Azahar
    single<org.example.globalconsole.azahar.data.repository.Game3DSRepository> {
        org.example.globalconsole.azahar.data.repositoryImpl.Game3DSRepositoryImpl(
            dataSourceFile = get(),
            dataSourceAzahar = get()
        )
    }
    single<org.example.globalconsole.azahar.data.repository.AzaharRepository> {
        org.example.globalconsole.azahar.data.repositoryImpl.AzaharRepositoryImpl(
            launcherAdapter = get(),
            gameAdapter = get()
        )
    }

    // Adaptadores de DuckStation (PS1)
    single { org.example.globalconsole.duckstation.data.database.GameDuckStationAdapter() }
    single { org.example.globalconsole.duckstation.data.database.LauncherDuckStationAdapter() }
    single { org.example.globalconsole.duckstation.data.database.GameDuckStationFileSystemAdapter(getEmulatorPathUseCase = get()) }

    // Repositorios de DuckStation
    single<org.example.globalconsole.duckstation.data.repository.GameDuckStationRepository> {
        org.example.globalconsole.duckstation.data.repositoryImpl.GameDuckStationRepositoryImpl(
            dataSourceFile = get(),
            dataSourceDuckStation = get()
        )
    }
    single<org.example.globalconsole.duckstation.data.repository.DuckStationRepository> {
        org.example.globalconsole.duckstation.data.repositoryImpl.DuckStationRepositoryImpl(
            launcherAdapter = get(),
            gameAdapter = get()
        )
    }
}
