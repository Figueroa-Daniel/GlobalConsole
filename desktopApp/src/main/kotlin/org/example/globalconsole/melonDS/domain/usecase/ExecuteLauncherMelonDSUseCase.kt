package org.example.globalconsole.melonDS.domain.usecase

import org.example.globalconsole.melonDS.data.repository.MelonDSRepository

/**
 * Caso de uso para iniciar la ejecución del Launcher de Melon DS.
 * 
 * @property repository Repositorio de Melon DS.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class ExecuteLauncherMelonDSUseCase(private val repository: MelonDSRepository) {
    /**
     * Inicia el proceso del launcher.
     *
     * @return true si se inició correctamente, false en caso de error.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-15
     */
    suspend operator fun invoke(): Boolean {
        return repository.executeLauncher()
    }
}
