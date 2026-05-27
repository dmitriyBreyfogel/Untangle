package view.menu;

import model.core.Game;
import model.level.LevelFactory;

import java.util.List;

final class GameSessionLauncher {
    private final LevelFactory levelFactory = new LevelFactory();

    Game newGame() {
        Game game = new Game();
        game.start();
        return game;
    }

    Game gameAtLevel(int levelNumber) {
        levelFactory.createLevel(levelNumber);
        Game game = new Game();
        game.start();
        if (levelNumber != 1) {
            game.loadLevel(levelNumber);
        }
        return game;
    }

    List<Integer> availableLevelNumbers() {
        return levelFactory.availableLevelNumbers();
    }
}
