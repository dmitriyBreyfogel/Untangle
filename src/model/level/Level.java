package model.level;

import model.core.GameField;
import model.core.Scheme;
import model.movement.MovementStrategy;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Level {
    private final int number;
    private final Scheme scheme;
    private final GameField gameField;

    Level(int number, List<Point2D> initialNodeCoordinates, Map<Integer, List<Integer>> nodeConnections, double fieldWidth, double fieldHeight) {
        this(number, initialNodeCoordinates, nodeConnections, Map.of(), fieldWidth, fieldHeight);
    }

    Level(
            int number,
            List<Point2D> initialNodeCoordinates,
            Map<Integer, List<Integer>> nodeConnections,
            Map<Integer, MovementStrategy> nodeMovementStrategies,
            double fieldWidth,
            double fieldHeight
    ) {
        if (number <= 0) {
            throw new IllegalArgumentException("Номер уровня должен быть положительным");
        }
        this.number = number;
        List<Point2D> copiedInitialNodeCoordinates = List.copyOf(Objects.requireNonNull(initialNodeCoordinates, "initialNodeCoordinates"));
        Map<Integer, List<Integer>> copiedNodeConnections = Map.copyOf(Objects.requireNonNull(nodeConnections, "nodeConnections"));
        Map<Integer, MovementStrategy> copiedNodeMovementStrategies = Map.copyOf(Objects.requireNonNull(nodeMovementStrategies, "nodeMovementStrategies"));

        this.gameField = new GameField(fieldWidth, fieldHeight);
        this.scheme = Scheme.create(copiedInitialNodeCoordinates, copiedNodeConnections, copiedNodeMovementStrategies, gameField);
    }

    public void reset() {
        scheme.reset();
    }

    public boolean isCompleted() {
        return !scheme.hasIntersections();
    }

    public int number() {
        return number;
    }

    public GameField gameField() {
        return gameField;
    }

    public Scheme scheme() {
        return scheme;
    }
}
