package org.example.globalconsole.navigatorWeb.domain.usecase

import org.example.globalconsole.generalDomain.entititys.NavigatorsWeb

/**
 * Reconoce procesos principales del navegador seleccionado sin depender del SO.
 * @author Daniel Figueroa Vidal
 */
class MatchNavigatorProcessUseCase {
    /**
     * Excluye shells y subprocesos para seguir la instancia que conserva las ventanas.
     * @param navigator Navegador seleccionado en la biblioteca.
     * @param executable Ruta del ejecutable comunicada por el sistema.
     * @param arguments Argumentos del proceso, excluyendo el ejecutable.
     * @return True si corresponde a un proceso principal del navegador elegido.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    operator fun invoke(navigator: NavigatorsWeb, executable: String?, arguments: List<String>): Boolean {
        if (executable == null || arguments.any {
                it == "--type" || it.startsWith("--type=") || it == "-contentproc"
            }) return false
        val name = executable.replace('\\', '/').substringAfterLast('/').lowercase().removeSuffix(".exe")
        val names = when (navigator) {
            NavigatorsWeb.BRAVE -> setOf("brave", "brave-browser", "brave-browser-stable")
            NavigatorsWeb.CHROME -> setOf("chrome", "google-chrome", "google-chrome-stable")
            NavigatorsWeb.FIREFOX -> setOf("firefox", "firefox-bin")
            NavigatorsWeb.EDGE -> setOf("msedge", "microsoft-edge", "microsoft-edge-stable")
            NavigatorsWeb.OPERA -> setOf("opera")
        }
        return name in names
    }
}
