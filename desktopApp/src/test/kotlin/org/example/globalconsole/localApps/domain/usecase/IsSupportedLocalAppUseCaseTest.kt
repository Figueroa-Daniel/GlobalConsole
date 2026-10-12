package org.example.globalconsole.localApps.domain.usecase

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Exclusiones de aplicaciones sin confundir herramientas que comparten nombres.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class IsSupportedLocalAppUseCaseTest {
    /**
     * Acepta utilidades, herramientas de Internet y Dolphin gestor de archivos.
     * @return Unit.
     * @throws AssertionError Si una herramienta válida queda excluida.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Test
    fun ordinaryAppsRemainAvailable() {
        val useCase = IsSupportedLocalAppUseCase()
        assertTrue(useCase("org.videolan.VLC", "VLC", listOf("vlc"), setOf("AudioVideo")))
        assertTrue(useCase("org.kde.dolphin.desktop", "Dolphin", listOf("dolphin"), setOf("System", "FileManager")))
        assertTrue(useCase("org.mozilla.Thunderbird", "Thunderbird", listOf("thunderbird"), setOf("Network", "Email")))
        assertTrue(useCase("editor.desktop", "Brave notes", listOf("/opt/editor/editor"), setOf("Office")))
        assertTrue(useCase("org.gnome.Terminal", "Terminal", listOf("gnome-terminal"), setOf("System")))
    }

    /**
     * Excluye categorías de juegos, navegadores y emuladores, incluso con nombres personalizados.
     * @return Unit.
     * @throws AssertionError Si una categoría o identidad excluida se admite.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Test
    fun categoriesAndNativeIdentitiesAreExcluded() {
        val useCase = IsSupportedLocalAppUseCase()
        for (category in listOf("Game", "ActionGame", "BoardGame", "Emulator", "WebBrowser")) {
            assertFalse(useCase("custom", "Aplicación", listOf("custom"), setOf(category)))
        }
        for (command in listOf("brave-browser", "firefox", "google-chrome", "msedge", "opera", "chromium", "duckstation", "pcsx2-qt", "citra", "rpcs3", "melonDS", "azahar", "retroarch", "dolphin-emu", "steam", "heroic", "lutris", "curseforge", "prismlauncher")) {
            assertFalse(useCase("custom", "Personalizado", listOf("/usr/bin/$command"), emptySet()), command)
        }
        assertFalse(useCase("custom", "Navegador", listOf("C:\\Apps\\Brave\\brave.exe"), emptySet()))
        assertFalse(useCase("custom", "Emulador", listOf("C:\\Apps\\Dolphin\\Dolphin.exe"), emptySet()))
    }

    /**
     * Identifica Flatpak, Snap, identidades desktop y accesos a juegos delegados.
     * @return Unit.
     * @throws AssertionError Si un wrapper evita las exclusiones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Test
    fun wrappedAndAliasedAppsCannotBypassExclusions() {
        val useCase = IsSupportedLocalAppUseCase()
        assertFalse(useCase("alias", "Personalizado", listOf("flatpak", "run", "com.brave.Browser"), emptySet()))
        assertFalse(useCase("alias", "Personalizado", listOf("snap", "run", "firefox"), emptySet()))
        assertFalse(useCase("org.DolphinEmu.dolphin-emu.desktop", "Personalizado", listOf("/opt/custom"), emptySet()))
        assertFalse(useCase("alias", "Portal", listOf("xdg-open", "steam://rungameid/400"), emptySet()))
        assertFalse(useCase("alias", "Juego", listOf("/games/sample/sample"), emptySet()))
        assertFalse(useCase("alias", "Juego", listOf("C:\\SteamLibrary\\steamapps\\common\\Example\\example.exe"), emptySet()))
        assertFalse(useCase("globalconsole.desktop", "GlobalConsole", listOf("java", "org.example.globalconsole.MainKt"), emptySet()))
    }
}
