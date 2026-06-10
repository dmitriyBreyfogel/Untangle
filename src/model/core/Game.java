package model.core;

import model.event.GameEvent;
import model.event.GameEventListener;
import model.event.GameFinishedEvent;
import model.event.GameStartedEvent;
import model.event.LevelCompletedEvent;
import model.event.LevelRestartedEvent;
import model.event.LevelStartedEvent;
import model.event.NodeMovedEvent;
import model.level.Level;
import model.level.LevelFactory;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Game {
    private int currentLevelNumber;
    private int maxCompletedLevelNumber;
    private int moveCounter;
    private boolean started;

    private final LevelFactory levelFactory;
    private final List<GameEventListener> eventListeners;
    private Level currentLevel;

    public Game() {
        this(new LevelFactory());
    }

    Game(LevelFactory levelFactory) {
        this.levelFactory = Objects.requireNonNull(levelFactory, "levelFactory");
        this.currentLevelNumber = 1;
        this.maxCompletedLevelNumber = 0;
        this.moveCounter = 0;
        this.started = false;
        this.eventListeners = new ArrayList<>();
    }

    public void addEventListener(GameEventListener eventListener) {
        eventListeners.add(Objects.requireNonNull(eventListener, "eventListener"));
    }

    public void removeEventListener(GameEventListener eventListener) {
        eventListeners.remove(Objects.requireNonNull(eventListener, "eventListener"));
    }

    public void start() {
        startAtLevel(1);
    }

    public void startAtLevel(int levelNumber) {
        if (started) {
            return;
        }
        Level level = levelFactory.createLevel(levelNumber);
        started = true;
        resetMoveCounter();
        loadLevel(level);
        publish(new GameStartedEvent(currentLevelNumber));
        publish(new LevelStartedEvent(currentLevelNumber));
    }

    public void continueGame() {
        if (started || !hasProgressToContinue()) {
            return;
        }
        started = true;
        resetMoveCounter();
        loadLevel(continueLevelNumber());
        publish(new GameStartedEvent(currentLevelNumber));
        publish(new LevelStartedEvent(currentLevelNumber));
    }

    public void finish() {
        if (!started && currentLevel == null) {
            return;
        }
        started = false;
        currentLevel = null;
        resetMoveCounter();
        publish(new GameFinishedEvent(maxCompletedLevelNumber));
    }

    private void loadLevel(int levelNumber) {
        Level level = levelFactory.createLevel(levelNumber);
        loadLevel(level);
    }

    private void loadLevel(Level level) {
        currentLevel = Objects.requireNonNull(level, "level");
        currentLevelNumber = level.number();
    }

    public void restartCurrentLevel() {
        if (currentLevel == null) {
            return;
        }
        resetMoveCounter();
        loadLevel(currentLevelNumber);
        publish(new LevelRestartedEvent(currentLevelNumber));
    }

    public boolean moveNode(Node node, Point2D destination) {
        Objects.requireNonNull(node, "node");
        Objects.requireNonNull(destination, "destination");
        if (!started || currentLevel == null) {
            return false;
        }

        Point2D previousPosition = new Point2D.Double(node.getX(), node.getY());
        boolean moved = currentLevel.scheme().moveNode(node, destination);
        if (moved) {
            Point2D currentPosition = new Point2D.Double(node.getX(), node.getY());
            registerMove(node, previousPosition, currentPosition);
        }
        return moved;
    }

    private void registerMove(Node node, Point2D previousPosition, Point2D currentPosition) {
        moveCounter++;
        publish(new NodeMovedEvent(
                currentLevelNumber,
                node,
                previousPosition.getX(),
                previousPosition.getY(),
                currentPosition.getX(),
                currentPosition.getY(),
                moveCounter
        ));
        validateMove();
    }

    private void validateMove() {
        if (currentLevel == null || !currentLevel.isCompleted()) {
            return;
        }
        completeCurrentLevel();
        resetMoveCounter();
        goToNextLevel();
    }

    private void completeCurrentLevel() {
        int completedLevelNumber = currentLevelNumber;
        maxCompletedLevelNumber = Math.max(maxCompletedLevelNumber, currentLevelNumber);
        publish(new LevelCompletedEvent(
                completedLevelNumber,
                maxCompletedLevelNumber,
                !levelExists(completedLevelNumber + 1)
        ));
    }

    private void goToNextLevel() {
        int next = currentLevelNumber + 1;
        if (!levelExists(next)) {
            finish();
            return;
        }
        loadLevel(next);
        publish(new LevelStartedEvent(currentLevelNumber));
    }

    private void resetMoveCounter() {
        moveCounter = 0;
    }

    public boolean hasProgressToContinue() {
        return maxCompletedLevelNumber > 0;
    }

    public int continueLevelNumber() {
        if (!hasProgressToContinue()) {
            return 1;
        }
        int nextUnlockedLevelNumber = maxCompletedLevelNumber + 1;
        return levelExists(nextUnlockedLevelNumber)
                ? nextUnlockedLevelNumber
                : maxCompletedLevelNumber;
    }

    public int currentLevelNumber() {
        return currentLevelNumber;
    }

    public int maxCompletedLevelNumber() {
        return maxCompletedLevelNumber;
    }

    public int moveCounter() {
        return moveCounter;
    }

    public boolean started() {
        return started;
    }

    public Level currentLevel() {
        return currentLevel;
    }

    private boolean levelExists(int levelNumber) {
        return levelFactory.availableLevelNumbers().contains(levelNumber);
    }

    private void publish(GameEvent event) {
        List<GameEventListener> listenersSnapshot = List.copyOf(eventListeners);
        for (GameEventListener eventListener : listenersSnapshot) {
            eventListener.onGameEvent(event);
        }
    }
}
