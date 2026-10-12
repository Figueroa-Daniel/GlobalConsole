package org.example.globalconsole.settings.domain

/**
 * Identifica emuladores y sus destinos de lanzamiento predeterminados.
 * @property id Clave estable de configuración.
 * @property windowsExecutable Nombre buscado en PATH en Windows.
 * @property flatpakId Identificador de Flatpak en Linux.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
enum class EmulatorTarget(val id: String, val windowsExecutable: String, val flatpakId: String) {
    PCSX2("pcsx2", "pcsx2-qt.exe", "net.pcsx2.PCSX2"),
    MELONDS("melonds", "melonds.exe", "net.kuribo64.melonDS"),
    DOLPHIN("dolphin", "Dolphin.exe", "org.DolphinEmu.dolphin-emu"),
    AZAHAR("azahar", "azahar.exe", "org.azahar_emu.Azahar"),
    DUCKSTATION("duckstation", "DuckStation.exe", "org.duckstation.DuckStation"),
    RPCS3("rpcs3", "rpcs3.exe", "net.rpcs3.RPCS3")
}
