package view;

import model.Game;
import model.Scheme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameStatusPanelTest {
    @Test
    @DisplayName("Status panel shows stopped game")
    void showsStoppedGame() {
        GameStatusPanel panel = SwingTestSupport.callOnEdt(() -> new GameStatusPanel(new Game()));

        JLabel levelLabel = SwingTestSupport.readField(panel, "levelNumberLabel", JLabel.class);
        JLabel moveLabel = SwingTestSupport.readField(panel, "moveCountLabel", JLabel.class);
        JLabel statusLabel = SwingTestSupport.readField(panel, "gameStatusLabel", JLabel.class);

        assertEquals("Уровень: -", levelLabel.getText());
        assertEquals("Ходы: 0", moveLabel.getText());
        assertEquals("Статус: Игра не запущена", statusLabel.getText());
    }

    @Test
    @DisplayName("Status panel reflects current game state")
    void reflectsCurrentGameState() {
        Game game = new Game();
        game.start();
        Scheme scheme = game.currentLevel().scheme();
        scheme.moveNode(scheme.getNodes().getFirst(), new Point2D.Double(12, 12));

        GameStatusPanel panel = SwingTestSupport.callOnEdt(() -> new GameStatusPanel(game));
        SwingTestSupport.runOnEdt(panel::refreshState);

        JLabel levelLabel = SwingTestSupport.readField(panel, "levelNumberLabel", JLabel.class);
        JLabel moveLabel = SwingTestSupport.readField(panel, "moveCountLabel", JLabel.class);
        JLabel statusLabel = SwingTestSupport.readField(panel, "gameStatusLabel", JLabel.class);

        assertEquals("Уровень: 1", levelLabel.getText());
        assertEquals("Ходы: 1", moveLabel.getText());
        assertEquals("Статус: Есть пересечения", statusLabel.getText());
    }

    @Test
    @DisplayName("Status panel rejects null game")
    void rejectsNullGame() {
        assertThrows(NullPointerException.class, () -> new GameStatusPanel(null));
    }

    @Test
    @DisplayName("Status panel updates after game finish")
    void updatesAfterGameFinish() {
        Game game = new Game();
        game.start();
        GameStatusPanel panel = SwingTestSupport.callOnEdt(() -> new GameStatusPanel(game));

        game.finish();
        SwingTestSupport.runOnEdt(panel::refreshState);

        JLabel levelLabel = SwingTestSupport.readField(panel, "levelNumberLabel", JLabel.class);
        JLabel moveLabel = SwingTestSupport.readField(panel, "moveCountLabel", JLabel.class);
        JLabel statusLabel = SwingTestSupport.readField(panel, "gameStatusLabel", JLabel.class);

        assertEquals("Уровень: -", levelLabel.getText());
        assertEquals("Ходы: 0", moveLabel.getText());
        assertEquals("Статус: Игра не запущена", statusLabel.getText());
    }

    @Test
    @DisplayName("Status panel updates after restart")
    void updatesAfterRestart() {
        Game game = new Game();
        game.start();
        game.currentLevel().scheme().moveNode(game.currentLevel().scheme().getNodes().getFirst(), new Point2D.Double(12, 12));
        GameStatusPanel panel = SwingTestSupport.callOnEdt(() -> new GameStatusPanel(game));

        game.restartLevel(1);
        SwingTestSupport.runOnEdt(panel::refreshState);

        JLabel levelLabel = SwingTestSupport.readField(panel, "levelNumberLabel", JLabel.class);
        JLabel moveLabel = SwingTestSupport.readField(panel, "moveCountLabel", JLabel.class);
        JLabel statusLabel = SwingTestSupport.readField(panel, "gameStatusLabel", JLabel.class);

        assertEquals("Уровень: 1", levelLabel.getText());
        assertEquals("Ходы: 0", moveLabel.getText());
        assertEquals("Статус: Есть пересечения", statusLabel.getText());
    }

    @Test
    @DisplayName("Status panel shows second level after winning move")
    void showsSecondLevelAfterWinningMove() {
        Game game = new Game();
        game.start();
        Scheme scheme = game.currentLevel().scheme();
        scheme.moveNode(scheme.getNodes().get(1), new Point2D.Double(90, 5));
        GameStatusPanel panel = SwingTestSupport.callOnEdt(() -> new GameStatusPanel(game));

        SwingTestSupport.runOnEdt(panel::refreshState);

        JLabel levelLabel = SwingTestSupport.readField(panel, "levelNumberLabel", JLabel.class);
        JLabel moveLabel = SwingTestSupport.readField(panel, "moveCountLabel", JLabel.class);
        JLabel statusLabel = SwingTestSupport.readField(panel, "gameStatusLabel", JLabel.class);

        assertEquals("Уровень: 2", levelLabel.getText());
        assertEquals("Ходы: 0", moveLabel.getText());
        assertEquals("Статус: Есть пересечения", statusLabel.getText());
    }

    @Test
    @DisplayName("Status panel configures three labels")
    void configuresThreeLabels() {
        GameStatusPanel panel = SwingTestSupport.callOnEdt(() -> new GameStatusPanel(new Game()));

        assertEquals(3, panel.getComponentCount());
    }

    @Test
    @DisplayName("Status panel can refresh multiple times")
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
