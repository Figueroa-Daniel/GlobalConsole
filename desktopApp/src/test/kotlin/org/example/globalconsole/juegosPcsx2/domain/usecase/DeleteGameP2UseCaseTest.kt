package org.example.globalconsole.juegosPcsx2.domain.usecase

import kotlinx.coroutines.test.runTest
import org.example.globalconsole.generalDomain.entititys.Platforms
import org.example.globalconsole.juegosPcsx2.domain.entitys.GameP2
import org.example.globalconsole.juegosPcsx2.fakes.FakeGameP2Repository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Pruebas unitarias para el caso de uso [DeleteGameP2UseCase].
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-03
 */
class DeleteGameP2UseCaseTest {

    private lateinit var fakeRepository: FakeGameP2Repository
    private lateinit var deleteGameP2UseCase: DeleteGameP2UseCase

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
        deleteGameP2UseCase = DeleteGameP2UseCase(fakeRepository)
    }

    /**
     * Valida que cuando el juego existe y se elimina con éxito, el caso de uso retorne true.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    @Test
    fun invoke_whenGameExistsAndDeleted_returnsTrue() = runTest {
        val mockGame = GameP2(id = "pcsx21", name = "MGS3", urlGameExecute = "/path/mgs3.iso", image = null, platform = Platforms.PCSX2)
        fakeRepository.games.add(mockGame)

        val result = deleteGameP2UseCase("pcsx21")

        assertTrue(result)
        assertTrue(fakeRepository.games.isEmpty())
    }

    /**
     * Valida que cuando el juego no existe en el repositorio, el caso de uso retorne false.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    @Test
    fun invoke_whenGameDoesNotExist_returnsFalse() = runTest {
        val result = deleteGameP2UseCase("non_existent_id")

        assertFalse(result)
    }

    /**
     * Valida que cuando el borrado falla en la capa de datos, el caso de uso retorne false.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    @Test
    fun invoke_whenDeletionFails_returnsFalse() = runTest {
        val mockGame = GameP2(id = "pcsx21", name = "MGS3", urlGameExecute = "/path/mgs3.iso", image = null, platform = Platforms.PCSX2)
        fakeRepository.games.add(mockGame)
        fakeRepository.shouldDeletionFail = true

        val result = deleteGameP2UseCase("pcsx21")

        assertFalse(result)
    }
}
