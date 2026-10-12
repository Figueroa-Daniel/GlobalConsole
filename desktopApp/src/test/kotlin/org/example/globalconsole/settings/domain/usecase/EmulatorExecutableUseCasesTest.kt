package org.example.globalconsole.settings.domain.usecase

import kotlinx.coroutines.runBlocking
import org.example.globalconsole.settings.data.SettingsRepositoryImpl
import org.example.globalconsole.settings.domain.EmulatorTarget
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.*

/** Valida comandos y persistencia sin iniciar emuladores. @author Daniel Figueroa Vidal */
class EmulatorExecutableUseCasesTest {
    @get:Rule val temporary = TemporaryFolder()

    /** @return Unit. @throws Exception Si falla el archivo temporal. @author Daniel Figueroa Vidal @since 2026-10-12 */
    @Test fun configuredExecutableSurvivesReloadAndKeepsGameFolder() = runBlocking {
        val file = temporary.newFile()
        val repo = SettingsRepositoryImpl(file)
        SaveEmulatorPathUseCase(repo)("pcsx2", "/roms")
        SaveEmulatorExecutableUseCase(repo)(EmulatorTarget.PCSX2, " C:\\Program Files\\PCSX2\\pcsx2-qt.exe ")
        val reloaded = SettingsRepositoryImpl(file)
        assertEquals("/roms", GetEmulatorPathUseCase(reloaded)("pcsx2"))
        assertEquals("C:\\Program Files\\PCSX2\\pcsx2-qt.exe", GetEmulatorExecutableUseCase(reloaded)(EmulatorTarget.PCSX2))
        assertEquals(listOf("C:\\Program Files\\PCSX2\\pcsx2-qt.exe", "-fullscreen", "-nogui", "D:\\Games\\Game one.iso"),
            BuildEmulatorCommandUseCase(reloaded)(EmulatorTarget.PCSX2, "Windows 11", "D:\\Games\\Game one.iso"))
    }

    /** @return Unit. @throws Exception Si falla el archivo temporal. @author Daniel Figueroa Vidal @since 2026-10-12 */
    @Test fun allTargetsUseConfiguredPathOnBothSystemsAndResetToDefaults() = runBlocking {
        val repo = SettingsRepositoryImpl(temporary.newFile())
        val build = BuildEmulatorCommandUseCase(repo)
        for (target in EmulatorTarget.entries) {
            SaveEmulatorExecutableUseCase(repo)(target, "/opt/My Emulator/app")
            for (os in listOf("Linux", "Windows 11")) {
                assertEquals(listOf("/opt/My Emulator/app"), build(target, os))
            }
            SaveEmulatorExecutableUseCase(repo)(target, " ")
            assertEquals(listOf("flatpak", "run", target.flatpakId), build(target, "Linux"))
            assertEquals(listOf(target.windowsExecutable), build(target, "Windows 11"))
        }
        assertEquals(listOf("rpcs3.exe"), build(EmulatorTarget.RPCS3, "Windows 11"))
        assertEquals(listOf("pcsx2-qt.exe", "-fullscreen", "-nogui", "game.iso"), build(EmulatorTarget.PCSX2, "Windows 11", "game.iso"))
        assertFailsWith<IllegalArgumentException> { build(EmulatorTarget.PCSX2, "Darwin") }
        Unit
    }

    /** @return Unit. @throws Exception Si falla el archivo temporal. @author Daniel Figueroa Vidal @since 2026-10-12 */
    @Test fun gameArgumentsArePreservedAndNotInterpretedAsShell() = runBlocking {
        val build = BuildEmulatorCommandUseCase(SettingsRepositoryImpl(temporary.newFile()))
        val game = "/roms/a b;$(command).iso"
        val flags = mapOf(EmulatorTarget.PCSX2 to listOf("-fullscreen", "-nogui"), EmulatorTarget.DUCKSTATION to listOf("-fullscreen"),
            EmulatorTarget.DOLPHIN to listOf("-b", "-e"), EmulatorTarget.MELONDS to listOf("-f"), EmulatorTarget.AZAHAR to listOf("-f"))
        for ((target, args) in flags) assertEquals(listOf("flatpak", "run", target.flatpakId) + args + game, build(target, "Linux", game))
    }
    /** @return Unit. @throws Exception Si falla el ejecutable temporal. @author Daniel Figueroa Vidal @since 2026-10-12 */
    @Test fun rpcs3LauncherRunsConfiguredProcessAndReportsFailedExit() = runBlocking {
        org.junit.Assume.assumeTrue(System.getProperty("os.name").lowercase().contains("linux"))
        val executable = temporary.newFile("fake emulator")
        executable.writeText("#!/bin/sh\nexit 0\n")
        assertTrue(executable.setExecutable(true))
        val settings = SettingsRepositoryImpl(temporary.newFile())
        SaveEmulatorExecutableUseCase(settings)(EmulatorTarget.RPCS3, executable.absolutePath)
        val adapter = org.example.globalconsole.PS3Launcher.data.database.LauncherPS3Adapter(BuildEmulatorCommandUseCase(settings))
        val execute = org.example.globalconsole.PS3Launcher.domain.usecase.ExecutePS3LauncherUseCase(
            adapter)
        assertTrue(execute())
        executable.writeText("#!/bin/sh\nexit 7\n")
        assertFalse(execute())
    }
}
