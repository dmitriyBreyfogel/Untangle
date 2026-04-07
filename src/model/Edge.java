package model;

import java.awt.geom.Point2D;
import java.util.Objects;

public final class Edge {
    private final Node nodeA;
    private final Node nodeB;

    private Point2D.Double start;
    private Point2D.Double end;

    private boolean intersecting;

    Edge(Node nodeA, Node nodeB) {
        this.nodeA = Objects.requireNonNull(nodeA, "nodeA");
        this.nodeB = Objects.requireNonNull(nodeB, "nodeB");
        if (nodeA == nodeB) {
            throw new IllegalArgumentException("Грань не может состоять из двух одинаковых узлов");
        }
        updateGeometry();
    }

    public void updateGeometry() {
        start = copyOf(nodeA.getPosition());
        end = copyOf(nodeB.getPosition());
    }

    public boolean intersects(Edge other) {
        Objects.requireNonNull(other, "other");
        if (other == this) {
            return false;
        }
        if (containsNode(other.nodeA) || containsNode(other.nodeB)) {
            return false;
        }
        return strictlyIntersects(start, end, other.start, other.end);
    }

    public boolean containsNode(Node node) {
        return node == nodeA || node == nodeB;
    }

    public boolean isIntersecting() {
        return intersecting;
    }

    void setIntersecting(boolean intersecting) {
        this.intersecting = intersecting;
    }

    Node getNodeA() {
        return nodeA;
    }

    Node getNodeB() {
        return nodeB;
    }

    private static boolean strictlyIntersects(Point2D a1, Point2D a2, Point2D b1, Point2D b2) {
        int o1 = orientation(a1, a2, b1);
        int o2 = orientation(a1, a2, b2);
        int o3 = orientation(b1, b2, a1);
        int o4 = orientation(b1, b2, a2);

        if (o1 == 0 || o2 == 0 || o3 == 0 || o4 == 0) {
            return false;
        }
        return o1 != o2 && o3 != o4;
    }

    private static int orientation(Point2D a, Point2D b, Point2D c) {
        double cross = (b.getX() - a.getX()) * (c.getY() - a.getY()) - (b.getY() - a.getY()) * (c.getX() - a.getX());
        double eps = 1e-10;
        if (Math.abs(cross) <= eps) {
            return 0;
        }
        return cross > 0 ? 1 : -1;
    }

    private static Point2D.Double copyOf(Point2D p) {
        return new Point2D.Double(p.getX(), p.getY());
    }
}
