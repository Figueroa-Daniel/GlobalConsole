package org.example.globalconsole.localApps.domain.usecase

/**
 * Clasifica accesos de escritorio para separar herramientas de juegos, emuladores y navegadores.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class IsSupportedLocalAppUseCase {
    private val excludedPrograms = setOf(
        "brave", "brave-browser", "firefox", "firefox-esr", "chrome", "google-chrome",
        "chromium", "chromium-browser", "msedge", "microsoft-edge", "opera", "opera-browser",
        "vivaldi", "vivaldi-stable", "librewolf", "waterfox", "floorp", "zen", "zen-browser",
        "tor-browser", "torbrowser-launcher", "start-tor-browser", "epiphany", "falkon", "qutebrowser",
        "palemoon", "midori", "thorium", "icecat", "seamonkey", "iexplore",
        "pcsx2", "pcsx2-qt", "duckstation", "duckstation-qt", "melonds", "rpcs3", "azahar",
        "citra", "retroarch", "dolphin-emu", "dolphin-emu-qt2", "ppsspp", "ppssppqt", "ppssppsdl",
        "cemu", "yuzu", "ryujinx", "suyu", "sudachi", "mame", "dosbox", "dosbox-x",
        "desmume", "mgba", "snes9x", "fceux", "mednafen", "xemu", "xenia", "shadps4",
        "steam", "heroic", "heroicgameslauncher", "lutris", "bottles", "epicgameslauncher",
        "goggalaxy", "galaxyclient", "ubisoftconnect", "eaapp", "eadesktop", "globalconsole"
    )
    private val excludedIds = setOf(
        "com.brave.browser", "org.mozilla.firefox", "com.google.chrome", "com.microsoft.edge",
        "com.opera.opera", "org.chromium.chromium", "org.gnome.epiphany", "org.dolphinemu.dolphin-emu",
        "net.pcsx2.pcsx2", "org.duckstation.duckstation", "net.kuribo64.melonds", "net.rpcs3.rpcs3",
        "org.libretro.retroarch", "org.citra_emu.citra", "org.azahar_emu.azahar", "io.github.zen_browser.zen",
        "com.valvesoftware.steam", "com.heroicgameslauncher.hgl", "com.usebottles.bottles", "net.lutris.lutris"
    )

    /**
     * Evalúa categorías e identidades; el nombre solo se usa para alias inequívocos.
     * @param identity Identificador del acceso o paquete.
     * @param name Nombre visible de la aplicación.
     * @param arguments Ejecutable y argumentos de lanzamiento.
     * @param categories Categorías publicadas por el sistema operativo.
     * @return True si el acceso pertenece al catálogo de aplicaciones generales.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    operator fun invoke(identity: String, name: String, arguments: List<String>, categories: Set<String>): Boolean {
        val kinds = categories.map { it.lowercase() }
        if (kinds.any { it == "game" || it.endsWith("game") || it in setOf("games", "juegos", "emulator", "webbrowser") }) return false
        val values = listOf(identity) + arguments
        if (values.any { value ->
            val normalized = value.lowercase().replace('\\', '/')
            normalized.contains("steam://") || normalized.contains("heroic://") ||
                normalized.contains("lutris:") || normalized.contains("/steamapps/common/") ||
                Regex("/(games|juegos|gog games|epic games)/").containsMatchIn(normalized) ||
                normalized.contains("org.example.globalconsole") || normalized == "-applaunch"
        }) return false
        if (name.trim().lowercase() in setOf("dolphin emulator", "emulador dolphin", "globalconsole")) return false
        return values.none { value ->
            val normalized = value.lowercase().replace('\\', '/')
            val token = normalized.substringAfterLast('/').substringAfter('=').removeSuffix(".desktop").removeSuffix(".exe").removeSuffix(".appimage")
            val id = normalized.removeSuffix(".desktop")
            id in excludedIds || token in excludedPrograms ||
                token.removeSuffix("-stable").removeSuffix("-beta").removeSuffix("-dev") in excludedPrograms ||
                (token == "dolphin" && normalized.endsWith(".exe")) ||
                id.startsWith("org.dolphinemu.")
        }
    }
}
