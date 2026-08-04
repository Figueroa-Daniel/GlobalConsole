package org.example.globalconsole.generalDomain.presentation.fakes

import org.example.globalconsole.juegosPcsx2.domain.entitys.GameP2
import org.example.globalconsole.juegosPcsx2.domain.usecase.GetGamesP2UseCase

/**
 * Implementación fake de [GetGamesP2UseCase] para pruebas unitarias del ViewModel.
 * Permite controlar la lista de juegos devuelta y simular errores.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-05
 */
class FakeGetGamesP2UseCase : GetGamesP2UseCase(repository = TODO("not needed in fake")) {

    /** Lista de juegos que devolverá la invocación del UseCase. */
    var games: MutableList<GameP2> = mutableListOf()

    /** Si es true, la invocación lanzará una excepción simulando un fallo. */
    var shouldThrowError: Boolean = false

    /**
     * Devuelve la lista fake configurada en [games], o lanza una excepción si [shouldThrowError] es true.
     *
     * @author Daniel Figueroa Vidal
     * @return Lista de [GameP2] configurada para el test.
     * @throws RuntimeException si [shouldThrowError] es true.
     * @since 2026-08-05
     */
    override suspend fun invoke(): List<GameP2> {
        if (shouldThrowError) throw RuntimeException("Error simulado en test")
        return games.toList()
    }
}
