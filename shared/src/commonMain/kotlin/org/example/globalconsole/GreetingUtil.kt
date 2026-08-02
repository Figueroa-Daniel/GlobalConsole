package org.example.globalconsole

/**
 * Función utilitaria que concatena el nombre del destinatario a un mensaje de saludo.
 *
 * @param to Nombre del destinatario del saludo.
 * @return Cadena formateada de saludo.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-02
 */
fun sayHello(to: String): String =
    "Hello, $to!"