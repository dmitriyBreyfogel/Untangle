package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class GameValidationTest {
    @Test
    @DisplayName("Игра не принимает null фабрику уровней")
    void gameRejectsNullFactory() {
        assertThrows(NullPointerException.class, () -> new Game(null));
    }

    @Test
    @DisplayName("Загрузка неизвестного уровня бросает исключение")
    void loadUnknownLevelThrows() {
        Game game = new Game();
        game.start();
        assertThrows(IllegalArgumentException.class, () -> game.loadLevel(999));
    }
}

