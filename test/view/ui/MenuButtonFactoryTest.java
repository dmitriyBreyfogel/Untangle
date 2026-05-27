package view.ui;

import view.SwingTestSupport;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuButtonFactoryTest {
    @Test
    @DisplayName("Фабрика создаёт округлённые кнопки меню")
    void createsRoundedButtons() {
        JButton button = MenuButtonFactory.primary("Играть", 180);

        assertInstanceOf(RoundedButton.class, button);
        assertEquals("Играть", button.getText());
        assertEquals(new Dimension(180, 50), button.getPreferredSize());
        assertEquals(new Dimension(180, 50), button.getMaximumSize());
        assertEquals(Cursor.HAND_CURSOR, button.getCursor().getType());
        assertFalse(button.isFocusPainted());
        assertFalse(button.isFocusable());
        assertFalse(button.isContentAreaFilled());
        assertFalse(button.isBorderPainted());
        assertFalse(button.isOpaque());
    }

    @Test
    @DisplayName("Фабрика задаёт разные палитры кнопок")
    void appliesDifferentPalettes() {
        JButton primary = MenuButtonFactory.primary("Основная", 120);
        JButton secondary = MenuButtonFactory.secondary("Обычная", 120);
        JButton danger = MenuButtonFactory.danger("Опасная", 120);

        assertEquals(new Color(234, 226, 214), primary.getBackground());
        assertEquals(new Color(62, 86, 118), secondary.getBackground());
        assertEquals(new Color(128, 56, 54), danger.getBackground());
    }

    @Test
    @DisplayName("Применение состояния меняет обычную кнопку")
    void applyStateUpdatesPlainButton() {
        JButton button = new JButton("Кнопка");
        Color background = new Color(1, 2, 3);
        Color foreground = new Color(4, 5, 6);

        MenuButtonFactory.applyState(button, background, foreground, Color.BLACK);

        assertEquals(background, button.getBackground());
        assertEquals(foreground, button.getForeground());
    }

    @Test
    @DisplayName("Округлённая кнопка рисует включённое состояние")
    void roundedButtonPaintsEnabledState() {
        JButton button = MenuButtonFactory.primary("", 120);
        button.setSize(120, 50);
        BufferedImage image = SwingTestSupport.createCanvas(120, 50);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            button.paint(graphics);
        } finally {
            graphics.dispose();
        }

        assertEquals(new Color(234, 226, 214).getRGB(), image.getRGB(60, 24));
    }

    @Test
    @DisplayName("Округлённая кнопка рисует выключенное состояние")
    void roundedButtonPaintsDisabledState() {
        JButton button = MenuButtonFactory.secondary("", 120);
        Color disabledBackground = new Color(8, 9, 10);
        MenuButtonFactory.applyState(
                button,
                Color.BLUE,
                Color.WHITE,
                Color.BLACK,
                disabledBackground,
                Color.GRAY,
                Color.DARK_GRAY
        );
        button.setEnabled(false);
        button.setSize(120, 50);
        BufferedImage image = SwingTestSupport.createCanvas(120, 50);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            button.paint(graphics);
        } finally {
            graphics.dispose();
        }

        assertEquals(disabledBackground.getRGB(), image.getRGB(60, 24));
    }

    @Test
    @DisplayName("Округлённая кнопка учитывает форму при попадании курсора")
    void roundedButtonUsesRoundedHitArea() {
        JButton button = MenuButtonFactory.primary("Играть", 120);
        button.setSize(120, 50);

        assertTrue(button.contains(60, 24));
        assertFalse(button.contains(0, 0));
    }
}
