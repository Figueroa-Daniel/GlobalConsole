package org.example.globalconsole.juegosPcsx2.domain.usecase

import kotlinx.coroutines.test.runTest
import org.example.globalconsole.generalDomain.entititys.Platforms
import org.example.globalconsole.juegosPcsx2.domain.entitys.GameP2
import org.example.globalconsole.juegosPcsx2.fakes.FakeGameP2Repository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Pruebas unitarias para el caso de uso [ExecuteGameP2UseCase].
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-03
 */
class ExecuteGameP2UseCaseTest {

    private lateinit var fakeRepository: FakeGameP2Repository
    private lateinit var executeGameP2UseCase: ExecuteGameP2UseCase

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
        executeGameP2UseCase = ExecuteGameP2UseCase(fakeRepository)
    }

    /**
     * Valida que cuando el juego existe y se inicia con éxito, el caso de uso retorne true.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    @Test
    fun invoke_whenGameExistsAndSucceeds_returnsTrue() = runTest {
        val mockGame = GameP2(id = "pcsx21", name = "MGS3", urlGameExecute = "/path/mgs3.iso", image = null, platform = Platforms.PCSX2)
        fakeRepository.games.add(mockGame)

        val result = executeGameP2UseCase("pcsx21")

        assertTrue(result)
        assertEquals("pcsx21", fakeRepository.executedGameId)
    }

    /**
     * Valida que cuando el juego no existe en el repositorio, el caso de uso retorne false.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    @Test
    fun invoke_whenGameDoesNotExist_returnsFalse() = runTest {
        val result = executeGameP2UseCase("non_existent_id")

        assertFalse(result)
    }

    /**
     * Valida que cuando la ejecución es fallida por error del repositorio, el caso de uso retorne false.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    @Test
    fun invoke_whenExecutionFails_returnsFalse() = runTest {
        val mockGame = GameP2(id = "pcsx21", name = "MGS3", urlGameExecute = "/path/mgs3.iso", image = null, platform = Platforms.PCSX2)
        fakeRepository.games.add(mockGame)
        fakeRepository.shouldExecutionFail = true

        val result = executeGameP2UseCase("pcsx21")

        assertFalse(result)
    }
}
