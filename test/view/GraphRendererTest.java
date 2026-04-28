package view;

import model.Game;
import model.Node;
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
    @DisplayName("Graph renderer rejects null field parameters")
    void rejectsNullFieldParameters() {
        assertThrows(NullPointerException.class, () -> new GraphRenderer(null));
    }

    @Test
    @DisplayName("Graph renderer rejects null graphics")
    void rejectsNullGraphics() {
        assertThrows(NullPointerException.class, () -> new GraphRenderer(new FieldParameters(12, 28)).drawGraph(null, new Game()));
    }

    @Test
    @DisplayName("Graph renderer rejects null game")
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
    @DisplayName("Graph renderer draws graph with default colors")
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

        assertEquals(new Color(52, 127, 196).getRGB(), image.getRGB(nodePoint.x, nodePoint.y));
        assertEquals(new Color(210, 70, 70).getRGB(), image.getRGB(edgePoint.x, edgePoint.y));
    }

    @Test
    @DisplayName("Graph renderer package method uses custom colors")
    void packageMethodUsesCustomColors() {
        Game game = startedGame();
        FieldParameters parameters = new FieldParameters(12, 28);
        GraphRenderer renderer = new GraphRenderer(parameters);
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        Node node = game.currentLevel().scheme().getNodes().getFirst();
        Color normalEdge = new Color(1, 2, 3);
        Color intersectingEdge = new Color(4, 5, 6);
        Color nodeColor = new Color(7, 8, 9);
        Color selectedColor = new Color(10, 11, 12);
        try {
            renderer.drawGraph(graphics, game, node, null, normalEdge, intersectingEdge, nodeColor, selectedColor);
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
    @DisplayName("Graph renderer does not fail when game is not started")
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

    private static Game startedGame() {
        Game game = new Game();
        game.start();
        return game;
    }
}
