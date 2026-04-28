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
        gameControlPanel.updateButtonAvailability(gameModel.started());
    }

    void handleMoveResult(int previousLevelNumber, int previousMaxCompletedLevelNumber) {
        if (gameModel.maxCompletedLevelNumber() > previousMaxCompletedLevelNumber) {
            showVictoryMessage(previousLevelNumber, !gameModel.started());
        }
        refreshView();
    }

    private void configureWindow() {
        setTitle("Untangle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 16));
        setMinimumSize(new Dimension(840, 920));
        setBackground(new Color(243, 239, 233));
        setLocationByPlatform(true);
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        getContentPane().setBackground(new Color(243, 239, 233));
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
        gameControlPanel.setRestartLevelAction(this::restartCurrentLevel);
        gameControlPanel.setFinishGameAction(this::finishGame);
    }

    private void startNewGame() {
        gameModel.start();
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
