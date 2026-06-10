package model.movement;

import java.awt.geom.Point2D;

public final class HorizontalMovementStrategy extends MovementStrategy {
    @Override
    protected Point2D resolveValidatedMove(MovementContext context) {
        Point2D currentPosition = context.currentPosition();
        Point2D requestedPosition = context.requestedPosition();
        return new Point2D.Double(requestedPosition.getX(), currentPosition.getY());
    }
}
