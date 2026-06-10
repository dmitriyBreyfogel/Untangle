package model.movement;

import java.awt.geom.Point2D;

public final class FreeMovementStrategy extends MovementStrategy {
    @Override
    protected Point2D resolveValidatedMove(MovementContext context) {
        return context.requestedPosition();
    }
}
