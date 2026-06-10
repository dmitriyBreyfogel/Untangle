package view.render.node;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Objects;

public final class MinimumDistanceNodeRenderStrategy implements NodeRenderStrategy {
    private final NodeRenderStrategy baseRenderStrategy = new DefaultNodeRenderStrategy();

    @Override
    public void render(Graphics2D graphics, Point center, int radius, Color color, boolean selected) {
        Objects.requireNonNull(graphics, "graphics");
        Objects.requireNonNull(center, "center");

        baseRenderStrategy.render(graphics, center, radius, Objects.requireNonNull(color, "color"), selected);

        int markerRadius = Math.max(radius + 6, 16);

        graphics.setStroke(new BasicStroke(2.2f));
        graphics.setColor(new Color(180, 40, 40, 190));
        graphics.drawOval(
                center.x - markerRadius,
                center.y - markerRadius,
                markerRadius * 2,
                markerRadius * 2
        );

        graphics.setStroke(new BasicStroke(2.0f));
        graphics.drawLine(center.x - 5, center.y, center.x + 5, center.y);
    }
}