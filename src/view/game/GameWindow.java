package view.game;

import model.core.Game;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.Objects;

public final class GameWindow extends JFrame {
    private final Game gameModel;
    private final Runnable returnToMenuAction;
    private GameStatusPanel gameStatusPanel;
    private GameFieldPanel gameFieldPanel;
    private GameControlPanel gameControlPanel;

    public GameWindow(Game gameModel) {
        this(gameModel, () -> {
        });
    }

    public GameWindow(Game gameModel, Runnable returnToMenuAction) {
        this.gameModel = Objects.requireNonNull(gameModel, "gameModel");
        this.returnToMenuAction = Objects.requireNonNull(returnToMenuAction, "returnToMenuAction");
        configureWindow();
        assembleLayout();
        bindControlActions();
        refreshView();
    }

    public void showWindow() {
        setVisible(true);
    }

    public void refreshView() {
        gameStatusPanel.refreshState();
        gameFieldPanel.refreshField();
        gameControlPanel.updateButtonAvailability(
                gameModel.started(),
                gameModel.hasProgressToContinue(),
                gameModel.continueLevelNumber()
        );
    }

    void handleMoveResult(int previousLevelNumber, int previousMaxCompletedLevelNumber) {
        handleMoveResult(new MoveResult(previousLevelNumber, previousMaxCompletedLevelNumber));
    }

    private void configureWindow() {
        setTitle("Untangle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 20));
        setMinimumSize(new Dimension(780, 720));
        Color windowBackground = new Color(14, 22, 32);
        setBackground(windowBackground);
        setLocationByPlatform(true);
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(24, 24, 22, 24));
        getContentPane().setBackground(windowBackground);
    }

    private void assembleLayout() {
        gameStatusPanel = new GameStatusPanel(gameModel);
        gameFieldPanel = new GameFieldPanel(gameModel, this::handleMoveResult);
        gameControlPanel = new GameControlPanel();
        add(gameStatusPanel, BorderLayout.NORTH);
        add(gameFieldPanel, BorderLayout.CENTER);
        add(gameControlPanel, BorderLayout.SOUTH);
        pack();
        fitPackedWindowToScreen();
        setLocationRelativeTo(null);
    }

    private void fitPackedWindowToScreen() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int maxWidth = Math.max(getMinimumSize().width, screenBounds.width - 32);
        int maxHeight = Math.max(getMinimumSize().height, screenBounds.height - 32);
        Dimension packedSize = getSize();
        int width = Math.min(packedSize.width, maxWidth);
        int height = Math.min(packedSize.height, maxHeight);
        setSize(new Dimension(width, height));
    }

    private void bindControlActions() {
        gameControlPanel.setStartGameAction(this::startNewGame);
        gameControlPanel.setRestartLevelAction(this::handleSecondaryAction);
        gameControlPanel.setFinishGameAction(this::finishGame);
    }

    private void startNewGame() {
        gameModel.start();
        refreshView();
    }

    private void handleSecondaryAction() {
        if (gameModel.started()) {
            restartCurrentLevel();
            return;
        }
        continueGame();
    }

    private void continueGame() {
        gameModel.continueGame();
        refreshView();
    }

    private void restartCurrentLevel() {
        if (gameModel.currentLevel() == null) {
            refreshView();
            return;
        }
        gameModel.restartCurrentLevel();
        refreshView();
    }

    private void finishGame() {
        gameModel.finish();
        returnToStartMenu();
    }

    private void showVictoryMessage(int completedLevelNumber, boolean gameFinished) {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        String message = gameFinished
                ? "Игра пройдена"
                : "Уровень " + completedLevelNumber + " пройден";
        JOptionPane.showMessageDialog(this, message, "Победа", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleMoveResult(MoveResult result) {
        if (gameModel.maxCompletedLevelNumber() > result.previousMaxCompletedLevelNumber()) {
            gameStatusPanel.showCompletedState();
            gameStatusPanel.paintImmediately(gameStatusPanel.getVisibleRect());
            showVictoryMessage(result.previousLevelNumber(), !gameModel.started());
            if (!gameModel.started()) {
                returnToStartMenu();
                return;
            }
        }
        refreshView();
    }

    private void returnToStartMenu() {
        if (!GraphicsEnvironment.isHeadless()) {
            returnToMenuAction.run();
        }
        dispose();
    }
}
