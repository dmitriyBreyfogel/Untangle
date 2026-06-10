package view.render.node;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public final class HorizontalNodeRenderStrategy extends NodeRenderStrategy {
    private final NodeRenderStrategy baseRenderStrategy = new DefaultNodeRenderStrategy();

    @Override
    protected void renderValidated(Graphics2D graphics, Point center, int radius, Color color, boolean selected) {
        baseRenderStrategy.render(graphics, center, radius, color, selected);

        int halfWidth = Math.max(7, radius - 3);
        int arrow = Math.max(4, radius / 3);
        graphics.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.setColor(new Color(20, 34, 48, 224));
        graphics.drawLine(center.x - halfWidth, center.y, center.x + halfWidth, center.y);
        graphics.drawLine(center.x - halfWidth, center.y, center.x - halfWidth + arrow, center.y - arrow);
        graphics.drawLine(center.x - halfWidth, center.y, center.x - halfWidth + arrow, center.y + arrow);
        graphics.drawLine(center.x + halfWidth, center.y, center.x + halfWidth - arrow, center.y - arrow);
        graphics.drawLine(center.x + halfWidth, center.y, center.x + halfWidth - arrow, center.y + arrow);
    }
}
