package org.example.globalconsole.config

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.globalconsole.HeroicGames.data.database.LauncherHeroicGamesAdapter
import org.example.globalconsole.HeroicGames.data.repositoryImpl.HGLauncherRepositoryImpl
import org.example.globalconsole.settings.data.SettingsRepositoryImpl
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test de integración que verifica que [SettingsRepositoryImpl] y [HGLauncherRepositoryImpl]
 * no se destruyen mutuamente al escribir en el archivo `config.json` compartido.
 *
 * Estos tests operan sobre un archivo temporal en el directorio de trabajo del proceso
 * de test (mismo directorio que el de la aplicación en producción).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
class AppConfigIntegrationTest {

    private val configFile = File("config.json")
    private lateinit var settingsRepo: SettingsRepositoryImpl
    private lateinit var hgLauncherRepo: HGLauncherRepositoryImpl

    /**
     * Inicializa los repositorios y garantiza que el archivo de config está limpio
     * al inicio de cada prueba.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @BeforeTest
    fun setUp() {
        configFile.delete()
        settingsRepo = SettingsRepositoryImpl()
        hgLauncherRepo = HGLauncherRepositoryImpl(
            adapter = object : LauncherHeroicGamesAdapter() {
                override fun executeLauncher(): Boolean = false
            }
        )
    }

    /**
     * Elimina el archivo de config tras cada prueba para no contaminar otros tests.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @AfterTest
    fun tearDown() {
        configFile.delete()
    }

    /**
     * Verifica que al activar Heroic y luego guardar la ruta del emulador,
     * la preferencia `heroicEnabled` se conserva en el archivo.
     *
     * Este test reproduce el Bug 1: antes de la corrección, `saveEmulatorPath`
     * sobreescribía el archivo con un modelo que no tenía `heroicEnabled`,
     * haciendo que Heroic volviera a aparecer como deshabilitado.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Test
    fun saveEmulatorPath_afterHeroicEnabled_preservesHeroicEnabled() = runTest {
        hgLauncherRepo.saveHeroicEnabled(true)
        settingsRepo.saveEmulatorPath("pcsx2", "/home/usuario/ISOs")

        assertTrue(hgLauncherRepo.isHeroicEnabled(), "heroicEnabled debe seguir siendo true tras guardar la ruta del emulador")
        assertEquals("/home/usuario/ISOs", settingsRepo.getEmulatorPath("pcsx2"))
    }

    /**
     * Verifica que al guardar la ruta del emulador y luego habilitar Heroic,
     * la ruta del emulador se conserva en el archivo.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Test
    fun saveHeroicEnabled_afterEmulatorPathSaved_preservesEmulatorPath() = runTest {
        settingsRepo.saveEmulatorPath("pcsx2", "/home/usuario/ISOs")
        hgLauncherRepo.saveHeroicEnabled(true)

        assertEquals("/home/usuario/ISOs", settingsRepo.getEmulatorPath("pcsx2"), "emulatorPath debe conservarse tras habilitar Heroic")
        assertTrue(hgLauncherRepo.isHeroicEnabled())
    }

    /**
     * Verifica que deshabilitar Heroic no destruye la ruta del emulador.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Test
    fun disableHeroic_doesNotDestroyEmulatorPath() = runTest {
        settingsRepo.saveEmulatorPath("pcsx2", "/home/usuario/ISOs")
        hgLauncherRepo.saveHeroicEnabled(true)
        hgLauncherRepo.saveHeroicEnabled(false)

        assertEquals("/home/usuario/ISOs", settingsRepo.getEmulatorPath("pcsx2"))
        assertFalse(hgLauncherRepo.isHeroicEnabled())
    }

    /**
     * Verifica que múltiples escrituras intercaladas de ambos repositorios
     * mantienen siempre la coherencia del estado final del archivo.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Test
    fun interleavedWrites_maintainFullConsistency() = runTest {
        settingsRepo.saveEmulatorPath("pcsx2", "/ruta/inicial")
        hgLauncherRepo.saveHeroicEnabled(true)
        settingsRepo.saveEmulatorPath("pcsx2", "/ruta/actualizada")
        hgLauncherRepo.saveHeroicEnabled(false)
        hgLauncherRepo.saveHeroicEnabled(true)

        assertEquals("/ruta/actualizada", settingsRepo.getEmulatorPath("pcsx2"))
        assertTrue(hgLauncherRepo.isHeroicEnabled())
    }

    /**
     * Verifica que `hideHGLauncher()` (que internamente llama a saveHeroicEnabled(false))
     * tampoco destruye la ruta del emulador.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Test
    fun hideHGLauncher_preservesEmulatorPath() = runTest {
        settingsRepo.saveEmulatorPath("pcsx2", "/home/usuario/ISOs")
        hgLauncherRepo.saveHeroicEnabled(true)
        hgLauncherRepo.hideHGLauncher()

        assertEquals("/home/usuario/ISOs", settingsRepo.getEmulatorPath("pcsx2"))
        assertFalse(hgLauncherRepo.isHeroicEnabled())
    }
}
