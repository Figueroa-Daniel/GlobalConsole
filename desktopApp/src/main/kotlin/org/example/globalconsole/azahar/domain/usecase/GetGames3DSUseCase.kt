package org.example.globalconsole.azahar.domain.usecase

import org.example.globalconsole.azahar.data.repository.Game3DSRepository
import org.example.globalconsole.azahar.domain.entitys.Game3DS

/**
 * Caso de uso para obtener todos los juegos de Nintendo 3DS (Azahar).
 *
 * @property repository Repositorio de juegos 3DS.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class GetGames3DSUseCase(private val repository: Game3DSRepository) {
    /**
     * Obtiene todos los juegos 3DS escaneados del directorio configurado.
     *
     * @return Lista de entidades de dominio [Game3DS].
     * @author Daniel Figueroa Vidal
     * @since 2026-08-26
     */
    suspend operator fun invoke(): List<Game3DS> {
        return repository.getAllGames3DS()
    }
}
