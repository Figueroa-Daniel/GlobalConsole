package org.example.globalconsole.settings.domain.usecase

import kotlinx.coroutines.test.runTest
import org.example.globalconsole.settings.data.FakeSettingsRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests unitarios para [SaveEmulatorPathUseCase].
 * Verifica la validación de la ruta y la persistencia correcta mediante un Fake.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-10
 */
class SaveEmulatorPathUseCaseTest {

    private val repository = FakeSettingsRepository()
    private val useCase = SaveEmulatorPathUseCase(repository)

    /**
     * Verifica que el use case lanza excepción si la ruta está vacía.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    @Test
    fun `lanza excepcion si la ruta esta vacia`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            useCase("pcsx2", "")
        }
    }

    /**
     * Verifica que el use case lanza excepción si la ruta está en blanco.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    @Test
    fun `lanza excepcion si la ruta esta en blanco`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            useCase("pcsx2", "   ")
        }
    }

    /**
     * Verifica que el use case persiste correctamente una ruta válida.
     * Usa el FakeRepository para confirmar que el valor se almacenó.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    @Test
    fun `persiste la ruta correctamente si es valida`() = runTest {
        val path = "/home/usuario/isos"
        useCase("pcsx2", path)
        val saved = repository.getEmulatorPath("pcsx2")
        assertEquals(path, saved)
    }

    /**
     * Verifica que el use case lanza excepción si el emulatorId está vacío.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    @Test
    fun `lanza excepcion si el emulatorId esta vacio`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            useCase("", "/home/usuario/isos")
        }
    }
}
