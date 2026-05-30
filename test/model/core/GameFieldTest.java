package model.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameFieldTest {
    @Test
    @DisplayName("Поле принимает точку внутри границ")
    void canPlaceInside() {
        GameField field = new GameField(100, 100);
        assertTrue(field.canPlace(new Point2D.Double(50, 50)));
    }

    @Test
    @DisplayName("Поле отклоняет точку вне границ")
    void cannotPlaceOutside() {
        GameField field = new GameField(100, 100);
        assertFalse(field.canPlace(new Point2D.Double(-1, 50)));
        assertFalse(field.canPlace(new Point2D.Double(50, 101)));
    }

    @Test
    @DisplayName("Поле принимает точку на границе")
    void canPlaceOnBoundary() {
        GameField field = new GameField(100, 100);
        assertTrue(field.canPlace(new Point2D.Double(0, 0)));
        assertTrue(field.canPlace(new Point2D.Double(100, 100)));
        assertTrue(field.canPlace(new Point2D.Double(100, 0)));
        assertTrue(field.canPlace(new Point2D.Double(0, 100)));
    }

    @Test
    @DisplayName("Поле отклоняет точку с нечисловыми координатами")
    void cannotPlaceNaNPoint() {
        GameField field = new GameField(100, 100);
        assertFalse(field.canPlace(new Point2D.Double(Double.NaN, 0)));
        assertFalse(field.canPlace(new Point2D.Double(0, Double.NaN)));
    }

    @Test
    @DisplayName("Поле требует положительные размеры")
    void fieldRejectsNonPositiveDimensions() {
        assertThrows(IllegalArgumentException.class, () -> new GameField(0, 100));
        assertThrows(IllegalArgumentException.class, () -> new GameField(100, 0));
        assertThrows(IllegalArgumentException.class, () -> new GameField(-1, 100));
    }

    @Test
    @DisplayName("Поле отклоняет точку с бесконечными координатами")
    void cannotPlaceInfinitePoint() {
        GameField field = new GameField(100, 100);
        assertFalse(field.canPlace(new Point2D.Double(Double.POSITIVE_INFINITY, 0)));
        assertFalse(field.canPlace(new Point2D.Double(0, Double.NEGATIVE_INFINITY)));
    }

    @Test
    @DisplayName("Поле хранит размеры")
    void fieldExposesState() {
        GameField field = new GameField(100, 75);

        assertTrue(field.width() == 100);
        assertTrue(field.height() == 75);
    }
}
