package view;

import model.Game;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.util.Objects;

public final class GameWindow extends JFrame {
    private final Game gameModel;
    private GameStatusPanel gameStatusPanel;
    private GameFieldPanel gameFieldPanel;
    private GameControlPanel gameControlPanel;

    public GameWindow(Game gameModel) {
        this.gameModel = Objects.requireNonNull(gameModel, "gameModel");
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
        if (gameModel.maxCompletedLevelNumber() > previousMaxCompletedLevelNumber) {
            gameStatusPanel.showCompletedState();
            gameStatusPanel.paintImmediately(gameStatusPanel.getVisibleRect());
            showVictoryMessage(previousLevelNumber, !gameModel.started());
        }
        refreshView();
    }

    private void configureWindow() {
        setTitle("Untangle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 20));
        setMinimumSize(new Dimension(900, 940));
        Color windowBackground = new Color(14, 22, 32);
        setBackground(windowBackground);
        setLocationByPlatform(true);
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(24, 24, 22, 24));
        getContentPane().setBackground(windowBackground);
    }

    private void assembleLayout() {
        gameStatusPanel = new GameStatusPanel(gameModel);
        gameFieldPanel = new GameFieldPanel(gameModel);
        gameControlPanel = new GameControlPanel();
        add(gameStatusPanel, BorderLayout.NORTH);
        add(gameFieldPanel, BorderLayout.CENTER);
        add(gameControlPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
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
        gameModel.restartLevel(gameModel.currentLevelNumber());
        refreshView();
    }

    private void finishGame() {
        gameModel.finish();
        refreshView();
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
}
