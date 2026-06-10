package model.event;

public record LevelStartedEvent(int levelNumber) implements GameEvent {
    public LevelStartedEvent {
        if (levelNumber <= 0) {
            throw new IllegalArgumentException("Номер уровня должен быть положительным");
        }
    }
}
