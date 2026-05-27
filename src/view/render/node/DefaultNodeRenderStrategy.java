package view.render.node;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Objects;

public final class DefaultNodeRenderStrategy implements NodeRenderStrategy {
    @Override
    public void render(Graphics2D graphics, Point center, int radius, Color color, boolean selected) {
        Objects.requireNonNull(graphics, "graphics");
        Objects.requireNonNull(center, "center");
        Objects.requireNonNull(color, "color");
        if (radius <= 0) {
            throw new IllegalArgumentException("Радиус узла должен быть положительным");
        }

        int diameter = radius * 2;
        graphics.setColor(new Color(4, 10, 18, 56));
        graphics.fillOval(center.x - radius + 2, center.y - radius + 4, diameter, diameter);
        if (selected) {
            int glowRadius = radius + 8;
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 78));
            graphics.fillOval(
                    center.x - glowRadius,
                    center.y - glowRadius,
                    glowRadius * 2,
                    glowRadius * 2
            );
        }

        graphics.setColor(color);
        graphics.fillOval(center.x - radius, center.y - radius, diameter, diameter);
        graphics.setStroke(new BasicStroke(2f));
        graphics.setColor(new Color(255, 248, 238, 220));
        graphics.drawOval(center.x - radius, center.y - radius, diameter, diameter);
        graphics.setColor(new Color(255, 255, 255, 92));
        graphics.fillOval(center.x - radius + 4, center.y - radius + 3, Math.max(4, radius - 6), Math.max(4, radius - 6));
        graphics.setColor(new Color(11, 22, 35, 36));
        graphics.drawOval(center.x - radius + 1, center.y - radius + 1, diameter - 2, diameter - 2);
    }
}
