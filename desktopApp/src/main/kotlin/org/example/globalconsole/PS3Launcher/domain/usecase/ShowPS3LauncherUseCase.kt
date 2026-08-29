package org.example.globalconsole.PS3Launcher.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.PS3Launcher.data.mappers.toDomain
import org.example.globalconsole.PS3Launcher.data.repository.PS3LauncherRepository
import org.example.globalconsole.PS3Launcher.domain.entitys.PS3Launcher

/**
 * Caso de uso para obtener la entidad del launcher de PS3 lista para la UI.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-22
 */
open class ShowPS3LauncherUseCase(
    private val repository: PS3LauncherRepository
) {
    open suspend operator fun invoke(): PS3Launcher = withContext(Dispatchers.IO) {
        return@withContext repository.showPS3Launcher().toDomain()
    }
}
