package org.example.globalconsole.settings.domain.usecase

import kotlinx.coroutines.test.runTest
import org.example.globalconsole.settings.domain.SettingsRepository
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests unitarios para [IsHeroicEnabledUseCase] y [SaveHeroicEnabledUseCase].
 *
 * Se utiliza un [FakeSettingsRepository] para simular la persistencia sin acceder
 * al sistema de archivos real, siguiendo el principio de aislamiento de tests unitarios (TDD).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-12
 */
class HeroicToggleUseCasesTest {

    /**
     * Implementación falsa de [SettingsRepository] que almacena las preferencias en memoria.
     * Permite controlar el estado del toggle sin interactuar con el sistema de archivos.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    private class FakeSettingsRepository : SettingsRepository {

        private var heroicEnabled = false
        var saveHeroicEnabledCallCount = 0
            private set

        override suspend fun saveEmulatorPath(emulatorId: String, path: String) {}
        override suspend fun getEmulatorPath(emulatorId: String): String? = null

        /**
         * Registra la llamada, incrementa el contador y persiste el valor en memoria.
         *
         * @param enabled Valor a persistir.
         * @author Daniel Figueroa Vidal
         * @since 2026-08-12
         */
        override suspend fun saveHeroicEnabled(enabled: Boolean) {
            saveHeroicEnabledCallCount++
            heroicEnabled = enabled
        }

        /**
         * Retorna el valor actualmente almacenado en memoria.
         *
         * @return El estado del toggle de Heroic.
         * @author Daniel Figueroa Vidal
         * @since 2026-08-12
         */
        override suspend fun isHeroicEnabled(): Boolean = heroicEnabled
    }

    // ── IsHeroicEnabledUseCase ────────────────────────────────────────────────

    /**
     * Verifica que [IsHeroicEnabledUseCase] retorna false cuando el repositorio
     * no tiene ninguna preferencia almacenada (estado inicial por defecto).
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    @Test
    fun isHeroicEnabled_whenNothingSaved_returnsFalse() = runTest {
        val repository = FakeSettingsRepository()
        val useCase = IsHeroicEnabledUseCase(repository)

        val result = useCase()

        assertFalse("Debe retornar false cuando no hay preferencia guardada", result)
    }

    /**
     * Verifica que [IsHeroicEnabledUseCase] retorna true después de que el repositorio
     * haya persistido el valor true mediante [SaveHeroicEnabledUseCase].
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    @Test
    fun isHeroicEnabled_afterSavingTrue_returnsTrue() = runTest {
        val repository = FakeSettingsRepository()
        val saveUseCase = SaveHeroicEnabledUseCase(repository)
        val isEnabledUseCase = IsHeroicEnabledUseCase(repository)

        saveUseCase(true)
        val result = isEnabledUseCase()

        assertTrue("Debe retornar true después de guardar enabled=true", result)
    }

    /**
     * Verifica que [IsHeroicEnabledUseCase] retorna false después de guardar false
     * previamente, garantizando que el ciclo enable → disable funciona correctamente.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    @Test
    fun isHeroicEnabled_afterSavingFalse_returnsFalse() = runTest {
        val repository = FakeSettingsRepository()
        val saveUseCase = SaveHeroicEnabledUseCase(repository)
        val isEnabledUseCase = IsHeroicEnabledUseCase(repository)

        saveUseCase(true)
        saveUseCase(false)
        val result = isEnabledUseCase()

        assertFalse("Debe retornar false después de guardar enabled=false", result)
    }

    // ── SaveHeroicEnabledUseCase ──────────────────────────────────────────────

    /**
     * Verifica que [SaveHeroicEnabledUseCase] delega exactamente una vez al repositorio
     * al ser invocado, garantizando que la llamada de persistencia se produce.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    @Test
    fun saveHeroicEnabled_whenInvoked_callsRepository() = runTest {
        val repository = FakeSettingsRepository()
        val useCase = SaveHeroicEnabledUseCase(repository)

        useCase(true)

        assertTrue(
            "El use case debe invocar al repositorio exactamente una vez",
            repository.saveHeroicEnabledCallCount == 1
        )
    }
}
