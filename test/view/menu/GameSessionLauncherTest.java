package view.menu;

import model.core.Game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameSessionLauncherTest {
    @Test
    @DisplayName("Запуск новой игры открывает первый уровень")
    void newGameStartsFirstLevel() {
        Game game = new GameSessionLauncher().newGame();

        assertTrue(game.started());
        assertNotNull(game.currentLevel());
        assertEquals(1, game.currentLevelNumber());
        assertEquals(0, game.moveCounter());
    }

    @Test
    @DisplayName("Запуск с уровня открывает выбранный уровень")
    void gameAtLevelStartsSelectedLevel() {
        Game game = new GameSessionLauncher().gameAtLevel(4);

        assertTrue(game.started());
        assertNotNull(game.currentLevel());
        assertEquals(4, game.currentLevelNumber());
        assertEquals(0, game.moveCounter());
    }

    @Test
    @DisplayName("Запуск с неизвестного уровня бросает исключение")
    void gameAtLevelRejectsUnknownLevel() {
        assertThrows(IllegalArgumentException.class, () -> new GameSessionLauncher().gameAtLevel(999));
    }

    @Test
    @DisplayName("Запускатель возвращает доступные уровни")
    void returnsAvailableLevelNumbers() {
        List<Integer> levelNumbers = new GameSessionLauncher().availableLevelNumbers();

        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), levelNumbers);
        assertThrows(UnsupportedOperationException.class, () -> levelNumbers.add(11));
    }
}
