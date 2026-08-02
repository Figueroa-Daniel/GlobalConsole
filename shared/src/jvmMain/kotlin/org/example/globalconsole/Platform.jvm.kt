package org.example.globalconsole

/**
 * Implementación de la plataforma de destino correspondiente a la máquina virtual de Java (JVM).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-02
 */
class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

/**
 * Implementación concreta de la función getPlatform para entornos JVM de escritorio.
 *
 * @return Instancia de [JVMPlatform].
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-02
 */
actual fun getPlatform(): Platform = JVMPlatform()