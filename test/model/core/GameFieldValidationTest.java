package model.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertThrows;

class GameFieldValidationTest {
    @Test
    @DisplayName("Поле не принимает пустую точку")
    void fieldRejectsNullPoint() {
        GameField field = new GameField(100, 100);
        assertThrows(NullPointerException.class, () -> field.canPlace(null));
    }

    @Test
    @DisplayName("Поле не принимает бесконечные размеры")
    void fieldRejectsInfiniteDimensions() {
        assertThrows(IllegalArgumentException.class, () -> new GameField(Double.POSITIVE_INFINITY, 100));
        assertThrows(IllegalArgumentException.class, () -> new GameField(100, Double.NEGATIVE_INFINITY));
    }
}
