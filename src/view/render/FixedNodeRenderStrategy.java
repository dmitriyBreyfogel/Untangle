package view.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Objects;

public final class FixedNodeRenderStrategy implements NodeRenderStrategy {
    private final NodeRenderStrategy baseRenderStrategy = new DefaultNodeRenderStrategy();

    @Override
    public void render(Graphics2D graphics, Point center, int radius, Color color, boolean selected) {
        Objects.requireNonNull(graphics, "graphics");
        Objects.requireNonNull(center, "center");
        baseRenderStrategy.render(graphics, center, radius, Objects.requireNonNull(color, "color"), selected);

        int bodyWidth = Math.max(9, radius + 1);
        int bodyHeight = Math.max(7, radius - 3);
        int bodyX = center.x - bodyWidth / 2;
        int bodyY = center.y - bodyHeight / 2 + 2;
        int shackleWidth = Math.max(7, radius - 2);
        int shackleHeight = Math.max(7, radius - 2);
        int shackleX = center.x - shackleWidth / 2;
        int shackleY = bodyY - shackleHeight / 2;

        graphics.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.setColor(new Color(20, 34, 48, 224));
        graphics.drawArc(shackleX, shackleY, shackleWidth, shackleHeight, 0, 180);
        graphics.fillRoundRect(bodyX, bodyY, bodyWidth, bodyHeight, 3, 3);
        graphics.setColor(new Color(255, 248, 238, 180));
        graphics.fillOval(center.x - 2, bodyY + bodyHeight / 2 - 1, 4, 4);
    }
}
