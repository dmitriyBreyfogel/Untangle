package model.event;

public record GameStartedEvent(int levelNumber) implements GameEvent {
    public GameStartedEvent {
        if (levelNumber <= 0) {
            throw new IllegalArgumentException("Номер уровня должен быть положительным");
        }
    }
}
