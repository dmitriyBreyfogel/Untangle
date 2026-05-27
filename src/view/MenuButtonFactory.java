package view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

final class MenuButtonFactory {
    private MenuButtonFactory() {
    }

    static JButton primary(String text, int width) {
        return create(text, width, new Color(234, 226, 214), new Color(45, 52, 63), new Color(191, 181, 166));
    }

    static JButton secondary(String text, int width) {
        return create(text, width, new Color(62, 86, 118), new Color(244, 240, 232), new Color(97, 126, 161));
    }

    static JButton danger(String text, int width) {
        return create(text, width, new Color(128, 56, 54), new Color(250, 241, 239), new Color(171, 101, 97));
    }

    private static JButton create(String text, int width, Color background, Color foreground, Color border) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(width, 52));
        button.setMaximumSize(new Dimension(width, 52));
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setOpaque(true);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(border, 1, true),
                new EmptyBorder(12, 18, 12, 18)
        ));
        return button;
    }
}
