package model;

import java.awt.geom.Point2D;
import java.util.Objects;

public final class Node {
    private Point2D.Double position;

    Node(Point2D position) {
        this.position = copyOf(position, "position");
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public void moveTo(Point2D destination) {
        position = copyOf(destination, "destination");
    }

    Point2D getPosition() {
        return position;
    }

    private static Point2D.Double copyOf(Point2D p, String paramName) {
        Objects.requireNonNull(p, paramName);
        double x = p.getX();
        double y = p.getY();
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException(paramName + " must have finite coordinates.");
        }
        return new Point2D.Double(x, y);
    }
}
