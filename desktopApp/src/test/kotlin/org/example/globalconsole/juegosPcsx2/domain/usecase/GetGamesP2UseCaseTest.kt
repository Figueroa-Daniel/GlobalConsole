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
 * Pruebas unitarias para el caso de uso [GetGamesP2UseCase].
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-03
 */
class GetGamesP2UseCaseTest {

    private lateinit var fakeRepository: FakeGameP2Repository
    private lateinit var getGamesP2UseCase: GetGamesP2UseCase

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
        getGamesP2UseCase = GetGamesP2UseCase(fakeRepository)
    }

    /**
     * Valida que cuando no hay juegos en el repositorio, el caso de uso retorne una lista vacía.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    @Test
    fun invoke_whenNoGames_returnsEmptyList() = runTest {
        val result = getGamesP2UseCase()
        assertTrue(result.isEmpty())
    }

    /**
     * Valida que cuando el repositorio contiene juegos, el caso de uso retorne la lista de esos juegos.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    @Test
    fun invoke_whenGamesExist_returnsGamesList() = runTest {
        val mockGame1 = GameP2(id = "pcsx21", name = "Metal Gear Solid 3", urlGameExecute = "/path/mgs3.iso", image = null, platform = Platforms.PCSX2)
        val mockGame2 = GameP2(id = "pcsx22", name = "Gran Turismo 4", urlGameExecute = "/path/gt4.iso", image = null, platform = Platforms.PCSX2)
        fakeRepository.games.addAll(listOf(mockGame1, mockGame2))

        val result = getGamesP2UseCase()

        assertEquals(2, result.size)
        assertEquals("pcsx21", result[0].id)
        assertEquals("Metal Gear Solid 3", result[0].name)
        assertEquals("pcsx22", result[1].id)
        assertEquals("Gran Turismo 4", result[1].name)
    }
}
