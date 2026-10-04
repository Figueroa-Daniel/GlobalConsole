package org.example.globalconsole.settings.domain.usecase

import kotlinx.coroutines.test.runTest
import org.example.globalconsole.settings.data.FakeSettingsRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests unitarios para los casos de uso de configuración de vista.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-10-04
 */
class ViewSettingsUseCasesTest {

    private lateinit var repository: FakeSettingsRepository
    private lateinit var getViewSettingsUseCase: GetViewSettingsUseCase
    private lateinit var saveViewSettingsUseCase: SaveViewSettingsUseCase

    @BeforeTest
    fun setUp() {
        repository = FakeSettingsRepository()
        getViewSettingsUseCase = GetViewSettingsUseCase(repository)
        saveViewSettingsUseCase = SaveViewSettingsUseCase(repository)
    }

    @Test
    fun getViewSettings_returnsDefaultValues() = runTest {
        val result = getViewSettingsUseCase()
        assertEquals("NONE", result.groupingMode)
        assertEquals("INLINE", result.launcherPosition)
    }

    @Test
    fun saveViewSettings_persistsBothValues() = runTest {
        saveViewSettingsUseCase("PLATFORM", "TOP")
        val result = getViewSettingsUseCase()
        assertEquals("PLATFORM", result.groupingMode)
        assertEquals("TOP", result.launcherPosition)
    }

    @Test
    fun saveViewSettings_overwritesPreviousValues() = runTest {
        saveViewSettingsUseCase("PLATFORM", "TOP")
        saveViewSettingsUseCase("CONSOLE_SERIES", "INLINE")
        val result = getViewSettingsUseCase()
        assertEquals("CONSOLE_SERIES", result.groupingMode)
        assertEquals("INLINE", result.launcherPosition)
    }
}
