package model.event;

public record GameFinishedEvent(int maxCompletedLevelNumber) implements GameEvent {
    public GameFinishedEvent {
        if (maxCompletedLevelNumber < 0) {
            throw new IllegalArgumentException("Прогресс не может быть отрицательным");
        }
    }
}
