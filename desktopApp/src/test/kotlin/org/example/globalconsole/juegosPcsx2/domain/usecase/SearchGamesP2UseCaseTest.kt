package org.example.globalconsole.juegosPcsx2.domain.usecase

import kotlinx.coroutines.test.runTest
import org.example.globalconsole.generalDomain.entititys.Platforms
import org.example.globalconsole.juegosPcsx2.domain.entitys.GameP2
import org.example.globalconsole.juegosPcsx2.fakes.FakeGameP2Repository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Pruebas unitarias para el caso de uso [SearchGamesP2UseCase].
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-03
 */
class SearchGamesP2UseCaseTest {

    private lateinit var fakeRepository: FakeGameP2Repository
    private lateinit var searchGamesP2UseCase: SearchGamesP2UseCase

    /**
     * Configuración inicial antes de cada prueba.
     * Instancia el repositorio fake y el caso de uso.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    @BeforeTest
    fun setUp() {
        fakeRepository = FakeGameP2Repository()
        searchGamesP2UseCase = SearchGamesP2UseCase(fakeRepository)
    }

    /**
     * Valida que cuando no hay juegos que coincidan con la búsqueda, se retorne una lista vacía.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    @Test
    fun invoke_whenNoMatches_returnsEmptyList() = runTest {
        val mockGame = GameP2(id = "pcsx21", name = "Gran Turismo 4", urlGameExecute = "/path/gt4.iso", image = null, platform = Platforms.PCSX2)
        fakeRepository.games.add(mockGame)

        val result = searchGamesP2UseCase("Metal Gear")

        assertTrue(result.isEmpty())
    }

    /**
     * Valida que cuando hay juegos que coinciden con la búsqueda, se retorne la lista filtrada.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    @Test
    fun invoke_whenMatchesExist_returnsFilteredList() = runTest {
        val mockGame1 = GameP2(id = "pcsx21", name = "Metal Gear Solid 3", urlGameExecute = "/path/mgs3.iso", image = null, platform = Platforms.PCSX2)
        val mockGame2 = GameP2(id = "pcsx22", name = "Gran Turismo 4", urlGameExecute = "/path/gt4.iso", image = null, platform = Platforms.PCSX2)
        fakeRepository.games.addAll(listOf(mockGame1, mockGame2))

        val result = searchGamesP2UseCase("metal")

        assertEquals(1, result.size)
        assertEquals("pcsx21", result[0].id)
        assertEquals("Metal Gear Solid 3", result[0].name)
    }
}
