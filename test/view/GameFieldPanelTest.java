package view;

import model.Game;
import model.Node;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameFieldPanelTest {
    @Test
    @DisplayName("Dragging node through panel moves model node and clears selection")
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
    @DisplayName("Dragging node with multiple mouse dragged events counts as one move")
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
    @DisplayName("Winning drag through panel advances to next level")
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
    @DisplayName("Field panel rejects null game")
    void rejectsNullGame() {
        assertThrows(NullPointerException.class, () -> new GameFieldPanel(null));
    }

    @Test
    @DisplayName("Field panel configures visual properties")
    void configuresVisualProperties() {
        GameFieldPanel panel = panel(new Game());

        assertTrue(panel.isOpaque());
        assertEquals(new Color(243, 239, 233), panel.getBackground());
        assertEquals(new Dimension(700, 700), panel.getPreferredSize());
        assertEquals(new Dimension(320, 320), panel.getMinimumSize());
        assertEquals(Cursor.HAND_CURSOR, panel.getCursor().getType());
        assertEquals("Перетаскивайте узлы, чтобы убрать пересечения", panel.getToolTipText());
    }

    @Test
    @DisplayName("Field panel attaches mouse listeners")
    void attachesMouseListeners() {
        GameFieldPanel panel = panel(new Game());

        assertTrue(panel.getMouseListeners().length > 0);
        assertTrue(panel.getMouseMotionListeners().length > 0);
    }

    @Test
    @DisplayName("Pressing outside node keeps selection empty")
    void pressingOutsideNodeKeepsSelectionEmpty() {
        GameFieldPanel panel = panel(startedGame());

        dispatch(panel, MouseEvent.MOUSE_PRESSED, new Point(399, 399));

        assertNull(SwingTestSupport.readField(panel, "selectedNode", Node.class));
    }

    @Test
    @DisplayName("Dragging without selected node does not change model")
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
    @DisplayName("Releasing mouse clears selected node")
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
    @DisplayName("Dragging node updates edge color before mouse release")
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

        assertEquals(new Color(66, 63, 60).getRGB(), image.getRGB(edgePoint.x, edgePoint.y));
        assertEquals(0, game.moveCounter());
    }

    @Test
    @DisplayName("Click without drag does not count as move")
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
    @DisplayName("Dragging beyond field clamps node to boundary")
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
    @DisplayName("Field panel paints field into graphics")
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
    @DisplayName("Field panel refresh does not throw")
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
