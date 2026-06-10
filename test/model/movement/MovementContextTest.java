package model.movement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MovementContextTest {
    @Test
    @DisplayName("Контекст движения копирует входные точки")
    void copiesInputPoints() {
        Point2D.Double currentPosition = new Point2D.Double(1, 2);
        Point2D.Double requestedPosition = new Point2D.Double(3, 4);
        Point2D.Double otherPosition = new Point2D.Double(5, 6);

        MovementContext context = new MovementContext(currentPosition, requestedPosition, List.of(otherPosition));
        currentPosition.setLocation(10, 20);
        requestedPosition.setLocation(30, 40);
        otherPosition.setLocation(50, 60);

        assertEquals(1, context.currentPosition().getX());
        assertEquals(2, context.currentPosition().getY());
        assertEquals(3, context.requestedPosition().getX());
        assertEquals(4, context.requestedPosition().getY());
        assertEquals(5, context.otherNodePositions().getFirst().getX());
        assertEquals(6, context.otherNodePositions().getFirst().getY());
    }

    @Test
    @DisplayName("Контекст движения возвращает копии своих точек")
    void returnsPointCopies() {
        MovementContext context = new MovementContext(
                new Point2D.Double(1, 2),
                new Point2D.Double(3, 4),
                List.of(new Point2D.Double(5, 6))
        );

        Point2D currentPosition = context.currentPosition();
        Point2D requestedPosition = context.requestedPosition();
        Point2D otherPosition = context.otherNodePositions().getFirst();

        assertNotSame(currentPosition, context.currentPosition());
        assertNotSame(requestedPosition, context.requestedPosition());
        assertNotSame(otherPosition, context.otherNodePositions().getFirst());
    }

    @Test
    @DisplayName("Контекст движения отклоняет некорректные параметры")
    void rejectsInvalidArgs() {
        assertThrows(NullPointerException.class, () -> new MovementContext(
                null,
                new Point2D.Double(1, 1),
                List.of()
        ));
        assertThrows(NullPointerException.class, () -> new MovementContext(
                new Point2D.Double(1, 1),
                null,
                List.of()
        ));
        assertThrows(NullPointerException.class, () -> new MovementContext(
                new Point2D.Double(1, 1),
                new Point2D.Double(2, 2),
                null
        ));

        List<Point2D> positions = new ArrayList<>();
        positions.add(null);
        assertThrows(NullPointerException.class, () -> new MovementContext(
                new Point2D.Double(1, 1),
                new Point2D.Double(2, 2),
                positions
        ));
        assertThrows(IllegalArgumentException.class, () -> new MovementContext(
                new Point2D.Double(Double.NaN, 1),
                new Point2D.Double(2, 2),
                List.of()
        ));
        assertThrows(IllegalArgumentException.class, () -> new MovementContext(
                new Point2D.Double(1, 1),
                new Point2D.Double(Double.POSITIVE_INFINITY, 2),
                List.of()
        ));
        assertThrows(IllegalArgumentException.class, () -> new MovementContext(
                new Point2D.Double(1, 1),
                new Point2D.Double(2, 2),
                List.of(new Point2D.Double(3, Double.NaN))
        ));
    }
}
