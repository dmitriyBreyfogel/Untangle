package model.movement;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public final class MinimumDistanceMovementStrategy extends MovementStrategy {
    private static final double EPS = 1e-9;
    private static final double DEFAULT_MINIMUM_DISTANCE = 10.0;

    private final double minimumDistance;

    public MinimumDistanceMovementStrategy() {
        this(DEFAULT_MINIMUM_DISTANCE);
    }

    public MinimumDistanceMovementStrategy(double minimumDistance) {
        if (!Double.isFinite(minimumDistance) || minimumDistance <= 0) {
            throw new IllegalArgumentException("Минимальная дистанция должна быть положительной конечной величиной");
        }
        this.minimumDistance = minimumDistance;
    }

    public double minimumDistance() {
        return minimumDistance;
    }

    @Override
    protected Point2D resolveValidatedMove(MovementContext context) {
        Point2D currentPosition = context.currentPosition();
        Point2D requestedPosition = context.requestedPosition();
        List<Point2D> otherNodePositions = context.otherNodePositions();

        if (!isInsideForbiddenArea(requestedPosition, otherNodePositions)) {
            return requestedPosition;
        }

        Point2D boundaryPosition = projectToForbiddenAreaBoundary(requestedPosition, currentPosition, otherNodePositions);
        return boundaryPosition == null ? currentPosition : boundaryPosition;
    }

    private boolean isInsideForbiddenArea(Point2D position, List<Point2D> otherNodePositions) {
        for (Point2D nodePosition : otherNodePositions) {
            if (position.distance(nodePosition) < minimumDistance - EPS) {
                return true;
            }
        }
        return false;
    }

    private Point2D projectToForbiddenAreaBoundary(
            Point2D requestedPosition,
            Point2D currentPosition,
            List<Point2D> otherNodePositions
    ) {
        List<Point2D> candidates = new ArrayList<>();
        for (Point2D nodePosition : otherNodePositions) {
            if (requestedPosition.distance(nodePosition) < minimumDistance - EPS) {
                candidates.add(projectToCircleBoundary(requestedPosition, nodePosition, currentPosition));
            }
        }

        for (int i = 0; i < otherNodePositions.size(); i++) {
            Point2D first = otherNodePositions.get(i);
            for (int j = i + 1; j < otherNodePositions.size(); j++) {
                Point2D second = otherNodePositions.get(j);
                candidates.addAll(circleIntersections(first, second));
            }
        }

        Point2D bestCandidate = null;
        double bestDistance = Double.POSITIVE_INFINITY;
        for (Point2D candidate : candidates) {
            if (!isOutsideForbiddenAreas(candidate, otherNodePositions)) {
                continue;
            }

            double distance = candidate.distance(requestedPosition);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestCandidate = candidate;
            }
        }
        return bestCandidate;
    }

    private Point2D projectToCircleBoundary(Point2D position, Point2D nodePosition, Point2D currentPosition) {
        double dx = position.getX() - nodePosition.getX();
        double dy = position.getY() - nodePosition.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= EPS) {
            dx = currentPosition.getX() - nodePosition.getX();
            dy = currentPosition.getY() - nodePosition.getY();
            distance = Math.sqrt(dx * dx + dy * dy);
        }
        if (distance <= EPS) {
            dx = 1.0;
            dy = 0.0;
            distance = 1.0;
        }

        return new Point2D.Double(
                nodePosition.getX() + dx * minimumDistance / distance,
                nodePosition.getY() + dy * minimumDistance / distance
        );
    }

    private List<Point2D> circleIntersections(Point2D first, Point2D second) {
        double dx = second.getX() - first.getX();
        double dy = second.getY() - first.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance <= EPS || distance > minimumDistance * 2.0 + EPS) {
            return List.of();
        }

        double middleDistance = distance / 2.0;
        double heightSquared = minimumDistance * minimumDistance - middleDistance * middleDistance;
        if (heightSquared < -EPS) {
            return List.of();
        }

        double height = Math.sqrt(Math.max(0.0, heightSquared));
        double middleX = first.getX() + dx / 2.0;
        double middleY = first.getY() + dy / 2.0;
        double offsetX = -dy * height / distance;
        double offsetY = dx * height / distance;

        if (height <= EPS) {
            return List.of(new Point2D.Double(middleX, middleY));
        }
        return List.of(
                new Point2D.Double(middleX + offsetX, middleY + offsetY),
                new Point2D.Double(middleX - offsetX, middleY - offsetY)
        );
    }

    private boolean isOutsideForbiddenAreas(Point2D point, List<Point2D> otherNodePositions) {
        for (Point2D nodePosition : otherNodePositions) {
            if (point.distance(nodePosition) < minimumDistance - EPS) {
                return false;
            }
        }
        return true;
    }
}
