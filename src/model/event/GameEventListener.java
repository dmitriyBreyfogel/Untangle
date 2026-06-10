package model.event;

@FunctionalInterface
public interface GameEventListener {
    void onGameEvent(GameEvent event);
}
