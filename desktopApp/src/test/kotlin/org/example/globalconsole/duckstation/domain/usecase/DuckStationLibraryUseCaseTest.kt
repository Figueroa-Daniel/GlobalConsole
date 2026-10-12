package org.example.globalconsole.duckstation.domain.usecase

import kotlinx.coroutines.runBlocking
import org.example.globalconsole.duckstation.data.database.*
import org.example.globalconsole.duckstation.data.repositoryImpl.GameDuckStationRepositoryImpl
import org.example.globalconsole.settings.data.SettingsRepositoryImpl
import org.example.globalconsole.settings.domain.usecase.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.*

/** Comprueba el escaneo real desde el caso de uso público. @author Daniel Figueroa Vidal */
class DuckStationLibraryUseCaseTest {
    @get:Rule val temporary = TemporaryFolder()

    /** @return Unit. @throws Exception Si falla la carpeta temporal. @author Daniel Figueroa Vidal @since 2026-10-12 */
    @Test fun cueHidesOnlyReferencedTracksAndFindsIndividualCover() = runBlocking {
        val dir = temporary.newFolder()
        File(dir, "Disc.CUE").writeText("FILE \"Track one.BIN\" BINARY\nFILE \"audio.bin\" BINARY\n")
        File(dir, "Track one.BIN").writeText("")
        File(dir, "audio.bin").writeText("")
        File(dir, "Standalone.bin").writeText("")
        File(dir, "Disc.jpg").writeText("")
        File(dir, "cover.png").writeText("")
        val settings = SettingsRepositoryImpl(temporary.newFile())
        SaveEmulatorPathUseCase(settings)("duckstation", dir.absolutePath)
        val useCase = GetGamesDuckStationUseCase(GameDuckStationRepositoryImpl(
            GameDuckStationFileSystemAdapter(GetEmulatorPathUseCase(settings)), GameDuckStationAdapter()))
        val games = useCase()
        assertEquals(setOf("Disc", "Standalone"), games.map { it.name }.toSet())
        assertEquals(File(dir, "Disc.jpg").absolutePath, games.single { it.name == "Disc" }.image)
        assertNull(games.single { it.name == "Standalone" }.image)
        assertTrue(games.single { it.name == "Disc" }.urlGameExecute.endsWith("Disc.CUE"))
    }

    /** @return Unit. @throws Exception Si falla la carpeta temporal. @author Daniel Figueroa Vidal @since 2026-10-12 */
    @Test fun cueWithRelativeWindowsSeparatorKeepsUnreferencedFilesAndOwnFolderCover() = runBlocking {
        val dir = temporary.newFolder()
        val child = File(dir, "tracks").apply { mkdirs() }
        File(dir, "Disc.cue").writeText("FILE \"tracks\\data.img\" BINARY")
        File(child, "data.img").writeText("")
        File(dir, "cover.png").writeText("")
        File(child, "Other.bin").writeText("")
        val settings = SettingsRepositoryImpl(temporary.newFile())
        SaveEmulatorPathUseCase(settings)("duckstation", dir.absolutePath)
        val games = GetGamesDuckStationUseCase(GameDuckStationRepositoryImpl(
            GameDuckStationFileSystemAdapter(GetEmulatorPathUseCase(settings)), GameDuckStationAdapter()))()
        assertEquals(setOf("Disc", "Other"), games.map { it.name }.toSet())
        assertEquals(File(dir, "cover.png").absolutePath, games.single { it.name == "Disc" }.image)
    }
    /** @return Unit. @throws Exception Si falla la carpeta temporal. @author Daniel Figueroa Vidal @since 2026-10-12 */
    @Test fun unsupportedDeletionReportsFailureWithoutRemovingFiles() = runBlocking {
        val dir = temporary.newFolder()
        val game = File(dir, "Keep.chd").apply { writeText("keep") }
        val settings = SettingsRepositoryImpl(temporary.newFile())
        SaveEmulatorPathUseCase(settings)("duckstation", dir.absolutePath)
        val repo = GameDuckStationRepositoryImpl(GameDuckStationFileSystemAdapter(GetEmulatorPathUseCase(settings)), GameDuckStationAdapter())
        val id = GetGamesDuckStationUseCase(repo)().single().id
        assertFalse(DeleteGameDuckStationUseCase(repo)(id))
        assertTrue(game.exists())
    }
}
