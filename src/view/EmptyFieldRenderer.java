package view;

import model.Game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

final class EmptyFieldRenderer {
    void draw(Graphics2D graphics, Rectangle clipBounds, Game gameModel) {
        int centerX = clipBounds.x + clipBounds.width / 2;
        int centerY = clipBounds.y + clipBounds.height / 2;

        graphics.setColor(new Color(32, 48, 69));
        graphics.fillRoundRect(centerX - 82, centerY - 118, 164, 30, 18, 18);
        graphics.setColor(new Color(242, 233, 219));
        graphics.setFont(new Font("Segoe UI", Font.BOLD, 12));
        drawCenteredString(graphics, "НОВАЯ ПАРТИЯ", centerX, centerY - 98);

        graphics.setColor(new Color(66, 60, 54));
        graphics.setFont(new Font("Segoe UI", Font.BOLD, 24));
        drawCenteredString(graphics, "Начните игру", centerX, centerY - 26);

        graphics.setColor(new Color(121, 112, 102));
        graphics.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        drawCenteredString(graphics, "Перетаскивайте узлы и выводите линии", centerX, centerY + 10);
        drawCenteredString(graphics, "из пересечений, пока схема не очистится", centerX, centerY + 32);

        graphics.setColor(new Color(229, 218, 199));
        graphics.fillRoundRect(centerX - 146, centerY + 54, 292, 36, 18, 18);
        graphics.setColor(new Color(68, 59, 53));
        graphics.setFont(new Font("Segoe UI", Font.BOLD, 13));
        drawCenteredString(graphics, "Нажмите «Новая игра»", centerX, centerY + 77);

        if (gameModel.hasProgressToContinue()) {
            graphics.setColor(new Color(123, 111, 99));
            graphics.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            drawCenteredString(
                    graphics,
                    "Или продолжите с уровня " + gameModel.continueLevelNumber(),
                    centerX,
                    centerY + 110
            );
        }
    }

    private void drawCenteredString(Graphics2D graphics, String text, int centerX, int baselineY) {
        int textWidth = graphics.getFontMetrics().stringWidth(text);
        graphics.drawString(text, centerX - textWidth / 2, baselineY);
    }
}
