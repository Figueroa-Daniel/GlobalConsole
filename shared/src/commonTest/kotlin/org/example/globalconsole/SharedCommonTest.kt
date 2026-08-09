package org.example.globalconsole

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pruebas unitarias de integración común ejecutadas a través de [kotlin.test].
 * Valida comportamientos compartidos en todas las plataformas.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-02
 */
class SharedCommonTest {

    /**
     * Prueba de ejemplo para validar que el motor de pruebas kotlin.test funciona correctamente.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    @Test
    fun example() {
        assertEquals(3, 1 + 2)
    }
}