package view.render;

import model.core.Game;
import model.core.Node;
import view.render.node.NodeRenderStrategyRegistry;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

final class GraphRenderer {
    private final FieldParameters fieldParameters;
    private final EdgeRenderer edgeRenderer;
    private final NodeRenderer nodeRenderer;

    GraphRenderer(FieldParameters fieldParameters) {
        this(fieldParameters, NodeRenderStrategyRegistry.createDefault());
    }

    GraphRenderer(FieldParameters fieldParameters, NodeRenderStrategyRegistry nodeRenderStrategyRegistry) {
        this.fieldParameters = Objects.requireNonNull(fieldParameters, "fieldParameters");
        this.edgeRenderer = new EdgeRenderer(fieldParameters);
        this.nodeRenderer = new NodeRenderer(fieldParameters, nodeRenderStrategyRegistry);
    }

    void drawGraph(Graphics2D graphics, Game gameModel) {
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
        FieldCoordinateMapper mapper = FieldCoordinateMapper.fromBounds(fieldParameters, graphics.getClipBounds(), gameModel);
        edgeRenderer.drawEdges(
                graphics,
                gameModel,
                mapper,
                selectedNode,
                selectedNodePosition,
                normalEdgeColor,
                intersectingEdgeColor
        );
        nodeRenderer.drawNodes(
                graphics,
                gameModel,
                mapper,
                selectedNode,
                selectedNodePosition,
                nodeColor,
                selectedNodeColor
        );
    }
}
