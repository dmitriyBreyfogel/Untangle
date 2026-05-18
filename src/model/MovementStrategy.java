package model;

import java.awt.geom.Point2D;

public interface MovementStrategy {
    Point2D resolveMove(Node node, Point2D requestedPosition);
}
