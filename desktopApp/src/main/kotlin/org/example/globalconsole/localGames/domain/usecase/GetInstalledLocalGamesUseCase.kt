package org.example.globalconsole.localGames.domain.usecase

import org.example.globalconsole.localGames.data.repository.LocalGamesRepository
import org.example.globalconsole.localGames.domain.entitys.LocalGameObj

/**
 * Coordina getInstalled mediante el repositorio de juegos locales.
 * @param repository Contrato de datos y ejecución.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class GetInstalledLocalGamesUseCase(private val repository: LocalGamesRepository) {
/**
 * Solicita getInstalled al repositorio.
 * @return List<LocalGameObj>.
 * @throws Exception Si el repositorio no puede completar la operación.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend operator fun invoke(): List<LocalGameObj> =
        repository.getInstalled()
}
