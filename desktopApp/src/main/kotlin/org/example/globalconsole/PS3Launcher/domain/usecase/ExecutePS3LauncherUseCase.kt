package org.example.globalconsole.PS3Launcher.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.PS3Launcher.data.database.LauncherPS3Adapter

/**
 * Caso de uso encargado de invocar la ejecución del emulador de PS3.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-22
 */
open class ExecutePS3LauncherUseCase(
    private val adapter: LauncherPS3Adapter
) {
    open suspend operator fun invoke(): Boolean = withContext(Dispatchers.IO) {
        return@withContext adapter.executeLauncher()
    }
}
