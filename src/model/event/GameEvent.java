package model.event;

public sealed interface GameEvent permits
        GameStartedEvent,
        LevelStartedEvent,
        LevelRestartedEvent,
        NodeMovedEvent,
        LevelCompletedEvent,
        GameFinishedEvent {
}
