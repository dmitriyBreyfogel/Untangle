package view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

public final class GameControlPanel extends JPanel {
    private final JButton startGameButton;
    private final JButton restartLevelButton;
    private final JButton finishGameButton;

    private Runnable startGameAction;
    private Runnable restartLevelAction;
    private Runnable finishGameAction;

    public GameControlPanel() {
        configurePanel();
        startGameButton = new JButton("Начать игру");
        restartLevelButton = new JButton("Перезапустить уровень");
        finishGameButton = new JButton("Завершить игру");
        createButtons();
        connectButtonHandlers();
        updateButtonAvailability(false);
    }

    public void setStartGameAction(Runnable startGameAction) {
        this.startGameAction = startGameAction;
    }

    public void setRestartLevelAction(Runnable restartLevelAction) {
        this.restartLevelAction = restartLevelAction;
    }

    public void setFinishGameAction(Runnable finishGameAction) {
        this.finishGameAction = finishGameAction;
    }

    public void updateButtonAvailability(boolean gameStarted) {
        startGameButton.setEnabled(!gameStarted);
        restartLevelButton.setEnabled(gameStarted);
        finishGameButton.setEnabled(gameStarted);
        startGameButton.setBackground(gameStarted ? new Color(219, 215, 207) : new Color(41, 53, 69));
        startGameButton.setForeground(gameStarted ? new Color(143, 137, 128) : new Color(248, 245, 240));
        restartLevelButton.setBackground(gameStarted ? new Color(246, 241, 233) : new Color(229, 224, 216));
        restartLevelButton.setForeground(gameStarted ? new Color(56, 54, 51) : new Color(150, 143, 134));
        finishGameButton.setBackground(gameStarted ? new Color(234, 218, 214) : new Color(229, 224, 216));
        finishGameButton.setForeground(gameStarted ? new Color(120, 52, 43) : new Color(150, 143, 134));
    }

    private void configurePanel() {
        setOpaque(false);
        setBorder(new EmptyBorder(8, 0, 0, 0));
        setLayout(new FlowLayout(FlowLayout.CENTER, 14, 0));
    }

    private void createButtons() {
        styleButton(startGameButton, 204);
        styleButton(restartLevelButton, 224);
        styleButton(finishGameButton, 196);
        add(startGameButton);
        add(restartLevelButton);
        add(finishGameButton);
    }

    private void connectButtonHandlers() {
        startGameButton.addActionListener(event -> handleStartGameClick());
        restartLevelButton.addActionListener(event -> handleRestartLevelClick());
        finishGameButton.addActionListener(event -> handleFinishGameClick());
    }

    private void handleStartGameClick() {
        if (startGameAction != null) {
            startGameAction.run();
        }
    }

    private void handleRestartLevelClick() {
        if (restartLevelAction != null) {
            restartLevelAction.run();
        }
    }

    private void handleFinishGameClick() {
        if (finishGameAction != null) {
            finishGameAction.run();
        }
    }

    private void styleButton(JButton button, int preferredWidth) {
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(preferredWidth, 42));
        button.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 200, 190), 1, true),
                new EmptyBorder(8, 18, 8, 18)
        ));
    }
}
