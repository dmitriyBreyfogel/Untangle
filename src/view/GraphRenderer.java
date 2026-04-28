package view;

import model.Game;
import model.Node;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

public final class GraphRenderer {
    private final FieldParameters fieldParameters;
    private final EdgeRenderer edgeRenderer;
    private final NodeRenderer nodeRenderer;

    public GraphRenderer(FieldParameters fieldParameters) {
        this.fieldParameters = Objects.requireNonNull(fieldParameters, "fieldParameters");
        this.edgeRenderer = new EdgeRenderer(fieldParameters);
        this.nodeRenderer = new NodeRenderer(fieldParameters);
    }

    public void drawGraph(Graphics2D graphics, Game gameModel) {
        drawGraph(
                graphics,
                gameModel,
                null,
                null,
                new Color(90, 90, 90),
                new Color(210, 70, 70),
                new Color(52, 127, 196),
                new Color(241, 166, 47)
        );
    }

    void drawGraph(
            Graphics2D graphics,
            Game gameModel,
            Node selectedNode,
            java.awt.geom.Point2D selectedNodePosition,
            Color normalEdgeColor,
            Color intersectingEdgeColor,
            Color nodeColor,
            Color selectedNodeColor
    ) {
        Objects.requireNonNull(graphics, "graphics");
        Objects.requireNonNull(gameModel, "gameModel");
        edgeRenderer.begin(graphics);
        nodeRenderer.begin(graphics);
        try {
            edgeRenderer.drawEdges(
                    graphics,
                    gameModel,
                    selectedNode,
                    selectedNodePosition,
                    normalEdgeColor,
                    intersectingEdgeColor
            );
            nodeRenderer.drawNodes(
                    graphics,
                    gameModel,
                    selectedNode,
                    selectedNodePosition,
                    nodeColor,
                    selectedNodeColor
            );
        } finally {
            nodeRenderer.end();
            edgeRenderer.end();
        }
    }
}
