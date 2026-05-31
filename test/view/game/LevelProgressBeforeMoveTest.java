package view.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class LevelProgressBeforeMoveTest {
    @Test
    @DisplayName("Снимок прогресса хранит состояние уровней до хода")
    void storesLevelProgressBeforeMove() {
        LevelProgressBeforeMove levelProgress = new LevelProgressBeforeMove(3, 2);

        assertEquals(3, levelProgress.levelNumberBeforeMove());
        assertEquals(2, levelProgress.maxCompletedLevelNumberBeforeMove());
    }

    @Test
    @DisplayName("Снимки прогресса сравниваются по значениям")
    void comparesByValues() {
        LevelProgressBeforeMove levelProgress = new LevelProgressBeforeMove(3, 2);

        assertEquals(new LevelProgressBeforeMove(3, 2), levelProgress);
        assertEquals(new LevelProgressBeforeMove(3, 2).hashCode(), levelProgress.hashCode());
        assertNotEquals(new LevelProgressBeforeMove(4, 2), levelProgress);
    }
}
