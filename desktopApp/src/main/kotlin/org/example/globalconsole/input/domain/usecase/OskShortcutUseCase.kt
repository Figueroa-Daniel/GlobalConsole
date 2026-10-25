package org.example.globalconsole.input.domain.usecase

/**
 * Estado de los controles que abren o confirman el teclado externo.
 * @author Daniel Figueroa Vidal
 */
data class OskShortcutState(
    val start: Boolean = false,
    val rightTrigger: Boolean = false,
    val rightBumper: Boolean = false,
    val rightStick: Boolean = false
)

/**
 * Acción exclusiva del atajo; abrir el teclado nunca confirma su contenido.
 * @author Daniel Figueroa Vidal
 */
enum class OskShortcutAction { NONE, TOGGLE, CONFIRM }

/**
 * Detecta flancos de los atajos sin depender del foco del sistema o de GLFW.
 * @author Daniel Figueroa Vidal
 */
class OskShortcutUseCase {
    private var previous = OskShortcutState()

    /**
     * Prioriza exclusivamente START + R2 sobre la confirmación con START.
     * @param current Controles de la lectura actual.
     * @param visible Indica si el teclado estaba abierto antes de esta lectura.
     * @return Una única acción, sin repetir mientras se mantiene el atajo.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    operator fun invoke(current: OskShortcutState, visible: Boolean): OskShortcutAction {
        val combo = current.start && current.rightTrigger
        val previousCombo = previous.start && previous.rightTrigger
        val action = when {
            combo && !previousCombo -> OskShortcutAction.TOGGLE
            visible && current.start && !previous.start && !combo -> OskShortcutAction.CONFIRM
            else -> OskShortcutAction.NONE
        }
        previous = current
        return action
    }

    /**
     * Reinicia los flancos al abandonar la aplicación externa.
     * @return Unit.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    fun reset() {
        previous = OskShortcutState()
    }
}
