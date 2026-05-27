package view.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

final class BoardRenderer {
    void draw(Graphics2D graphics, Rectangle clipBounds, FieldParameters fieldParameters) {
        drawBackdrop(graphics, clipBounds);
        int padding = fieldParameters.fieldPadding();
        int fieldWidth = Math.max(1, clipBounds.width - padding * 2);
        int fieldHeight = Math.max(1, clipBounds.height - padding * 2);
        drawBoardSurface(graphics, padding, fieldWidth, fieldHeight);
        drawBoardTexture(graphics, padding, fieldWidth, fieldHeight);
    }

    private void drawBackdrop(Graphics2D graphics, Rectangle clipBounds) {
        graphics.setPaint(new GradientPaint(
                0,
                clipBounds.y,
                new Color(10, 18, 29),
                0,
                clipBounds.y + clipBounds.height,
                new Color(24, 36, 49)
        ));
        graphics.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
        graphics.setPaint(new GradientPaint(
                clipBounds.x,
                clipBounds.y,
                new Color(255, 214, 143, 34),
                clipBounds.x + clipBounds.width,
                clipBounds.y + clipBounds.height,
                new Color(255, 255, 255, 0)
        ));
        graphics.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
    }

    private void drawBoardSurface(Graphics2D graphics, int padding, int fieldWidth, int fieldHeight) {
        int arc = 42;
        graphics.setColor(new Color(0, 0, 0, 48));
        graphics.fillRoundRect(padding + 10, padding + 14, fieldWidth, fieldHeight, arc, arc);
        graphics.setColor(new Color(6, 12, 19, 26));
        graphics.fillRoundRect(padding + 5, padding + 6, fieldWidth, fieldHeight, arc, arc);
        graphics.setPaint(new GradientPaint(
                0,
                padding,
                new Color(251, 246, 236),
                0,
                padding + fieldHeight,
                new Color(227, 219, 205)
        ));
        graphics.fillRoundRect(padding, padding, fieldWidth, fieldHeight, arc, arc);
        graphics.setColor(new Color(171, 156, 136));
        graphics.drawRoundRect(padding, padding, fieldWidth, fieldHeight, arc, arc);
        graphics.setColor(new Color(255, 255, 255, 92));
        graphics.drawRoundRect(padding + 2, padding + 2, fieldWidth - 4, fieldHeight - 4, arc - 6, arc - 6);
    }

    private void drawBoardTexture(Graphics2D graphics, int padding, int fieldWidth, int fieldHeight) {
        Shape previousClip = graphics.getClip();
        RoundRectangle2D boardShape = new RoundRectangle2D.Float(padding, padding, fieldWidth, fieldHeight, 42, 42);
        graphics.clip(boardShape);
        graphics.setColor(new Color(255, 255, 255, 24));
        for (int y = padding + 22; y < padding + fieldHeight; y += 40) {
            graphics.drawLine(padding + 18, y, padding + fieldWidth - 18, y);
        }
        graphics.setColor(new Color(35, 52, 72, 14));
        for (int x = padding + 22; x < padding + fieldWidth; x += 40) {
            graphics.drawLine(x, padding + 18, x, padding + fieldHeight - 18);
        }
        graphics.setColor(new Color(255, 255, 255, 18));
        for (int x = padding + 24; x < padding + fieldWidth - 10; x += 56) {
            for (int y = padding + 28; y < padding + fieldHeight - 10; y += 56) {
                graphics.fillOval(x, y, 3, 3);
            }
        }
        graphics.setClip(previousClip);
    }
}
