package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelFactoryTest {
    @Test
    @DisplayName("Фабрика создаёт уровень по номеру")
    void createsLevel() {
        Level level = new LevelFactory().createLevel(1);
        assertEquals(1, level.number());
        assertTrue(level.scheme().hasIntersections());
    }

    @Test
    @DisplayName("Фабрика создаёт второй уровень")
    void createsSecondLevel() {
        Level level = new LevelFactory().createLevel(2);

        assertEquals(2, level.number());
        assertTrue(level.scheme().hasIntersections());
        assertEquals(5, level.scheme().getNodes().size());
    }

    @Test
    @DisplayName("Фабрика отклоняет неизвестный номер уровня")
    void unknownLevelRejected() {
        assertThrows(IllegalArgumentException.class, () -> new LevelFactory().createLevel(999));
    }
}
