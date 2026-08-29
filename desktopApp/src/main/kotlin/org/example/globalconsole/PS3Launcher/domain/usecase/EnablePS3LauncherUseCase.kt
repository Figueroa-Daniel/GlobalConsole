package org.example.globalconsole.PS3Launcher.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.PS3Launcher.data.repository.PS3LauncherRepository

/**
 * Caso de uso para habilitar el launcher de PS3 en la biblioteca.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-22
 */
open class EnablePS3LauncherUseCase(
    private val repository: PS3LauncherRepository
) {
    open suspend operator fun invoke() = withContext(Dispatchers.IO) {
        repository.savePS3Enabled(true)
    }
}
