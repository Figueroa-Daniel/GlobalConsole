package org.example.globalconsole

/**
 * Clase utilitaria encargada de generar saludos que incluyen la información del sistema operativo.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-02
 */
class Greeting {
    private val platform = getPlatform()

    /**
     * Retorna un saludo personalizado con el nombre de la plataforma de ejecución.
     *
     * @return El texto del saludo generado.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    fun greet(): String {
        return sayHello(platform.name)
    }
}