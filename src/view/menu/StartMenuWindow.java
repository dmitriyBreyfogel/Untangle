package view.menu;

import model.core.Game;
import view.game.GameWindow;
import view.ui.MenuButtonFactory;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Objects;

public final class StartMenuWindow extends JFrame {
    private final GameSessionLauncher gameSessionLauncher;

    public StartMenuWindow() {
        this(new GameSessionLauncher());
    }

    StartMenuWindow(GameSessionLauncher gameSessionLauncher) {
        this.gameSessionLauncher = Objects.requireNonNull(gameSessionLauncher, "gameSessionLauncher");
        configureWindow();
        assembleLayout();
    }

    public void showWindow() {
        setVisible(true);
    }

    private void configureWindow() {
        setTitle("Untangle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(520, 520));
        setResizable(false);
        getContentPane().setBackground(new Color(14, 22, 32));
        setLocationByPlatform(true);
    }

    private void assembleLayout() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(14, 22, 32));
        content.setBorder(new EmptyBorder(28, 28, 28, 28));

        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setOpaque(true);
        menu.setBackground(new Color(19, 29, 41));
        menu.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(58, 73, 91), 1, true),
                new EmptyBorder(42, 48, 42, 48)
        ));

        JLabel title = new JLabel("Untangle", SwingConstants.CENTER);
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 44));
        title.setForeground(new Color(241, 236, 228));

        JLabel subtitle = new JLabel("Распутайте схему", SwingConstants.CENTER);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(176, 186, 199));

        JButton newGameButton = MenuButtonFactory.primary("Новая игра", 300);
        JButton selectLevelButton = MenuButtonFactory.secondary("Выбрать уровень", 300);
        JButton exitButton = MenuButtonFactory.danger("Выйти", 300);
        newGameButton.setAlignmentX(CENTER_ALIGNMENT);
        selectLevelButton.setAlignmentX(CENTER_ALIGNMENT);
        exitButton.setAlignmentX(CENTER_ALIGNMENT);

        newGameButton.addActionListener(event -> openGame(gameSessionLauncher.newGame()));
        selectLevelButton.addActionListener(event -> chooseLevel());
        exitButton.addActionListener(event -> exitApplication());

        menu.add(Box.createVerticalGlue());
        menu.add(title);
        menu.add(Box.createRigidArea(new Dimension(0, 8)));
        menu.add(subtitle);
        menu.add(Box.createRigidArea(new Dimension(0, 42)));
        menu.add(newGameButton);
        menu.add(Box.createRigidArea(new Dimension(0, 14)));
        menu.add(selectLevelButton);
        menu.add(Box.createRigidArea(new Dimension(0, 14)));
        menu.add(exitButton);
        menu.add(Box.createVerticalGlue());

        content.add(menu, BorderLayout.CENTER);
        add(content);
        pack();
        setLocationRelativeTo(null);
    }

    private void chooseLevel() {
        LevelSelectionDialog dialog = new LevelSelectionDialog(this, gameSessionLauncher.availableLevelNumbers());
        Integer levelNumber = dialog.showDialog();
        if (levelNumber != null) {
            openGame(gameSessionLauncher.gameAtLevel(levelNumber));
        }
    }

    private void openGame(Game game) {
        GameWindow gameWindow = new GameWindow(game, () -> new StartMenuWindow().showWindow());
        gameWindow.showWindow();
        dispose();
    }

    private void exitApplication() {
        dispose();
        System.exit(0);
    }
}
