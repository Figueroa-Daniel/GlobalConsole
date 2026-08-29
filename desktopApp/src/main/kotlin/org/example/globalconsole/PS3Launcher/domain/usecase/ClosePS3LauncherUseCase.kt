package org.example.globalconsole.PS3Launcher.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.PS3Launcher.data.repository.PS3LauncherRepository

/**
 * Caso de uso para cerrar el proceso activo del emulador de PS3.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-22
 */
open class ClosePS3LauncherUseCase(
    private val repository: PS3LauncherRepository
) {
    open suspend operator fun invoke(): Boolean = withContext(Dispatchers.IO) {
        return@withContext repository.closeLauncher()
    }
}
