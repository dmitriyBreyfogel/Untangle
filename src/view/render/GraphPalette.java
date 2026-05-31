package view.render;

import java.awt.Color;
import java.util.Objects;

public record GraphPalette(
        Color normalEdgeColor,
        Color intersectingEdgeColor,
        Color nodeColor,
        Color selectedNodeColor
) {
    public GraphPalette {
        Objects.requireNonNull(normalEdgeColor, "normalEdgeColor");
        Objects.requireNonNull(intersectingEdgeColor, "intersectingEdgeColor");
        Objects.requireNonNull(nodeColor, "nodeColor");
        Objects.requireNonNull(selectedNodeColor, "selectedNodeColor");
    }

    public static GraphPalette defaultPalette() {
        return new GraphPalette(
                new Color(106, 126, 152),
                new Color(226, 97, 76),
                new Color(74, 163, 180),
                new Color(243, 188, 65)
        );
    }
}
