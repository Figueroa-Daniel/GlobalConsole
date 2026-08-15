package org.example.globalconsole.melonDS.domain.usecase

import org.example.globalconsole.melonDS.data.repository.GameDSRepository

/**
 * Caso de uso encargado de cerrar forzosamente el emulador Melon DS para juegos de Nintendo DS.
 *
 * @property repository Repositorio de juegos de DS.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class CloseGameDSUseCase(private val repository: GameDSRepository) {
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
