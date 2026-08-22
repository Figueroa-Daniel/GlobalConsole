package org.example.globalconsole.PS3Launcher.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.PS3Launcher.data.repository.PS3LauncherRepository

/**
 * Caso de uso para consultar si el launcher de PS3 debe mostrarse en la biblioteca.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-22
 */
open class FindPS3LauncherUseCase(
    private val repository: PS3LauncherRepository
) {
    open suspend operator fun invoke(): Boolean = withContext(Dispatchers.IO) {
        return@withContext repository.isPS3Enabled()
    }
}
