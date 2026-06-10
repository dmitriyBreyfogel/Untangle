package model.event;

public record LevelRestartedEvent(int levelNumber) implements GameEvent {
    public LevelRestartedEvent {
        if (levelNumber <= 0) {
            throw new IllegalArgumentException("Номер уровня должен быть положительным");
        }
    }
}
