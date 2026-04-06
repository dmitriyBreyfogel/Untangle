package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameFieldTest {
    @Test
    @DisplayName("Поле принимает точку внутри границ")
    void canPlaceInside() {
        Scheme scheme = Scheme.create(
                List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 10), new Point2D.Double(0, 10), new Point2D.Double(10, 0)),
                Map.of(0, List.of(1, 2), 2, List.of(3))
        );
        GameField field = new GameField(100, 100, scheme);
        assertTrue(field.canPlace(new Point2D.Double(50, 50)));
    }

    @Test
    @DisplayName("Поле отклоняет точку вне границ")
    void cannotPlaceOutside() {
        Scheme scheme = Scheme.create(
                List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 10), new Point2D.Double(0, 10), new Point2D.Double(10, 0)),
                Map.of(0, List.of(1, 2), 2, List.of(3))
        );
        GameField field = new GameField(100, 100, scheme);
        assertFalse(field.canPlace(new Point2D.Double(-1, 50)));
        assertFalse(field.canPlace(new Point2D.Double(50, 101)));
    }

    @Test
    @DisplayName("Поле принимает точку на границе")
    void canPlaceOnBoundary() {
        Scheme scheme = Scheme.create(
                List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 10), new Point2D.Double(0, 10), new Point2D.Double(10, 0)),
                Map.of(0, List.of(1, 2), 2, List.of(3))
        );
        GameField field = new GameField(100, 100, scheme);
        assertTrue(field.canPlace(new Point2D.Double(0, 0)));
        assertTrue(field.canPlace(new Point2D.Double(100, 100)));
        assertTrue(field.canPlace(new Point2D.Double(100, 0)));
        assertTrue(field.canPlace(new Point2D.Double(0, 100)));
    }

    @Test
    @DisplayName("Поле отклоняет точку с NaN координатами")
    void cannotPlaceNaNPoint() {
        Scheme scheme = Scheme.create(
                List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 10), new Point2D.Double(0, 10), new Point2D.Double(10, 0)),
                Map.of(0, List.of(1, 2), 2, List.of(3))
        );
        GameField field = new GameField(100, 100, scheme);
        assertFalse(field.canPlace(new Point2D.Double(Double.NaN, 0)));
        assertFalse(field.canPlace(new Point2D.Double(0, Double.NaN)));
    }

    @Test
    @DisplayName("Поле требует положительные размеры")
    void fieldRejectsNonPositiveDimensions() {
        Scheme scheme = Scheme.create(
                List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 10), new Point2D.Double(0, 10), new Point2D.Double(10, 0)),
                Map.of(0, List.of(1, 2), 2, List.of(3))
        );
        assertThrows(IllegalArgumentException.class, () -> new GameField(0, 100, scheme));
        assertThrows(IllegalArgumentException.class, () -> new GameField(100, 0, scheme));
        assertThrows(IllegalArgumentException.class, () -> new GameField(-1, 100, scheme));
    }

    @Test
    @DisplayName("Поле отклоняет точку с бесконечными координатами")
    void cannotPlaceInfinitePoint() {
        Scheme scheme = Scheme.create(
                List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 10), new Point2D.Double(0, 10), new Point2D.Double(10, 0)),
                Map.of(0, List.of(1, 2), 2, List.of(3))
        );
        GameField field = new GameField(100, 100, scheme);
        assertFalse(field.canPlace(new Point2D.Double(Double.POSITIVE_INFINITY, 0)));
        assertFalse(field.canPlace(new Point2D.Double(0, Double.NEGATIVE_INFINITY)));
    }

    @Test
    @DisplayName("Поле хранит размеры и привязанную схему")
    void fieldExposesState() {
        Scheme scheme = Scheme.create(
                List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 10), new Point2D.Double(0, 10), new Point2D.Double(10, 0)),
                Map.of(0, List.of(1, 2), 2, List.of(3))
        );
        GameField field = new GameField(100, 75, scheme);

        assertTrue(field.width() == 100);
        assertTrue(field.height() == 75);
        assertSame(scheme, field.scheme());
    }
}
