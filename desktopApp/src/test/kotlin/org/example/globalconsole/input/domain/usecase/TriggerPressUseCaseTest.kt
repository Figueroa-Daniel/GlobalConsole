package org.example.globalconsole.input.domain.usecase

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Regresiones de la lectura de gatillos analógicos con histéresis.
 * @author Daniel Figueroa Vidal
 */
class TriggerPressUseCaseTest {
    /**
     * Reposo en -1 o 0 no cuenta como pulsado, y una pulsación completa sí.
     * @return Unit.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    @Test
    fun rest_isNeverPressedAndFullPullIs() {
        for (rest in listOf(-1f, 0f)) {
            val useCase = TriggerPressUseCase()
            assertFalse(useCase(rest))
            assertTrue(useCase(1f))
        }
    }

    /**
     * Evita parpadeos entre los umbrales de pulsación y liberación.
     * @return Unit.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    @Test
    fun hysteresis_keepsStateBetweenThresholds() {
        val useCase = TriggerPressUseCase()
        assertFalse(useCase(0.3f))
        assertTrue(useCase(0.6f))
        assertTrue(useCase(0.3f))
        assertFalse(useCase(0.1f))
        assertFalse(useCase(0.3f))
    }
}
