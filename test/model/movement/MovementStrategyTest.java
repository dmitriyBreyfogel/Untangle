package model.movement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MovementStrategyTest {
    @Test
    @DisplayName("Свободная стратегия возвращает запрошенную позицию")
    void freeStrategyReturnsRequestedPosition() {
        Point2D currentPosition = new Point2D.Double(10, 10);
        Point2D requestedPosition = new Point2D.Double(20, 30);

        Point2D resolvedPosition = new FreeMovementStrategy().resolveMove(context(currentPosition, requestedPosition));

        assertEquals(20, resolvedPosition.getX());
        assertEquals(30, resolvedPosition.getY());
        assertNotSame(requestedPosition, resolvedPosition);
    }

    @Test
    @DisplayName("Неподвижная стратегия возвращает текущую позицию узла")
    void fixedStrategyReturnsCurrentPosition() {
        Point2D currentPosition = new Point2D.Double(10, 10);

        Point2D resolvedPosition = new FixedMovementStrategy().resolveMove(context(currentPosition, new Point2D.Double(20, 30)));

        assertEquals(10, resolvedPosition.getX());
        assertEquals(10, resolvedPosition.getY());
    }

    @Test
    @DisplayName("Горизонтальная стратегия сохраняет текущую координату игрек узла")
    void horizontalStrategyKeepsCurrentY() {
        Point2D currentPosition = new Point2D.Double(10, 10);

        Point2D resolvedPosition = new HorizontalMovementStrategy().resolveMove(context(currentPosition, new Point2D.Double(20, 30)));

        assertEquals(20, resolvedPosition.getX());
        assertEquals(10, resolvedPosition.getY());
    }

    @Test
    @DisplayName("Стратегии движения не принимают пустые параметры")
    void strategiesRejectNullArgs() {
        assertThrows(NullPointerException.class, () -> new FreeMovementStrategy().resolveMove(null));
        assertThrows(NullPointerException.class, () -> new FixedMovementStrategy().resolveMove(null));
        assertThrows(NullPointerException.class, () -> new HorizontalMovementStrategy().resolveMove(null));
    }

    private static MovementContext context(Point2D currentPosition, Point2D requestedPosition) {
        return new MovementContext(currentPosition, requestedPosition, List.of());
    }
}
