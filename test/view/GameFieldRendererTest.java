package view;

import model.Game;
import model.Node;
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

class GameFieldRendererTest {
    @Test
    @DisplayName("Renderer draws selected node with selected color")
    void drawsSelectedNodeWithSelectedColor() {
        Game game = startedGame();
        Node selectedNode = game.currentLevel().scheme().getNodes().getFirst();
        FieldParameters parameters = new FieldParameters(12, 28);
        Color selectedColor = new Color(230, 155, 44);
        GameFieldRenderer renderer = renderer(parameters, selectedColor);

        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            renderer.drawField(graphics, game, selectedNode);
        } finally {
            graphics.dispose();
        }

        Point point = SwingTestSupport.toScreenPoint(parameters, game, selectedNode.getX(), selectedNode.getY(), image.getWidth(), image.getHeight());

        assertEquals(selectedColor.getRGB(), image.getRGB(point.x, point.y));
    }

    @Test
    @DisplayName("Renderer paints field even when game is not started")
    void paintsFieldWhenGameIsStopped() {
        Game game = new Game();
        GameFieldRenderer renderer = renderer(new FieldParameters(12, 28), new Color(230, 155, 44));

        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            renderer.drawField(graphics, game, null);
        } finally {
            graphics.dispose();
        }

        assertNotEquals(0, image.getRGB(image.getWidth() / 2, image.getHeight() / 2));
    }

    @Test
    @DisplayName("Renderer rejects null field parameters")
    void rejectsNullFieldParameters() {
        assertThrows(NullPointerException.class, () -> new GameFieldRenderer(null, Color.BLACK, Color.RED, Color.BLUE, Color.ORANGE));
    }

    @Test
    @DisplayName("Renderer rejects null colors")
    void rejectsNullColors() {
        FieldParameters parameters = new FieldParameters(12, 28);
        assertThrows(NullPointerException.class, () -> new GameFieldRenderer(parameters, null, Color.RED, Color.BLUE, Color.ORANGE));
        assertThrows(NullPointerException.class, () -> new GameFieldRenderer(parameters, Color.BLACK, null, Color.BLUE, Color.ORANGE));
        assertThrows(NullPointerException.class, () -> new GameFieldRenderer(parameters, Color.BLACK, Color.RED, null, Color.ORANGE));
        assertThrows(NullPointerException.class, () -> new GameFieldRenderer(parameters, Color.BLACK, Color.RED, Color.BLUE, null));
    }

    @Test
    @DisplayName("Renderer rejects null graphics")
    void rejectsNullGraphics() {
        assertThrows(NullPointerException.class, () -> renderer(new FieldParameters(12, 28), Color.ORANGE).drawField(null, new Game(), null));
    }

    @Test
    @DisplayName("Renderer rejects null game")
    void rejectsNullGame() {
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            assertThrows(NullPointerException.class, () -> renderer(new FieldParameters(12, 28), Color.ORANGE).drawField(graphics, null, null));
        } finally {
            graphics.dispose();
        }
    }

    @Test
    @DisplayName("Renderer paints background and board with different colors")
    void paintsBackgroundAndBoardWithDifferentColors() {
        Game game = startedGame();
        FieldParameters parameters = new FieldParameters(12, 28);
        GameFieldRenderer renderer = renderer(parameters, new Color(230, 155, 44));
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            renderer.drawField(graphics, game, null);
        } finally {
            graphics.dispose();
        }

        int outerPixel = image.getRGB(5, 5);
        int innerPixel = image.getRGB(parameters.fieldPadding() + 10, parameters.fieldPadding() + 10);

        assertNotEquals(outerPixel, innerPixel);
    }

    @Test
    @DisplayName("Renderer uses regular node color for unselected node")
    void usesRegularNodeColorForUnselectedNode() {
        Game game = startedGame();
        FieldParameters parameters = new FieldParameters(12, 28);
        Color nodeColor = new Color(53, 111, 179);
        GameFieldRenderer renderer = new GameFieldRenderer(
                parameters,
                new Color(66, 63, 60),
                new Color(198, 59, 59),
                nodeColor,
                new Color(230, 155, 44)
        );
        Node node = game.currentLevel().scheme().getNodes().getFirst();

        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            renderer.drawField(graphics, game, null);
        } finally {
            graphics.dispose();
        }

        Point point = SwingTestSupport.toScreenPoint(parameters, game, node.getX(), node.getY(), image.getWidth(), image.getHeight());

        assertEquals(nodeColor.getRGB(), image.getRGB(point.x, point.y));
    }

    @Test
    @DisplayName("Renderer updates edge color in preview before mouse release")
    void updatesEdgeColorInPreviewBeforeMouseRelease() {
        Game game = startedGame();
        FieldParameters parameters = new FieldParameters(12, 28);
        Color normalEdgeColor = new Color(66, 63, 60);
        Color intersectingEdgeColor = new Color(198, 59, 59);
        GameFieldRenderer renderer = new GameFieldRenderer(
                parameters,
                normalEdgeColor,
                intersectingEdgeColor,
                new Color(53, 111, 179),
                new Color(230, 155, 44)
        );
        Node selectedNode = game.currentLevel().scheme().getNodes().get(1);

        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            renderer.drawField(graphics, game, selectedNode, new Point2D.Double(90, 5));
        } finally {
            graphics.dispose();
        }

        Point point = SwingTestSupport.toScreenPoint(parameters, game, 50, 7.5, image.getWidth(), image.getHeight());

        assertEquals(normalEdgeColor.getRGB(), image.getRGB(point.x, point.y));
        assertNotEquals(intersectingEdgeColor.getRGB(), image.getRGB(point.x, point.y));
    }

    @Test
    @DisplayName("Renderer can draw without explicit clip")
    void canDrawWithoutExplicitClip() {
        GameFieldRenderer renderer = renderer(new FieldParameters(12, 28), new Color(230, 155, 44));
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = image.createGraphics();
        try {
            assertDoesNotThrow(() -> renderer.drawField(graphics, startedGame(), null));
        } finally {
            graphics.dispose();
        }
    }

    private static Game startedGame() {
        Game game = new Game();
        game.start();
        return game;
    }

    private static GameFieldRenderer renderer(FieldParameters parameters, Color selectedColor) {
        return new GameFieldRenderer(
                parameters,
                new Color(66, 63, 60),
                new Color(198, 59, 59),
                new Color(53, 111, 179),
                selectedColor
        );
    }
}
