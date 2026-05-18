package view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NodeRenderStrategyTest {
    @Test
    @DisplayName("Стратегии рендера рисуют видимые маркеры")
    void strategiesDrawVisibleMarkers() {
        BufferedImage image = SwingTestSupport.createCanvas(80, 80);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            Point center = new Point(40, 40);
            new DefaultNodeRenderStrategy().render(graphics, center, 12, Color.BLUE, false);
            int defaultPixel = image.getRGB(center.x, center.y);

            new FixedNodeRenderStrategy().render(graphics, center, 12, Color.BLUE, false);
            int fixedPixel = image.getRGB(center.x, center.y);
            assertNotEquals(defaultPixel, fixedPixel);

            new DefaultNodeRenderStrategy().render(graphics, center, 12, Color.BLUE, false);
            defaultPixel = image.getRGB(center.x, center.y);
            new HorizontalNodeRenderStrategy().render(graphics, center, 12, Color.BLUE, false);
            int horizontalPixel = image.getRGB(center.x, center.y);
            assertNotEquals(defaultPixel, horizontalPixel);
        } finally {
            graphics.dispose();
        }
    }

    @Test
    @DisplayName("Стратегии рендера отклоняют некорректные параметры")
    void strategiesRejectInvalidArgs() {
        BufferedImage image = SwingTestSupport.createCanvas(80, 80);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            NodeRenderStrategy strategy = new DefaultNodeRenderStrategy();
            assertThrows(NullPointerException.class, () -> strategy.render(null, new Point(40, 40), 12, Color.BLUE, false));
            assertThrows(NullPointerException.class, () -> strategy.render(graphics, null, 12, Color.BLUE, false));
            assertThrows(NullPointerException.class, () -> strategy.render(graphics, new Point(40, 40), 12, null, false));
            assertThrows(IllegalArgumentException.class, () -> strategy.render(graphics, new Point(40, 40), 0, Color.BLUE, false));
            assertDoesNotThrow(() -> new FixedNodeRenderStrategy().render(graphics, new Point(40, 40), 12, Color.BLUE, false));
            assertDoesNotThrow(() -> new HorizontalNodeRenderStrategy().render(graphics, new Point(40, 40), 12, Color.BLUE, false));
        } finally {
            graphics.dispose();
        }
    }
}
