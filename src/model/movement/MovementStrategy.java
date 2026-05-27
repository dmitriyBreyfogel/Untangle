package model.movement;

import java.awt.geom.Point2D;

public interface MovementStrategy {
    Point2D resolveMove(Point2D currentPosition, Point2D requestedPosition);
}
