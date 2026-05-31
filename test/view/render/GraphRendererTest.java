package view.render;

import view.SwingTestSupport;

import model.core.Game;
import model.core.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import view.render.node.NodeRenderStrategyRegistry;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @DisplayName("Рендерер графа не принимает пустой реестр стратегий отрисовки")
    void rejectsNullNodeRenderStrategyRegistry() {
        assertThrows(NullPointerException.class, () -> new GraphRenderer(new FieldParameters(12, 28), (NodeRenderStrategyRegistry) null));
    }

    private static Game startedGame() {
        Game game = new Game();
        game.start();
        return game;
    }
}
