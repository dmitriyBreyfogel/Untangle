package model.event;

public record LevelCompletedEvent(
        int levelNumber,
        int maxCompletedLevelNumber,
        boolean lastLevel
) implements GameEvent {
    public LevelCompletedEvent {
        if (levelNumber <= 0) {
            throw new IllegalArgumentException("Номер уровня должен быть положительным");
        }
        if (maxCompletedLevelNumber < levelNumber) {
            throw new IllegalArgumentException("Прогресс не может быть меньше пройденного уровня");
        }
    }
}
