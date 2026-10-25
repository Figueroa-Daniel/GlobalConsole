package org.example.globalconsole.localGames.domain.usecase

import kotlinx.coroutines.*
import kotlin.test.*
import java.nio.file.Files
import org.example.globalconsole.localGames.data.database.LocalGamesAccessAdapter
import org.example.globalconsole.localGames.data.repositoryImpl.LocalGamesRepositoryImpl

/**
 * Casos de uso reales contra procesos temporales propios, sin ejecutar juegos del usuario.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class LocalGameLifecycleUseCasesTest {
    /**
     * El cierre del caso de uso termina el proceso propio y permite otra ejecución.
     * @return Unit.
     * @throws Exception Si falla el proceso de fixture o la espera acotada.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Test
    fun close_stopsOwnedProcessAndAllowsRelaunch() = runBlocking {
        if (!System.getProperty("os.name").startsWith("Linux")) return@runBlocking
        val root = Files.createTempDirectory("local-process-").toFile()
        var pid: Long? = null
        try {
            val marker = root.resolve("pid")
            root.resolve("game.desktop").writeText("[Desktop Entry]\nType=Application\nName=Fixture\nCategories=Game;\nExec=/bin/sh -c \"echo \$\$ > ${marker.path}; exec /bin/sleep 30\"\n")
            val repo = LocalGamesRepositoryImpl(LocalGamesAccessAdapter(listOf(root), "Linux"), root.resolve("config.json"))
            val game = GetInstalledLocalGamesUseCase(repo)().single()
            val running = async { ExecuteLocalGameUseCase(repo)(game) }
            withTimeout(5000) { while (!marker.exists() || marker.readText().isBlank()) delay(20) }
            pid = marker.readText().trim().toLong()
            assertFalse(running.isCompleted)
            assertTrue(CloseLocalGameUseCase(repo)())
            assertTrue(withTimeout(5000) { running.await() })
            assertFalse(ProcessHandle.of(pid).map { it.isAlive }.orElse(false))
            root.resolve("game.desktop").writeText("[Desktop Entry]\nType=Application\nName=Fixture\nCategories=Game;\nExec=/bin/true\n")
            assertTrue(ExecuteLocalGameUseCase(repo)(game))
        } finally {
            pid?.let { ProcessHandle.of(it).ifPresent { process -> process.destroyForcibly() } }
            root.deleteRecursively()
        }
    }

    /**
     * Cancelar la ejecución no deja un proceso huérfano sin posibilidad de cierre.
     * @return Unit.
     * @throws Exception Si falla el proceso de fixture o la espera acotada.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Test
    fun cancellation_closesOwnedProcess() = runBlocking {
        if (!System.getProperty("os.name").startsWith("Linux")) return@runBlocking
        val root = Files.createTempDirectory("local-cancel-").toFile()
        var pid: Long? = null
        try {
            val marker = root.resolve("pid")
            root.resolve("game.desktop").writeText("[Desktop Entry]\nType=Application\nName=Fixture\nCategories=Game;\nExec=/bin/sh -c \"echo \$\$ > ${marker.path}; exec /bin/sleep 30\"\n")
            val repo = LocalGamesRepositoryImpl(LocalGamesAccessAdapter(listOf(root), "Linux"), root.resolve("config.json"))
            val game = GetInstalledLocalGamesUseCase(repo)().single()
            val running = launch { ExecuteLocalGameUseCase(repo)(game) }
            withTimeout(5000) { while (!marker.exists() || marker.readText().isBlank()) delay(20) }
            pid = marker.readText().trim().toLong()
            running.cancelAndJoin()
            assertFalse(ProcessHandle.of(pid).map { it.isAlive }.orElse(false))
        } finally {
            pid?.let { ProcessHandle.of(it).ifPresent { process -> process.destroyForcibly() } }
            root.deleteRecursively()
        }
    }
}
