package view.render;

import view.SwingTestSupport;

import model.core.Game;
import model.movement.FreeMovementStrategy;
import model.core.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import view.render.node.DefaultNodeRenderStrategy;
import view.render.node.NodeRenderStrategyRegistry;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NodeRendererTest {
    @Test
    @DisplayName("Рендерер узлов не принимает пустые параметры поля")
    void rejectsNullFieldParameters() {
        assertThrows(NullPointerException.class, () -> new NodeRenderer(null));
    }

    @Test
    @DisplayName("Рендерер узлов не принимает пустой графический контекст")
    void rejectsNullGraphics() {
        assertThrows(NullPointerException.class, () -> new NodeRenderer(new FieldParameters(12, 28)).drawNodes(null, new Game()));
    }

    @Test
    @DisplayName("Рендерер узлов не принимает пустую игру")
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
    @DisplayName("Рендерер узлов рисует обычный узел цветом узла")
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
            renderer.drawNodes(graphics, game, null, null, nodeColor, selectedColor);
        } finally {
            graphics.dispose();
        }

        Point point = SwingTestSupport.toScreenPoint(parameters, game, node.getX(), node.getY(), image.getWidth(), image.getHeight());

        assertEquals(nodeColor.getRGB(), image.getRGB(point.x, point.y));
    }

    @Test
    @DisplayName("Рендерер узлов рисует выбранный узел выбранным цветом")
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
            renderer.drawNodes(graphics, game, node, null, nodeColor, selectedColor);
        } finally {
            graphics.dispose();
        }

        Point point = SwingTestSupport.toScreenPoint(parameters, game, node.getX(), node.getY(), image.getWidth(), image.getHeight());

        assertEquals(selectedColor.getRGB(), image.getRGB(point.x, point.y));
    }

    @Test
    @DisplayName("Рендерер узлов рисует неподвижный узел с маркером замка")
    void drawsFixedNodeWithLockMarker() {
        Game game = startedGame();
        FieldParameters parameters = new FieldParameters(12, 28);
        Node node = game.currentLevel().scheme().getNodes().get(2);
        NodeRenderer renderer = new NodeRenderer(parameters);
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        Color nodeColor = new Color(40, 120, 190);
        try {
            renderer.drawNodes(graphics, game, null, null, nodeColor, Color.ORANGE);
        } finally {
            graphics.dispose();
        }

        Point point = SwingTestSupport.toScreenPoint(parameters, game, node.getX(), node.getY(), image.getWidth(), image.getHeight());

        assertNotEquals(nodeColor.getRGB(), image.getRGB(point.x, point.y));
    }

    @Test
    @DisplayName("Рендерер узлов рисует горизонтальный узел с маркером стрелки")
    void drawsHorizontalNodeWithArrowMarker() {
        Game game = startedGame();
        FieldParameters parameters = new FieldParameters(12, 28);
        Node node = game.currentLevel().scheme().getNodes().get(3);
        NodeRenderer renderer = new NodeRenderer(parameters);
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        Color nodeColor = new Color(40, 120, 190);
        try {
            renderer.drawNodes(graphics, game, null, null, nodeColor, Color.ORANGE);
        } finally {
            graphics.dispose();
        }

        Point point = SwingTestSupport.toScreenPoint(parameters, game, node.getX(), node.getY(), image.getWidth(), image.getHeight());

        assertNotEquals(nodeColor.getRGB(), image.getRGB(point.x, point.y));
    }

    @Test
    @DisplayName("Рендерер узлов может использовать кастомный реестр стратегий рендера")
    void usesCustomRenderStrategyRegistry() {
        Game game = startedGame();
        FieldParameters parameters = new FieldParameters(12, 28);
        Node node = game.currentLevel().scheme().getNodes().getFirst();
        NodeRenderStrategyRegistry registry = new NodeRenderStrategyRegistry(new DefaultNodeRenderStrategy());
        registry.register(FreeMovementStrategy.class, (graphics, center, radius, color, selected) -> {
            graphics.setColor(Color.MAGENTA);
            graphics.fillRect(center.x, center.y, 1, 1);
        });
        NodeRenderer renderer = new NodeRenderer(parameters, registry);
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            renderer.drawNodes(graphics, game, null, null, Color.BLUE, Color.ORANGE);
        } finally {
            graphics.dispose();
        }

        Point point = SwingTestSupport.toScreenPoint(parameters, game, node.getX(), node.getY(), image.getWidth(), image.getHeight());

        assertEquals(Color.MAGENTA.getRGB(), image.getRGB(point.x, point.y));
    }

    @Test
    @DisplayName("Рендерер узлов не меняет изображение, когда игра не запущена")
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
    @DisplayName("Рендерер узлов может рисовать без явной области отсечения")
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
    @DisplayName("Пакетный метод рендерера узлов не принимает пустые цвета")
    void packageMethodRejectsNullColors() {
        Game game = startedGame();
        NodeRenderer renderer = new NodeRenderer(new FieldParameters(12, 28));
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            assertThrows(NullPointerException.class, () -> renderer.drawNodes(graphics, game, null, null, null, Color.ORANGE));
            assertThrows(NullPointerException.class, () -> renderer.drawNodes(graphics, game, null, null, Color.BLUE, null));
        } finally {
            graphics.dispose();
        }
    }

    @Test
    @DisplayName("Рендерер узлов не принимает пустой реестр стратегий рендера")
    void rejectsNullRenderStrategyRegistry() {
        assertThrows(NullPointerException.class, () -> new NodeRenderer(new FieldParameters(12, 28), null));
    }

    private static Game startedGame() {
        Game game = new Game();
        game.start();
        return game;
    }
}
