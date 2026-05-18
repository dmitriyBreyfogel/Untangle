package model;

import java.awt.geom.Point2D;
import java.util.Objects;

public final class HorizontalMovementStrategy implements MovementStrategy {
    @Override
    public Point2D resolveMove(Node node, Point2D requestedPosition) {
        Objects.requireNonNull(node, "node");
        Objects.requireNonNull(requestedPosition, "requestedPosition");
        return new Point2D.Double(requestedPosition.getX(), node.getY());
    }
}
