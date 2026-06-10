package view.render.node;

import model.movement.MinimumDistanceMovementStrategy;
import view.SwingTestSupport;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MinimumDistanceNodeRenderStrategyTest {
    @Test
    @DisplayName("Стратегия рендера рисует кольцо запретной области")
    void drawsForbiddenAreaRing() {
        BufferedImage image = SwingTestSupport.createCanvas(100, 100);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            new MinimumDistanceNodeRenderStrategy().render(graphics, new Point(50, 50), 12, Color.BLUE, false);
        } finally {
            graphics.dispose();
        }

        Color markerPixel = new Color(image.getRGB(68, 50), true);
        assertTrue(markerPixel.getAlpha() > 0);
        assertTrue(markerPixel.getRed() > markerPixel.getBlue());
    }

    @Test
    @DisplayName("Стратегия рендера добавляет маркер поверх обычного узла")
    void drawsMarkerOverDefaultNode() {
        Point center = new Point(50, 50);
        BufferedImage defaultImage = SwingTestSupport.createCanvas(100, 100);
        Graphics2D defaultGraphics = SwingTestSupport.createGraphics(defaultImage);
        try {
            new DefaultNodeRenderStrategy().render(defaultGraphics, center, 12, Color.BLUE, false);
        } finally {
            defaultGraphics.dispose();
        }

        BufferedImage minimumDistanceImage = SwingTestSupport.createCanvas(100, 100);
        Graphics2D minimumDistanceGraphics = SwingTestSupport.createGraphics(minimumDistanceImage);
        try {
            new MinimumDistanceNodeRenderStrategy().render(minimumDistanceGraphics, center, 12, Color.BLUE, false);
        } finally {
            minimumDistanceGraphics.dispose();
        }

        assertNotEquals(defaultImage.getRGB(center.x, center.y), minimumDistanceImage.getRGB(center.x, center.y));
    }

    @Test
    @DisplayName("Стратегия рендера сохраняет свечение выбранного узла")
    void selectedNodeKeepsGlow() {
        BufferedImage image = SwingTestSupport.createCanvas(100, 100);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            new MinimumDistanceNodeRenderStrategy().render(graphics, new Point(50, 50), 12, Color.BLUE, true);
        } finally {
            graphics.dispose();
        }

        assertNotEquals(0, image.getRGB(35, 50));
    }

    @Test
    @DisplayName("Стратегия рендера масштабирует кольцо от радиуса узла")
    void scalesRingWithNodeRadius() {
        BufferedImage image = SwingTestSupport.createCanvas(140, 140);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            new MinimumDistanceNodeRenderStrategy().render(graphics, new Point(70, 70), 20, Color.BLUE, false);
        } finally {
            graphics.dispose();
        }

        Color markerPixel = new Color(image.getRGB(96, 70), true);
        assertTrue(markerPixel.getAlpha() > 0);
        assertTrue(markerPixel.getRed() > markerPixel.getBlue());
    }

    @Test
    @DisplayName("Стратегия рендера работает для выбранного и обычного состояния")
    void rendersSelectedAndRegularStates() {
        BufferedImage image = SwingTestSupport.createCanvas(100, 100);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            MinimumDistanceNodeRenderStrategy strategy = new MinimumDistanceNodeRenderStrategy();

            assertDoesNotThrow(() -> strategy.render(graphics, new Point(30, 50), 12, Color.BLUE, false));
            assertDoesNotThrow(() -> strategy.render(graphics, new Point(70, 50), 12, Color.BLUE, true));
        } finally {
            graphics.dispose();
        }
    }

    @Test
    @DisplayName("Стратегия рендера регистрируется для стратегии минимальной дистанции")
    void defaultRegistryResolvesMinimumDistanceRenderer() {
        NodeRenderStrategyRegistry registry = NodeRenderStrategyRegistry.createDefault();

        assertInstanceOf(
                MinimumDistanceNodeRenderStrategy.class,
                registry.resolve(new MinimumDistanceMovementStrategy(List.of()))
        );
    }

    @Test
    @DisplayName("Стратегия рендера отклоняет некорректные параметры")
    void rejectsInvalidArgs() {
        BufferedImage image = SwingTestSupport.createCanvas(100, 100);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            MinimumDistanceNodeRenderStrategy strategy = new MinimumDistanceNodeRenderStrategy();

            assertThrows(NullPointerException.class, () -> strategy.render(null, new Point(50, 50), 12, Color.BLUE, false));
            assertThrows(NullPointerException.class, () -> strategy.render(graphics, null, 12, Color.BLUE, false));
            assertThrows(NullPointerException.class, () -> strategy.render(graphics, new Point(50, 50), 12, null, false));
            assertThrows(IllegalArgumentException.class, () -> strategy.render(graphics, new Point(50, 50), 0, Color.BLUE, false));
        } finally {
            graphics.dispose();
        }
    }
}
