package view.ui;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

final class RoundedButton extends JButton {
    private static final int ARC = 22;

    private Color enabledBackground;
    private Color enabledForeground;
    private Color enabledBorder;
    private Color disabledBackground = new Color(57, 67, 81);
    private Color disabledForeground = new Color(141, 150, 161);
    private Color disabledBorder = new Color(86, 95, 107);

    RoundedButton(String text, int width) {
        super(text);
        setFocusPainted(false);
        setFocusable(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setBorder(new EmptyBorder(10, 20, 10, 20));
        setPreferredSize(new Dimension(width, 50));
        setMaximumSize(new Dimension(width, 50));
    }

    void applyPalette(Color background, Color foreground, Color border) {
        enabledBackground = background;
        enabledForeground = foreground;
        enabledBorder = border;
        setBackground(background);
        setForeground(foreground);
        repaint();
    }

    void applyDisabledPalette(Color background, Color foreground, Color border) {
        disabledBackground = background;
        disabledForeground = foreground;
        disabledBorder = border;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphicsCopy = (Graphics2D) graphics.create();
        try {
            graphicsCopy.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            Color background = currentBackground();
            Color border = currentBorder();

            if (isEnabled()) {
                graphicsCopy.setColor(new Color(0, 0, 0, 42));
                graphicsCopy.fillRoundRect(3, 5, width - 6, height - 7, ARC, ARC);
            }

            graphicsCopy.setColor(background);
            graphicsCopy.fillRoundRect(1, 1, width - 3, height - 5, ARC, ARC);
            graphicsCopy.setColor(new Color(255, 255, 255, isEnabled() ? 46 : 18));
            graphicsCopy.drawRoundRect(3, 3, width - 7, height - 9, ARC - 4, ARC - 4);
            graphicsCopy.setStroke(new BasicStroke(1.2f));
            graphicsCopy.setColor(border);
            graphicsCopy.drawRoundRect(1, 1, width - 3, height - 5, ARC, ARC);
        } finally {
            graphicsCopy.dispose();
        }
        setForeground(isEnabled() ? enabledForeground : disabledForeground);
        super.paintComponent(graphics);
    }

    @Override
    protected void paintBorder(Graphics graphics) {
    }

    @Override
    public boolean contains(int x, int y) {
        return new RoundRectangle2D.Float(1, 1, getWidth() - 3, getHeight() - 5, ARC, ARC).contains(x, y);
    }

    private Color currentBackground() {
        if (!isEnabled()) {
            return disabledBackground;
        }
        if (getModel().isPressed()) {
            return shift(enabledBackground, -18);
        }
        if (getModel().isRollover()) {
            return shift(enabledBackground, 12);
        }
        return enabledBackground;
    }

    private Color currentBorder() {
        if (!isEnabled()) {
            return disabledBorder;
        }
        if (getModel().isRollover()) {
            return shift(enabledBorder, 28);
        }
        return enabledBorder;
    }

    private static Color shift(Color color, int delta) {
        return new Color(
                clamp(color.getRed() + delta),
                clamp(color.getGreen() + delta),
                clamp(color.getBlue() + delta),
                color.getAlpha()
        );
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
