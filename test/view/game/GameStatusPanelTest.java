package view.game;

import view.SwingTestSupport;

import model.core.Game;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameStatusPanelTest {
    @Test
    @DisplayName("Панель статуса показывает остановленную игру")
    void showsStoppedGame() {
        GameStatusPanel panel = SwingTestSupport.callOnEdt(() -> new GameStatusPanel(new Game()));

        JLabel levelLabel = SwingTestSupport.readField(panel, "levelNumberLabel", JLabel.class);
        JLabel moveLabel = SwingTestSupport.readField(panel, "moveCountLabel", JLabel.class);
        JLabel statusLabel = SwingTestSupport.readField(panel, "gameStatusLabel", JLabel.class);

        assertEquals("Уровень -", levelLabel.getText());
        assertEquals("Ходы 0", moveLabel.getText());
        assertEquals("Подготовка", statusLabel.getText());
    }

    @Test
    @DisplayName("Панель статуса отражает текущее состояние игры")
    void reflectsCurrentGameState() {
        Game game = new Game();
        game.start();
        game.moveNode(game.currentLevel().scheme().getNodes().getFirst(), new Point2D.Double(12, 12));

        GameStatusPanel panel = SwingTestSupport.callOnEdt(() -> new GameStatusPanel(game));
        SwingTestSupport.runOnEdt(panel::refreshState);

        JLabel levelLabel = SwingTestSupport.readField(panel, "levelNumberLabel", JLabel.class);
        JLabel moveLabel = SwingTestSupport.readField(panel, "moveCountLabel", JLabel.class);
        JLabel statusLabel = SwingTestSupport.readField(panel, "gameStatusLabel", JLabel.class);

        assertEquals("Уровень 1", levelLabel.getText());
        assertEquals("Ходы 1", moveLabel.getText());
        assertEquals("Распутывание", statusLabel.getText());
    }

    @Test
    @DisplayName("Панель статуса не принимает пустую игру")
    void rejectsNullGame() {
        assertThrows(NullPointerException.class, () -> new GameStatusPanel(null));
    }

    @Test
    @DisplayName("Панель статуса обновляется после завершения игры")
    void updatesAfterGameFinish() {
        Game game = new Game();
        game.start();
        GameStatusPanel panel = SwingTestSupport.callOnEdt(() -> new GameStatusPanel(game));

        game.finish();
        SwingTestSupport.runOnEdt(panel::refreshState);

        JLabel levelLabel = SwingTestSupport.readField(panel, "levelNumberLabel", JLabel.class);
        JLabel moveLabel = SwingTestSupport.readField(panel, "moveCountLabel", JLabel.class);
        JLabel statusLabel = SwingTestSupport.readField(panel, "gameStatusLabel", JLabel.class);

        assertEquals("Уровень -", levelLabel.getText());
        assertEquals("Ходы 0", moveLabel.getText());
        assertEquals("Подготовка", statusLabel.getText());
    }

    @Test
    @DisplayName("Панель статуса обновляется после перезапуска")
    void updatesAfterRestart() {
        Game game = new Game();
        game.start();
        game.moveNode(game.currentLevel().scheme().getNodes().getFirst(), new Point2D.Double(12, 12));
        GameStatusPanel panel = SwingTestSupport.callOnEdt(() -> new GameStatusPanel(game));

        game.restartCurrentLevel();
        SwingTestSupport.runOnEdt(panel::refreshState);

        JLabel levelLabel = SwingTestSupport.readField(panel, "levelNumberLabel", JLabel.class);
        JLabel moveLabel = SwingTestSupport.readField(panel, "moveCountLabel", JLabel.class);
        JLabel statusLabel = SwingTestSupport.readField(panel, "gameStatusLabel", JLabel.class);

        assertEquals("Уровень 1", levelLabel.getText());
        assertEquals("Ходы 0", moveLabel.getText());
        assertEquals("Распутывание", statusLabel.getText());
    }

    @Test
    @DisplayName("Панель статуса показывает второй уровень после победного хода")
    void showsSecondLevelAfterWinningMove() {
        Game game = new Game();
        game.start();
        game.moveNode(game.currentLevel().scheme().getNodes().get(1), new Point2D.Double(90, 5));
        GameStatusPanel panel = SwingTestSupport.callOnEdt(() -> new GameStatusPanel(game));

        SwingTestSupport.runOnEdt(panel::refreshState);

        JLabel levelLabel = SwingTestSupport.readField(panel, "levelNumberLabel", JLabel.class);
        JLabel moveLabel = SwingTestSupport.readField(panel, "moveCountLabel", JLabel.class);
        JLabel statusLabel = SwingTestSupport.readField(panel, "gameStatusLabel", JLabel.class);

        assertEquals("Уровень 2", levelLabel.getText());
        assertEquals("Ходы 0", moveLabel.getText());
        assertEquals("Распутывание", statusLabel.getText());
    }

    @Test
    @DisplayName("Панель статуса может показать состояние распутанной схемы")
    void showsCompletedSchemeState() {
        Game game = new Game();
        GameStatusPanel panel = SwingTestSupport.callOnEdt(() -> new GameStatusPanel(game));

        SwingTestSupport.runOnEdt(panel::showCompletedState);

        JLabel statusLabel = SwingTestSupport.readField(panel, "gameStatusLabel", JLabel.class);

        assertEquals("Схема распутана", statusLabel.getText());
    }

    @Test
    @DisplayName("Панель статуса настраивает три метки")
    void configuresThreeLabels() {
        GameStatusPanel panel = SwingTestSupport.callOnEdt(() -> new GameStatusPanel(new Game()));

        assertEquals(3, panel.getComponentCount());
    }

    @Test
    @DisplayName("Панель статуса может обновляться несколько раз")
    void canRefreshMultipleTimes() {
        Game game = new Game();
        GameStatusPanel panel = SwingTestSupport.callOnEdt(() -> new GameStatusPanel(game));

        assertDoesNotThrow(() -> SwingTestSupport.runOnEdt(() -> {
            panel.refreshState();
            game.start();
            panel.refreshState();
            game.finish();
            panel.refreshState();
        }));
    }
}
