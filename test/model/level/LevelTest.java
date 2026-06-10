package model.level;

import model.core.Node;
import model.core.Scheme;
import model.movement.FixedMovementStrategy;
import model.movement.FreeMovementStrategy;
import model.movement.HorizontalMovementStrategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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

    @Test
    @DisplayName("Уровень создаёт поле и схему с ожидаемыми параметрами")
    void levelCreatesFieldAndScheme() {
        Level level = new LevelFactory().createLevel(1);

        assertEquals(100, level.gameField().width());
        assertEquals(100, level.gameField().height());
        assertEquals(4, level.scheme().getNodes().size());
    }

    @Test
    @DisplayName("Уровень копирует входные коллекции")
    void levelCopiesInputCollections() {
        List<Point2D> coordinates = new ArrayList<>(List.of(
                new Point2D.Double(0, 0),
                new Point2D.Double(1, 0),
                new Point2D.Double(0, 1)
        ));
        Map<Integer, List<Integer>> connections = new HashMap<>();
        connections.put(0, new ArrayList<>(List.of(1, 2)));
        connections.put(1, new ArrayList<>(List.of(2)));

        Level level = new Level(1, coordinates, connections, 100, 100);

        ((Point2D.Double) coordinates.getFirst()).setLocation(50, 50);
        coordinates.clear();
        connections.get(0).clear();
        connections.clear();

        assertEquals(3, level.scheme().getNodes().size());
        assertEquals(0, level.scheme().getNodes().getFirst().getX());
        assertEquals(0, level.scheme().getNodes().getFirst().getY());
        assertEquals(3, level.scheme().getEdges().size());
    }

    @Test
    @DisplayName("Уровень передаёт стратегии движения в схему")
    void levelPassesMovementStrategiesToScheme() {
        Level level = new Level(
                1,
                List.of(
                        new Point2D.Double(0, 0),
                        new Point2D.Double(1, 0),
                        new Point2D.Double(0, 1)
                ),
                Map.of(0, List.of(1, 2), 1, List.of(2)),
                Map.of(0, new FixedMovementStrategy(), 1, new HorizontalMovementStrategy()),
                100,
                100
        );

        assertEquals(FixedMovementStrategy.class, level.scheme().getNodes().get(0).movementStrategyType());
        assertEquals(HorizontalMovementStrategy.class, level.scheme().getNodes().get(1).movementStrategyType());
        assertEquals(FreeMovementStrategy.class, level.scheme().getNodes().get(2).movementStrategyType());
    }
}
