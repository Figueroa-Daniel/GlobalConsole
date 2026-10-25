package org.example.globalconsole.navigatorWeb.domain.usecase

import org.example.globalconsole.generalDomain.entititys.NavigatorsWeb
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Selección de procesos principales al delegar en un navegador ya abierto.
 * @author Daniel Figueroa Vidal
 */
class MatchNavigatorProcessUseCaseTest {
    /**
     * Brave nativo y Flatpak se reconocen sin confundir Chrome ni procesos auxiliares.
     * @return Unit.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Test
    fun brave_matchesMainProcessOnly() {
        val match = MatchNavigatorProcessUseCase()
        assertTrue(match(NavigatorsWeb.BRAVE, "/opt/brave.com/brave/brave", emptyList()))
        assertTrue(match(NavigatorsWeb.BRAVE, "/app/brave/brave", listOf("--new-window")))
        assertTrue(match(NavigatorsWeb.BRAVE, "C:\\Program Files\\BraveSoftware\\Brave-Browser\\Application\\brave.exe", emptyList()))
        assertFalse(match(NavigatorsWeb.BRAVE, "/opt/brave.com/brave/brave", listOf("--type=renderer")))
        assertFalse(match(NavigatorsWeb.BRAVE, "/opt/brave.com/brave/brave", listOf("--type", "zygote")))
        assertFalse(match(NavigatorsWeb.BRAVE, "/opt/google/chrome/chrome", emptyList()))
        assertFalse(match(NavigatorsWeb.BRAVE, "/usr/bin/sh", listOf("-c", "brave-browser")))
        assertFalse(match(NavigatorsWeb.BRAVE, null, emptyList()))
    }

    /**
     * Cada navegador solo selecciona sus ejecutables principales.
     * @return Unit.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Test
    fun otherBrowsers_doNotAttachToBrave() {
        val match = MatchNavigatorProcessUseCase()
        assertTrue(match(NavigatorsWeb.FIREFOX, "/usr/lib/firefox/firefox", emptyList()))
        assertFalse(match(NavigatorsWeb.FIREFOX, "/usr/lib/firefox/firefox", listOf("-contentproc")))
        assertTrue(match(NavigatorsWeb.CHROME, "/opt/google/chrome/chrome", emptyList()))
        assertTrue(match(NavigatorsWeb.EDGE, "/opt/microsoft/msedge/msedge", emptyList()))
        assertTrue(match(NavigatorsWeb.OPERA, "/usr/lib/opera/opera", emptyList()))
        for (browser in NavigatorsWeb.entries.filter { it != NavigatorsWeb.BRAVE }) {
            assertFalse(match(browser, "/opt/brave.com/brave/brave", emptyList()))
        }
    }
}
