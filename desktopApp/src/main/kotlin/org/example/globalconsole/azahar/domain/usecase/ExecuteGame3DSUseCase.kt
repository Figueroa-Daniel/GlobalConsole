package org.example.globalconsole.azahar.domain.usecase

import org.example.globalconsole.azahar.data.repository.AzaharRepository

/**
 * Caso de uso para iniciar la ejecución de un juego en Azahar (Nintendo 3DS).
 *
 * @property repository Repositorio de Azahar.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class ExecuteGame3DSUseCase(private val repository: AzaharRepository) {
    /**
     * Inicia el proceso del juego a pantalla completa.
     *
     * @param executeUrl Ruta absoluta del archivo ROM a ejecutar.
     * @return true si se ejecutó y terminó correctamente, false en caso de error.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-26
     */
    suspend operator fun invoke(executeUrl: String?): Boolean {
        return repository.executeGame(executeUrl)
    }
}
