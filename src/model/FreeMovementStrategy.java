package model;

import java.awt.geom.Point2D;
import java.util.Objects;

public final class FreeMovementStrategy implements MovementStrategy {
    @Override
    public Point2D resolveMove(Node node, Point2D requestedPosition) {
        Objects.requireNonNull(node, "node");
        return Objects.requireNonNull(requestedPosition, "requestedPosition");
    }
}
