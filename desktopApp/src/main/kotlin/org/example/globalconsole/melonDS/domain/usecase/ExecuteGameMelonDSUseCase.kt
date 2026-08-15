package org.example.globalconsole.melonDS.domain.usecase

import org.example.globalconsole.melonDS.data.repository.MelonDSRepository

/**
 * Caso de uso para iniciar la ejecución de un juego en Melon DS.
 * 
 * @property repository Repositorio de Melon DS.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class ExecuteGameMelonDSUseCase(private val repository: MelonDSRepository) {
    /**
     * Inicia el proceso del juego a pantalla completa.
     *
     * @param executeUrl Ruta absoluta del archivo ROM a ejecutar.
     * @return true si se ejecutó y terminó correctamente, false en caso de error.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-15
     */
    suspend operator fun invoke(executeUrl: String?): Boolean {
        return repository.executeGame(executeUrl)
    }
}
