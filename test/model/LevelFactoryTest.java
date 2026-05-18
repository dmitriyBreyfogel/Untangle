package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
    @DisplayName("Фабрика создаёт уровни с растущей сложностью")
    void createsLevelsWithGrowingComplexity() {
        LevelFactory factory = new LevelFactory();
        int[] expectedNodes = {0, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
        int[] expectedEdges = {0, 4, 8, 9, 11, 13, 16, 17, 18, 23, 24};
        int[] expectedIntersectingEdges = {0, 2, 2, 4, 9, 8, 13, 17, 14, 17, 22};

        for (int number = 1; number <= 10; number++) {
            Level level = factory.createLevel(number);

            assertEquals(number, level.number());
            assertTrue(level.scheme().hasIntersections());
            assertEquals(expectedNodes[number], level.scheme().getNodes().size());
            assertEquals(expectedEdges[number], level.scheme().getEdges().size());
            assertEquals(expectedIntersectingEdges[number], level.scheme().getIntersectingEdges().size());
            assertAllNodesConnected(level.scheme());
            assertGraphIsConnected(level.scheme());
        }
    }

    @Test
    @DisplayName("Фабрика задаёт разные стратегии движения узлов")
    void createsNodesWithDifferentMovementStrategies() {
        Level level = new LevelFactory().createLevel(1);
        List<Node> nodes = level.scheme().getNodes();

        assertInstanceOf(FreeMovementStrategy.class, nodes.get(0).getMovementStrategy());
        assertInstanceOf(FreeMovementStrategy.class, nodes.get(1).getMovementStrategy());
        assertInstanceOf(FixedMovementStrategy.class, nodes.get(2).getMovementStrategy());
        assertInstanceOf(HorizontalMovementStrategy.class, nodes.get(3).getMovementStrategy());
    }

    @Test
    @DisplayName("Фабрика отклоняет неизвестный номер уровня")
    void unknownLevelRejected() {
        assertThrows(IllegalArgumentException.class, () -> new LevelFactory().createLevel(999));
    }

    private static void assertAllNodesConnected(Scheme scheme) {
        for (Node node : scheme.getNodes()) {
            assertTrue(scheme.getEdgesOfNode(node).size() > 0);
        }
    }

    private static void assertGraphIsConnected(Scheme scheme) {
        Set<Node> visited = new HashSet<>();
        ArrayDeque<Node> queue = new ArrayDeque<>();
        Node firstNode = scheme.getNodes().getFirst();
        visited.add(firstNode);
        queue.add(firstNode);

        while (!queue.isEmpty()) {
            Node current = queue.removeFirst();
            for (Edge edge : scheme.getEdgesOfNode(current)) {
                Node next = edge.getNodeA() == current ? edge.getNodeB() : edge.getNodeA();
                if (visited.add(next)) {
                    queue.add(next);
                }
            }
        }

        assertEquals(scheme.getNodes().size(), visited.size());
    }
}
