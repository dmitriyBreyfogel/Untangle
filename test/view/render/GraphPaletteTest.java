package view.render;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GraphPaletteTest {
    @Test
    @DisplayName("Палитра графа хранит цвета рёбер и узлов")
    void storesGraphColors() {
        GraphPalette palette = new GraphPalette(
                new Color(1, 2, 3),
                new Color(4, 5, 6),
                new Color(7, 8, 9),
                new Color(10, 11, 12)
        );

        assertEquals(new Color(1, 2, 3), palette.normalEdgeColor());
        assertEquals(new Color(4, 5, 6), palette.intersectingEdgeColor());
        assertEquals(new Color(7, 8, 9), palette.nodeColor());
        assertEquals(new Color(10, 11, 12), palette.selectedNodeColor());
    }

    @Test
    @DisplayName("Палитра графа создаёт цвета по умолчанию")
    void createsDefaultPalette() {
        GraphPalette palette = GraphPalette.defaultPalette();

        assertEquals(new Color(106, 126, 152), palette.normalEdgeColor());
        assertEquals(new Color(226, 97, 76), palette.intersectingEdgeColor());
        assertEquals(new Color(74, 163, 180), palette.nodeColor());
        assertEquals(new Color(243, 188, 65), palette.selectedNodeColor());
    }

    @Test
    @DisplayName("Палитра графа не принимает пустые цвета")
    void rejectsNullColors() {
        assertThrows(NullPointerException.class, () -> new GraphPalette(null, Color.RED, Color.BLUE, Color.ORANGE));
        assertThrows(NullPointerException.class, () -> new GraphPalette(Color.BLACK, null, Color.BLUE, Color.ORANGE));
        assertThrows(NullPointerException.class, () -> new GraphPalette(Color.BLACK, Color.RED, null, Color.ORANGE));
        assertThrows(NullPointerException.class, () -> new GraphPalette(Color.BLACK, Color.RED, Color.BLUE, null));
    }
}
