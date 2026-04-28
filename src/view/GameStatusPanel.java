package view;

import model.Game;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Objects;

public final class GameStatusPanel extends JPanel {
    private final Game gameModel;
    private final JLabel levelNumberLabel;
    private final JLabel moveCountLabel;
    private final JLabel gameStatusLabel;

    public GameStatusPanel(Game gameModel) {
        this.gameModel = Objects.requireNonNull(gameModel, "gameModel");
        configurePanel();
        levelNumberLabel = new JLabel();
        moveCountLabel = new JLabel();
        gameStatusLabel = new JLabel();
        createLabels();
        refreshState();
    }

    public void refreshState() {
        refreshLevelNumber();
        refreshMoveCount();
        refreshStatusText();
    }

    private void configurePanel() {
        setOpaque(true);
        setBackground(new Color(251, 248, 242));
        setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 214, 202), 1, true),
                new EmptyBorder(14, 18, 14, 18)
        ));
        setLayout(new GridLayout(1, 3, 18, 0));
    }

    private void createLabels() {
        styleLabel(levelNumberLabel);
        styleLabel(moveCountLabel);
        styleLabel(gameStatusLabel);
        add(levelNumberLabel);
        add(moveCountLabel);
        add(gameStatusLabel);
    }

    private void refreshLevelNumber() {
        String levelText = gameModel.currentLevel() == null
                ? "-"
                : Integer.toString(gameModel.currentLevelNumber());
        levelNumberLabel.setForeground(new Color(53, 50, 47));
        levelNumberLabel.setText("Уровень: " + levelText);
    }

    private void refreshMoveCount() {
        moveCountLabel.setForeground(new Color(53, 50, 47));
        moveCountLabel.setText("Ходы: " + gameModel.moveCounter());
    }

    private void refreshStatusText() {
        if (!gameModel.started()) {
            gameStatusLabel.setForeground(new Color(124, 117, 109));
        } else if (gameModel.currentLevel().scheme().hasIntersections()) {
            gameStatusLabel.setForeground(new Color(168, 67, 67));
        } else {
            gameStatusLabel.setForeground(new Color(58, 121, 86));
        }
        gameStatusLabel.setText("Статус: " + getStatusText());
    }

    private String getStatusText() {
        if (!gameModel.started()) {
            return "Игра не запущена";
        }
        return gameModel.currentLevel().scheme().hasIntersections()
                ? "Есть пересечения"
                : "Уровень завершён";
    }

    private void styleLabel(JLabel label) {
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
    }
}
