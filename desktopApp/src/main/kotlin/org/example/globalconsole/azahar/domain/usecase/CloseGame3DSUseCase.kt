package org.example.globalconsole.azahar.domain.usecase

import org.example.globalconsole.azahar.data.repository.Game3DSRepository

/**
 * Caso de uso encargado de cerrar forzosamente el emulador Azahar para juegos de Nintendo 3DS.
 *
 * @property repository Repositorio de juegos de 3DS.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class CloseGame3DSUseCase(private val repository: Game3DSRepository) {
    /**
     * Ejecuta la acción de cierre.
     *
     * @return True si se cerró exitosamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-26
     */
    suspend operator fun invoke(): Boolean {
        return repository.closeGame()
    }
}
