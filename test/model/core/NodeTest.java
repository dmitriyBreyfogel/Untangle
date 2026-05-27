package model.core;

import model.movement.FixedMovementStrategy;
import model.movement.FreeMovementStrategy;
import model.movement.HorizontalMovementStrategy;
import model.movement.MovementStrategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NodeTest {
    @Test
    @DisplayName("Узел перемещается в новую точку")
    void nodeMoves() {
        Node node = new Node(new Point2D.Double(10, 10));
        node.moveDirectlyTo(new Point2D.Double(20, 30));
        assertEquals(20, node.getX());
        assertEquals(30, node.getY());
        assertInstanceOf(FreeMovementStrategy.class, node.getMovementStrategy());
    }

    @Test
    @DisplayName("Узел хранит переданную стратегию движения")
    void nodeKeepsMovementStrategy() {
        MovementStrategy movementStrategy = new HorizontalMovementStrategy();
        Node node = new Node(new Point2D.Double(10, 10), movementStrategy);

        assertSame(movementStrategy, node.getMovementStrategy());
    }

    @Test
    @DisplayName("Неподвижная стратегия оставляет узел на месте")
    void fixedStrategyKeepsNodeInPlace() {
        Node node = new Node(new Point2D.Double(10, 10), new FixedMovementStrategy());

        Point2D resolvedPosition = node.resolveMove(new Point2D.Double(20, 30));

        assertEquals(10, resolvedPosition.getX());
        assertEquals(10, resolvedPosition.getY());
    }

    @Test
    @DisplayName("Горизонтальная стратегия меняет только X координату")
    void horizontalStrategyMovesOnlyHorizontally() {
        Node node = new Node(new Point2D.Double(10, 10), new HorizontalMovementStrategy());

        Point2D resolvedPosition = node.resolveMove(new Point2D.Double(20, 30));

        assertEquals(20, resolvedPosition.getX());
        assertEquals(10, resolvedPosition.getY());
    }

    @Test
    @DisplayName("Узел отклоняет координаты NaN")
    void nodeRejectsNaN() {
        assertThrows(IllegalArgumentException.class, () -> new Node(new Point2D.Double(Double.NaN, 0)));
        Node node = new Node(new Point2D.Double(0, 0));
        assertThrows(IllegalArgumentException.class, () -> node.resolveMove(new Point2D.Double(0, Double.NaN)));
    }

    @Test
    @DisplayName("Узел отклоняет бесконечные координаты")
    void nodeRejectsInfinity() {
        assertThrows(IllegalArgumentException.class, () -> new Node(new Point2D.Double(Double.POSITIVE_INFINITY, 0)));
        Node node = new Node(new Point2D.Double(0, 0));
        assertThrows(IllegalArgumentException.class, () -> node.resolveMove(new Point2D.Double(0, Double.NEGATIVE_INFINITY)));
    }

    @Test
    @DisplayName("Узел не принимает null позицию")
    void nodeRejectsNull() {
        assertThrows(NullPointerException.class, () -> new Node(null));
        assertThrows(NullPointerException.class, () -> new Node(new Point2D.Double(0, 0), null));
        Node node = new Node(new Point2D.Double(0, 0));
        assertThrows(NullPointerException.class, () -> node.resolveMove(null));
    }

    @Test
    @DisplayName("Узел копирует входные координаты")
    void nodeCopiesInputCoordinates() {
        Point2D.Double initial = new Point2D.Double(10, 10);
        Node node = new Node(initial);

        initial.setLocation(99, 77);
        assertEquals(10, node.getX());
        assertEquals(10, node.getY());

        Point2D.Double destination = new Point2D.Double(20, 30);
        node.moveDirectlyTo(destination);
        destination.setLocation(1, 2);

        assertEquals(20, node.getX());
        assertEquals(30, node.getY());
    }
}
