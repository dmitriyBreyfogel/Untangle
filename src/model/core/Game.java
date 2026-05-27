package model.core;

import model.level.Level;
import model.level.LevelFactory;

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
        if (started) {
            return;
        }
        started = true;
        resetMoveCounter();
        loadLevel(1);
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

    public void loadLevel(int levelNumber) {
        Level level = levelFactory.createLevel(levelNumber);
        loadLevel(level);
    }

    private void loadLevel(Level level) {
        currentLevel = Objects.requireNonNull(level, "level");
        currentLevelNumber = level.number();
        currentLevel.scheme().bindGame(this);
    }

    private boolean isWin() {
        if (currentLevel == null) {
            return false;
        }
        return !currentLevel.scheme().hasIntersections();
    }

    public void restartLevel(int levelNumber) {
        resetMoveCounter();
        loadLevel(levelNumber);
    }

    public void incrementMoveCounter() {
        moveCounter++;
    }

    public void validateMove() {
        if (!isWin()) {
            return;
        }
        completeLevel();
        resetMoveCounter();
        goToNextLevel();
    }

    public void completeLevel() {
        maxCompletedLevelNumber = Math.max(maxCompletedLevelNumber, currentLevelNumber);
    }

    public void goToNextLevel() {
        int next = currentLevelNumber + 1;
        try {
            loadLevel(next);
        } catch (IllegalArgumentException e) {
            finish();
        }
    }

    public void resetMoveCounter() {
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
