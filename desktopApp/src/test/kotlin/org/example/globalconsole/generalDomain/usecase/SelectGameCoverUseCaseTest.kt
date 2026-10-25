package org.example.globalconsole.generalDomain.usecase

import org.junit.Test
import kotlin.test.*

/** Evita carátulas compartidas accidentalmente. @author Daniel Figueroa Vidal */
class SelectGameCoverUseCaseTest {
    /** @return Unit. @throws Nothing No lanza excepciones. @author Daniel Figueroa Vidal @since 2026-10-12 */
    @Test fun matchingNameAndIndividualCropWinRegardlessOfOrder() {
        val select = SelectGameCoverUseCase()
        assertEquals("Game.cover.png", select("Game", listOf("cover.png", "Game.jpg", "Game.cover.png"), 2))
        assertEquals("GAME.JPG", select("Game", listOf("Other.png", "GAME.JPG"), 2))
        assertNull(select("Missing", listOf("Other.png", "cover.png"), 2))
    }
    /** @return Unit. @throws Nothing No lanza excepciones. @author Daniel Figueroa Vidal @since 2026-10-12 */
    @Test fun genericCoverOnlyAppliesToOneGameAndAmbiguityHasNoGuess() {
        val select = SelectGameCoverUseCase()
        assertEquals("cover_cropped.png", select("Game", listOf("cover.jpg", "cover_cropped.png"), 1))
        assertEquals("poster.jpg", select("Game", listOf("poster.jpg"), 1))
        assertNull(select("Game", listOf("a.jpg", "b.jpg"), 1))
        assertNull(select("Game", emptyList(), 1))
    }
}
