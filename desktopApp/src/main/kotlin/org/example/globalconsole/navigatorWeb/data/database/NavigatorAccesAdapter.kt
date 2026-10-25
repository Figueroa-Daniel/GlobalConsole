package org.example.globalconsole.navigatorWeb.data.database

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.generalDomain.entititys.NavigatorsWeb
import org.example.globalconsole.navigatorWeb.domain.usecase.MatchNavigatorProcessUseCase

/**
 * Adaptador encargado de detectar los navegadores web instalados en el sistema y de
 * lanzarlos mediante procesos nativos (Windows, o Linux con Flatpak o binario nativo).
 * Mantiene en memoria los navegadores ocultos por el usuario.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class NavigatorAccesAdapter {

    @Volatile
    private var activeSession: BrowserSession? = null
    private val matchProcess = MatchNavigatorProcessUseCase()

    /**
     * Procesos seguidos durante una ejecución, incluso si el lanzador ya terminó.
     * @author Daniel Figueroa Vidal
     */
    private class BrowserSession(val launcher: Process) {
        @Volatile var closed = false
        @Volatile var browsers: List<ProcessHandle> = emptyList()
    }
    private val hiddenNavigators = mutableSetOf<NavigatorsWeb>()

    /**
     * Obtiene los navegadores instalados en el sistema, estén ocultos o no.
     *
     * @return Lista de [NavigatorsWeb] instalados.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun isNavigatorsInstalled(): List<NavigatorsWeb> =
        NavigatorsWeb.entries.filter { isNavigatorInstalled(it) }

    /**
     * Comprueba si un navegador está instalado ejecutando su comando de verificación
     * y evaluando el código de salida (0 = instalado).
     *
     * @param navigatorType Navegador a comprobar.
     * @return True si el comando finaliza con código 0, false si falla o el SO no está soportado.
     * @throws Exception Capturada internamente; cualquier fallo del proceso devuelve false.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private suspend fun isNavigatorInstalled(navigatorType: NavigatorsWeb): Boolean {
        return try {
            succeeds(comandIsNavigatorInstalled(navigatorType))
        } catch (e: Exception) {
            System.err.println("Error al verificar el navegador $navigatorType: ${e.message}")
            false
        }
    }

    /**
     * Obtiene el comando de consola que verifica la instalación de un navegador según el SO.
     *
     * @param navigatorType Navegador a verificar.
     * @return Comando de verificación.
     * @throws IllegalArgumentException Si la combinación SO/navegador no está soportada.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private suspend fun comandIsNavigatorInstalled(navigatorType: NavigatorsWeb):String {
        val os = getOS()
        val pair: Pair<String, NavigatorsWeb> = Pair(os, navigatorType)

        return when (pair) {
            // Chrome
            Pair("Windows", NavigatorsWeb.CHROME) -> "cmd /c where chrome >nul 2>&1"
            Pair("Linux", NavigatorsWeb.CHROME) -> linuxCheckCommand("com.google.Chrome", "google-chrome", "google-chrome-stable")

            // Firefox
            Pair("Windows", NavigatorsWeb.FIREFOX) -> "cmd /c where firefox >nul 2>&1"
            Pair("Linux", NavigatorsWeb.FIREFOX) -> linuxCheckCommand("org.mozilla.firefox", "firefox")

            // Edge
            Pair("Windows", NavigatorsWeb.EDGE) -> "cmd /c where msedge >nul 2>&1"
            Pair("Linux", NavigatorsWeb.EDGE) -> linuxCheckCommand("com.microsoft.Edge", "microsoft-edge", "microsoft-edge-stable")

            // Opera
            Pair("Windows", NavigatorsWeb.OPERA) -> "cmd /c where opera >nul 2>&1"
            Pair("Linux", NavigatorsWeb.OPERA) -> linuxCheckCommand("com.opera.Opera", "opera")

            // Brave
            Pair("Windows", NavigatorsWeb.BRAVE) -> "cmd /c where brave >nul 2>&1"
            Pair("Linux", NavigatorsWeb.BRAVE) -> linuxCheckCommand("com.brave.Browser", "brave-browser", "brave")

            else -> throw IllegalArgumentException("Combinación no soportada: $pair")
        }

    }

    /**
     * Oculta un navegador para que no aparezca en la lista de instalados.
     *
     * @param navigatorType Navegador a ocultar.
     * @return True si pasó a estar oculto, false si ya lo estaba.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun hideNavigator(navigatorType: NavigatorsWeb): Boolean =
        hiddenNavigators.add(navigatorType)

    /**
     * Vuelve a mostrar un navegador previamente oculto.
     *
     * @param navigatorType Navegador a mostrar.
     * @return True si dejó de estar oculto, false si no lo estaba.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun showNavigator(navigatorType: NavigatorsWeb): Boolean =
        hiddenNavigators.remove(navigatorType)

    /**
     * Mantiene la sesión mientras el lanzador o la instancia delegada siguen vivos.
     * @param urlExecute Comando de ejecución del navegador.
     * @param navigator Navegador cuyos procesos principales deben seguirse.
     * @return True si el lanzamiento terminó correctamente o se cerró desde la interfaz.
     * @throws CancellationException Si se cancela la espera de la sesión.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun executeNavigator(urlExecute: String, navigator: NavigatorsWeb): Boolean = withContext(Dispatchers.IO) {
        var session: BrowserSession? = null
        try {
            val process = shellProcessBuilder(urlExecute).inheritIO().start()
            val current = BrowserSession(process)
            session = current
            activeSession = current
            println("[Navegador] Iniciado $navigator; lanzador=${process.pid()}")
            current.browsers = findBrowserProcesses(navigator)
            while (process.isAlive && !current.closed) {
                delay(100)
            }
            if (current.closed) return@withContext true
            if (process.exitValue() != 0) return@withContext false

            current.browsers = findBrowserProcesses(navigator)
            if (current.browsers.isNotEmpty()) {
                println("[Navegador] $navigator continúa en instancia existente: ${current.browsers.map { it.pid() }}")
            }
            while (!current.closed && current.browsers.any { it.isAlive }) {
                delay(100)
            }
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            System.err.println("Error al lanzar el navegador: ${e.message}")
            false
        } finally {
            if (activeSession === session) activeSession = null
        }
    }

    /**
     * Selecciona procesos principales del navegador del mismo usuario de GlobalConsole.
     * @param navigator Navegador cuya instancia puede haber recibido la apertura.
     * @return Procesos vivos coincidentes, excluyendo auxiliares y otros navegadores.
     * @throws SecurityException Si el sistema impide consultar procesos.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private fun findBrowserProcesses(navigator: NavigatorsWeb): List<ProcessHandle> {
        val user = ProcessHandle.current().info().user().orElse(null) ?: return emptyList()
        return ProcessHandle.allProcesses().use { processes ->
            processes.filter { process ->
                val info = process.info()
                process.isAlive && info.user().orElse(null) == user &&
                    matchProcess(navigator, info.command().orElse(null), info.arguments().orElse(emptyArray()).toList())
            }.toList()
        }
    }

    /**
     * Cierra únicamente el lanzador y los procesos de la sesión del navegador seleccionado.
     * @return True si no quedan procesos vivos o no había una sesión activa.
     * @throws CancellationException Si se cancela la espera del cierre.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun closeNavigator(): Boolean = withContext(Dispatchers.IO) {
        val session = activeSession ?: return@withContext true
        session.closed = true
        try {
            val roots = (session.browsers + session.launcher.toHandle()).distinctBy { it.pid() }
            val descendants = roots.flatMap { root -> root.descendants().use { it.toList() } }
            roots.filter { it.isAlive }.forEach { it.destroy() }
            delay(500)
            val remaining = (descendants + roots).distinctBy { it.pid() }.filter { it.isAlive }
            remaining.forEach { it.destroyForcibly() }
            delay(100)
            remaining.none { it.isAlive }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            System.err.println("Error al cerrar el navegador: ${e.message}")
            false
        } finally {
            if (activeSession === session) activeSession = null
        }
    }

    /**
     * Obtiene el comando de ejecución de un navegador según el SO. En Linux usa Flatpak si
     * está instalado y, si no, el binario nativo (deb, snap, etc.) encontrado en el PATH.
     * Para Firefox se añade `--new-instance` para evitar que delegue en sesiones previas en segundo plano
     * y cierre el proceso padre de inmediato.
     *
     * @param navigatorType Navegador a ejecutar.
     * @return Comando de ejecución.
     * @throws IllegalArgumentException Si la combinación SO/navegador no está soportada.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun getUrlExecutionNavigator(navigatorType: NavigatorsWeb): String {

        val os = getOS()
        val pair: Pair<String, NavigatorsWeb> = Pair(os, navigatorType)

        return when (pair) {
            // Chrome
            Pair("Windows", NavigatorsWeb.CHROME) -> "start chrome"
            Pair("Linux", NavigatorsWeb.CHROME) -> linuxRunCommand("com.google.Chrome", "google-chrome", "google-chrome-stable")

            // Firefox: se fuerza nueva instancia para que el proceso no finalice de inmediato si ya estaba abierto en el sistema
            Pair("Windows", NavigatorsWeb.FIREFOX) -> "start firefox -new-instance"
            Pair("Linux", NavigatorsWeb.FIREFOX) -> "${linuxRunCommand("org.mozilla.firefox", "firefox")} --new-instance"

            // Edge
            Pair("Windows", NavigatorsWeb.EDGE) -> "start msedge"
            Pair("Linux", NavigatorsWeb.EDGE) -> linuxRunCommand("com.microsoft.Edge", "microsoft-edge", "microsoft-edge-stable")

            // Opera
            Pair("Windows", NavigatorsWeb.OPERA) -> "start opera"
            Pair("Linux", NavigatorsWeb.OPERA) -> linuxRunCommand("com.opera.Opera", "opera")

            // Brave
            Pair("Windows", NavigatorsWeb.BRAVE) -> "start brave"
            Pair("Linux", NavigatorsWeb.BRAVE) -> linuxRunCommand("com.brave.Browser", "brave-browser", "brave")

            else -> throw IllegalArgumentException("Combinación no soportada: $pair")
        }
    }

    /**
     * Detecta el sistema operativo actual.
     *
     * @return "Windows", "Linux" o cadena vacía si no está soportado.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun getOS(): String {
        val os = System.getProperty("os.name").lowercase()
        return when {
            os.contains("windows") -> "Windows"
            os.contains("linux") -> "Linux"
            else -> ""
        }
    }

    /**
     * Construye el comando que comprueba la instalación en Linux: Flatpak o binario nativo en el PATH.
     * El código de salida es 0 si cualquiera de las alternativas existe.
     *
     * @param flatpakId Identificador de la aplicación Flatpak.
     * @param binaries Nombres de ejecutable nativo posibles.
     * @return Comando de verificación para `sh -c`.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private fun linuxCheckCommand(flatpakId: String, vararg binaries: String): String =
        (listOf("flatpak info $flatpakId") + binaries.map { "command -v $it" })
            .joinToString(" || ") { "$it >/dev/null 2>&1" }

    /**
     * Elige el comando de ejecución en Linux: Flatpak si está instalado, si no el primer
     * binario nativo disponible en el PATH.
     *
     * @param flatpakId Identificador de la aplicación Flatpak.
     * @param binaries Nombres de ejecutable nativo posibles.
     * @return Comando de ejecución; por defecto el de Flatpak.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private suspend fun linuxRunCommand(flatpakId: String, vararg binaries: String): String {
        if (succeeds("flatpak info $flatpakId >/dev/null 2>&1")) return "flatpak run $flatpakId"
        return binaries.firstOrNull { succeeds("command -v $it >/dev/null 2>&1") }
            ?: "flatpak run $flatpakId"
    }

    /**
     * Ejecuta un comando en la shell nativa y comprueba que termine con código 0.
     *
     * @param command Comando a ejecutar.
     * @return True si el código de salida es 0, false en otro caso o si falla el proceso.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private suspend fun succeeds(command: String): Boolean = try {
        withContext(Dispatchers.IO) { shellProcessBuilder(command).start().waitFor() == 0 }
    } catch (e: Exception) {
        false
    }

    /**
     * Crea un [ProcessBuilder] que interpreta el comando con la shell nativa
     * (`cmd /c` en Windows, `sh -c` en Linux), necesaria por las redirecciones y `start`.
     *
     * @param command Comando completo a interpretar.
     * @return ProcessBuilder configurado.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private fun shellProcessBuilder(command: String): ProcessBuilder {
        val isWindows = System.getProperty("os.name").lowercase().contains("windows")
        return if (isWindows) ProcessBuilder("cmd", "/c", command)
        else ProcessBuilder("sh", "-c", command)
    }
}