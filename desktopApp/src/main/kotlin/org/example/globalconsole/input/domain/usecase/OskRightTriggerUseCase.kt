package org.example.globalconsole.input.domain.usecase

/**
 * Identifica R2 físico para el teclado sin aceptar los dos gatillos como equivalentes.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
class OskRightTriggerUseCase {
    /**
     * Corrige exclusivamente el GUID HJDX cuyo intercambio se verificó en diagnóstico.
     * @param guid Identificador del mando; null conserva el mapeo estándar.
     * @param leftAxis Lectura del eje izquierdo normalizado por GLFW.
     * @param rightAxis Lectura del eje derecho normalizado por GLFW.
     * @return Lectura analógica del gatillo físico R2.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    operator fun invoke(guid: String?, leftAxis: Float, rightAxis: Float): Float =
        if (swapped(guid)) leftAxis else rightAxis

    /**
     * Obtiene L2 físico con el mapeo complementario al de R2.
     * @param guid Identificador del mando.
     * @param leftAxis Lectura del eje izquierdo de GLFW.
     * @param rightAxis Lectura del eje derecho de GLFW.
     * @return Lectura analógica del gatillo físico L2.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    fun left(guid: String?, leftAxis: Float, rightAxis: Float): Float =
        if (swapped(guid)) rightAxis else leftAxis

    /**
     * Detecta el único mapeo invertido confirmado con diagnóstico físico.
     * @param guid Identificador del mando.
     * @return True para el mando HJDX documentado.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    private fun swapped(guid: String?): Boolean =
        guid.equals("03000000632500002605000010010000", ignoreCase = true)
}
