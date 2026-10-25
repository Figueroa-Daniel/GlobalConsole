package org.example.globalconsole.input.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Regresiones de entrada y navegación del teclado por mando.
 * @author Daniel Figueroa Vidal
 */
class KeyboardInputUseCaseTest {
    /**
     * Recorre todos los bordes y comprueba índices y recuperación de columna.
     * @return Unit.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-04
     */
    @Test
    fun navigation_keepsEveryKeyReachableAndRestoresColumn() {
        val useCase = KeyboardInputUseCase()
        var state = KeyboardState(row = 3, column = 9, preferredColumn = 9)
        state = useCase(state, KeyboardAction.DOWN)
        assertEquals(2, state.column)
        state = useCase(state, KeyboardAction.UP)
        assertEquals(9, state.column)
        for (row in 0..4) {
            for (col in useCase.layout(state)[row].indices) {
                for (action in listOf(KeyboardAction.UP, KeyboardAction.DOWN, KeyboardAction.LEFT, KeyboardAction.RIGHT)) {
                    val next = useCase(KeyboardState(row = row, column = col, preferredColumn = if (row == 4) col * 4 else col), action)
                    assertTrue(next.column in useCase.layout(next)[next.row].indices)
                }
            }
        }
        state = useCase(KeyboardState(), KeyboardAction.LEFT)
        assertEquals(11, state.column)
        assertEquals(0, useCase(state, KeyboardAction.RIGHT).column)
    }

    /**
     * Mayúsculas, números, acentos, espacio, borrado e Intro comparten una única ruta.
     * @return Unit.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-04
     */
    @Test
    fun keys_applyCurrentCaseAndConfirmWithA() {
        val useCase = KeyboardInputUseCase()
        var state = useCase(KeyboardState(row = 3), KeyboardAction.PRESS)
        assertTrue(state.upperCase)
        state = useCase(state.copy(row = 1, column = 0), KeyboardAction.PRESS)
        assertEquals("Q", state.text)
        state = useCase(state.copy(row = 0, column = 0), KeyboardAction.PRESS)
        state = useCase(state.copy(row = 2, column = 9), KeyboardAction.PRESS)
        assertEquals("Q1Ñ", state.text)
        state = useCase(state.copy(row = 4, column = 1), KeyboardAction.PRESS)
        assertEquals("Q1Ñ ", state.text)
        state = useCase(state, KeyboardAction.DELETE)
        assertEquals("Q1Ñ", state.text)
        assertTrue(useCase(state.copy(row = 4, column = 2), KeyboardAction.PRESS).confirmed)
        assertEquals("", useCase(KeyboardState(), KeyboardAction.DELETE).text)
        assertEquals("", useCase(KeyboardState(text = "🎮"), KeyboardAction.DELETE).text)
    }

    /**
     * Con el búfer vacío el borrado se delega al campo externo; con texto no.
     * @return Unit.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    @Test
    fun delete_onEmptyBufferRequestsExternalDelete() {
        val useCase = KeyboardInputUseCase()
        val withText = useCase(KeyboardState(text = "ab"), KeyboardAction.DELETE)
        assertEquals("a", withText.text)
        assertEquals(0, withText.externalDeletes)
        var state = useCase(KeyboardState(), KeyboardAction.DELETE)
        assertEquals(1, state.externalDeletes)
        state = useCase(state.copy(row = 0, column = 11), KeyboardAction.PRESS)
        assertEquals(2, state.externalDeletes)
        assertEquals("", state.text)
    }
    /**
     * Edita en la posición pendiente sin mover el foco de la tecla seleccionada.
     * @return Unit.
     * @throws AssertionError Si la inserción, el borrado o los límites son incorrectos.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    @Test
    fun triggersMoveCaretInsidePendingText() {
        val useCase = KeyboardInputUseCase()
        var state = KeyboardState(text = "abc", row = 3, column = 2, preferredColumn = 2)
        state = useCase(state, KeyboardAction.TEXT_LEFT)
        assertEquals(2, state.textCursor)
        assertEquals(3, state.row)
        assertEquals(2, state.column)
        state = useCase(state, KeyboardAction.PRESS)
        assertEquals("abxc", state.text)
        assertEquals(3, state.textCursor)
        state = useCase(state, KeyboardAction.DELETE)
        assertEquals("abc", state.text)
        assertEquals(2, state.textCursor)
        state = useCase(state, KeyboardAction.TEXT_RIGHT)
        assertEquals(3, state.textCursor)
        assertEquals(KeyboardState(text = "abc").textCursor, useCase(state, KeyboardAction.TEXT_RIGHT).textCursor)
        state = useCase(KeyboardState(text = "abc", textCursor = 0), KeyboardAction.TEXT_LEFT)
        assertEquals(0, state.textCursor)
        assertEquals("abc", useCase(state, KeyboardAction.DELETE).text)
        assertEquals(0, useCase(state, KeyboardAction.DELETE).externalDeletes)
    }

    /**
     * Mueve y borra caracteres Unicode completos, incluso emojis fuera del BMP.
     * @return Unit.
     * @throws AssertionError Si el cursor separa un par sustituto.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    @Test
    fun caretMovementPreservesUnicodeCodePoints() {
        val useCase = KeyboardInputUseCase()
        var state = KeyboardState(text = "A🎮B")
        state = useCase(state, KeyboardAction.TEXT_LEFT)
        assertEquals(3, state.textCursor)
        state = useCase(state, KeyboardAction.TEXT_LEFT)
        assertEquals(1, state.textCursor)
        state = useCase(state, KeyboardAction.TEXT_RIGHT)
        assertEquals(3, state.textCursor)
        state = useCase(state, KeyboardAction.DELETE)
        assertEquals("AB", state.text)
        assertEquals(1, state.textCursor)
    }

}
