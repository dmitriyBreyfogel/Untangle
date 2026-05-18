package model;

import java.awt.geom.Point2D;
import java.util.Objects;

public final class FixedMovementStrategy implements MovementStrategy {
    @Override
    public Point2D resolveMove(Node node, Point2D requestedPosition) {
        Objects.requireNonNull(requestedPosition, "requestedPosition");
        Objects.requireNonNull(node, "node");
        return new Point2D.Double(node.getX(), node.getY());
    }
}
