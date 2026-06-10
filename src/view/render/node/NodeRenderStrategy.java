package view.render.node;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Objects;

public abstract class NodeRenderStrategy {
    public final void render(Graphics2D graphics, Point center, int radius, Color color, boolean selected) {
        Objects.requireNonNull(graphics, "graphics");
        Objects.requireNonNull(center, "center");
        Objects.requireNonNull(color, "color");
        if (radius <= 0) {
            throw new IllegalArgumentException("Радиус узла должен быть положительным");
        }
        renderValidated(graphics, center, radius, color, selected);
    }

    protected abstract void renderValidated(Graphics2D graphics, Point center, int radius, Color color, boolean selected);
}
