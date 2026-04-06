package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SchemeValidationTest {
    @Test
    @DisplayName("Схема требует минимум три узла")
    void requiresAtLeastThreeNodes() {
        assertThrows(IllegalArgumentException.class, () -> Scheme.create(
                List.of(new Point2D.Double(0, 0), new Point2D.Double(1, 1)),
                Map.of(0, List.of(1))
        ));
    }

    @Test
    @DisplayName("Схема требует минимум три грани")
    void requiresAtLeastThreeEdges() {
        assertThrows(IllegalArgumentException.class, () -> Scheme.create(
                List.of(new Point2D.Double(0, 0), new Point2D.Double(1, 0), new Point2D.Double(0, 1)),
                Map.of(0, List.of(1), 1, List.of(2))
        ));
    }

    @Test
    @DisplayName("Схема отклоняет индекс узла вне диапазона (from)")
    void rejectsOutOfRangeFromIndex() {
        assertThrows(IllegalArgumentException.class, () -> Scheme.create(
                List.of(new Point2D.Double(0, 0), new Point2D.Double(1, 0), new Point2D.Double(0, 1)),
                Map.of(3, List.of(0, 1, 2))
        ));
    }

    @Test
    @DisplayName("Схема отклоняет индекс узла вне диапазона (to)")
    void rejectsOutOfRangeToIndex() {
        assertThrows(IllegalArgumentException.class, () -> Scheme.create(
                List.of(new Point2D.Double(0, 0), new Point2D.Double(1, 0), new Point2D.Double(0, 1)),
                Map.of(0, List.of(1, 99), 1, List.of(2))
        ));
    }

    @Test
    @DisplayName("Схема отклоняет связь узла с самим собой")
    void rejectsSelfConnection() {
        assertThrows(IllegalArgumentException.class, () -> Scheme.create(
                List.of(new Point2D.Double(0, 0), new Point2D.Double(1, 0), new Point2D.Double(0, 1)),
                Map.of(0, List.of(0, 1), 1, List.of(2))
        ));
    }

    @Test
    @DisplayName("Схема отклоняет изолированный узел")
    void rejectsIsolatedNode() {
        assertThrows(IllegalArgumentException.class, () -> Scheme.create(
                List.of(
                        new Point2D.Double(0, 0),
                        new Point2D.Double(1, 0),
                        new Point2D.Double(0, 1),
                        new Point2D.Double(10, 10)
                ),
                Map.of(
                        0, List.of(1, 2),
                        1, List.of(2)
                )
        ));
    }

    @Test
    @DisplayName("Дублирующиеся связи создают уникальные грани")
    void duplicateConnectionsProduceUniqueEdges() {
        Scheme scheme = Scheme.create(
                List.of(
                        new Point2D.Double(0, 0),
                        new Point2D.Double(1, 0),
                        new Point2D.Double(0, 1)
                ),
                Map.of(
                        0, List.of(1, 2),
                        1, List.of(0, 2),
                        2, List.of(0, 1)
                )
        );

        assertEquals(3, scheme.getEdges().size());
    }

    @Test
    @DisplayName("Схема отклоняет NaN в начальных координатах")
    void rejectsNaNInitialCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> Scheme.create(
                List.of(
                        new Point2D.Double(Double.NaN, 0),
                        new Point2D.Double(1, 0),
                        new Point2D.Double(0, 1)
                ),
                Map.of(0, List.of(1, 2), 1, List.of(2))
        ));
    }

    @Test
    @DisplayName("Схема не принимает null параметры")
    void rejectsNullArgs() {
        assertThrows(NullPointerException.class, () -> Scheme.create(null, Map.of()));
        assertThrows(NullPointerException.class, () -> Scheme.create(List.of(new Point2D.Double(0, 0), new Point2D.Double(1, 0), new Point2D.Double(0, 1)), null));
    }

    @Test
    @DisplayName("Схема не принимает null при привязке игры и поля")
    void bindRejectsNull() {
        Scheme scheme = Scheme.create(
                List.of(new Point2D.Double(0, 0), new Point2D.Double(1, 0), new Point2D.Double(0, 1)),
                Map.of(0, List.of(1, 2), 1, List.of(2))
        );
        assertThrows(NullPointerException.class, () -> scheme.bindGame(null));
        assertThrows(NullPointerException.class, () -> scheme.bindGameField(null));
    }

    @Test
    @DisplayName("Схема не принимает null в перемещении узла")
    void moveNodeRejectsNullArgs() {
        Scheme scheme = Scheme.create(
                List.of(new Point2D.Double(0, 0), new Point2D.Double(1, 0), new Point2D.Double(0, 1)),
                Map.of(0, List.of(1, 2), 1, List.of(2))
        );
        Node node = scheme.getNodes().get(0);
        assertThrows(NullPointerException.class, () -> scheme.moveNode(null, new Point2D.Double(0, 0)));
        assertThrows(NullPointerException.class, () -> scheme.moveNode(node, null));
    }

    @Test
    @DisplayName("Схема не принимает null в запросе граней узла")
    void getEdgesOfNodeRejectsNull() {
        Scheme scheme = Scheme.create(
                List.of(new Point2D.Double(0, 0), new Point2D.Double(1, 0), new Point2D.Double(0, 1)),
                Map.of(0, List.of(1, 2), 1, List.of(2))
        );
        assertThrows(NullPointerException.class, () -> scheme.getEdgesOfNode(null));
    }
}
