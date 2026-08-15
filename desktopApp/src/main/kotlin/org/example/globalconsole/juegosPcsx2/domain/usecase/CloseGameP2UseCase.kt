package org.example.globalconsole.juegosPcsx2.domain.usecase

import org.example.globalconsole.juegosPcsx2.data.repository.GameP2Repository

/**
 * Caso de uso encargado de cerrar forzosamente el emulador PCSX2.
 *
 * @property repository Repositorio de juegos de PS2.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class CloseGameP2UseCase(private val repository: GameP2Repository) {
    /**
     * Ejecuta la acción de cierre.
     *
     * @return True si se cerró exitosamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-15
     */
    suspend operator fun invoke(): Boolean {
        return repository.closeGame()
    }
}
