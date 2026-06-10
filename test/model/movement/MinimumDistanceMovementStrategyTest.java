package model.movement;

import model.core.Node;
import model.core.Scheme;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MinimumDistanceMovementStrategyTest {
    private static final double EPS = 1e-9;

    @AfterEach
    void resetMinimumDistance() {
        MinimumDistanceMovementStrategy.setMinimumDistance(10.0);
    }

    @Test
    @DisplayName("Стратегия разрешает движение вне запретных областей")
    void allowsMoveOutsideForbiddenAreas() {
        MinimumDistanceMovementStrategy strategy = new MinimumDistanceMovementStrategy(List.of(
                new Point2D.Double(80, 80),
                new Point2D.Double(90, 10)
        ));

        Point2D resolved = strategy.resolveMove(new Point2D.Double(0, 0), new Point2D.Double(20, 20));

        assertEquals(20, resolved.getX(), EPS);
        assertEquals(20, resolved.getY(), EPS);
    }

    @Test
    @DisplayName("Стратегия останавливает узел на границе запретной области")
    void stopsAtForbiddenAreaBoundary() {
        MinimumDistanceMovementStrategy strategy = new MinimumDistanceMovementStrategy(List.of(
                new Point2D.Double(20, 0)
        ));

        Point2D resolved = strategy.resolveMove(new Point2D.Double(0, 0), new Point2D.Double(18, 0));

        assertEquals(10, resolved.getX(), EPS);
        assertEquals(0, resolved.getY(), EPS);
    }

    @Test
    @DisplayName("Стратегия разрешает конечную позицию вне запретной области")
    void allowsFinalPositionOutsideForbiddenArea() {
        MinimumDistanceMovementStrategy strategy = new MinimumDistanceMovementStrategy(List.of(
                new Point2D.Double(20, 0)
        ));

        Point2D resolved = strategy.resolveMove(new Point2D.Double(0, 0), new Point2D.Double(40, 0));

        assertEquals(40, resolved.getX(), EPS);
        assertEquals(0, resolved.getY(), EPS);
    }

    @Test
    @DisplayName("Стратегия считает пересекающиеся запретные области единым барьером")
    void treatsOverlappingForbiddenAreasAsOneBarrier() {
        Point2D firstNodePosition = new Point2D.Double(20, 0);
        Point2D secondNodePosition = new Point2D.Double(35, 0);
        MinimumDistanceMovementStrategy strategy = new MinimumDistanceMovementStrategy(List.of(
                firstNodePosition,
                secondNodePosition
        ));

        Point2D resolved = strategy.resolveMove(new Point2D.Double(0, 0), new Point2D.Double(27.5, 0));

        assertEquals(27.5, resolved.getX(), EPS);
        assertEquals(6.614378277661476, Math.abs(resolved.getY()), EPS);
        assertTrue(resolved.distance(firstNodePosition) >= 10 - EPS);
        assertTrue(resolved.distance(secondNodePosition) >= 10 - EPS);
    }

    @Test
    @DisplayName("Стратегия использует ближайшую границу для запрошенной позиции")
    void usesNearestBoundaryForRequestedPosition() {
        MinimumDistanceMovementStrategy strategy = new MinimumDistanceMovementStrategy(List.of(
                new Point2D.Double(50, 0),
                new Point2D.Double(20, 0)
        ));

        Point2D resolved = strategy.resolveMove(new Point2D.Double(0, 0), new Point2D.Double(18, 0));

        assertEquals(10, resolved.getX(), EPS);
        assertEquals(0, resolved.getY(), EPS);
    }

    @Test
    @DisplayName("Стратегия игнорирует точку текущего узла в списке позиций")
    void ignoresCurrentNodePosition() {
        MinimumDistanceMovementStrategy strategy = new MinimumDistanceMovementStrategy(List.of(
                new Point2D.Double(0, 0),
                new Point2D.Double(80, 80)
        ));

        Point2D resolved = strategy.resolveMove(new Point2D.Double(0, 0), new Point2D.Double(5, 0));

        assertEquals(5, resolved.getX(), EPS);
        assertEquals(0, resolved.getY(), EPS);
    }

    @Test
    @DisplayName("Стратегия использует статически изменяемое минимальное расстояние")
    void usesStaticMinimumDistance() {
        MinimumDistanceMovementStrategy strategy = new MinimumDistanceMovementStrategy(List.of(
                new Point2D.Double(20, 0)
        ));

        MinimumDistanceMovementStrategy.setMinimumDistance(5.0);
        Point2D resolvedWithFive = strategy.resolveMove(new Point2D.Double(0, 0), new Point2D.Double(18, 0));

        MinimumDistanceMovementStrategy.setMinimumDistance(8.0);
        Point2D resolvedWithEight = strategy.resolveMove(new Point2D.Double(0, 0), new Point2D.Double(18, 0));

        assertEquals(15, resolvedWithFive.getX(), EPS);
        assertEquals(12, resolvedWithEight.getX(), EPS);
    }

    @Test
    @DisplayName("Стратегия читает изменённые координаты переданных точек")
    void readsMutatedNodePositions() {
        Point2D.Double nodePosition = new Point2D.Double(100, 0);
        MinimumDistanceMovementStrategy strategy = new MinimumDistanceMovementStrategy(List.of(nodePosition));
        nodePosition.setLocation(20, 0);

        Point2D resolved = strategy.resolveMove(new Point2D.Double(0, 0), new Point2D.Double(18, 0));

        assertEquals(10, resolved.getX(), EPS);
        assertEquals(0, resolved.getY(), EPS);
    }

    @Test
    @DisplayName("Стратегия в схеме учитывает обновлённые позиции других узлов")
    void tracksMovedNodesInsideScheme() {
        Scheme scheme = Scheme.create(
                List.of(
                        new Point2D.Double(0, 0),
                        new Point2D.Double(30, 0),
                        new Point2D.Double(0, 30)
                ),
                Map.of(0, List.of(1, 2), 1, List.of(2)),
                Map.of(0, new MinimumDistanceMovementStrategy())
        );
        Node minimumDistanceNode = scheme.getNodes().get(0);
        Node regularNode = scheme.getNodes().get(1);

        scheme.moveNode(regularNode, new Point2D.Double(20, 0));
        scheme.moveNode(minimumDistanceNode, new Point2D.Double(15, 0));

        assertEquals(10, minimumDistanceNode.getX(), EPS);
        assertEquals(0, minimumDistanceNode.getY(), EPS);
    }

    @Test
    @DisplayName("Касательное движение по границе не блокируется")
    void tangentMoveIsAllowed() {
        MinimumDistanceMovementStrategy strategy = new MinimumDistanceMovementStrategy(List.of(
                new Point2D.Double(20, 0)
        ));

        Point2D resolved = strategy.resolveMove(new Point2D.Double(0, 10), new Point2D.Double(40, 10));

        assertEquals(40, resolved.getX(), EPS);
        assertEquals(10, resolved.getY(), EPS);
    }

    @Test
    @DisplayName("Движение от границы запретной области наружу не блокируется")
    void movingAwayFromBoundaryIsAllowed() {
        MinimumDistanceMovementStrategy strategy = new MinimumDistanceMovementStrategy(List.of(
                new Point2D.Double(20, 0)
        ));

        Point2D resolved = strategy.resolveMove(new Point2D.Double(10, 0), new Point2D.Double(0, 0));

        assertEquals(0, resolved.getX(), EPS);
        assertEquals(0, resolved.getY(), EPS);
    }

    @Test
    @DisplayName("Движение вдоль границы запретной области скользит по радиусу")
    void slidesAlongForbiddenAreaBoundary() {
        MinimumDistanceMovementStrategy strategy = new MinimumDistanceMovementStrategy(List.of(
                new Point2D.Double(10, 90)
        ));

        Point2D resolved = strategy.resolveMove(new Point2D.Double(10, 80), new Point2D.Double(15, 85));

        assertEquals(17.071067811865476, resolved.getX(), EPS);
        assertEquals(82.92893218813452, resolved.getY(), EPS);
        assertEquals(10, resolved.distance(new Point2D.Double(10, 90)), EPS);
    }

    @Test
    @DisplayName("Скольжение по пересечению запретных областей выводит на общую границу")
    void slidesToMergedForbiddenAreaBoundary() {
        Point2D firstNodePosition = new Point2D.Double(20, 0);
        Point2D secondNodePosition = new Point2D.Double(35, 0);
        MinimumDistanceMovementStrategy strategy = new MinimumDistanceMovementStrategy(List.of(
                firstNodePosition,
                secondNodePosition
        ));

        Point2D resolved = strategy.resolveMove(new Point2D.Double(10, 0), new Point2D.Double(27.5, 0));

        assertEquals(27.5, resolved.getX(), EPS);
        assertEquals(6.614378277661476, Math.abs(resolved.getY()), EPS);
        assertTrue(resolved.distance(firstNodePosition) >= 10 - EPS);
        assertTrue(resolved.distance(secondNodePosition) >= 10 - EPS);
    }

    @Test
    @DisplayName("Стратегия возвращает новую точку результата")
    void returnsNewResolvedPoint() {
        Point2D requested = new Point2D.Double(20, 20);
        MinimumDistanceMovementStrategy strategy = new MinimumDistanceMovementStrategy(List.of());

        Point2D resolved = strategy.resolveMove(new Point2D.Double(0, 0), requested);

        assertNotSame(requested, resolved);
    }

    @Test
    @DisplayName("Стратегия отклоняет некорректные параметры")
    void rejectsInvalidArgs() {
        assertThrows(NullPointerException.class, () -> new MinimumDistanceMovementStrategy(null));

        List<Point2D> positionsWithNull = new ArrayList<>();
        positionsWithNull.add(null);
        assertThrows(NullPointerException.class, () -> new MinimumDistanceMovementStrategy(positionsWithNull));

        MinimumDistanceMovementStrategy strategy = new MinimumDistanceMovementStrategy(List.of());
        assertThrows(NullPointerException.class, () -> strategy.resolveMove(null, new Point2D.Double(1, 1)));
        assertThrows(NullPointerException.class, () -> strategy.resolveMove(new Point2D.Double(1, 1), null));
        assertThrows(IllegalArgumentException.class, () -> strategy.resolveMove(
                new Point2D.Double(Double.NaN, 1),
                new Point2D.Double(1, 1)
        ));
        assertThrows(IllegalArgumentException.class, () -> strategy.resolveMove(
                new Point2D.Double(1, 1),
                new Point2D.Double(Double.POSITIVE_INFINITY, 1)
        ));
        assertThrows(IllegalArgumentException.class, () -> MinimumDistanceMovementStrategy.setMinimumDistance(0));
        assertThrows(IllegalArgumentException.class, () -> MinimumDistanceMovementStrategy.setMinimumDistance(-1));
        assertThrows(IllegalArgumentException.class, () -> MinimumDistanceMovementStrategy.setMinimumDistance(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> MinimumDistanceMovementStrategy.setMinimumDistance(Double.POSITIVE_INFINITY));
    }
}
