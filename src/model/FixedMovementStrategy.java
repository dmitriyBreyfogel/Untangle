package model;

import java.awt.geom.Point2D;
import java.util.Objects;

public final class FixedMovementStrategy implements MovementStrategy {
    @Override
    public Point2D resolveMove(Point2D currentPosition, Point2D requestedPosition) {
        Objects.requireNonNull(currentPosition, "currentPosition");
        Objects.requireNonNull(requestedPosition, "requestedPosition");
        return currentPosition;
    }
}
