package view.menu;

import view.ui.MenuButtonFactory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.List;
import java.util.Objects;

final class LevelSelectionDialog extends JDialog {
    private Integer selectedLevelNumber;

    LevelSelectionDialog(Frame owner, List<Integer> levelNumbers) {
        super(owner, "Выбор уровня", true);
        Objects.requireNonNull(levelNumbers, "levelNumbers");
        configureDialog();
        assembleLayout(levelNumbers);
    }

    Integer showDialog() {
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return selectedLevelNumber;
    }

    private void configureDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(new Color(14, 22, 32));
    }

    private void assembleLayout(List<Integer> levelNumbers) {
        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(true);
        content.setBackground(new Color(19, 29, 41));
        content.setBorder(new EmptyBorder(26, 28, 28, 28));

        JLabel title = new JLabel("Выберите уровень", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(241, 236, 228));
        content.add(title, BorderLayout.NORTH);

        JPanel levelPanel = new JPanel(new GridLayout(0, 5, 10, 10));
        levelPanel.setOpaque(false);
        for (int levelNumber : levelNumbers) {
            levelPanel.add(createLevelButton(levelNumber));
        }
        content.add(levelPanel, BorderLayout.CENTER);

        add(content);
        pack();
    }

    private JButton createLevelButton(int levelNumber) {
        JButton button = MenuButtonFactory.secondary(Integer.toString(levelNumber), 64);
        button.addActionListener(event -> selectLevel(levelNumber));
        return button;
    }

    private void selectLevel(int levelNumber) {
        selectedLevelNumber = levelNumber;
        dispose();
    }
}
