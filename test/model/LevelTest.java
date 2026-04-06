package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelTest {
    @Test
    @DisplayName("Сброс уровня возвращает узлы в начальные координаты")
    void resetRestoresInitialCoordinates() {
        Level level = new LevelFactory().createLevel(1);
        assertEquals(1, level.number());
        Scheme scheme = level.scheme();
        Node node0 = scheme.getNodes().get(0);

        scheme.moveNode(node0, new Point2D.Double(50, 50));
        assertEquals(50, node0.getX());

        level.reset();
        assertEquals(10, node0.getX());
        assertEquals(10, node0.getY());
        assertTrue(scheme.hasIntersections());
    }

    @Test
    @DisplayName("Уровень не принимает неположительный номер")
    void levelRejectsNonPositiveNumber() {
        assertThrows(IllegalArgumentException.class, () -> new Level(0, List.of(), Map.of(), 100, 100));
        assertThrows(IllegalArgumentException.class, () -> new Level(-1, List.of(), Map.of(), 100, 100));
    }
}
