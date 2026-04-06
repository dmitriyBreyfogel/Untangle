package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

class GameFieldValidationTest {
    @Test
    @DisplayName("Поле не принимает null схему")
    void fieldRejectsNullScheme() {
        assertThrows(NullPointerException.class, () -> new GameField(100, 100, null));
    }

    @Test
    @DisplayName("Поле не принимает null точку")
    void fieldRejectsNullPoint() {
        Scheme scheme = Scheme.create(
                List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 10), new Point2D.Double(0, 10), new Point2D.Double(10, 0)),
                Map.of(0, List.of(1, 2), 2, List.of(3))
        );
        GameField field = new GameField(100, 100, scheme);
        assertThrows(NullPointerException.class, () -> field.canPlace(null));
    }
}

