package view;

import model.Game;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameWindowTest {
    private GameWindow window;

    @AfterEach
    void tearDown() {
        if (window != null) {
            SwingTestSupport.runOnEdt(window::dispose);
        }
    }

    @Test
    @DisplayName("Game window binds control actions to model flow")
    void bindsControlActionsToModelFlow() {
        requireWindowEnvironment();

        Game game = new Game();
        window = createWindow(game);
        GameControlPanel controlPanel = SwingTestSupport.readField(window, "gameControlPanel", GameControlPanel.class);
        JButton startButton = SwingTestSupport.readField(controlPanel, "startGameButton", JButton.class);
        JButton finishButton = SwingTestSupport.readField(controlPanel, "finishGameButton", JButton.class);

        SwingTestSupport.runOnEdt(startButton::doClick);
        assertTrue(game.started());

        SwingTestSupport.runOnEdt(finishButton::doClick);
        assertFalse(game.started());
    }

    @Test
    @DisplayName("Game window rejects null game")
    void rejectsNullGame() {
        requireWindowEnvironment();

        assertThrows(NullPointerException.class, () -> new GameWindow(null));
    }

    @Test
    @DisplayName("Game window configures frame")
    void configuresFrame() {
        requireWindowEnvironment();

        window = createWindow(new Game());

        assertEquals("Untangle", window.getTitle());
        assertEquals(GameWindow.EXIT_ON_CLOSE, window.getDefaultCloseOperation());
        assertTrue(window.getLayout() instanceof BorderLayout);
    }

    @Test
    @DisplayName("Game window assembles all panels")
    void assemblesAllPanels() {
        requireWindowEnvironment();

        window = createWindow(new Game());

        assertNotNull(SwingTestSupport.readField(window, "gameStatusPanel", GameStatusPanel.class));
        assertNotNull(SwingTestSupport.readField(window, "gameFieldPanel", GameFieldPanel.class));
        assertNotNull(SwingTestSupport.readField(window, "gameControlPanel", GameControlPanel.class));
        assertEquals(3, window.getContentPane().getComponentCount());
    }

    @Test
    @DisplayName("Game window starts with stopped state buttons")
    void startsWithStoppedStateButtons() {
        requireWindowEnvironment();

        window = createWindow(new Game());
        GameControlPanel controlPanel = SwingTestSupport.readField(window, "gameControlPanel", GameControlPanel.class);
        JButton startButton = SwingTestSupport.readField(controlPanel, "startGameButton", JButton.class);
        JButton restartButton = SwingTestSupport.readField(controlPanel, "restartLevelButton", JButton.class);
        JButton finishButton = SwingTestSupport.readField(controlPanel, "finishGameButton", JButton.class);

        assertTrue(startButton.isEnabled());
        assertFalse(restartButton.isEnabled());
        assertFalse(finishButton.isEnabled());
    }

    @Test
    @DisplayName("Game window show window makes frame visible")
    void showWindowMakesFrameVisible() {
        requireWindowEnvironment();

        window = createWindow(new Game());
        SwingTestSupport.runOnEdt(window::showWindow);

        assertTrue(window.isVisible());
    }

    @Test
    @DisplayName("Game window refresh updates buttons after game start")
    void refreshUpdatesButtonsAfterGameStart() {
        requireWindowEnvironment();

        Game game = new Game();
        window = createWindow(game);
        game.start();
        SwingTestSupport.runOnEdt(window::refreshView);
        GameControlPanel controlPanel = SwingTestSupport.readField(window, "gameControlPanel", GameControlPanel.class);
        JButton startButton = SwingTestSupport.readField(controlPanel, "startGameButton", JButton.class);
        JButton restartButton = SwingTestSupport.readField(controlPanel, "restartLevelButton", JButton.class);
        JButton finishButton = SwingTestSupport.readField(controlPanel, "finishGameButton", JButton.class);

        assertFalse(startButton.isEnabled());
        assertTrue(restartButton.isEnabled());
        assertTrue(finishButton.isEnabled());
    }

    @Test
    @DisplayName("Game window refresh updates status labels")
    void refreshUpdatesStatusLabels() {
        requireWindowEnvironment();

        Game game = new Game();
        window = createWindow(game);
        game.start();
        SwingTestSupport.runOnEdt(window::refreshView);
        GameStatusPanel statusPanel = SwingTestSupport.readField(window, "gameStatusPanel", GameStatusPanel.class);
        JLabel levelLabel = SwingTestSupport.readField(statusPanel, "levelNumberLabel", JLabel.class);
        JLabel moveLabel = SwingTestSupport.readField(statusPanel, "moveCountLabel", JLabel.class);

        assertEquals("Уровень: 1", levelLabel.getText());
        assertEquals("Ходы: 0", moveLabel.getText());
    }

    @Test
    @DisplayName("Game window restart button resets move counter")
    void restartButtonResetsMoveCounter() {
        requireWindowEnvironment();

        Game game = new Game();
        game.start();
        game.currentLevel().scheme().moveNode(game.currentLevel().scheme().getNodes().getFirst(), new java.awt.geom.Point2D.Double(12, 12));
        window = createWindow(game);
        SwingTestSupport.runOnEdt(window::refreshView);

        GameControlPanel controlPanel = SwingTestSupport.readField(window, "gameControlPanel", GameControlPanel.class);
        JButton restartButton = SwingTestSupport.readField(controlPanel, "restartLevelButton", JButton.class);
        SwingTestSupport.runOnEdt(restartButton::doClick);

        assertEquals(0, game.moveCounter());
        assertTrue(game.started());
    }

    @Test
    @DisplayName("Game window finish button clears current level")
    void finishButtonClearsCurrentLevel() {
        requireWindowEnvironment();

        Game game = new Game();
        window = createWindow(game);
        GameControlPanel controlPanel = SwingTestSupport.readField(window, "gameControlPanel", GameControlPanel.class);
        JButton startButton = SwingTestSupport.readField(controlPanel, "startGameButton", JButton.class);
        JButton finishButton = SwingTestSupport.readField(controlPanel, "finishGameButton", JButton.class);

        SwingTestSupport.runOnEdt(startButton::doClick);
        SwingTestSupport.runOnEdt(finishButton::doClick);

        assertFalse(game.started());
        assertNull(game.currentLevel());
    }

    @Test
    @DisplayName("Game window refresh after finish restores stopped state")
    void refreshAfterFinishRestoresStoppedState() {
        requireWindowEnvironment();

        Game game = new Game();
        window = createWindow(game);
        game.start();
        game.finish();
        SwingTestSupport.runOnEdt(window::refreshView);
        GameStatusPanel statusPanel = SwingTestSupport.readField(window, "gameStatusPanel", GameStatusPanel.class);
        JLabel statusLabel = SwingTestSupport.readField(statusPanel, "gameStatusLabel", JLabel.class);

        assertEquals("Статус: Игра не запущена", statusLabel.getText());
    }

    @Test
    @DisplayName("Game window refresh when level is absent does not throw")
    void refreshWhenLevelIsAbsentDoesNotThrow() {
        requireWindowEnvironment();

        Game game = new Game();
        window = createWindow(game);

        assertDoesNotThrow(() -> SwingTestSupport.runOnEdt(window::refreshView));
    }

    private static void requireWindowEnvironment() {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    }

    private GameWindow createWindow(Game game) {
        return SwingTestSupport.callOnEdt(() -> new GameWindow(game));
    }
}
