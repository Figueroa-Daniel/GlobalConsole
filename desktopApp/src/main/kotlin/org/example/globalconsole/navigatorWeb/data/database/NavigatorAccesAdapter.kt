package org.example.globalconsole.navigatorWeb.data.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.generalDomain.entititys.NavigatorsWeb

/**
 * Adaptador encargado de detectar los navegadores web instalados en el sistema y de
 * lanzarlos mediante procesos nativos (Windows, o Linux con Flatpak o binario nativo).
 * Mantiene en memoria los navegadores ocultos por el usuario.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class NavigatorAccesAdapter {

    private var activeProcess: Process? = null
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
     * Ejecuta el navegador con el comando indicado y bloquea hasta que se cierre
     * (ciclo de vida: lanzamiento -> espera -> retorno).
     *
     * @param urlExecute Comando de ejecución del navegador (ver [getUrlExecutionNavigator]).
     * @return True si el proceso se inició y finalizó correctamente, false en caso de error.
     * @throws Exception Capturada internamente; cualquier fallo del proceso devuelve false.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun executeNavigator(urlExecute:String): Boolean = withContext(Dispatchers.IO) {
        try {
            val process = shellProcessBuilder(urlExecute).inheritIO().start()
            activeProcess = process
            println("Navegador iniciado con: $urlExecute")
            process.waitFor()
            activeProcess = null
            true
        } catch (e: Exception) {
            System.err.println("Error al lanzar el navegador: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    /**
     * Cierra forzosamente el navegador en ejecución junto a sus procesos hijos.
     *
     * @return True si se cerró correctamente (o no había proceso), false en caso de error.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun closeNavigator(): Boolean {
        return try {
            activeProcess?.let { process ->
                process.descendants().forEach { it.destroyForcibly() }
                process.destroyForcibly()
            }
            activeProcess = null

            // Fallback robusto a nivel de sistema operativo para asegurar el cierre
            val os = System.getProperty("os.name").lowercase()
            if (os.contains("linux")) {
                val targets = listOf("chrome", "google-chrome", "firefox", "brave", "brave-browser", "msedge", "opera")
                targets.forEach { target ->
                    try {
                        ProcessBuilder("killall", "-9", target).start()
                    } catch (_: Exception) {}
                }
            } else if (os.contains("windows")) {
                val targets = listOf("chrome.exe", "firefox.exe", "brave.exe", "msedge.exe", "opera.exe")
                targets.forEach { target ->
                    try {
                        ProcessBuilder("taskkill", "/IM", target, "/F").start()
                    } catch (_: Exception) {}
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Obtiene el comando de ejecución de un navegador según el SO. En Linux usa Flatpak si
     * está instalado y, si no, el binario nativo (deb, snap, etc.) encontrado en el PATH.
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

            // Firefox
            Pair("Windows", NavigatorsWeb.FIREFOX) -> "start firefox"
            Pair("Linux", NavigatorsWeb.FIREFOX) -> linuxRunCommand("org.mozilla.firefox", "firefox")

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