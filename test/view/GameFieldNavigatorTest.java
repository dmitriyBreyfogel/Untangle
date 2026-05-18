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
    @DisplayName("Навигатор выбирает, перемещает и очищает узел")
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
    @DisplayName("Навигатор преобразует координаты в обе стороны")
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
    @DisplayName("Навигатор не принимает null параметры поля")
    void rejectsNullFieldParameters() {
        assertThrows(NullPointerException.class, () -> new GameFieldNavigator(null, new Game(), () -> new Dimension(100, 100)));
    }

    @Test
    @DisplayName("Навигатор не принимает null игру")
    void rejectsNullGame() {
        assertThrows(NullPointerException.class, () -> new GameFieldNavigator(new FieldParameters(12, 28), null, () -> new Dimension(100, 100)));
    }

    @Test
    @DisplayName("Навигатор не принимает null поставщик размера")
    void rejectsNullSizeSupplier() {
        assertThrows(NullPointerException.class, () -> new GameFieldNavigator(new FieldParameters(12, 28), new Game(), null));
    }

    @Test
    @DisplayName("Навигатор не принимает null точку при поиске узла")
    void rejectsNullPointInFindNode() {
        GameFieldNavigator navigator = navigator(new Game(), 400, 400);

        assertThrows(NullPointerException.class, () -> navigator.findNodeAtScreenPoint(null));
    }

    @Test
    @DisplayName("Навигатор не принимает null точку при переводе в экранные координаты")
    void rejectsNullPointInConvertToScreen() {
        GameFieldNavigator navigator = navigator(new Game(), 400, 400);

        assertThrows(NullPointerException.class, () -> navigator.convertToScreenCoordinates(null));
    }

    @Test
    @DisplayName("Навигатор не принимает null точку при переводе в координаты модели")
    void rejectsNullPointInConvertToModel() {
        GameFieldNavigator navigator = navigator(new Game(), 400, 400);

        assertThrows(NullPointerException.class, () -> navigator.convertToModelCoordinates(null));
    }

    @Test
    @DisplayName("Навигатор не находит узел, когда игра не запущена")
    void findsNoNodeWhenGameIsNotStarted() {
        GameFieldNavigator navigator = navigator(new Game(), 400, 400);

        assertNull(navigator.findNodeAtScreenPoint(new Point(100, 100)));
    }

    @Test
    @DisplayName("Навигатор выбирает null, когда точка вне всех узлов")
    void selectsNullWhenPointIsOutsideEveryNode() {
        Game game = startedGame();
        GameFieldNavigator navigator = navigator(game, 400, 400);

        navigator.selectNode(new Point(399, 399));

        assertNull(navigator.selectedNode());
    }

    @Test
    @DisplayName("Навигатор ничего не перемещает без выбранного узла")
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
    @DisplayName("Навигатор ничего не перемещает после завершения игры")
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
    @DisplayName("Навигатор ограничивает координаты модели границами поля")
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
    @DisplayName("Навигатор работает с нулевым размером панели")
    void worksWithZeroPanelSizeSupplier() {
        Game game = startedGame();
        GameFieldNavigator navigator = new GameFieldNavigator(new FieldParameters(12, 28), game, () -> new Dimension(0, 0));

        assertDoesNotThrow(() -> navigator.convertToScreenCoordinates(new Point2D.Double(50, 50)));
    }

    @Test
    @DisplayName("Навигатор не принимает null размер панели от поставщика")
    void rejectsNullPanelSizeFromSupplier() {
        GameFieldNavigator navigator = new GameFieldNavigator(new FieldParameters(12, 28), new Game(), () -> null);

        assertThrows(NullPointerException.class, () -> navigator.convertToScreenCoordinates(new Point2D.Double(50, 50)));
    }

    @Test
    @DisplayName("Навигатор возвращает тот же узел при клике точно по центру")
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
