package org.example.globalconsole.input.domain.usecase

/**
 * Estado puro de edición y posición del teclado, sin dependencias de Compose.
 * @property text Texto en edición.
 * @property upperCase Capa de mayúsculas activa.
 * @property row Fila seleccionada.
 * @property column Columna seleccionada.
 * @property preferredColumn Ancla horizontal al cruzar teclas anchas.
 * @property confirmed Solicitud de confirmar la edición.
 * @author Daniel Figueroa Vidal
 */
data class KeyboardState(
    val text: String = "",
    val upperCase: Boolean = false,
    val row: Int = 0,
    val column: Int = 0,
    val preferredColumn: Int = 0,
    val confirmed: Boolean = false
)

/** Acciones independientes del dispositivo. @author Daniel Figueroa Vidal */
enum class KeyboardAction { UP, DOWN, LEFT, RIGHT, PRESS, DELETE, SHIFT, CONFIRM }

/**
 * Resuelve la entrada y el desplazamiento seguro entre teclas de diferentes tamaños.
 * @author Daniel Figueroa Vidal
 */
class KeyboardInputUseCase {
    private val rows = listOf(
        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "⌫"),
        listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "á", "é"),
        listOf("a", "s", "d", "f", "g", "h", "j", "k", "l", "ñ", "í", "ó"),
        listOf("⇧", "z", "x", "c", "v", "b", "n", "m", "ú", "ü", ".", "/"),
        listOf(",", "ESPACIO", "↵")
    )

    /**
     * Devuelve las etiquetas correspondientes a la capa actual.
     * @param state Estado de edición.
     * @return Filas visibles.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-04
     */
    fun layout(state: KeyboardState): List<List<String>> =
        if (state.upperCase) rows.map { row -> row.map { it.uppercase() } } else rows

    /**
     * Aplica un evento de forma atómica, manteniendo el foco dentro de la cuadrícula.
     * @param state Estado anterior válido.
     * @param action Acción del usuario.
     * @return Nuevo estado de texto y foco.
     * @throws Nothing No lanza excepciones para estados válidos.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-04
     */
    operator fun invoke(state: KeyboardState, action: KeyboardAction): KeyboardState {
        return when (action) {
            KeyboardAction.UP, KeyboardAction.DOWN -> {
                val row = (state.row + if (action == KeyboardAction.UP) 4 else 1) % 5
                state.copy(row = row, column = if (row == 4) state.preferredColumn / 4 else state.preferredColumn)
            }
            KeyboardAction.LEFT, KeyboardAction.RIGHT -> {
                val count = rows[state.row].size
                val col = (state.column + if (action == KeyboardAction.LEFT) count - 1 else 1) % count
                state.copy(column = col, preferredColumn = if (state.row == 4) col * 4 + 1 else col)
            }
            KeyboardAction.SHIFT -> state.copy(upperCase = !state.upperCase)
            KeyboardAction.CONFIRM -> state.copy(confirmed = true)
            KeyboardAction.DELETE -> state.copy(text = if (state.text.isEmpty()) "" else state.text.substring(0, state.text.offsetByCodePoints(state.text.length, -1)))
            KeyboardAction.PRESS -> when (val key = layout(state)[state.row][state.column]) {
                "⇧" -> invoke(state, KeyboardAction.SHIFT)
                "↵" -> invoke(state, KeyboardAction.CONFIRM)
                "⌫" -> invoke(state, KeyboardAction.DELETE)
                "ESPACIO" -> state.copy(text = state.text + " ")
                else -> state.copy(text = state.text + key)
            }
        }
    }
}
