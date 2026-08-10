package org.example.globalconsole.settings.domain.usecase

import kotlinx.coroutines.test.runTest
import org.example.globalconsole.settings.data.FakeSettingsRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Tests unitarios para [GetEmulatorPathUseCase].
 * Verifica que retorna null cuando no hay ruta configurada y el valor correcto si existe.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-10
 */
class GetEmulatorPathUseCaseTest {

    private val repository = FakeSettingsRepository()
    private val useCase = GetEmulatorPathUseCase(repository)

    /**
     * Verifica que retorna null si no hay ruta configurada para el emulador.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    @Test
    fun `retorna null si no hay ruta configurada`() = runTest {
        val result = useCase("pcsx2")
        assertNull(result)
    }

    /**
     * Verifica que retorna la ruta correcta cuando está configurada.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    @Test
    fun `retorna la ruta correcta cuando esta configurada`() = runTest {
        val expectedPath = "/home/usuario/isos"
        repository.saveEmulatorPath("pcsx2", expectedPath)

        val result = useCase("pcsx2")

        assertEquals(expectedPath, result)
    }

    /**
     * Verifica que retorna null para un emulatorId diferente al configurado.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    @Test
    fun `retorna null para un emulatorId no configurado`() = runTest {
        repository.saveEmulatorPath("pcsx2", "/home/usuario/isos")
        val result = useCase("heroic")
        assertNull(result)
    }
}
