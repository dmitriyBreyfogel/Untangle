package model.movement;

import java.awt.geom.Point2D;
import java.util.Objects;

public abstract class MovementStrategy {
    public final Point2D resolveMove(MovementContext context) {
        Objects.requireNonNull(context, "context");
        Point2D resolvedPosition = resolveValidatedMove(context);
        return MovementContext.copyOf(resolvedPosition, "resolvedPosition");
    }

    protected abstract Point2D resolveValidatedMove(MovementContext context);
}
