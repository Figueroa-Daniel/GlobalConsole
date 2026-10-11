package org.example.globalconsole.localGames.domain.usecase

import kotlinx.coroutines.test.runTest
import kotlin.test.*
import java.nio.file.Files
import org.example.globalconsole.localGames.data.database.LocalGamesAccessAdapter
import org.example.globalconsole.localGames.data.repositoryImpl.LocalGamesRepositoryImpl

/**
 * Verifica los casos de uso con archivos aislados y el repositorio real.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class LocalGamesIntegrationUseCasesTest {
    /**
     * Comprueba prioridad, categorías, argumentos y exclusión sin ejecutar juegos.
     * @return Unit.
     * @throws Exception Si falla un archivo temporal.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Test
    fun discovery_filtersAndPreservesArguments() = runTest {
        val root = Files.createTempDirectory("local-games").toFile()
        try {
            val user = root.resolve("user").apply { mkdirs() }
            val system = root.resolve("system").apply { mkdirs() }
            user.resolve("hidden.desktop").writeText("[Desktop Entry]\nHidden=true\n")
            system.resolve("hidden.desktop").writeText("[Desktop Entry]\nType=Application\nName=Hidden\nCategories=Game;\nExec=/bin/true\n")
            user.resolve("game.desktop").writeText("""
                [Desktop Entry]
                Type=Application
                Name=Mi juego
                Categories=ActionGame;Game;
                Exec="/path with spaces/game" --title %c %U %%
                [Desktop Action Other]
                Name=Otra acción
                Exec=incorrecto
            """.trimIndent())
            user.resolve("duplicate.desktop").writeText(user.resolve("game.desktop").readText())
            user.resolve("tool.desktop").writeText("[Desktop Entry]\nType=Application\nName=Tool\nCategories=GameTools;\nExec=tool\n")
            user.resolve("emu.desktop").writeText("[Desktop Entry]\nType=Application\nName=Dolphin\nCategories=Game;\nExec=dolphin-emu\n")
            val repo = LocalGamesRepositoryImpl(LocalGamesAccessAdapter(listOf(user, system), "Linux"), root.resolve("config.json"))
            val games = GetInstalledLocalGamesUseCase(repo)()
            assertEquals(1, games.size)
            assertEquals("Mi juego", games.single().name)
            assertEquals(listOf("/path with spaces/game", "--title", "Mi juego", "%"), games.single().arguments)
        } finally { root.deleteRecursively() }
    }

    /**
     * Conserva campos ajenos y preferencias entre instancias sin borrar archivos.
     * @return Unit.
     * @throws Exception Si falla un archivo temporal.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Test
    fun preferences_surviveRestartAndPreserveOtherFields() = runTest {
        val root = Files.createTempDirectory("local-games").toFile()
        try {
            val apps = root.resolve("apps").apply { mkdirs() }
            val entry = apps.resolve("game.desktop").apply {
                writeText("[Desktop Entry]\nType=Application\nName=Juego\nCategories=Game;\nExec=/bin/true\n")
            }
            val config = root.resolve("config.json").apply { writeText("{\"heroicEnabled\":true,\"futureField\":42}") }
            val adapter = LocalGamesAccessAdapter(listOf(apps), "Linux")
            val repo = LocalGamesRepositoryImpl(adapter, config)
            val game = GetInstalledLocalGamesUseCase(repo)().single()
            assertFalse(AddLocalGameUseCase(repo)("unknown"))
            assertTrue(AddLocalGameUseCase(repo)(game.id))
            assertFalse(AddLocalGameUseCase(repo)(game.id))
            val restarted = LocalGamesRepositoryImpl(adapter, config)
            assertEquals(game.id, GetLocalGamesUseCase(restarted)().single().id)
            assertTrue(config.readText().contains("\"futureField\": 42"))
            assertTrue(config.readText().contains("\"heroicEnabled\": true"))
            assertTrue(RemoveLocalGameUseCase(restarted)(game.id))
            assertTrue(entry.exists())
            assertTrue(GetLocalGamesUseCase(restarted)().isEmpty())
            config.writeText("broken")
            assertFailsWith<Exception> { AddLocalGameUseCase(repo)(game.id) }
            assertEquals("broken", config.readText())
        } finally { root.deleteRecursively() }
    }
    /**
     * Conserva accesos de juegos Steam y argumentos Flatpak sin mostrar launchers.
     * @return Unit.
     * @throws Exception Si falla un archivo temporal.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Test
    fun discovery_keepsGameLaunchArguments() = runTest {
        val root = Files.createTempDirectory("local-launch-args").toFile()
        try {
            root.resolve("steam.desktop").writeText("[Desktop Entry]\nType=Application\nName=Steam\nCategories=Game;\nExec=steam\n")
            root.resolve("steam-game.desktop").writeText("[Desktop Entry]\nType=Application\nName=Portal\nCategories=Game;\nExec=steam steam://rungameid/400\n")
            root.resolve("flatpak.desktop").writeText("[Desktop Entry]\nType=Application\nName=Puzzle\nCategories=Game;\nX-Flatpak=org.example.Puzzle\nExec=flatpak run --branch=stable --command=puzzle org.example.Puzzle --fullscreen %U\n")
            root.resolve("invalid.desktop").writeText("[Desktop Entry]\nType=Application\nName=Invalid\nCategories=Game;\nExec=game %z\n")
            val repo = LocalGamesRepositoryImpl(LocalGamesAccessAdapter(listOf(root), "Linux"), root.resolve("config.json"))
            val games = GetInstalledLocalGamesUseCase(repo)()
            assertEquals(listOf("Portal", "Puzzle"), games.map { it.name })
            assertEquals(listOf("steam", "steam://rungameid/400"), games.first().arguments)
            assertEquals(listOf("flatpak", "run", "--branch=stable", "--command=puzzle", "org.example.Puzzle", "--fullscreen"), games.last().arguments)
        } finally { root.deleteRecursively() }
    }

    /**
     * Resuelve iconos con nombre reverse-DNS sin tratar el último segmento como extensión.
     * @return Unit.
     * @throws Exception Si falla un archivo temporal.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Test
    fun discovery_resolvesReverseDnsIcons() = runTest {
        val root = Files.createTempDirectory("local-icons").toFile()
        val previousHome = System.getProperty("user.home")
        try {
            System.setProperty("user.home", root.absolutePath)
            val icon = root.resolve(".local/share/icons/hicolor/128x128/apps/org.example.Puzzle.png")
            icon.parentFile.mkdirs()
            icon.writeBytes(byteArrayOf(1))
            val apps = root.resolve("apps").apply { mkdirs() }
            apps.resolve("puzzle.desktop").writeText("[Desktop Entry]\nType=Application\nName=Puzzle\nCategories=Game;\nExec=/bin/true\nIcon=org.example.Puzzle\n")
            val repo = LocalGamesRepositoryImpl(LocalGamesAccessAdapter(listOf(apps), "Linux"), root.resolve("config.json"))
            assertEquals(icon.absolutePath, GetInstalledLocalGamesUseCase(repo)().single().image)
        } finally {
            System.setProperty("user.home", previousHome)
            root.deleteRecursively()
        }
    }
}
