package view;

import model.Game;
import model.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameFieldNavigatorTest {
    @Test
    @DisplayName("Navigator selects moves and clears node")
    void selectsMovesAndClearsNode() {
        Game game = startedGame();
        GameFieldNavigator navigator = navigator(game, 400, 400);
        Node node = game.currentLevel().scheme().getNodes().getFirst();

        Point nodePoint = navigator.convertToScreenCoordinates(new Point2D.Double(node.getX(), node.getY()));
        navigator.selectNode(nodePoint);
        assertSame(node, navigator.selectedNode());

        Point targetPoint = navigator.convertToScreenCoordinates(new Point2D.Double(20, 20));
        navigator.moveSelectedNode(targetPoint);

        assertEquals(20, node.getX(), 1.0);
        assertEquals(20, node.getY(), 1.0);
        assertEquals(1, game.moveCounter());

        navigator.clearSelectedNode();
        assertNull(navigator.selectedNode());
    }

    @Test
    @DisplayName("Navigator converts coordinates both ways")
    void convertsCoordinatesBothWays() {
        Game game = startedGame();
        GameFieldNavigator navigator = navigator(game, 420, 360);

        Point2D modelPoint = new Point2D.Double(25, 75);
        Point screenPoint = navigator.convertToScreenCoordinates(modelPoint);
        Point2D convertedBack = navigator.convertToModelCoordinates(screenPoint);

        assertEquals(modelPoint.getX(), convertedBack.getX(), 0.5);
        assertEquals(modelPoint.getY(), convertedBack.getY(), 0.5);
    }

    @Test
    @DisplayName("Navigator rejects null field parameters")
    void rejectsNullFieldParameters() {
        assertThrows(NullPointerException.class, () -> new GameFieldNavigator(null, new Game(), () -> new Dimension(100, 100)));
    }

    @Test
    @DisplayName("Navigator rejects null game")
    void rejectsNullGame() {
        assertThrows(NullPointerException.class, () -> new GameFieldNavigator(new FieldParameters(12, 28), null, () -> new Dimension(100, 100)));
    }

    @Test
    @DisplayName("Navigator rejects null size supplier")
    void rejectsNullSizeSupplier() {
        assertThrows(NullPointerException.class, () -> new GameFieldNavigator(new FieldParameters(12, 28), new Game(), null));
    }

    @Test
    @DisplayName("Navigator rejects null point in find node")
    void rejectsNullPointInFindNode() {
        GameFieldNavigator navigator = navigator(new Game(), 400, 400);

        assertThrows(NullPointerException.class, () -> navigator.findNodeAtScreenPoint(null));
    }

    @Test
    @DisplayName("Navigator rejects null point in convert to screen")
    void rejectsNullPointInConvertToScreen() {
        GameFieldNavigator navigator = navigator(new Game(), 400, 400);

        assertThrows(NullPointerException.class, () -> navigator.convertToScreenCoordinates(null));
    }

    @Test
    @DisplayName("Navigator rejects null point in convert to model")
    void rejectsNullPointInConvertToModel() {
        GameFieldNavigator navigator = navigator(new Game(), 400, 400);

        assertThrows(NullPointerException.class, () -> navigator.convertToModelCoordinates(null));
    }

    @Test
    @DisplayName("Navigator finds no node when game is not started")
    void findsNoNodeWhenGameIsNotStarted() {
        GameFieldNavigator navigator = navigator(new Game(), 400, 400);

        assertNull(navigator.findNodeAtScreenPoint(new Point(100, 100)));
    }

    @Test
    @DisplayName("Navigator selects null when point is outside every node")
    void selectsNullWhenPointIsOutsideEveryNode() {
        Game game = startedGame();
        GameFieldNavigator navigator = navigator(game, 400, 400);

        navigator.selectNode(new Point(399, 399));

        assertNull(navigator.selectedNode());
    }

    @Test
    @DisplayName("Navigator move without selected node does nothing")
    void moveWithoutSelectedNodeDoesNothing() {
        Game game = startedGame();
        Node node = game.currentLevel().scheme().getNodes().getFirst();
        GameFieldNavigator navigator = navigator(game, 400, 400);

        navigator.moveSelectedNode(new Point(200, 200));

        assertEquals(10, node.getX(), 0.001);
        assertEquals(10, node.getY(), 0.001);
        assertEquals(0, game.moveCounter());
    }

    @Test
    @DisplayName("Navigator move after finish does nothing")
    void moveAfterFinishDoesNothing() {
        Game game = startedGame();
        Node node = game.currentLevel().scheme().getNodes().getFirst();
        GameFieldNavigator navigator = navigator(game, 400, 400);
        navigator.selectNode(navigator.convertToScreenCoordinates(new Point2D.Double(node.getX(), node.getY())));

        game.finish();
        navigator.moveSelectedNode(new Point(200, 200));

        assertEquals(10, node.getX(), 0.001);
        assertEquals(10, node.getY(), 0.001);
    }

    @Test
    @DisplayName("Navigator clamps model coordinates to field bounds")
    void clampsModelCoordinatesToFieldBounds() {
        GameFieldNavigator navigator = navigator(startedGame(), 400, 400);

        Point2D topLeft = navigator.convertToModelCoordinates(new Point(-100, -100));
        Point2D bottomRight = navigator.convertToModelCoordinates(new Point(1000, 1000));

        assertEquals(0, topLeft.getX(), 0.001);
        assertEquals(0, topLeft.getY(), 0.001);
        assertEquals(100, bottomRight.getX(), 0.001);
        assertEquals(100, bottomRight.getY(), 0.001);
    }

    @Test
    @DisplayName("Navigator works with zero panel size supplier")
    void worksWithZeroPanelSizeSupplier() {
        Game game = startedGame();
        GameFieldNavigator navigator = new GameFieldNavigator(new FieldParameters(12, 28), game, () -> new Dimension(0, 0));

        assertDoesNotThrow(() -> navigator.convertToScreenCoordinates(new Point2D.Double(50, 50)));
    }

    @Test
    @DisplayName("Navigator rejects null panel size from supplier")
    void rejectsNullPanelSizeFromSupplier() {
        GameFieldNavigator navigator = new GameFieldNavigator(new FieldParameters(12, 28), new Game(), () -> null);

        assertThrows(NullPointerException.class, () -> navigator.convertToScreenCoordinates(new Point2D.Double(50, 50)));
    }

    @Test
    @DisplayName("Navigator returns same node when clicking exactly on node center")
    void returnsSameNodeWhenClickingOnNodeCenter() {
        Game game = startedGame();
        GameFieldNavigator navigator = navigator(game, 400, 400);
        Node node = game.currentLevel().scheme().getNodes().get(1);

        Point screenPoint = navigator.convertToScreenCoordinates(new Point2D.Double(node.getX(), node.getY()));

        assertSame(node, navigator.findNodeAtScreenPoint(screenPoint));
    }

    private static Game startedGame() {
        Game game = new Game();
        game.start();
        return game;
    }

    private static GameFieldNavigator navigator(Game game, int width, int height) {
        return new GameFieldNavigator(new FieldParameters(12, 28), game, () -> new Dimension(width, height));
    }
}
