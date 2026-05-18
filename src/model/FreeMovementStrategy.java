package model;

import java.awt.geom.Point2D;
import java.util.Objects;

public final class FreeMovementStrategy implements MovementStrategy {
    @Override
    public Point2D resolveMove(Point2D currentPosition, Point2D requestedPosition) {
        Objects.requireNonNull(currentPosition, "currentPosition");
        return Objects.requireNonNull(requestedPosition, "requestedPosition");
    }
}
