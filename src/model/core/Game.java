package model.core;

import model.level.Level;
import model.level.LevelFactory;

import java.awt.geom.Point2D;
import java.util.Objects;

public final class Game {
    private int currentLevelNumber;
    private int maxCompletedLevelNumber;
    private int moveCounter;
    private boolean started;

    private final LevelFactory levelFactory;
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
    }

    public void continueGame() {
        if (started || !hasProgressToContinue()) {
            return;
        }
        started = true;
        resetMoveCounter();
        loadLevel(continueLevelNumber());
    }

    public void finish() {
        started = false;
        currentLevel = null;
        resetMoveCounter();
    }

    private void loadLevel(int levelNumber) {
        Level level = levelFactory.createLevel(levelNumber);
        loadLevel(level);
    }

    private void loadLevel(Level level) {
        currentLevel = Objects.requireNonNull(level, "level");
        currentLevelNumber = level.number();
    }

    private boolean isWin() {
        if (currentLevel == null) {
            return false;
        }
        return !currentLevel.scheme().hasIntersections();
    }

    public void restartCurrentLevel() {
        if (currentLevel == null) {
            return;
        }
        resetMoveCounter();
        loadLevel(currentLevelNumber);
    }

    public boolean moveNode(Node node, Point2D destination) {
        Objects.requireNonNull(node, "node");
        Objects.requireNonNull(destination, "destination");
        if (!started || currentLevel == null) {
            return false;
        }

        boolean moved = currentLevel.scheme().moveNode(node, destination);
        if (moved) {
            registerMove();
        }
        return moved;
    }

    private void registerMove() {
        moveCounter++;
        validateMove();
    }

    private void validateMove() {
        if (!isWin()) {
            return;
        }
        completeCurrentLevel();
        resetMoveCounter();
        goToNextLevel();
    }

    private void completeCurrentLevel() {
        maxCompletedLevelNumber = Math.max(maxCompletedLevelNumber, currentLevelNumber);
    }

    private void goToNextLevel() {
        int next = currentLevelNumber + 1;
        try {
            loadLevel(next);
        } catch (IllegalArgumentException e) {
            finish();
        }
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
        try {
            levelFactory.createLevel(levelNumber);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
