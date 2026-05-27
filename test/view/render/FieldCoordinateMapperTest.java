package view.render;

import model.core.Game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FieldCoordinateMapperTest {
    @Test
    @DisplayName("Маппер переводит координаты модели в экранные координаты")
    void convertsModelCoordinatesToScreenCoordinates() {
        FieldCoordinateMapper mapper = new FieldCoordinateMapper(new FieldParameters(12, 10), new Dimension(420, 220), 200, 100);

        Point point = mapper.toScreenCoordinates(new Point2D.Double(100, 50));

        assertEquals(new Point(210, 110), point);
    }

    @Test
    @DisplayName("Маппер переводит экранные координаты в координаты модели")
    void convertsScreenCoordinatesToModelCoordinates() {
        FieldCoordinateMapper mapper = new FieldCoordinateMapper(new FieldParameters(12, 10), new Dimension(420, 220), 200, 100);

        Point2D point = mapper.toModelCoordinates(new Point(210, 110));

        assertEquals(100, point.getX(), 0.001);
        assertEquals(50, point.getY(), 0.001);
    }

    @Test
    @DisplayName("Маппер ограничивает экранную точку границами поля")
    void clampsScreenCoordinatesToFieldBounds() {
        FieldCoordinateMapper mapper = new FieldCoordinateMapper(new FieldParameters(12, 10), new Dimension(420, 220), 200, 100);

        Point2D topLeft = mapper.toModelCoordinates(new Point(-100, -100));
        Point2D bottomRight = mapper.toModelCoordinates(new Point(1000, 1000));

        assertEquals(0, topLeft.getX(), 0.001);
        assertEquals(0, topLeft.getY(), 0.001);
        assertEquals(200, bottomRight.getX(), 0.001);
        assertEquals(100, bottomRight.getY(), 0.001);
    }

    @Test
    @DisplayName("Маппер создаётся по границам панели")
    void createsFromBounds() {
        Game game = new Game();
        game.start();
        FieldCoordinateMapper mapper = FieldCoordinateMapper.fromBounds(
                new FieldParameters(12, 10),
                new Rectangle(0, 0, 420, 220),
                game
        );

        Point point = mapper.toScreenCoordinates(new Point2D.Double(50, 50));

        assertEquals(new Point(210, 110), point);
    }

    @Test
    @DisplayName("Маппер использует безопасные границы при отсутствии прямоугольника")
    void createsFromNullBounds() {
        Game game = new Game();

        FieldCoordinateMapper mapper = FieldCoordinateMapper.fromBounds(new FieldParameters(12, 10), null, game);

        assertEquals(new Point(10, 10), mapper.toScreenCoordinates(new Point2D.Double(0, 0)));
    }

    @Test
    @DisplayName("Маппер не принимает пустые параметры")
    void rejectsNullArgs() {
        FieldParameters parameters = new FieldParameters(12, 10);

        assertThrows(NullPointerException.class, () -> new FieldCoordinateMapper(null, new Dimension(100, 100), 100, 100));
        assertThrows(NullPointerException.class, () -> new FieldCoordinateMapper(parameters, null, 100, 100));
        assertThrows(NullPointerException.class, () -> FieldCoordinateMapper.fromPanel(parameters, new Dimension(100, 100), null));
        FieldCoordinateMapper mapper = new FieldCoordinateMapper(parameters, new Dimension(100, 100), 100, 100);
        assertThrows(NullPointerException.class, () -> mapper.toScreenCoordinates(null));
        assertThrows(NullPointerException.class, () -> mapper.toModelCoordinates(null));
    }

    @Test
    @DisplayName("Маппер отклоняет некорректные размеры поля")
    void rejectsInvalidFieldDimensions() {
        FieldParameters parameters = new FieldParameters(12, 10);
        Dimension panelSize = new Dimension(100, 100);

        assertThrows(IllegalArgumentException.class, () -> new FieldCoordinateMapper(parameters, panelSize, 0, 100));
        assertThrows(IllegalArgumentException.class, () -> new FieldCoordinateMapper(parameters, panelSize, 100, 0));
        assertThrows(IllegalArgumentException.class, () -> new FieldCoordinateMapper(parameters, panelSize, Double.NaN, 100));
        assertThrows(IllegalArgumentException.class, () -> new FieldCoordinateMapper(parameters, panelSize, 100, Double.POSITIVE_INFINITY));
    }
}
