package org.example.globalconsole.melonDS.domain.usecase

import org.example.globalconsole.melonDS.data.repository.MelonDSRepository

/**
 * Caso de uso para cerrar la ejecución del Launcher de Melon DS.
 * Se espera que sea invocado por la UI al pulsar el botón Home del mando.
 *
 * @property repository Repositorio de Melon DS.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class CloseLauncherMelonDSUseCase(private val repository: MelonDSRepository) {
    /**
     * Cierra el proceso del launcher.
     *
     * @return true si se cerró correctamente, false en caso de error o si no estaba abierto.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-15
     */
    suspend operator fun invoke(): Boolean {
        return repository.closeLauncher()
    }
}
