package org.example.globalconsole.settings.domain.usecase

import kotlinx.coroutines.test.runTest
import org.example.globalconsole.settings.data.SettingsRepositoryImpl
import org.example.globalconsole.settings.domain.AppearanceSettings
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Valida los casos de uso de apariencia con archivos aislados de la configuración real.
 * @author Daniel Figueroa Vidal
 */
class AppearanceUseCasesTest {
    /**
     * Comprueba defaults, migración y persistencia de cada color y extremo del brillo.
     * @return Unit.
     * @throws Exception Si falla el archivo temporal.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-04
     */
    @Test
    fun appearance_survivesRestartAndPreservesSettings() = runTest {
        val file = Files.createTempFile("metro-settings", ".json").toFile()
        try {
            file.writeText("""{"heroicEnabled":true,"emulatorPaths":{"pcsx2":"/games"},"groupingMode":"PLATFORM","launcherPosition":"TOP"}""")
            val repo = SettingsRepositoryImpl(file)
            assertEquals(AppearanceSettings(), GetAppearanceSettingsUseCase(repo)())
            for (color in AppearanceSettings.COLORS) {
                for (intensity in listOf(0, 45, 100)) {
                    val previousView = GetViewSettingsUseCase(repo)()
                    val previousPath = GetEmulatorPathUseCase(repo)("pcsx2")
                    val expected = AppearanceSettings(color, intensity)
                    SaveAppearanceSettingsUseCase(repo)(expected)
                    val restarted = SettingsRepositoryImpl(file)
                    assertEquals(previousView, GetViewSettingsUseCase(restarted)())
                    assertEquals(previousPath, GetEmulatorPathUseCase(restarted)("pcsx2"))
                    assertEquals(expected, GetAppearanceSettingsUseCase(restarted)())
                    SaveViewSettingsUseCase(restarted)("CONSOLE_SERIES", "INLINE")
                    SaveEmulatorPathUseCase(restarted)("pcsx2", "/games/new")
                    assertEquals(expected, GetAppearanceSettingsUseCase(restarted)())
                    assertTrue(file.readText().contains("\"heroicEnabled\": true"))
                }
            }
        } finally { file.delete() }
    }

    /**
     * Rechaza valores inválidos al guardar sin alterar el último valor válido.
     * @return Unit.
     * @throws Exception Si falla el archivo temporal.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-04
     */
    @Test
    fun save_rejectsInvalidValues() = runTest {
        val file = Files.createTempFile("metro-invalid", ".json").toFile()
        try {
            file.writeText("{}")
            val repo = SettingsRepositoryImpl(file)
            val save = SaveAppearanceSettingsUseCase(repo)
            for (invalid in listOf(AppearanceSettings("UNKNOWN"), AppearanceSettings(glowIntensity = -1), AppearanceSettings(glowIntensity = 101))) {
                assertFailsWith<IllegalArgumentException> { save(invalid) }
                assertEquals(AppearanceSettings(), GetAppearanceSettingsUseCase(repo)())
            }
            file.writeText("""{"accentColor":"UNKNOWN","glowIntensity":200}""")
            assertEquals(AppearanceSettings(glowIntensity = 100), GetAppearanceSettingsUseCase(repo)())
            file.writeText("""{"accentColor":"BLUE","glowIntensity":-20}""")
            assertEquals(AppearanceSettings("BLUE", 0), GetAppearanceSettingsUseCase(repo)())
        } finally { file.delete() }
    }
    /**
     * Los nuevos colores se aceptan y conservan al reiniciar el repositorio.
     * @return Unit.
     * @throws Exception Si falla el archivo temporal.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-04
     */
    @Test
    fun expandedPalette_preservesRedAndDeepBlueSelections() = runTest {
        val file = Files.createTempFile("metro-expanded", ".json").toFile()
        try {
            file.writeText("{}")
            val repo = SettingsRepositoryImpl(file)
            for (color in listOf("RED", "CRIMSON", "NAVY", "COBALT", "TEAL", "ORANGE")) {
                val expected = AppearanceSettings(color, 70)
                SaveAppearanceSettingsUseCase(repo)(expected)
                assertEquals(expected, GetAppearanceSettingsUseCase(SettingsRepositoryImpl(file))())
            }
        } finally { file.delete() }
    }
    /**
     * Los morados saturados se conservan al reiniciar con cualquier intensidad.
     * @return Unit.
     * @throws Exception Si falla el archivo temporal.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-04
     */
    @Test
    fun neonPurples_surviveRestartAtEveryGlowBoundary() = runTest {
        val file = Files.createTempFile("metro-neon", ".json").toFile()
        try {
            file.writeText("{}")
            for (color in listOf("ELECTRIC_PURPLE", "NEON_PURPLE", "ULTRAVIOLET")) {
                for (glow in listOf(0, 45, 100)) {
                    val expected = AppearanceSettings(color, glow)
                    SaveAppearanceSettingsUseCase(SettingsRepositoryImpl(file))(expected)
                    assertEquals(expected, GetAppearanceSettingsUseCase(SettingsRepositoryImpl(file))())
                }
            }
        } finally { file.delete() }
    }
}
