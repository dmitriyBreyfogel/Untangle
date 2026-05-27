package model.core;

import model.movement.FreeMovementStrategy;
import model.movement.MovementStrategy;

import java.awt.geom.Point2D;
import java.util.Objects;

public final class Node {
    private static final MovementStrategy DEFAULT_MOVEMENT_STRATEGY = new FreeMovementStrategy();

    private Point2D.Double position;
    private final MovementStrategy movementStrategy;

    Node(Point2D position) {
        this(position, DEFAULT_MOVEMENT_STRATEGY);
    }

    Node(Point2D position, MovementStrategy movementStrategy) {
        this.position = copyOf(position, "position");
        this.movementStrategy = Objects.requireNonNull(movementStrategy, "movementStrategy");
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public Point2D resolveMove(Point2D destination) {
        Point2D requestedPosition = copyOf(destination, "destination");
        Point2D currentPosition = copyOf(position, "currentPosition");
        Point2D resolvedPosition = movementStrategy.resolveMove(currentPosition, requestedPosition);
        return copyOf(resolvedPosition, "resolvedPosition");
    }

    public MovementStrategy getMovementStrategy() {
        return movementStrategy;
    }

    Point2D getPosition() {
        return position;
    }

    void moveDirectlyTo(Point2D destination) {
        position = copyOf(destination, "destination");
    }

    boolean isAt(Point2D destination) {
        Objects.requireNonNull(destination, "destination");
        return Math.abs(position.getX() - destination.getX()) <= 1e-9
                && Math.abs(position.getY() - destination.getY()) <= 1e-9;
    }

    private static Point2D.Double copyOf(Point2D p, String paramName) {
        Objects.requireNonNull(p, paramName);
        double x = p.getX();
        double y = p.getY();
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException(paramName + " должен иметь конечное значение");
        }
        return new Point2D.Double(x, y);
    }
}
