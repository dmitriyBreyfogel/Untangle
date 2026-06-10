package model.movement;

import model.core.Node;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MinimumDistanceMovementStrategy implements NodeAwareMovementStrategy {
    private static final double EPS = 1e-9;
    private static double minimumDistance = 10.0;

    private final List<Point2D> staticNodePositions;
    private List<Node> nodes;

    public MinimumDistanceMovementStrategy() {
        this(List.of());
    }

    public MinimumDistanceMovementStrategy(List<Point2D> nodePositions) {
        Objects.requireNonNull(nodePositions, "nodePositions");
        for (Point2D nodePosition : nodePositions) {
            Objects.requireNonNull(nodePosition, "nodePosition");
        }
        this.staticNodePositions = List.copyOf(nodePositions);
        this.nodes = List.of();
    }

    @Override
    public void setNodes(List<Node> nodes) {
        Objects.requireNonNull(nodes, "nodes");
        for (Node node : nodes) {
            Objects.requireNonNull(node, "node");
        }
        this.nodes = List.copyOf(nodes);
    }

    public static double getMinimumDistance() {
        return minimumDistance;
    }

    public static void setMinimumDistance(double newMinimumDistance) {
        if (!Double.isFinite(newMinimumDistance) || newMinimumDistance <= 0) {
            throw new IllegalArgumentException("Минимальная дистанция должна быть положительной конечной величиной");
        }

        minimumDistance = newMinimumDistance;
    }

    @Override
    public Point2D resolveMove(Point2D currentPosition, Point2D requestedPosition) {
        Point2D current = copyOf(currentPosition, "currentPosition");
        Point2D requested = copyOf(requestedPosition, "requestedPosition");
        List<Point2D> nodePositions = currentNodePositions();

        if (!isInsideForbiddenArea(requested, current, nodePositions)) {
            return requested;
        }

        Point2D boundaryPosition = projectToForbiddenAreaBoundary(requested, current, nodePositions);
        return boundaryPosition == null ? current : boundaryPosition;
    }

    private List<Point2D> currentNodePositions() {
        List<Point2D> positions = new ArrayList<>(staticNodePositions.size() + nodes.size());
        positions.addAll(staticNodePositions);
        for (Node node : nodes) {
            positions.add(new Point2D.Double(node.getX(), node.getY()));
        }
        return positions;
    }

    private boolean isInsideForbiddenArea(Point2D position, Point2D currentPosition, List<Point2D> nodePositions) {
        for (Point2D rawNodePosition : nodePositions) {
            Point2D nodePosition = copyOf(rawNodePosition, "nodePosition");
            if (!isSamePoint(currentPosition, nodePosition)
                    && position.distance(nodePosition) < minimumDistance - EPS) {
                return true;
            }
        }
        return false;
    }

    private Point2D projectToForbiddenAreaBoundary(
            Point2D requestedPosition,
            Point2D currentPosition,
            List<Point2D> nodePositions
    ) {
        List<Point2D> candidates = new ArrayList<>();
        for (Point2D rawNodePosition : nodePositions) {
            Point2D nodePosition = copyOf(rawNodePosition, "nodePosition");
            if (!isSamePoint(currentPosition, nodePosition)
                    && requestedPosition.distance(nodePosition) < minimumDistance - EPS) {
                candidates.add(projectToCircleBoundary(requestedPosition, nodePosition, currentPosition));
            }
        }

        for (int i = 0; i < nodePositions.size(); i++) {
            Point2D first = copyOf(nodePositions.get(i), "nodePosition");
            if (isSamePoint(currentPosition, first)) {
                continue;
            }
            for (int j = i + 1; j < nodePositions.size(); j++) {
                Point2D second = copyOf(nodePositions.get(j), "nodePosition");
                if (isSamePoint(currentPosition, second)) {
                    continue;
                }
                candidates.addAll(circleIntersections(first, second));
            }
        }

        Point2D bestCandidate = null;
        double bestDistance = Double.POSITIVE_INFINITY;
        for (Point2D candidate : candidates) {
            if (!isOutsideForbiddenAreas(candidate, currentPosition, nodePositions)) {
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

    private boolean isOutsideForbiddenAreas(Point2D point, Point2D currentPosition, List<Point2D> nodePositions) {
        for (Point2D rawNodePosition : nodePositions) {
            Point2D nodePosition = copyOf(rawNodePosition, "nodePosition");
            if (!isSamePoint(currentPosition, nodePosition)
                    && point.distance(nodePosition) < minimumDistance - EPS) {
                return false;
            }
        }
        return true;
    }

    private static Point2D.Double copyOf(Point2D point, String paramName) {
        Objects.requireNonNull(point, paramName);
        double x = point.getX();
        double y = point.getY();
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException(paramName + " должен иметь конечное значение");
        }
        return new Point2D.Double(x, y);
    }

    private boolean isSamePoint(Point2D first, Point2D second) {
        return first.distance(second) <= EPS;
    }
}
