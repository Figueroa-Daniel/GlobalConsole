package org.example.globalconsole.HeroicGames.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.HeroicGames.data.mappers.toDomain
import org.example.globalconsole.HeroicGames.data.repository.HGLauncherRepository
import org.example.globalconsole.HeroicGames.domain.entitys.HGLauncher

/**
 * Caso de uso de dominio que recupera los datos de Heroic Games Launcher
 * y los transforma en la entidad de dominio [HGLauncher] lista para la UI.
 *
 * Usa el [HGLauncherRepository] para obtener el [HGLauncherDto] y el mapper
 * [toDomain] para convertirlo, respetando la arquitectura de capas establecida.
 *
 * La clase es [open] para permitir la creación de implementaciones Fake en los
 * tests unitarios sin necesidad de frameworks de mocking.
 *
 * @property repository Repositorio de Heroic Games Launcher.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
open class ShowHGLauncherUseCase(
    private val repository: HGLauncherRepository
) {

    /**
     * Obtiene los datos del launcher desde el repositorio y los convierte a
     * la entidad de dominio [HGLauncher] mediante el mapper correspondiente.
     *
     * @return [HGLauncher] con los datos del launcher listos para la UI.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    open suspend operator fun invoke(): HGLauncher = withContext(Dispatchers.IO) {
        repository.showHGLauncher().toDomain()
    }
}