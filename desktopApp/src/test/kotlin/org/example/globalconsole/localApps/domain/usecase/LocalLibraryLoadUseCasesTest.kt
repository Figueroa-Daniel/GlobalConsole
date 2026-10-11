package org.example.globalconsole.localApps.domain.usecase

import kotlinx.coroutines.runBlocking
import kotlin.test.*
import java.nio.file.Files
import org.example.globalconsole.localApps.data.database.LocalAppsAccessAdapter
import org.example.globalconsole.localApps.data.repositoryImpl.LocalAppsRepositoryImpl
import org.example.globalconsole.localGames.data.database.LocalGamesAccessAdapter
import org.example.globalconsole.localGames.data.repositoryImpl.LocalGamesRepositoryImpl
import org.example.globalconsole.localGames.domain.usecase.GetLocalGamesUseCase
import org.example.globalconsole.localGames.domain.usecase.GetInstalledLocalGamesUseCase

/**
 * Evita trabajo de detección e iconos ajenos al cargar la biblioteca mediante casos de uso.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class LocalLibraryLoadUseCasesTest {
    /**
     * Una selección vacía no debe invocar ni siquiera el detector nativo de Windows.
     * @return Unit.
     * @throws Exception Si se intenta detectar programas o falla el fixture.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Test
    fun emptySelectionDoesNotScanSystem() = runBlocking {
        val root = Files.createTempDirectory("empty-library").toFile()
        try {
            val config = root.resolve("config.json").apply { writeText("{}") }
            val apps = LocalAppsRepositoryImpl(LocalAppsAccessAdapter(emptyList(), "Windows"), config)
            val games = LocalGamesRepositoryImpl(LocalGamesAccessAdapter(emptyList(), "Windows"), config)
            assertTrue(GetLocalAppsUseCase(apps)().isEmpty())
            assertTrue(GetLocalGamesUseCase(games)().isEmpty())
        } finally { root.deleteRecursively() }
    }

    /**
     * La carga y la activación resuelven solo iconos admitidos y respetan cambios del sistema.
     * @return Unit.
     * @throws Exception Si falla una aserción o la preparación de accesos.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Test
    fun selectedEntriesAndCategoriesAreFilteredBeforeIcons() = runBlocking {
        val root = Files.createTempDirectory("filtered-library").toFile()
        try {
            val entries = root.resolve("entries").apply { mkdirs() }
            for ((name, category) in listOf("selected" to "Utility", "other" to "Office", "game" to "Game", "browser" to "WebBrowser")) {
                entries.resolve("$name.desktop").writeText("[Desktop Entry]\nType=Application\nName=$name\nCategories=$category;\nExec=/bin/true --$name\nIcon=$name\n")
            }
            val config = root.resolve("config.json").apply {
                writeText("{\"enabledLocalApps\":[\"app:linux:selected.desktop\"],\"enabledLocalGames\":[\"local:linux:game.desktop\"]}")
            }
            val icons = mutableListOf<String>()
            val apps = LocalAppsRepositoryImpl(LocalAppsAccessAdapter(listOf(entries), "Linux", iconResolver = { icons.add(it.orEmpty()); null }), config)
            val games = LocalGamesRepositoryImpl(LocalGamesAccessAdapter(listOf(entries), "Linux", iconResolver = { icons.add(it.orEmpty()); null }), config)
            assertEquals(listOf("selected"), GetLocalAppsUseCase(apps)().map { it.name })
            assertEquals(listOf("selected"), icons)
            icons.clear()
            assertEquals(listOf("game"), GetLocalGamesUseCase(games)().map { it.name })
            assertEquals(listOf("game"), icons)
            icons.clear()
            GetInstalledLocalGamesUseCase(games)()
            assertEquals(listOf("game"), icons)
            icons.clear()
            GetInstalledLocalAppsUseCase(apps)()
            assertEquals(setOf("other", "selected"), icons.toSet())
            icons.clear()
            assertTrue(AddLocalAppUseCase(apps)("app:linux:other.desktop"))
            assertEquals(listOf("other"), icons)
            entries.resolve("selected.desktop").writeText("[Desktop Entry]\nHidden=true\n")
            assertEquals(listOf("other"), GetLocalAppsUseCase(apps)().map { it.name })
            entries.resolve("other.desktop").delete()
            assertTrue(GetLocalAppsUseCase(apps)().isEmpty())
        } finally { root.deleteRecursively() }
    }
}
