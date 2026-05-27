package view.game;

import model.core.Game;

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

final class GameStatusPanel extends JPanel {
    private final Game gameModel;
    private final JLabel levelNumberLabel;
    private final JLabel moveCountLabel;
    private final JLabel gameStatusLabel;

    GameStatusPanel(Game gameModel) {
        this.gameModel = Objects.requireNonNull(gameModel, "gameModel");
        configurePanel();
        levelNumberLabel = new JLabel();
        moveCountLabel = new JLabel();
        gameStatusLabel = new JLabel();
        createLabels();
        refreshState();
    }

    void refreshState() {
        refreshLevelNumber();
        refreshMoveCount();
        refreshStageLabel();
    }

    private void configurePanel() {
        setOpaque(true);
        setBackground(new Color(19, 29, 41));
        setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(58, 73, 91), 1, true),
                new EmptyBorder(14, 14, 14, 14)
        ));
        setLayout(new GridLayout(1, 3, 12, 0));
    }

    private void createLabels() {
        styleMetricLabel(levelNumberLabel);
        styleMetricLabel(moveCountLabel);
        styleStageLabel(gameStatusLabel);
        add(levelNumberLabel);
        add(moveCountLabel);
        add(gameStatusLabel);
    }

    private void refreshLevelNumber() {
        String levelText = gameModel.currentLevel() == null
                ? "-"
                : formatNumber(gameModel.currentLevelNumber());
        levelNumberLabel.setForeground(new Color(241, 236, 228));
        levelNumberLabel.setText("Уровень " + levelText);
    }

    private void refreshMoveCount() {
        moveCountLabel.setForeground(new Color(241, 236, 228));
        moveCountLabel.setText("Ходы " + formatNumber(gameModel.moveCounter()));
    }

    private void refreshStageLabel() {
        if (!gameModel.started()) {
            applyStoppedStageStyle();
        } else if (gameModel.currentLevel().scheme().hasIntersections()) {
            applyUntanglingStageStyle();
        } else {
            applySolvedStageStyle();
        }
        gameStatusLabel.setText(getMomentText());
    }

    void showCompletedState() {
        applySolvedStageStyle();
        gameStatusLabel.setText("Схема распутана");
    }

    private String getMomentText() {
        if (!gameModel.started()) {
            return "Подготовка";
        }
        return gameModel.currentLevel().scheme().hasIntersections()
                ? "Распутывание"
                : "Схема распутана";
    }

    private String formatNumber(int value) {
        return Integer.toString(value);
    }

    private void applyStoppedStageStyle() {
        gameStatusLabel.setBackground(new Color(66, 77, 93));
        gameStatusLabel.setForeground(new Color(234, 230, 223));
    }

    private void applyUntanglingStageStyle() {
        gameStatusLabel.setBackground(new Color(140, 63, 58));
        gameStatusLabel.setForeground(new Color(252, 242, 239));
    }

    private void applySolvedStageStyle() {
        gameStatusLabel.setBackground(new Color(54, 113, 89));
        gameStatusLabel.setForeground(new Color(238, 247, 241));
    }

    private void styleMetricLabel(JLabel label) {
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setBackground(new Color(28, 41, 57));
        label.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 88, 109), 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));
    }

    private void styleStageLabel(JLabel label) {
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 88, 109), 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));
    }
}
