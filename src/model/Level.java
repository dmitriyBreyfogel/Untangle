package model;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Level {
    private final int number;
    private final List<Point2D> initialNodeCoordinates;
    private final Map<Integer, List<Integer>> nodeConnections;

    private final Scheme scheme;
    private final GameField gameField;

    Level(int number, List<Point2D> initialNodeCoordinates, Map<Integer, List<Integer>> nodeConnections, double fieldWidth, double fieldHeight) {
        if (number <= 0) {
            throw new IllegalArgumentException("Номер уровня должен быть положительным");
        }
        this.number = number;
        this.initialNodeCoordinates = List.copyOf(Objects.requireNonNull(initialNodeCoordinates, "initialNodeCoordinates"));
        this.nodeConnections = Map.copyOf(Objects.requireNonNull(nodeConnections, "nodeConnections"));

        this.scheme = Scheme.create(this.initialNodeCoordinates, this.nodeConnections);
        this.gameField = new GameField(fieldWidth, fieldHeight, scheme);
    }

    public void reset() {
        scheme.reset();
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
