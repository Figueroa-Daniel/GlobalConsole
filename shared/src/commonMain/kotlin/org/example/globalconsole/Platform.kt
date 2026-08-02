package org.example.globalconsole

/**
 * Representa una plataforma o sistema operativo de destino en el módulo compartido.
 *
 * @property name Nombre descriptivo del sistema operativo o plataforma de ejecución.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-02
 */
interface Platform {
    val name: String
}

/**
 * Obtiene la instancia de la plataforma actual utilizando el mecanismo `expect/actual` de Kotlin Multiplatform.
 *
 * @return La instancia específica de [Platform] de la plataforma en ejecución.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-02
 */
expect fun getPlatform(): Platform