package model.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class GameValidationTest {
    @Test
    @DisplayName("Игра не принимает пустую фабрику уровней")
    void gameRejectsNullFactory() {
        assertThrows(NullPointerException.class, () -> new Game(null));
    }

    @Test
    @DisplayName("Игра не принимает пустых слушателей событий")
    void gameRejectsNullEventListeners() {
        Game game = new Game();

        assertThrows(NullPointerException.class, () -> game.addEventListener(null));
        assertThrows(NullPointerException.class, () -> game.removeEventListener(null));
    }

    @Test
    @DisplayName("Загрузка неизвестного уровня бросает исключение")
    void loadUnknownLevelThrows() {
        Game game = new Game();
        assertThrows(IllegalArgumentException.class, () -> game.startAtLevel(999));
    }
}

