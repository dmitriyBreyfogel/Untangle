package view;

import model.Game;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EdgeRendererTest {
    @Test
    @DisplayName("Edge renderer rejects null field parameters")
    void rejectsNullFieldParameters() {
        assertThrows(NullPointerException.class, () -> new EdgeRenderer(null));
    }

    @Test
    @DisplayName("Edge renderer rejects null graphics")
    void rejectsNullGraphics() {
        assertThrows(NullPointerException.class, () -> new EdgeRenderer(new FieldParameters(12, 28)).drawEdges(null, new Game()));
    }

    @Test
    @DisplayName("Edge renderer rejects null game")
    void rejectsNullGame() {
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            assertThrows(NullPointerException.class, () -> new EdgeRenderer(new FieldParameters(12, 28)).drawEdges(graphics, null));
        } finally {
            graphics.dispose();
        }
    }

    @Test
    @DisplayName("Edge renderer draws intersecting edge with intersecting color")
    void drawsIntersectingEdgeWithIntersectingColor() {
        Game game = startedGame();
        FieldParameters parameters = new FieldParameters(12, 28);
        EdgeRenderer renderer = new EdgeRenderer(parameters);
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        Color normal = new Color(10, 20, 30);
        Color intersecting = new Color(200, 30, 40);
        try {
            renderer.begin(graphics);
            renderer.drawEdges(graphics, game, normal, intersecting);
        } finally {
            renderer.end();
            graphics.dispose();
        }

        Point point = SwingTestSupport.toScreenPoint(parameters, game, 50, 50, image.getWidth(), image.getHeight());

        assertEquals(intersecting.getRGB(), image.getRGB(point.x, point.y));
    }

    @Test
    @DisplayName("Edge renderer draws non intersecting edge with normal color")
    void drawsNonIntersectingEdgeWithNormalColor() {
        Game game = startedGame();
        FieldParameters parameters = new FieldParameters(12, 28);
        EdgeRenderer renderer = new EdgeRenderer(parameters);
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        Color normal = new Color(10, 20, 30);
        Color intersecting = new Color(200, 30, 40);
        try {
            renderer.begin(graphics);
            renderer.drawEdges(graphics, game, normal, intersecting);
        } finally {
            renderer.end();
            graphics.dispose();
        }

        Point point = SwingTestSupport.toScreenPoint(parameters, game, 10, 50, image.getWidth(), image.getHeight());

        assertEquals(normal.getRGB(), image.getRGB(point.x, point.y));
    }

    @Test
    @DisplayName("Edge renderer leaves image unchanged when game is not started")
    void leavesImageUnchangedWhenGameIsNotStarted() {
        Game game = new Game();
        EdgeRenderer renderer = new EdgeRenderer(new FieldParameters(12, 28));
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            renderer.drawEdges(graphics, game);
        } finally {
            graphics.dispose();
        }

        assertEquals(0, image.getRGB(160, 160));
    }

    @Test
    @DisplayName("Edge renderer can draw without explicit clip")
    void canDrawWithoutExplicitClip() {
        Game game = startedGame();
        EdgeRenderer renderer = new EdgeRenderer(new FieldParameters(12, 28));
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = image.createGraphics();
        try {
            assertDoesNotThrow(() -> renderer.drawEdges(graphics, game));
        } finally {
            graphics.dispose();
        }
    }

    @Test
    @DisplayName("Edge renderer package method rejects null colors")
    void packageMethodRejectsNullColors() {
        Game game = startedGame();
        EdgeRenderer renderer = new EdgeRenderer(new FieldParameters(12, 28));
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            renderer.begin(graphics);
            assertThrows(NullPointerException.class, () -> renderer.drawEdges(graphics, game, null, Color.RED));
            assertThrows(NullPointerException.class, () -> renderer.drawEdges(graphics, game, Color.BLACK, null));
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
