package org.example.globalconsole.settings.domain.usecase

import kotlinx.coroutines.test.runTest
import org.example.globalconsole.settings.data.FakeSettingsRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests unitarios para los casos de uso de sensibilidad del ratón.
 * 
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
class MouseSensitivityUseCasesTest {

    private lateinit var repository: FakeSettingsRepository
    private lateinit var getMouseSensitivityUseCase: GetMouseSensitivityUseCase
    private lateinit var saveMouseSensitivityUseCase: SaveMouseSensitivityUseCase

    @BeforeTest
    fun setUp() {
        repository = FakeSettingsRepository()
        getMouseSensitivityUseCase = GetMouseSensitivityUseCase(repository)
        saveMouseSensitivityUseCase = SaveMouseSensitivityUseCase(repository)
    }

    @Test
    fun getMouseSensitivity_returnsDefaultValue() = runTest {
        val result = getMouseSensitivityUseCase()
        assertEquals(14f, result, "El valor por defecto de la sensibilidad debe ser 14f")
    }

    @Test
    fun saveMouseSensitivity_updatesValue() = runTest {
        saveMouseSensitivityUseCase(25.5f)
        val result = getMouseSensitivityUseCase()
        assertEquals(25.5f, result, "La sensibilidad guardada debe ser 25.5f")
    }
}
