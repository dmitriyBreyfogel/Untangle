package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchemeTest {
    @Test
    @DisplayName("Схема находит пересекающиеся грани")
    void findsIntersectingEdges() {
        Scheme scheme = Scheme.create(
                List.of(
                        new Point2D.Double(10, 10),
                        new Point2D.Double(90, 90),
                        new Point2D.Double(10, 90),
                        new Point2D.Double(90, 10)
                ),
                Map.of(
                        0, List.of(1, 2),
                        2, List.of(3),
                        1, List.of(3)
                )
        );
        new GameField(100, 100, scheme);

        assertTrue(scheme.hasIntersections());
        assertEquals(2, scheme.getIntersectingEdges().size());
    }

    @Test
    @DisplayName("Перемещение узла обновляет пересечения")
    void movingNodeUpdatesIntersections() {
        Scheme scheme = Scheme.create(
                List.of(
                        new Point2D.Double(10, 10),
                        new Point2D.Double(90, 90),
                        new Point2D.Double(10, 90),
                        new Point2D.Double(90, 10)
                ),
                Map.of(
                        0, List.of(1, 2),
                        2, List.of(3),
                        1, List.of(3)
                )
        );
        new GameField(100, 100, scheme);

        Node node1 = scheme.getNodes().get(1);
        scheme.moveNode(node1, new Point2D.Double(90, 5));

        assertFalse(scheme.hasIntersections());
        assertEquals(0, scheme.getIntersectingEdges().size());
    }

    @Test
    @DisplayName("Сброс схемы возвращает начальные координаты и пересечения")
    void resetRestoresInitialState() {
        Scheme scheme = Scheme.create(
                List.of(
                        new Point2D.Double(10, 10),
                        new Point2D.Double(90, 90),
                        new Point2D.Double(10, 90),
                        new Point2D.Double(90, 10)
                ),
                Map.of(
                        0, List.of(1, 2),
                        2, List.of(3),
                        1, List.of(3)
                )
        );
        new GameField(100, 100, scheme);

        scheme.moveNode(scheme.getNodes().get(1), new Point2D.Double(90, 5));
        assertFalse(scheme.hasIntersections());

        scheme.reset();
        assertTrue(scheme.hasIntersections());
        assertEquals(90, scheme.getNodes().get(1).getX());
        assertEquals(90, scheme.getNodes().get(1).getY());
    }

    @Test
    @DisplayName("Схема возвращает инцидентные грани узла")
    void edgesOfNodeAreReturned() {
        Scheme scheme = Scheme.create(
                List.of(
                        new Point2D.Double(10, 10),
                        new Point2D.Double(90, 90),
                        new Point2D.Double(10, 90),
                        new Point2D.Double(90, 10)
                ),
                Map.of(
                        0, List.of(1, 2),
                        2, List.of(3),
                        1, List.of(3)
                )
        );
        new GameField(100, 100, scheme);

        assertEquals(2, scheme.getEdgesOfNode(scheme.getNodes().get(0)).size());
        assertEquals(2, scheme.getEdgesOfNode(scheme.getNodes().get(1)).size());
        assertEquals(2, scheme.getEdgesOfNode(scheme.getNodes().get(2)).size());
        assertEquals(2, scheme.getEdgesOfNode(scheme.getNodes().get(3)).size());
    }

    @Test
    @DisplayName("Список узлов схемы неизменяемый")
    void nodesListIsUnmodifiable() {
        Scheme scheme = Scheme.create(
                List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(0, 10)),
                Map.of(0, List.of(1, 2), 1, List.of(2))
        );

        assertThrows(UnsupportedOperationException.class, () -> scheme.getNodes().add(new Node(new Point2D.Double(1, 1))));
    }

    @Test
    @DisplayName("Список пересекающихся граней неизменяемый")
    void intersectingEdgesListIsUnmodifiable() {
        Scheme scheme = Scheme.create(
                List.of(
                        new Point2D.Double(10, 10),
                        new Point2D.Double(90, 90),
                        new Point2D.Double(10, 90),
                        new Point2D.Double(90, 10)
                ),
                Map.of(
                        0, List.of(1, 2),
                        2, List.of(3),
                        1, List.of(3)
                )
        );
        new GameField(100, 100, scheme);

        assertThrows(UnsupportedOperationException.class, () -> scheme.getIntersectingEdges().add(scheme.getEdges().get(0)));
    }
}
