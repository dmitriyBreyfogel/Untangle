package view.render;

import view.SwingTestSupport;

import model.core.Game;
import model.core.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GraphRendererTest {
    @Test
    @DisplayName("Рендерер графа не принимает пустые параметры поля")
    void rejectsNullFieldParameters() {
        assertThrows(NullPointerException.class, () -> new GraphRenderer(null));
    }

    @Test
    @DisplayName("Рендерер графа не принимает пустой графический контекст")
    void rejectsNullGraphics() {
        assertThrows(NullPointerException.class, () -> new GraphRenderer(new FieldParameters(12, 28)).drawGraph(null, new Game()));
    }

    @Test
    @DisplayName("Рендерер графа не принимает пустую игру")
    void rejectsNullGame() {
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            assertThrows(NullPointerException.class, () -> new GraphRenderer(new FieldParameters(12, 28)).drawGraph(graphics, null));
        } finally {
            graphics.dispose();
        }
    }

    @Test
    @DisplayName("Рендерер графа рисует граф цветами по умолчанию")
    void drawsGraphWithDefaultColors() {
        Game game = startedGame();
        FieldParameters parameters = new FieldParameters(12, 28);
        GraphRenderer renderer = new GraphRenderer(parameters);
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        Node node = game.currentLevel().scheme().getNodes().getFirst();
        try {
            renderer.drawGraph(graphics, game);
        } finally {
            graphics.dispose();
        }

        Point nodePoint = SwingTestSupport.toScreenPoint(parameters, game, node.getX(), node.getY(), image.getWidth(), image.getHeight());
        Point edgePoint = SwingTestSupport.toScreenPoint(parameters, game, 50, 50, image.getWidth(), image.getHeight());

        GraphPalette palette = GraphPalette.defaultPalette();
        assertEquals(palette.nodeColor().getRGB(), image.getRGB(nodePoint.x, nodePoint.y));
        assertEquals(palette.intersectingEdgeColor().getRGB(), image.getRGB(edgePoint.x, edgePoint.y));
    }

    @Test
    @DisplayName("Рендерер графа использует кастомную палитру")
    void usesCustomPalette() {
        Game game = startedGame();
        FieldParameters parameters = new FieldParameters(12, 28);
        Color normalEdge = new Color(1, 2, 3);
        Color intersectingEdge = new Color(4, 5, 6);
        Color nodeColor = new Color(7, 8, 9);
        Color selectedColor = new Color(10, 11, 12);
        GraphRenderer renderer = new GraphRenderer(
                parameters,
                new GraphPalette(normalEdge, intersectingEdge, nodeColor, selectedColor)
        );
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        Node node = game.currentLevel().scheme().getNodes().getFirst();
        try {
            renderer.drawGraph(graphics, game, node, null);
        } finally {
            graphics.dispose();
        }

        Point nodePoint = SwingTestSupport.toScreenPoint(parameters, game, node.getX(), node.getY(), image.getWidth(), image.getHeight());
        Point edgePoint = SwingTestSupport.toScreenPoint(parameters, game, 50, 50, image.getWidth(), image.getHeight());

        assertEquals(selectedColor.getRGB(), image.getRGB(nodePoint.x, nodePoint.y));
        assertEquals(intersectingEdge.getRGB(), image.getRGB(edgePoint.x, edgePoint.y));
        assertNotEquals(0, image.getRGB(nodePoint.x, nodePoint.y));
    }

    @Test
    @DisplayName("Рендерер графа не падает, когда игра не запущена")
    void doesNotFailWhenGameIsNotStarted() {
        GraphRenderer renderer = new GraphRenderer(new FieldParameters(12, 28));
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            assertDoesNotThrow(() -> renderer.drawGraph(graphics, new Game()));
        } finally {
            graphics.dispose();
        }
    }

    @Test
    @DisplayName("Рендерер графа не принимает пустую палитру")
    void rejectsNullPalette() {
        assertThrows(NullPointerException.class, () -> new GraphRenderer(new FieldParameters(12, 28), (GraphPalette) null));
    }

    private static Game startedGame() {
        Game game = new Game();
        game.start();
        return game;
    }
}
