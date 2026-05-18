package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MovementStrategyTest {
    @Test
    @DisplayName("Свободная стратегия возвращает запрошенную позицию")
    void freeStrategyReturnsRequestedPosition() {
        Node node = new Node(new Point2D.Double(10, 10));
        Point2D requestedPosition = new Point2D.Double(20, 30);

        Point2D resolvedPosition = new FreeMovementStrategy().resolveMove(node, requestedPosition);

        assertSame(requestedPosition, resolvedPosition);
    }

    @Test
    @DisplayName("Неподвижная стратегия возвращает текущую позицию узла")
    void fixedStrategyReturnsCurrentPosition() {
        Node node = new Node(new Point2D.Double(10, 10));

        Point2D resolvedPosition = new FixedMovementStrategy().resolveMove(node, new Point2D.Double(20, 30));

        assertEquals(10, resolvedPosition.getX());
        assertEquals(10, resolvedPosition.getY());
    }

    @Test
    @DisplayName("Горизонтальная стратегия сохраняет текущий Y узла")
    void horizontalStrategyKeepsCurrentY() {
        Node node = new Node(new Point2D.Double(10, 10));

        Point2D resolvedPosition = new HorizontalMovementStrategy().resolveMove(node, new Point2D.Double(20, 30));

        assertEquals(20, resolvedPosition.getX());
        assertEquals(10, resolvedPosition.getY());
    }

    @Test
    @DisplayName("Стратегии движения не принимают null параметры")
    void strategiesRejectNullArgs() {
        Node node = new Node(new Point2D.Double(10, 10));
        Point2D requestedPosition = new Point2D.Double(20, 30);

        assertThrows(NullPointerException.class, () -> new FreeMovementStrategy().resolveMove(null, requestedPosition));
        assertThrows(NullPointerException.class, () -> new FreeMovementStrategy().resolveMove(node, null));
        assertThrows(NullPointerException.class, () -> new FixedMovementStrategy().resolveMove(null, requestedPosition));
        assertThrows(NullPointerException.class, () -> new FixedMovementStrategy().resolveMove(node, null));
        assertThrows(NullPointerException.class, () -> new HorizontalMovementStrategy().resolveMove(null, requestedPosition));
        assertThrows(NullPointerException.class, () -> new HorizontalMovementStrategy().resolveMove(node, null));
    }
}
