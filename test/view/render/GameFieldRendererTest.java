package view.render;

import view.SwingTestSupport;

import model.core.Game;
import model.core.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import view.render.node.NodeRenderStrategyRegistry;

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
    @DisplayName("Рендерер поля рисует выбранный узел выбранным цветом")
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
    @DisplayName("Рендерер поля рисует поле, даже когда игра не запущена")
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
    @DisplayName("Рендерер поля не принимает пустые параметры поля")
    void rejectsNullFieldParameters() {
        assertThrows(NullPointerException.class, () -> new GameFieldRenderer(null));
        assertThrows(NullPointerException.class, () -> new GameFieldRenderer(null, GraphPalette.defaultPalette()));
    }

    @Test
    @DisplayName("Рендерер поля не принимает пустой реестр стратегий отрисовки")
    void rejectsNullNodeRenderStrategyRegistry() {
        FieldParameters parameters = new FieldParameters(12, 28);

        assertThrows(NullPointerException.class, () -> new GameFieldRenderer(parameters, (NodeRenderStrategyRegistry) null));
    }

    @Test
    @DisplayName("Рендерер поля не принимает пустую палитру графа")
    void rejectsNullGraphPalette() {
        FieldParameters parameters = new FieldParameters(12, 28);

        assertThrows(NullPointerException.class, () -> new GameFieldRenderer(parameters, (GraphPalette) null));
    }

    @Test
    @DisplayName("Рендерер поля не принимает пустой графический контекст")
    void rejectsNullGraphics() {
        assertThrows(NullPointerException.class, () -> renderer(new FieldParameters(12, 28), Color.ORANGE).drawField(null, new Game(), null));
    }

    @Test
    @DisplayName("Рендерер поля не принимает пустую игру")
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
    @DisplayName("Рендерер поля рисует фон и доску разными цветами")
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
    @DisplayName("Рендерер поля использует обычный цвет для невыбранного узла")
    void usesRegularNodeColorForUnselectedNode() {
        Game game = startedGame();
        FieldParameters parameters = new FieldParameters(12, 28);
        Color nodeColor = new Color(53, 111, 179);
        GraphPalette palette = new GraphPalette(
                new Color(66, 63, 60),
                new Color(198, 59, 59),
                nodeColor,
                new Color(230, 155, 44)
        );
        GameFieldRenderer renderer = new GameFieldRenderer(parameters, palette);
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
    @DisplayName("Рендерер поля обновляет цвет грани в предпросмотре до отпускания мыши")
    void updatesEdgeColorInPreviewBeforeMouseRelease() {
        Game game = startedGame();
        FieldParameters parameters = new FieldParameters(12, 28);
        Color normalEdgeColor = new Color(66, 63, 60);
        Color intersectingEdgeColor = new Color(198, 59, 59);
        GraphPalette palette = new GraphPalette(
                normalEdgeColor,
                intersectingEdgeColor,
                new Color(53, 111, 179),
                new Color(230, 155, 44)
        );
        GameFieldRenderer renderer = new GameFieldRenderer(parameters, palette);
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
    @DisplayName("Рендерер поля может рисовать без явной области отсечения")
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
                new GraphPalette(
                        new Color(66, 63, 60),
                        new Color(198, 59, 59),
                        new Color(53, 111, 179),
                        selectedColor
                )
        );
    }
}
