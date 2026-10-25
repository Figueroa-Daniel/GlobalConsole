package org.example.globalconsole.localApps.domain.usecase

import kotlinx.coroutines.*
import kotlin.test.*
import java.nio.file.Files
import org.example.globalconsole.localApps.data.database.LocalAppsAccessAdapter
import org.example.globalconsole.localApps.data.repositoryImpl.LocalAppsRepositoryImpl

/**
 * Casos de uso reales contra procesos temporales propios, sin ejecutar aplicaciones del usuario.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class LocalAppLifecycleUseCasesTest {
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
            root.resolve("app.desktop").writeText("[Desktop Entry]\nType=Application\nName=Fixture\nCategories=Utility;\nExec=/bin/sh -c \"echo \$\$ > ${marker.path}; exec /bin/sleep 30\"\n")
            val repo = LocalAppsRepositoryImpl(LocalAppsAccessAdapter(listOf(root), "Linux"), root.resolve("config.json"))
            val game = GetInstalledLocalAppsUseCase(repo)().single()
            val running = async { ExecuteLocalAppUseCase(repo)(game) }
            withTimeout(5000) { while (!marker.exists() || marker.readText().isBlank()) delay(20) }
            pid = marker.readText().trim().toLong()
            assertFalse(running.isCompleted)
            assertTrue(CloseLocalAppUseCase(repo)())
            assertTrue(withTimeout(5000) { running.await() })
            assertFalse(ProcessHandle.of(pid).map { it.isAlive }.orElse(false))
            root.resolve("app.desktop").writeText("[Desktop Entry]\nType=Application\nName=Fixture\nCategories=Utility;\nExec=/bin/true\n")
            assertTrue(ExecuteLocalAppUseCase(repo)(game))
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
            root.resolve("app.desktop").writeText("[Desktop Entry]\nType=Application\nName=Fixture\nCategories=Utility;\nExec=/bin/sh -c \"echo \$\$ > ${marker.path}; exec /bin/sleep 30\"\n")
            val repo = LocalAppsRepositoryImpl(LocalAppsAccessAdapter(listOf(root), "Linux"), root.resolve("config.json"))
            val game = GetInstalledLocalAppsUseCase(repo)().single()
            val running = launch { ExecuteLocalAppUseCase(repo)(game) }
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
