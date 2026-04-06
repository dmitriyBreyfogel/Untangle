package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EdgeTest {
    @Test
    @DisplayName("Грань определяет пересечение с другой гранью")
    void detectsIntersection() {
        Node a1 = new Node(new Point2D.Double(0, 0));
        Node a2 = new Node(new Point2D.Double(10, 10));
        Edge a = new Edge(a1, a2);

        Node b1 = new Node(new Point2D.Double(0, 10));
        Node b2 = new Node(new Point2D.Double(10, 0));
        Edge b = new Edge(b1, b2);

        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));
    }

    @Test
    @DisplayName("Грани с общим узлом не считаются пересекающимися")
    void sharedNodeIsNotIntersection() {
        Node shared = new Node(new Point2D.Double(0, 0));
        Edge a = new Edge(shared, new Node(new Point2D.Double(10, 10)));
        Edge b = new Edge(shared, new Node(new Point2D.Double(10, 0)));

        assertFalse(a.intersects(b));
        assertFalse(b.intersects(a));
    }

    @Test
    @DisplayName("Грань не может соединять узел сам с собой")
    void edgeCannotConnectNodeToItself() {
        Node node = new Node(new Point2D.Double(0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Edge(node, node));
    }

    @Test
    @DisplayName("Грань корректно сообщает о принадлежности узла")
    void containsNodeWorks() {
        Node a = new Node(new Point2D.Double(0, 0));
        Node b = new Node(new Point2D.Double(1, 1));
        Node c = new Node(new Point2D.Double(2, 2));
        Edge edge = new Edge(a, b);

        assertTrue(edge.containsNode(a));
        assertTrue(edge.containsNode(b));
        assertFalse(edge.containsNode(c));
    }

    @Test
    @DisplayName("Обновление геометрии учитывает перемещение узла")
    void updateGeometryReflectsNodeMove() {
        Node a1 = new Node(new Point2D.Double(0, 0));
        Node a2 = new Node(new Point2D.Double(10, 10));
        Edge a = new Edge(a1, a2);

        Node b1 = new Node(new Point2D.Double(0, 10));
        Node b2 = new Node(new Point2D.Double(10, 0));
        Edge b = new Edge(b1, b2);

        assertTrue(a.intersects(b));

        a2.moveTo(new Point2D.Double(0, 1));
        a.updateGeometry();

        assertFalse(a.intersects(b));
    }

    @Test
    @DisplayName("Касание в точке не считается пересечением")
    void touchingAtPointIsNotIntersection() {
        Edge a = new Edge(
                new Node(new Point2D.Double(0, 0)),
                new Node(new Point2D.Double(10, 0))
        );
        Edge b = new Edge(
                new Node(new Point2D.Double(10, 0)),
                new Node(new Point2D.Double(10, 10))
        );

        assertFalse(a.intersects(b));
        assertFalse(b.intersects(a));
    }

    @Test
    @DisplayName("Пересекаемость корректно выставляется схемой")
    void intersectingFlagCanBeSet() {
        Edge edge = new Edge(
                new Node(new Point2D.Double(0, 0)),
                new Node(new Point2D.Double(1, 1))
        );

        assertFalse(edge.isIntersecting());
        edge.setIntersecting(true);
        assertTrue(edge.isIntersecting());
        edge.setIntersecting(false);
        assertFalse(edge.isIntersecting());
    }

    @Test
    @DisplayName("Пересечение с null гранью запрещено")
    void intersectsRejectsNull() {
        Edge edge = new Edge(
                new Node(new Point2D.Double(0, 0)),
                new Node(new Point2D.Double(1, 1))
        );
        assertThrows(NullPointerException.class, () -> edge.intersects(null));
    }

    @Test
    @DisplayName("Грань не пересекает сама себя")
    void edgeDoesNotIntersectItself() {
        Edge edge = new Edge(
                new Node(new Point2D.Double(0, 0)),
                new Node(new Point2D.Double(1, 1))
        );
        assertFalse(edge.intersects(edge));
    }
}
