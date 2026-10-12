package org.example.globalconsole.systemApps.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Ejecuta y cierra exclusivamente el árbol de procesos de un programa nativo.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class NativeApplicationProcess {
    private val processLock = Any()
    @Volatile private var active: Session? = null

    /**
     * Conserva los descendientes aunque el lanzador finalice antes que el juego.
     * @property process Proceso creado por GlobalConsole.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private class Session(val process: Process) {
        @Volatile var closing = false
        val children = java.util.concurrent.ConcurrentHashMap<Long, ProcessHandle>()
    }

    /**
     * Lanza argumentos sin shell y espera al proceso y a sus descendientes conocidos.
     * @param arguments Ejecutable y argumentos resueltos.
     * @param workingDirectory Directorio de trabajo opcional.
     * @return True si termina normalmente o mediante cierre solicitado.
     * @throws Exception Si no puede iniciar el ejecutable o se cancela la espera.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun execute(arguments: List<String>, workingDirectory: String?): Boolean = withContext(Dispatchers.IO) {
        require(arguments.isNotEmpty()) { "La aplicación no tiene comando de ejecución" }
        val session = synchronized(processLock) {
            check(active == null) { "Ya hay una aplicación en ejecución" }
            val builder = ProcessBuilder(arguments).redirectOutput(ProcessBuilder.Redirect.INHERIT).redirectError(ProcessBuilder.Redirect.INHERIT)
            workingDirectory?.let { builder.directory(File(it)) }
            Session(builder.start()).also { active = it }
        }
        try {
            while (session.process.isAlive || session.children.values.any { it.isAlive }) {
                session.process.descendants().use { stream -> stream.forEach { session.children[it.pid()] = it } }
                delay(50)
            }
            session.closing || session.process.exitValue() == 0
        } finally {
            try {
                if (session.process.isAlive || session.children.values.any { it.isAlive }) {
                    withContext(NonCancellable) { close() }
                }
            } finally {
                synchronized(processLock) { if (active === session) active = null }
            }
        }
    }

    /**
     * Cierra únicamente el árbol de procesos iniciado para el programa actual.
     * @return True si los procesos han terminado o no había juego activo.
     * @throws Exception Si el sistema impide terminar procesos o se cancela la espera.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun close(): Boolean = withContext(Dispatchers.IO) {
        val session = synchronized(processLock) { active?.also { it.closing = true } } ?: return@withContext true
        session.process.descendants().use { stream -> stream.forEach { session.children[it.pid()] = it } }
        val children = session.children.values.toList()
        children.filter { it.isAlive }.forEach { it.destroy() }
        if (children.isNotEmpty()) delay(150)
        val handles = children + session.process.toHandle()
        if (session.process.isAlive) session.process.destroy()
        delay(350)
        handles.filter { it.isAlive }.forEach { it.destroyForcibly() }
        session.process.waitFor(2, TimeUnit.SECONDS)
        handles.none { it.isAlive }
    }

}
