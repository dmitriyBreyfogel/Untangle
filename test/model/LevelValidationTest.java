package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LevelValidationTest {
    @Test
    @DisplayName("Уровень не принимает null параметры")
    void levelRejectsNullArgs() {
        assertThrows(NullPointerException.class, () -> new Level(1, null, Map.of(), 100, 100));
        assertThrows(NullPointerException.class, () -> new Level(1, List.of(new Point2D.Double(0, 0), new Point2D.Double(1, 0), new Point2D.Double(0, 1)), null, 100, 100));
    }
}

