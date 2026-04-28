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
import static org.junit.jupiter.api.Assertions.assertThrows;

class NodeRendererTest {
    @Test
    @DisplayName("Node renderer rejects null field parameters")
    void rejectsNullFieldParameters() {
        assertThrows(NullPointerException.class, () -> new NodeRenderer(null));
    }

    @Test
    @DisplayName("Node renderer rejects null graphics")
    void rejectsNullGraphics() {
        assertThrows(NullPointerException.class, () -> new NodeRenderer(new FieldParameters(12, 28)).drawNodes(null, new Game()));
    }

    @Test
    @DisplayName("Node renderer rejects null game")
    void rejectsNullGame() {
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            assertThrows(NullPointerException.class, () -> new NodeRenderer(new FieldParameters(12, 28)).drawNodes(graphics, null));
        } finally {
            graphics.dispose();
        }
    }

    @Test
    @DisplayName("Node renderer draws regular node with node color")
    void drawsRegularNodeWithNodeColor() {
        Game game = startedGame();
        FieldParameters parameters = new FieldParameters(12, 28);
        Node node = game.currentLevel().scheme().getNodes().getFirst();
        NodeRenderer renderer = new NodeRenderer(parameters);
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        Color nodeColor = new Color(40, 120, 190);
        Color selectedColor = new Color(210, 140, 30);
        try {
            renderer.begin(graphics);
            renderer.drawNodes(graphics, game, null, null, nodeColor, selectedColor);
        } finally {
            renderer.end();
            graphics.dispose();
        }

        Point point = SwingTestSupport.toScreenPoint(parameters, game, node.getX(), node.getY(), image.getWidth(), image.getHeight());

        assertEquals(nodeColor.getRGB(), image.getRGB(point.x, point.y));
    }

    @Test
    @DisplayName("Node renderer draws selected node with selected color")
    void drawsSelectedNodeWithSelectedColor() {
        Game game = startedGame();
        FieldParameters parameters = new FieldParameters(12, 28);
        Node node = game.currentLevel().scheme().getNodes().getFirst();
        NodeRenderer renderer = new NodeRenderer(parameters);
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        Color nodeColor = new Color(40, 120, 190);
        Color selectedColor = new Color(210, 140, 30);
        try {
            renderer.begin(graphics);
            renderer.drawNodes(graphics, game, node, null, nodeColor, selectedColor);
        } finally {
            renderer.end();
            graphics.dispose();
        }

        Point point = SwingTestSupport.toScreenPoint(parameters, game, node.getX(), node.getY(), image.getWidth(), image.getHeight());

        assertEquals(selectedColor.getRGB(), image.getRGB(point.x, point.y));
    }

    @Test
    @DisplayName("Node renderer leaves image unchanged when game is not started")
    void leavesImageUnchangedWhenGameIsNotStarted() {
        Game game = new Game();
        NodeRenderer renderer = new NodeRenderer(new FieldParameters(12, 28));
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            renderer.drawNodes(graphics, game);
        } finally {
            graphics.dispose();
        }

        assertEquals(0, image.getRGB(160, 160));
    }

    @Test
    @DisplayName("Node renderer can draw without explicit clip")
    void canDrawWithoutExplicitClip() {
        Game game = startedGame();
        NodeRenderer renderer = new NodeRenderer(new FieldParameters(12, 28));
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = image.createGraphics();
        try {
            assertDoesNotThrow(() -> renderer.drawNodes(graphics, game));
        } finally {
            graphics.dispose();
        }
    }

    @Test
    @DisplayName("Node renderer package method rejects null colors")
    void packageMethodRejectsNullColors() {
        Game game = startedGame();
        NodeRenderer renderer = new NodeRenderer(new FieldParameters(12, 28));
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            renderer.begin(graphics);
            assertThrows(NullPointerException.class, () -> renderer.drawNodes(graphics, game, null, null, null, Color.ORANGE));
            assertThrows(NullPointerException.class, () -> renderer.drawNodes(graphics, game, null, null, Color.BLUE, null));
        } finally {
            renderer.end();
            graphics.dispose();
        }
    }

    private static Game startedGame() {
        Game game = new Game();
        game.start();
        return game;
    }
}
