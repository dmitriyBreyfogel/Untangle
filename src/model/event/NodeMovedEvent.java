package model.event;

import model.core.Node;

import java.util.Objects;

public record NodeMovedEvent(
        int levelNumber,
        Node node,
        double previousX,
        double previousY,
        double currentX,
        double currentY,
        int moveCounter
) implements GameEvent {
    public NodeMovedEvent {
        if (levelNumber <= 0) {
            throw new IllegalArgumentException("Номер уровня должен быть положительным");
        }
        Objects.requireNonNull(node, "node");
        if (!Double.isFinite(previousX) || !Double.isFinite(previousY)
                || !Double.isFinite(currentX) || !Double.isFinite(currentY)) {
            throw new IllegalArgumentException("Координаты должны быть конечными");
        }
        if (moveCounter <= 0) {
            throw new IllegalArgumentException("Счётчик ходов должен быть положительным");
        }
    }
}
