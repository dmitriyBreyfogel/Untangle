package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public interface NodeRenderStrategy {
    void render(Graphics2D graphics, Point center, int radius, Color color, boolean selected);
}
