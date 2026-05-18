package model;

import java.awt.geom.Point2D;
import java.util.Objects;

public final class HorizontalMovementStrategy implements MovementStrategy {
    @Override
    public Point2D resolveMove(Point2D currentPosition, Point2D requestedPosition) {
        Objects.requireNonNull(currentPosition, "currentPosition");
        Objects.requireNonNull(requestedPosition, "requestedPosition");
        return new Point2D.Double(requestedPosition.getX(), currentPosition.getY());
    }
}
