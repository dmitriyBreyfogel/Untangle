package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NodeTest {
    @Test
    @DisplayName("Узел перемещается в новую точку")
    void nodeMoves() {
        Node node = new Node(new Point2D.Double(10, 10));
        node.moveTo(new Point2D.Double(20, 30));
        assertEquals(20, node.getX());
        assertEquals(30, node.getY());
    }

    @Test
    @DisplayName("Узел отклоняет координаты NaN")
    void nodeRejectsNaN() {
        assertThrows(IllegalArgumentException.class, () -> new Node(new Point2D.Double(Double.NaN, 0)));
        Node node = new Node(new Point2D.Double(0, 0));
        assertThrows(IllegalArgumentException.class, () -> node.moveTo(new Point2D.Double(0, Double.NaN)));
    }

    @Test
    @DisplayName("Узел не принимает null позицию")
    void nodeRejectsNull() {
        assertThrows(NullPointerException.class, () -> new Node(null));
        Node node = new Node(new Point2D.Double(0, 0));
        assertThrows(NullPointerException.class, () -> node.moveTo(null));
    }
}

