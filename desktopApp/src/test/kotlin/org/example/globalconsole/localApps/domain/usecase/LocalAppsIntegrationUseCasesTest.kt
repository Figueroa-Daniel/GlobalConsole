package org.example.globalconsole.localApps.domain.usecase

import kotlinx.coroutines.*
import kotlin.test.*
import java.nio.file.Files
import org.example.globalconsole.localApps.data.database.LocalAppsAccessAdapter
import org.example.globalconsole.localApps.data.repositoryImpl.LocalAppsRepositoryImpl
import org.example.globalconsole.localGames.data.database.LocalGamesAccessAdapter
import org.example.globalconsole.localGames.data.repositoryImpl.LocalGamesRepositoryImpl
import org.example.globalconsole.localGames.domain.usecase.*

/**
 * Casos de uso reales contra accesos y preferencias temporales, sin alterar instalaciones.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class LocalAppsIntegrationUseCasesTest {
    /**
     * Separa categorías e identidades y respeta ocultación, prioridad y argumentos.
     * @return Unit.
     * @throws Exception Si un fixture o aserción falla.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Test
    fun scanSeparatesAppsAndHonorsDesktopOverrides() = runBlocking {
        val root = Files.createTempDirectory("apps-discovery").toFile()
        try {
            val user = root.resolve("user").apply { mkdirs() }
            val system = root.resolve("system").apply { mkdirs() }
            val entries = listOf(
                Triple("editor", "Utility;", "\"/opt/My Editor/editor\" --name %c %U"),
                Triple("game", "Game;", "/opt/sample/sample"),
                Triple("browser", "WebBrowser;", "/opt/custom/custom"),
                Triple("alias", "Network;", "flatpak run com.brave.Browser"),
                Triple("emulator", "Utility;", "duckstation"),
                Triple("dolphin", "System;FileManager;", "dolphin"),
                Triple("hidden", "Office;", "/opt/hidden/hidden")
            )
            for ((id, categories, exec) in entries) {
                system.resolve("$id.desktop").writeText("[Desktop Entry]\nType=Application\nName=$id\nCategories=$categories\nExec=$exec\n")
            }
            user.resolve("hidden.desktop").writeText("[Desktop Entry]\nHidden=true\n")
            system.resolve("copy.desktop").writeText(system.resolve("editor.desktop").readText())
            val repo = LocalAppsRepositoryImpl(LocalAppsAccessAdapter(listOf(user, system), "Linux"), root.resolve("config.json"))
            val apps = GetInstalledLocalAppsUseCase(repo)()
            assertEquals(listOf("dolphin", "editor"), apps.map { it.name })
            assertEquals(listOf("/opt/My Editor/editor", "--name", "editor"), apps.last().arguments)
            assertFalse(AddLocalAppUseCase(repo)("app:linux:game.desktop"))
        } finally { root.deleteRecursively() }
    }

    /**
     * Las preferencias de juegos y aplicaciones se conservan incluso al escribir a la vez.
     * @return Unit.
     * @throws Exception Si falla la escritura o la validación.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Test
    fun concurrentPreferencesPreserveBothModulesAndUnknownFields() = runBlocking {
        val root = Files.createTempDirectory("apps-config").toFile()
        try {
            val entries = root.resolve("entries").apply { mkdirs() }
            entries.resolve("editor.desktop").writeText("[Desktop Entry]\nType=Application\nName=Editor\nCategories=Utility;\nExec=/bin/true\n")
            entries.resolve("game.desktop").writeText("[Desktop Entry]\nType=Application\nName=Game\nCategories=Game;\nExec=/bin/true\n")
            val config = root.resolve("config.json").apply { writeText("{\"enabledNavigators\":[\"BRAVE\"],\"custom\":42}") }
            val apps = LocalAppsRepositoryImpl(LocalAppsAccessAdapter(listOf(entries), "Linux"), config)
            val games = LocalGamesRepositoryImpl(LocalGamesAccessAdapter(listOf(entries), "Linux"), config)
            val app = GetInstalledLocalAppsUseCase(apps)().single()
            val game = GetInstalledLocalGamesUseCase(games)().single()
            coroutineScope {
                launch(Dispatchers.Default) { assertTrue(AddLocalAppUseCase(apps)(app.id)) }
                launch(Dispatchers.Default) { assertTrue(AddLocalGameUseCase(games)(game.id)) }
            }
            assertTrue(IsEnabledLocalGameUseCase(games)(game.id))
            val restarted = LocalAppsRepositoryImpl(LocalAppsAccessAdapter(listOf(entries), "Linux"), config)
            assertEquals(app.id, GetLocalAppsUseCase(restarted)().single().id)
            assertFalse(AddLocalAppUseCase(restarted)(app.id))
            assertTrue(RemoveLocalAppUseCase(restarted)(app.id))
            assertTrue(entries.resolve("editor.desktop").exists())
            assertTrue(config.readText().contains("\"custom\": 42"))
            assertTrue(config.readText().contains("BRAVE"))
            config.writeText("invalid")
            assertFailsWith<Exception> { AddLocalAppUseCase(apps)(app.id) }
            assertEquals("invalid", config.readText())
        } finally { root.deleteRecursively() }
    }
}
