package view.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MoveResultTest {
    @Test
    @DisplayName("Результат хода хранит состояние до перемещения")
    void storesPreviousState() {
        MoveResult result = new MoveResult(3, 2);

        assertEquals(3, result.previousLevelNumber());
        assertEquals(2, result.previousMaxCompletedLevelNumber());
    }

    @Test
    @DisplayName("Результаты хода сравниваются по значениям")
    void comparesByValues() {
        MoveResult result = new MoveResult(3, 2);

        assertEquals(new MoveResult(3, 2), result);
        assertEquals(new MoveResult(3, 2).hashCode(), result.hashCode());
        assertNotEquals(new MoveResult(4, 2), result);
    }
}
