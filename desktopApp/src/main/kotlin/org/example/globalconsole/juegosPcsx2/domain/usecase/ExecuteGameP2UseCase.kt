package org.example.globalconsole.juegosPcsx2.domain.usecase

import org.example.globalconsole.juegosPcsx2.data.repository.GameP2Repository

/**
 * Caso de uso encargado de ejecutar un juego de PS2 específico.
 *
 * @property repository Repositorio de juegos de PS2.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-03
 */
class ExecuteGameP2UseCase(
    private val repository: GameP2Repository
) {
    /**
     * Ejecuta el caso de uso para iniciar el juego especificado por su identificador.
     *
     * @param id Identificador único del juego a ejecutar.
     * @return True si la ejecución se inició con éxito, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    suspend operator fun invoke(id: String): Boolean {
        return repository.executeGameP2(id)
    }
}
