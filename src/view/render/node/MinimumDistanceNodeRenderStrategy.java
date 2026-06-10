package view.render.node;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public final class MinimumDistanceNodeRenderStrategy extends NodeRenderStrategy {
    private final NodeRenderStrategy baseRenderStrategy = new DefaultNodeRenderStrategy();

    @Override
    protected void renderValidated(Graphics2D graphics, Point center, int radius, Color color, boolean selected) {
        baseRenderStrategy.render(graphics, center, radius, color, selected);

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
