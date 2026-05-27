package view.ui;

import javax.swing.JButton;
import java.awt.Color;

public final class MenuButtonFactory {
    private static final Color DISABLED_BACKGROUND = new Color(57, 67, 81);
    private static final Color DISABLED_FOREGROUND = new Color(141, 150, 161);
    private static final Color DISABLED_BORDER = new Color(86, 95, 107);

    private MenuButtonFactory() {
    }

    public static JButton primary(String text, int width) {
        return create(text, width, new Color(234, 226, 214), new Color(45, 52, 63), new Color(191, 181, 166));
    }

    public static JButton secondary(String text, int width) {
        return create(text, width, new Color(62, 86, 118), new Color(244, 240, 232), new Color(97, 126, 161));
    }

    public static JButton danger(String text, int width) {
        return create(text, width, new Color(128, 56, 54), new Color(250, 241, 239), new Color(171, 101, 97));
    }

    public static void applyState(
            JButton button,
            Color background,
            Color foreground,
            Color border,
            Color disabledBackground,
            Color disabledForeground,
            Color disabledBorder
    ) {
        if (button instanceof RoundedButton roundedButton) {
            roundedButton.applyPalette(background, foreground, border);
            roundedButton.applyDisabledPalette(disabledBackground, disabledForeground, disabledBorder);
            return;
        }
        button.setBackground(background);
        button.setForeground(foreground);
    }

    public static void applyState(JButton button, Color background, Color foreground, Color border) {
        applyState(button, background, foreground, border, DISABLED_BACKGROUND, DISABLED_FOREGROUND, DISABLED_BORDER);
    }

    private static RoundedButton create(String text, int width, Color background, Color foreground, Color border) {
        RoundedButton button = new RoundedButton(text, width);
        button.applyPalette(background, foreground, border);
        button.applyDisabledPalette(DISABLED_BACKGROUND, DISABLED_FOREGROUND, DISABLED_BORDER);
        return button;
    }
}
