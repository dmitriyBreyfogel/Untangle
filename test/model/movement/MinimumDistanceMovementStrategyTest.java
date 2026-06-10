package model.movement;

import model.core.Node;
import model.core.Scheme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MinimumDistanceMovementStrategyTest {
    private static final double EPS = 1e-9;

    @Test
    @DisplayName("Стратегия разрешает движение вне запретных областей")
    void allowsMoveOutsideForbiddenAreas() {
        Point2D resolved = resolve(
                new MinimumDistanceMovementStrategy(),
                point(0, 0),
                point(20, 20),
                point(80, 80),
                point(90, 10)
        );

        assertEquals(20, resolved.getX(), EPS);
        assertEquals(20, resolved.getY(), EPS);
    }

    @Test
    @DisplayName("Стратегия останавливает узел на границе запретной области")
    void stopsAtForbiddenAreaBoundary() {
        Point2D resolved = resolve(new MinimumDistanceMovementStrategy(), point(0, 0), point(18, 0), point(20, 0));

        assertEquals(10, resolved.getX(), EPS);
        assertEquals(0, resolved.getY(), EPS);
    }

    @Test
    @DisplayName("Стратегия разрешает конечную позицию вне запретной области")
    void allowsFinalPositionOutsideForbiddenArea() {
        Point2D resolved = resolve(new MinimumDistanceMovementStrategy(), point(0, 0), point(40, 0), point(20, 0));

        assertEquals(40, resolved.getX(), EPS);
        assertEquals(0, resolved.getY(), EPS);
    }

    @Test
    @DisplayName("Стратегия считает пересекающиеся запретные области единым барьером")
    void treatsOverlappingForbiddenAreasAsOneBarrier() {
        Point2D firstNodePosition = point(20, 0);
        Point2D secondNodePosition = point(35, 0);

        Point2D resolved = resolve(
                new MinimumDistanceMovementStrategy(),
                point(0, 0),
                point(27.5, 0),
                firstNodePosition,
                secondNodePosition
        );

        assertEquals(27.5, resolved.getX(), EPS);
        assertEquals(6.614378277661476, Math.abs(resolved.getY()), EPS);
        assertTrue(resolved.distance(firstNodePosition) >= 10 - EPS);
        assertTrue(resolved.distance(secondNodePosition) >= 10 - EPS);
    }

    @Test
    @DisplayName("Стратегия использует ближайшую границу для запрошенной позиции")
    void usesNearestBoundaryForRequestedPosition() {
        Point2D resolved = resolve(
                new MinimumDistanceMovementStrategy(),
                point(0, 0),
                point(18, 0),
                point(50, 0),
                point(20, 0)
        );

        assertEquals(10, resolved.getX(), EPS);
        assertEquals(0, resolved.getY(), EPS);
    }

    @Test
    @DisplayName("Стратегия использует только остальные узлы из контекста")
    void usesOnlyOtherNodesFromContext() {
        Point2D resolved = resolve(new MinimumDistanceMovementStrategy(), point(0, 0), point(5, 0));

        assertEquals(5, resolved.getX(), EPS);
        assertEquals(0, resolved.getY(), EPS);
    }

    @Test
    @DisplayName("Стратегия хранит минимальную дистанцию в конкретном экземпляре")
    void usesInstanceMinimumDistance() {
        MinimumDistanceMovementStrategy five = new MinimumDistanceMovementStrategy(5.0);
        MinimumDistanceMovementStrategy eight = new MinimumDistanceMovementStrategy(8.0);

        Point2D resolvedWithFive = resolve(five, point(0, 0), point(18, 0), point(20, 0));
        Point2D resolvedWithEight = resolve(eight, point(0, 0), point(18, 0), point(20, 0));

        assertEquals(5, five.minimumDistance(), EPS);
        assertEquals(8, eight.minimumDistance(), EPS);
        assertEquals(15, resolvedWithFive.getX(), EPS);
        assertEquals(12, resolvedWithEight.getX(), EPS);
    }

    @Test
    @DisplayName("Стратегия в схеме учитывает обновлённые позиции других узлов")
    void tracksMovedNodesInsideScheme() {
        Scheme scheme = Scheme.create(
                List.of(
                        point(0, 0),
                        point(30, 0),
                        point(0, 30)
                ),
                Map.of(0, List.of(1, 2), 1, List.of(2)),
                Map.of(0, new MinimumDistanceMovementStrategy())
        );
        Node minimumDistanceNode = scheme.getNodes().get(0);
        Node regularNode = scheme.getNodes().get(1);

        scheme.moveNode(regularNode, point(20, 0));
        scheme.moveNode(minimumDistanceNode, point(15, 0));

        assertEquals(10, minimumDistanceNode.getX(), EPS);
        assertEquals(0, minimumDistanceNode.getY(), EPS);
    }

    @Test
    @DisplayName("Предварительный расчёт в схеме применяет стратегию минимальной дистанции")
    void schemePreviewUsesMinimumDistanceStrategy() {
        Scheme scheme = Scheme.create(
                List.of(
                        point(0, 0),
                        point(20, 0),
                        point(0, 30)
                ),
                Map.of(0, List.of(1, 2), 1, List.of(2)),
                Map.of(0, new MinimumDistanceMovementStrategy())
        );
        Node minimumDistanceNode = scheme.getNodes().get(0);

        Point2D preview = scheme.previewMove(minimumDistanceNode, point(18, 0));

        assertEquals(10, preview.getX(), EPS);
        assertEquals(0, preview.getY(), EPS);
        assertEquals(0, minimumDistanceNode.getX(), EPS);
        assertEquals(0, minimumDistanceNode.getY(), EPS);
    }

    @Test
    @DisplayName("Касательное движение по границе не блокируется")
    void tangentMoveIsAllowed() {
        Point2D resolved = resolve(new MinimumDistanceMovementStrategy(), point(0, 10), point(40, 10), point(20, 0));

        assertEquals(40, resolved.getX(), EPS);
        assertEquals(10, resolved.getY(), EPS);
    }

    @Test
    @DisplayName("Движение от границы запретной области наружу не блокируется")
    void movingAwayFromBoundaryIsAllowed() {
        Point2D resolved = resolve(new MinimumDistanceMovementStrategy(), point(10, 0), point(0, 0), point(20, 0));

        assertEquals(0, resolved.getX(), EPS);
        assertEquals(0, resolved.getY(), EPS);
    }

    @Test
    @DisplayName("Движение вдоль границы запретной области скользит по радиусу")
    void slidesAlongForbiddenAreaBoundary() {
        Point2D resolved = resolve(new MinimumDistanceMovementStrategy(), point(10, 80), point(15, 85), point(10, 90));

        assertEquals(17.071067811865476, resolved.getX(), EPS);
        assertEquals(82.92893218813452, resolved.getY(), EPS);
        assertEquals(10, resolved.distance(point(10, 90)), EPS);
    }

    @Test
    @DisplayName("Скольжение по пересечению запретных областей выводит на общую границу")
    void slidesToMergedForbiddenAreaBoundary() {
        Point2D firstNodePosition = point(20, 0);
        Point2D secondNodePosition = point(35, 0);

        Point2D resolved = resolve(
                new MinimumDistanceMovementStrategy(),
                point(10, 0),
                point(27.5, 0),
                firstNodePosition,
                secondNodePosition
        );

        assertEquals(27.5, resolved.getX(), EPS);
        assertEquals(6.614378277661476, Math.abs(resolved.getY()), EPS);
        assertTrue(resolved.distance(firstNodePosition) >= 10 - EPS);
        assertTrue(resolved.distance(secondNodePosition) >= 10 - EPS);
    }

    @Test
    @DisplayName("Стратегия возвращает новую точку результата")
    void returnsNewResolvedPoint() {
        Point2D requested = point(20, 20);

        Point2D resolved = new MinimumDistanceMovementStrategy().resolveMove(new MovementContext(
                point(0, 0),
                requested,
                List.of()
        ));

        assertNotSame(requested, resolved);
    }

    @Test
    @DisplayName("Стратегия отклоняет некорректные параметры")
    void rejectsInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new MinimumDistanceMovementStrategy(0));
        assertThrows(IllegalArgumentException.class, () -> new MinimumDistanceMovementStrategy(-1));
        assertThrows(IllegalArgumentException.class, () -> new MinimumDistanceMovementStrategy(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> new MinimumDistanceMovementStrategy(Double.POSITIVE_INFINITY));
        assertThrows(NullPointerException.class, () -> new MinimumDistanceMovementStrategy().resolveMove(null));
        assertThrows(IllegalArgumentException.class, () -> new MinimumDistanceMovementStrategy().resolveMove(new MovementContext(
                point(0, 0),
                point(1, 1),
                List.of(point(Double.NaN, 1))
        )));
    }

    private static Point2D resolve(
            MinimumDistanceMovementStrategy strategy,
            Point2D currentPosition,
            Point2D requestedPosition,
            Point2D... otherNodePositions
    ) {
        return strategy.resolveMove(new MovementContext(currentPosition, requestedPosition, List.of(otherNodePositions)));
    }

    private static Point2D point(double x, double y) {
        return new Point2D.Double(x, y);
    }
}
