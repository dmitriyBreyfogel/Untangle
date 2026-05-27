package model.core;

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

        for (int level = 1; level <= 10; level++) {
            solveCurrentLevel(game);
        }

        assertFalse(game.started());
        assertNull(game.currentLevel());
        assertEquals(10, game.maxCompletedLevelNumber());
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

        game.restartCurrentLevel();
        assertEquals(0, game.moveCounter());
        assertEquals(1, game.currentLevelNumber());
        assertTrue(game.currentLevel().scheme().hasIntersections());
    }

    @Test
    @DisplayName("Переход за последний уровень завершает игру")
    void goingPastLastLevelFinishesGame() {
        Game game = new Game();
        game.startAtLevel(10);
        assertEquals(10, game.currentLevelNumber());

        solveCurrentLevel(game);

        assertFalse(game.started());
        assertNull(game.currentLevel());
    }

    @Test
    @DisplayName("Перезапуск без текущего уровня ничего не меняет")
    void restartWithoutCurrentLevelDoesNothing() {
        Game game = new Game();

        game.restartCurrentLevel();

        assertFalse(game.started());
        assertNull(game.currentLevel());
        assertEquals(0, game.maxCompletedLevelNumber());
        assertEquals(0, game.moveCounter());
    }

    @Test
    @DisplayName("Повторное завершение уже пройденного уровня не уменьшает прогресс")
    void completeLevelDoesNotDecreaseProgress() {
        Game game = new Game();
        game.startAtLevel(2);
        solveCurrentLevel(game);

        game.finish();
        game.startAtLevel(1);
        solveCurrentLevel(game);

        assertEquals(2, game.maxCompletedLevelNumber());
    }

    private static void solveCurrentLevel(Game game) {
        int levelNumber = game.currentLevelNumber();
        Scheme scheme = game.currentLevel().scheme();
        Point2D[] positions = solvedLevelPositions(levelNumber);

        for (int i = 0; i < positions.length && game.started() && game.currentLevelNumber() == levelNumber; i++) {
            Node node = scheme.getNodes().get(i);
            scheme.moveNode(node, positions[i]);
            if (game.started() && game.currentLevelNumber() == levelNumber) {
                assertEquals(levelNumber, game.currentLevelNumber());
                assertTrue(game.currentLevel().scheme().hasIntersections());
            }
        }
    }

    private static Point2D[] solvedLevelPositions(int levelNumber) {
        return switch (levelNumber) {
            case 1 -> new Point2D[]{
                    point(10, 10), point(90, 5), point(10, 90), point(90, 10)
            };
            case 2 -> new Point2D[]{
                    point(15, 15), point(85, 15), point(85, 85), point(15, 85), point(50, 50)
            };
            case 3 -> new Point2D[]{
                    point(50, 8), point(50, 28), point(88, 80), point(68, 68), point(12, 80), point(32, 68)
            };
            case 4 -> new Point2D[]{
                    point(50, 8), point(85, 25), point(85, 70), point(50, 92), point(15, 70), point(15, 25), point(50, 50)
            };
            case 5 -> new Point2D[]{
                    point(12, 20), point(37, 20), point(63, 20), point(88, 20), point(12, 80), point(37, 80), point(63, 80), point(88, 80)
            };
            case 6 -> new Point2D[]{
                    point(15, 15), point(50, 15), point(85, 15), point(15, 50), point(50, 50), point(85, 50), point(15, 85), point(50, 85), point(85, 85)
            };
            case 7 -> new Point2D[]{
                    point(50, 6), point(90, 35), point(75, 88), point(25, 88), point(10, 35), point(50, 30), point(68, 43), point(61, 67), point(39, 67), point(32, 43)
            };
            case 8 -> new Point2D[]{
                    point(50, 6), point(82, 18), point(94, 50), point(82, 82), point(50, 94), point(18, 82), point(6, 50), point(18, 18), point(65, 35), point(35, 65), point(50, 50)
            };
            case 9 -> new Point2D[]{
                    point(10, 12), point(37, 12), point(63, 12), point(90, 12), point(10, 50), point(37, 50), point(63, 50), point(90, 50), point(10, 88), point(37, 88), point(63, 88), point(90, 88)
            };
            case 10 -> new Point2D[]{
                    point(50, 5), point(72, 11), point(89, 28), point(95, 50), point(89, 72), point(72, 89), point(50, 95), point(28, 89), point(11, 72), point(5, 50), point(11, 28), point(28, 11), point(50, 50)
            };
            default -> throw new IllegalStateException("Unexpected level number: " + levelNumber);
        };
    }

    private static Point2D point(double x, double y) {
        return new Point2D.Double(x, y);
    }
}
