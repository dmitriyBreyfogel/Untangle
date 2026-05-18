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
        startGameButton = new JButton("Новая игра");
        restartLevelButton = new JButton();
        finishGameButton = new JButton("Закончить партию");
        createButtons();
        connectButtonHandlers();
        updateButtonAvailability(false, false, 1);
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
        updateButtonAvailability(gameStarted, false, 1);
    }

    public void updateButtonAvailability(boolean gameStarted, boolean canContinue, int continueLevelNumber) {
        configureButtonCopy(gameStarted, continueLevelNumber);
        startGameButton.setEnabled(!gameStarted);
        restartLevelButton.setEnabled(gameStarted || canContinue);
        finishGameButton.setEnabled(gameStarted);
        applyButtonState(
                startGameButton,
                !gameStarted,
                new Color(35, 54, 79),
                new Color(248, 244, 236),
                new Color(84, 111, 145),
                new Color(57, 67, 81),
                new Color(141, 150, 161),
                new Color(86, 95, 107)
        );
        applyButtonState(
                restartLevelButton,
                gameStarted || canContinue,
                gameStarted ? new Color(234, 226, 214) : new Color(62, 86, 118),
                gameStarted ? new Color(45, 52, 63) : new Color(244, 240, 232),
                gameStarted ? new Color(191, 181, 166) : new Color(97, 126, 161),
                new Color(57, 67, 81),
                new Color(141, 150, 161),
                new Color(86, 95, 107)
        );
        applyButtonState(
                finishGameButton,
                gameStarted,
                new Color(128, 56, 54),
                new Color(250, 241, 239),
                new Color(171, 101, 97),
                new Color(57, 67, 81),
                new Color(141, 150, 161),
                new Color(86, 95, 107)
        );
    }

    private void configurePanel() {
        setOpaque(false);
        setBorder(new EmptyBorder(12, 0, 0, 0));
        setLayout(new FlowLayout(FlowLayout.CENTER, 16, 0));
    }

    private void createButtons() {
        startGameButton.setToolTipText("Запустить новую партию");
        finishGameButton.setToolTipText("Закрыть текущую партию");
        styleButton(startGameButton, 186);
        styleButton(restartLevelButton, 254);
        styleButton(finishGameButton, 208);
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
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setPreferredSize(new Dimension(preferredWidth, 48));
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setOpaque(true);
        button.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(86, 95, 107), 1, true),
                new EmptyBorder(10, 18, 10, 18)
        ));
    }

    private void configureButtonCopy(boolean gameStarted, int continueLevelNumber) {
        restartLevelButton.setText(gameStarted
                ? "Перезапустить уровень"
                : "Продолжить с уровня " + formatLevelNumber(continueLevelNumber));
        restartLevelButton.setToolTipText(gameStarted
                ? "Собрать текущий уровень заново"
                : "Продолжить с ближайшего доступного уровня");
        finishGameButton.setToolTipText(gameStarted
                ? "Закрыть текущую партию"
                : "Нет активной партии");
    }

    private String formatLevelNumber(int levelNumber) {
        return Integer.toString(levelNumber);
    }

    private void applyButtonState(
            JButton button,
            boolean enabled,
            Color enabledBackground,
            Color enabledForeground,
            Color enabledBorder,
            Color disabledBackground,
            Color disabledForeground,
            Color disabledBorder
    ) {
        button.setBackground(enabled ? enabledBackground : disabledBackground);
        button.setForeground(enabled ? enabledForeground : disabledForeground);
        button.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(enabled ? enabledBorder : disabledBorder, 1, true),
                new EmptyBorder(10, 18, 10, 18)
        ));
    }
}
