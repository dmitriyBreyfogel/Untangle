package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {
    @Test
    @DisplayName("Старт игры загружает первый уровень")
    void startLoadsFirstLevel() {
        Game game = new Game();
        game.start();

        assertTrue(game.started());
        assertNotNull(game.currentLevel());
        assertEquals(1, game.currentLevelNumber());
        assertEquals(0, game.moveCounter());
    }

    @Test
    @DisplayName("Недопустимое перемещение не считается ходом")
    void invalidMoveNotCounted() {
        Game game = new Game();
        game.start();

        Scheme scheme = game.currentLevel().scheme();
        Node node0 = scheme.getNodes().getFirst();
        scheme.moveNode(node0, new Point2D.Double(-1, 0));

        assertEquals(0, game.moveCounter());
        assertEquals(10, node0.getX());
        assertEquals(10, node0.getY());
    }

    @Test
    @DisplayName("Победный ход переводит на следующий уровень")
    void winningMoveAdvancesLevel() {
        Game game = new Game();
        game.start();

        Scheme scheme = game.currentLevel().scheme();
        Node node1 = scheme.getNodes().get(1);
        scheme.moveNode(node1, new Point2D.Double(90, 5));

        assertEquals(2, game.currentLevelNumber());
        assertEquals(1, game.maxCompletedLevelNumber());
        assertEquals(0, game.moveCounter());
        assertTrue(game.started());
    }

    @Test
    @DisplayName("Полный игровой цикл заканчивает игру")
    void fullCycleEndsGame() {
        Game game = new Game();
        game.start();

        solveCurrentLevel(game);
        solveCurrentLevel(game);
        solveCurrentLevel(game);
        solveCurrentLevel(game);
        solveCurrentLevel(game);

        assertFalse(game.started());
        assertNull(game.currentLevel());
        assertEquals(5, game.maxCompletedLevelNumber());
        assertEquals(0, game.moveCounter());
    }

    @Test
    @DisplayName("Обычный ход увеличивает счётчик и не завершает уровень")
    void regularMoveIncrementsCounter() {
        Game game = new Game();
        game.start();

        Scheme scheme = game.currentLevel().scheme();
        scheme.moveNode(scheme.getNodes().getFirst(), new Point2D.Double(12, 12));

        assertEquals(1, game.moveCounter());
        assertEquals(1, game.currentLevelNumber());
        assertEquals(0, game.maxCompletedLevelNumber());
        assertTrue(game.currentLevel().scheme().hasIntersections());
    }

    @Test
    @DisplayName("Повторный старт не перезапускает игру и не сбрасывает счётчик")
    void secondStartDoesNothing() {
        Game game = new Game();
        game.start();

        Scheme scheme = game.currentLevel().scheme();
        scheme.moveNode(scheme.getNodes().getFirst(), new Point2D.Double(12, 12));
        assertEquals(1, game.moveCounter());

        game.start();
        assertEquals(1, game.moveCounter());
        assertEquals(1, game.currentLevelNumber());
        assertNotNull(game.currentLevel());
    }

    @Test
    @DisplayName("Новая игра после прогресса снова запускается с первого уровня")
    void startAfterProgressLoadsFirstLevel() {
        Game game = new Game();
        game.start();

        Scheme scheme = game.currentLevel().scheme();
        scheme.moveNode(scheme.getNodes().get(1), new Point2D.Double(90, 5));
        assertEquals(2, game.currentLevelNumber());

        game.finish();
        game.start();

        assertTrue(game.started());
        assertEquals(1, game.currentLevelNumber());
        assertEquals(0, game.moveCounter());
    }

    @Test
    @DisplayName("Продолжение игры открывает следующий доступный уровень")
    void continueGameLoadsNextUnlockedLevel() {
        Game game = new Game();
        game.start();

        Scheme scheme = game.currentLevel().scheme();
        scheme.moveNode(scheme.getNodes().get(1), new Point2D.Double(90, 5));
        game.finish();

        game.continueGame();

        assertTrue(game.started());
        assertEquals(2, game.currentLevelNumber());
        assertNotNull(game.currentLevel());
        assertEquals(0, game.moveCounter());
    }

    @Test
    @DisplayName("Продолжение без прогресса ничего не делает")
    void continueWithoutProgressDoesNothing() {
        Game game = new Game();

        game.continueGame();

        assertFalse(game.started());
        assertNull(game.currentLevel());
        assertEquals(1, game.continueLevelNumber());
        assertFalse(game.hasProgressToContinue());
    }

    @Test
    @DisplayName("Завершение игры очищает состояние")
    void finishClearsState() {
        Game game = new Game();
        game.start();

        game.finish();

        assertFalse(game.started());
        assertNull(game.currentLevel());
        assertEquals(0, game.moveCounter());
    }

    @Test
    @DisplayName("Перезапуск уровня сбрасывает счётчик ходов")
    void restartLevelResetsMoveCounter() {
        Game game = new Game();
        game.start();

        Scheme scheme = game.currentLevel().scheme();
        scheme.moveNode(scheme.getNodes().getFirst(), new Point2D.Double(12, 12));
        assertEquals(1, game.moveCounter());

        game.restartLevel(1);
        assertEquals(0, game.moveCounter());
        assertEquals(1, game.currentLevelNumber());
        assertTrue(game.currentLevel().scheme().hasIntersections());
    }

    @Test
    @DisplayName("Переход за последний уровень завершает игру")
    void goingPastLastLevelFinishesGame() {
        Game game = new Game();
        game.start();

        game.loadLevel(5);
        assertEquals(5, game.currentLevelNumber());

        game.goToNextLevel();
        assertFalse(game.started());
        assertNull(game.currentLevel());
    }

    @Test
    @DisplayName("Проверка хода без текущего уровня ничего не меняет")
    void validateMoveWithoutCurrentLevelDoesNothing() {
        Game game = new Game();

        game.validateMove();

        assertFalse(game.started());
        assertNull(game.currentLevel());
        assertEquals(0, game.maxCompletedLevelNumber());
        assertEquals(0, game.moveCounter());
    }

    @Test
    @DisplayName("Повторное завершение уже пройденного уровня не уменьшает прогресс")
    void completeLevelDoesNotDecreaseProgress() {
        Game game = new Game();
        game.start();
        game.loadLevel(2);
        game.completeLevel();

        game.loadLevel(1);
        game.completeLevel();

        assertEquals(2, game.maxCompletedLevelNumber());
    }

    private static void solveCurrentLevel(Game game) {
        Scheme scheme = game.currentLevel().scheme();
        switch (game.currentLevelNumber()) {
            case 1, 3, 4, 5 -> scheme.moveNode(scheme.getNodes().get(1), new Point2D.Double(90, 5));
            case 2 -> scheme.moveNode(scheme.getNodes().getFirst(), new Point2D.Double(0, 80));
            default -> throw new IllegalStateException("Unexpected level number: " + game.currentLevelNumber());
        }
    }
}
