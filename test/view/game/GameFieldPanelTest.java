package view.game;

import view.SwingTestSupport;

import model.core.Game;
import model.core.Node;
import view.render.FieldParameters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameFieldPanelTest {
    @Test
    @DisplayName("Перетаскивание узла через панель перемещает узел модели и очищает выбор")
    void draggingNodeMovesModelNodeAndClearsSelection() {
        Game game = startedGame();
        GameFieldPanel panel = panel(game);

        GameFieldNavigator navigator = SwingTestSupport.readField(panel, "gameFieldNavigator", GameFieldNavigator.class);
        Node node = game.currentLevel().scheme().getNodes().getFirst();
        Point pressPoint = navigator.convertToScreenCoordinates(new Point2D.Double(node.getX(), node.getY()));
        Point dragPoint = navigator.convertToScreenCoordinates(new Point2D.Double(20, 20));

        dispatch(panel, MouseEvent.MOUSE_PRESSED, pressPoint);
        dispatch(panel, MouseEvent.MOUSE_DRAGGED, dragPoint);
        dispatch(panel, MouseEvent.MOUSE_RELEASED, dragPoint);

        assertEquals(20, node.getX(), 1.0);
        assertEquals(20, node.getY(), 1.0);
        assertEquals(1, game.moveCounter());
        assertNull(SwingTestSupport.readField(panel, "selectedNode", Node.class));
    }

    @Test
    @DisplayName("Перетаскивание с несколькими событиями движения мыши считается одним ходом")
    void draggingNodeWithMultipleMouseDraggedEventsCountsAsOneMove() {
        Game game = startedGame();
        GameFieldPanel panel = panel(game);
        GameFieldNavigator navigator = SwingTestSupport.readField(panel, "gameFieldNavigator", GameFieldNavigator.class);
        Node node = game.currentLevel().scheme().getNodes().getFirst();
        Point pressPoint = navigator.convertToScreenCoordinates(new Point2D.Double(node.getX(), node.getY()));
        Point middlePoint = navigator.convertToScreenCoordinates(new Point2D.Double(15, 15));
        Point finalPoint = navigator.convertToScreenCoordinates(new Point2D.Double(20, 20));

        dispatch(panel, MouseEvent.MOUSE_PRESSED, pressPoint);
        dispatch(panel, MouseEvent.MOUSE_DRAGGED, middlePoint);
        dispatch(panel, MouseEvent.MOUSE_DRAGGED, finalPoint);

        assertEquals(0, game.moveCounter());
        assertEquals(10, node.getX(), 0.001);
        assertEquals(10, node.getY(), 0.001);

        dispatch(panel, MouseEvent.MOUSE_RELEASED, finalPoint);

        assertEquals(1, game.moveCounter());
        assertEquals(20, node.getX(), 1.0);
        assertEquals(20, node.getY(), 1.0);
    }

    @Test
    @DisplayName("Победное перетаскивание через панель переводит на следующий уровень")
    void winningDragAdvancesToNextLevel() {
        Game game = startedGame();
        GameFieldPanel panel = panel(game);

        GameFieldNavigator navigator = SwingTestSupport.readField(panel, "gameFieldNavigator", GameFieldNavigator.class);
        Node node = game.currentLevel().scheme().getNodes().get(1);
        Point pressPoint = navigator.convertToScreenCoordinates(new Point2D.Double(node.getX(), node.getY()));
        Point dragPoint = navigator.convertToScreenCoordinates(new Point2D.Double(90, 5));

        dispatch(panel, MouseEvent.MOUSE_PRESSED, pressPoint);
        dispatch(panel, MouseEvent.MOUSE_DRAGGED, dragPoint);
        dispatch(panel, MouseEvent.MOUSE_RELEASED, dragPoint);

        assertEquals(2, game.currentLevelNumber());
        assertEquals(0, game.moveCounter());
    }

    @Test
    @DisplayName("Панель поля передаёт обработчику прогресс уровней до хода")
    void levelProgressHandlerReceivesStateBeforeMove() {
        Game game = startedGame();
        AtomicReference<LevelProgressBeforeMove> handledLevelProgress = new AtomicReference<>();
        GameFieldPanel panel = SwingTestSupport.callOnEdt(() -> new GameFieldPanel(game, handledLevelProgress::set));
        SwingTestSupport.runOnEdt(() -> panel.setSize(400, 400));
        GameFieldNavigator navigator = SwingTestSupport.readField(panel, "gameFieldNavigator", GameFieldNavigator.class);
        Node node = game.currentLevel().scheme().getNodes().getFirst();
        Point pressPoint = navigator.convertToScreenCoordinates(new Point2D.Double(node.getX(), node.getY()));
        Point dragPoint = navigator.convertToScreenCoordinates(new Point2D.Double(20, 20));

        dispatch(panel, MouseEvent.MOUSE_PRESSED, pressPoint);
        dispatch(panel, MouseEvent.MOUSE_DRAGGED, dragPoint);
        dispatch(panel, MouseEvent.MOUSE_RELEASED, dragPoint);

        assertEquals(new LevelProgressBeforeMove(1, 0), handledLevelProgress.get());
    }

    @Test
    @DisplayName("Панель поля не принимает пустую игру")
    void rejectsNullGame() {
        assertThrows(NullPointerException.class, () -> new GameFieldPanel(null));
    }

    @Test
    @DisplayName("Панель поля не принимает пустой обработчик прогресса уровней")
    void rejectsNullLevelProgressHandler() {
        assertThrows(NullPointerException.class, () -> new GameFieldPanel(new Game(), null));
    }

    @Test
    @DisplayName("Панель поля настраивает визуальные свойства")
    void configuresVisualProperties() {
        GameFieldPanel panel = panel(new Game());

        assertTrue(panel.isOpaque());
        assertEquals(new Color(14, 22, 32), panel.getBackground());
        assertEquals(new Dimension(620, 620), panel.getPreferredSize());
        assertEquals(new Dimension(320, 320), panel.getMinimumSize());
        assertEquals(Cursor.HAND_CURSOR, panel.getCursor().getType());
        assertEquals("Перетаскивайте узлы, чтобы распутать схему", panel.getToolTipText());
    }

    @Test
    @DisplayName("Панель поля подключает слушатели мыши")
    void attachesMouseListeners() {
        GameFieldPanel panel = panel(new Game());

        assertTrue(panel.getMouseListeners().length > 0);
        assertTrue(panel.getMouseMotionListeners().length > 0);
    }

    @Test
    @DisplayName("Нажатие вне узла оставляет выбор пустым")
    void pressingOutsideNodeKeepsSelectionEmpty() {
        GameFieldPanel panel = panel(startedGame());

        dispatch(panel, MouseEvent.MOUSE_PRESSED, new Point(399, 399));

        assertNull(SwingTestSupport.readField(panel, "selectedNode", Node.class));
    }

    @Test
    @DisplayName("Перетаскивание без выбранного узла не меняет модель")
    void draggingWithoutSelectedNodeDoesNotChangeModel() {
        Game game = startedGame();
        GameFieldPanel panel = panel(game);
        Node node = game.currentLevel().scheme().getNodes().getFirst();

        dispatch(panel, MouseEvent.MOUSE_DRAGGED, new Point(250, 250));

        assertEquals(10, node.getX(), 0.001);
        assertEquals(10, node.getY(), 0.001);
        assertEquals(0, game.moveCounter());
    }

    @Test
    @DisplayName("Отпускание мыши очищает выбранный узел")
    void releasingMouseClearsSelectedNode() {
        Game game = startedGame();
        GameFieldPanel panel = panel(game);
        GameFieldNavigator navigator = SwingTestSupport.readField(panel, "gameFieldNavigator", GameFieldNavigator.class);
        Node node = game.currentLevel().scheme().getNodes().getFirst();
        Point pressPoint = navigator.convertToScreenCoordinates(new Point2D.Double(node.getX(), node.getY()));

        dispatch(panel, MouseEvent.MOUSE_PRESSED, pressPoint);
        dispatch(panel, MouseEvent.MOUSE_RELEASED, pressPoint);

        assertNull(SwingTestSupport.readField(panel, "selectedNode", Node.class));
    }

    @Test
    @DisplayName("Перетаскивание узла обновляет цвет грани до отпускания мыши")
    void draggingNodeUpdatesEdgeColorBeforeMouseRelease() {
        Game game = startedGame();
        GameFieldPanel panel = panel(game);
        GameFieldNavigator navigator = SwingTestSupport.readField(panel, "gameFieldNavigator", GameFieldNavigator.class);
        FieldParameters parameters = SwingTestSupport.readField(panel, "fieldParameters", FieldParameters.class);
        Node node = game.currentLevel().scheme().getNodes().get(1);
        Point pressPoint = navigator.convertToScreenCoordinates(new Point2D.Double(node.getX(), node.getY()));
        Point dragPoint = navigator.convertToScreenCoordinates(new Point2D.Double(90, 5));

        dispatch(panel, MouseEvent.MOUSE_PRESSED, pressPoint);
        dispatch(panel, MouseEvent.MOUSE_DRAGGED, dragPoint);

        BufferedImage image = SwingTestSupport.createCanvas(400, 400);
        SwingTestSupport.runOnEdt(() -> {
            Graphics2D graphics = SwingTestSupport.createGraphics(image);
            try {
                panel.paintComponent(graphics);
            } finally {
                graphics.dispose();
            }
        });

        Point edgePoint = SwingTestSupport.toScreenPoint(parameters, game, 50, 7.5, image.getWidth(), image.getHeight());

        assertEquals(new Color(106, 126, 152).getRGB(), image.getRGB(edgePoint.x, edgePoint.y));
        assertEquals(0, game.moveCounter());
    }

    @Test
    @DisplayName("Клик без перетаскивания не считается ходом")
    void clickWithoutDragDoesNotCountAsMove() {
        Game game = startedGame();
        GameFieldPanel panel = panel(game);
        GameFieldNavigator navigator = SwingTestSupport.readField(panel, "gameFieldNavigator", GameFieldNavigator.class);
        Node node = game.currentLevel().scheme().getNodes().getFirst();
        Point pressPoint = navigator.convertToScreenCoordinates(new Point2D.Double(node.getX(), node.getY()));

        dispatch(panel, MouseEvent.MOUSE_PRESSED, pressPoint);
        dispatch(panel, MouseEvent.MOUSE_RELEASED, pressPoint);

        assertEquals(0, game.moveCounter());
        assertEquals(10, node.getX(), 0.001);
        assertEquals(10, node.getY(), 0.001);
    }

    @Test
    @DisplayName("Перетаскивание за пределы поля ограничивает узел границей")
    void draggingBeyondFieldClampsNodeToBoundary() {
        Game game = startedGame();
        GameFieldPanel panel = panel(game);
        GameFieldNavigator navigator = SwingTestSupport.readField(panel, "gameFieldNavigator", GameFieldNavigator.class);
        Node node = game.currentLevel().scheme().getNodes().getFirst();
        Point pressPoint = navigator.convertToScreenCoordinates(new Point2D.Double(node.getX(), node.getY()));

        dispatch(panel, MouseEvent.MOUSE_PRESSED, pressPoint);
        dispatch(panel, MouseEvent.MOUSE_DRAGGED, new Point(10000, 10000));
        assertEquals(10, node.getX(), 0.001);
        assertEquals(10, node.getY(), 0.001);
        dispatch(panel, MouseEvent.MOUSE_RELEASED, new Point(10000, 10000));

        assertEquals(100, node.getX(), 0.001);
        assertEquals(100, node.getY(), 0.001);
    }

    @Test
    @DisplayName("Панель поля рисует поле в графический контекст")
    void paintsFieldIntoGraphics() {
        GameFieldPanel panel = panel(startedGame());
        BufferedImage image = SwingTestSupport.createCanvas(320, 320);

        SwingTestSupport.runOnEdt(() -> {
            Graphics2D graphics = SwingTestSupport.createGraphics(image);
            try {
                panel.setSize(320, 320);
                panel.paintComponent(graphics);
            } finally {
                graphics.dispose();
            }
        });

        assertNotTransparent(image.getRGB(160, 160));
    }

    @Test
    @DisplayName("Панель поля не падает при обновлении")
    void refreshDoesNotThrow() {
        GameFieldPanel panel = panel(new Game());

        assertDoesNotThrow(() -> SwingTestSupport.runOnEdt(panel::refreshField));
    }

    private static Game startedGame() {
        Game game = new Game();
        game.start();
        return game;
    }

    private static GameFieldPanel panel(Game game) {
        GameFieldPanel panel = SwingTestSupport.callOnEdt(() -> new GameFieldPanel(game));
        SwingTestSupport.runOnEdt(() -> panel.setSize(400, 400));
        return panel;
    }

    private static void dispatch(GameFieldPanel panel, int eventId, Point point) {
        SwingTestSupport.runOnEdt(() -> panel.dispatchEvent(new MouseEvent(
                panel,
                eventId,
                System.currentTimeMillis(),
                0,
                point.x,
                point.y,
                1,
                false,
                MouseEvent.BUTTON1
        )));
    }

    private static void assertNotTransparent(int rgb) {
        assertFalse(((rgb >>> 24) & 0xFF) == 0);
    }
}
