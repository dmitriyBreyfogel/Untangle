package model.movement;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MovementContext {
    private final Point2D.Double currentPosition;
    private final Point2D.Double requestedPosition;
    private final List<Point2D.Double> otherNodePositions;

    public MovementContext(Point2D currentPosition, Point2D requestedPosition, List<Point2D> otherNodePositions) {
        this.currentPosition = copyOf(currentPosition, "currentPosition");
        this.requestedPosition = copyOf(requestedPosition, "requestedPosition");
        Objects.requireNonNull(otherNodePositions, "otherNodePositions");
        List<Point2D.Double> copiedPositions = new ArrayList<>(otherNodePositions.size());
        for (Point2D position : otherNodePositions) {
            copiedPositions.add(copyOf(position, "otherNodePosition"));
        }
        this.otherNodePositions = List.copyOf(copiedPositions);
    }

    public Point2D currentPosition() {
        return copyOf(currentPosition, "currentPosition");
    }

    public Point2D requestedPosition() {
        return copyOf(requestedPosition, "requestedPosition");
    }

    public List<Point2D> otherNodePositions() {
        List<Point2D> positions = new ArrayList<>(otherNodePositions.size());
        for (Point2D position : otherNodePositions) {
            positions.add(copyOf(position, "otherNodePosition"));
        }
        return List.copyOf(positions);
    }

    static Point2D.Double copyOf(Point2D point, String paramName) {
        Objects.requireNonNull(point, paramName);
        double x = point.getX();
        double y = point.getY();
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException(paramName + " должен иметь конечное значение");
        }
        return new Point2D.Double(x, y);
    }
}
