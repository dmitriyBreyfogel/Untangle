package view.render;

import model.core.Game;
import model.core.Node;
import view.render.node.NodeRenderStrategyRegistry;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Objects;

final class GraphRenderer {
    private final FieldParameters fieldParameters;
    private final GraphPalette graphPalette;
    private final EdgeRenderer edgeRenderer;
    private final NodeRenderer nodeRenderer;

    GraphRenderer(FieldParameters fieldParameters) {
        this(fieldParameters, GraphPalette.defaultPalette());
    }

    GraphRenderer(FieldParameters fieldParameters, GraphPalette graphPalette) {
        this(fieldParameters, graphPalette, NodeRenderStrategyRegistry.createDefault());
    }

    GraphRenderer(FieldParameters fieldParameters, NodeRenderStrategyRegistry nodeRenderStrategyRegistry) {
        this(fieldParameters, GraphPalette.defaultPalette(), nodeRenderStrategyRegistry);
    }

    GraphRenderer(
            FieldParameters fieldParameters,
            GraphPalette graphPalette,
            NodeRenderStrategyRegistry nodeRenderStrategyRegistry
    ) {
        this.fieldParameters = Objects.requireNonNull(fieldParameters, "fieldParameters");
        this.graphPalette = Objects.requireNonNull(graphPalette, "graphPalette");
        this.edgeRenderer = new EdgeRenderer(fieldParameters);
        this.nodeRenderer = new NodeRenderer(fieldParameters, nodeRenderStrategyRegistry);
    }

    void drawGraph(Graphics2D graphics, Game gameModel) {
        drawGraph(graphics, gameModel, null, null);
    }

    void drawGraph(
            Graphics2D graphics,
            Game gameModel,
            Node selectedNode,
            Point2D selectedNodePosition
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
                graphPalette
        );
        nodeRenderer.drawNodes(
                graphics,
                gameModel,
                mapper,
                selectedNode,
                selectedNodePosition,
                graphPalette
        );
    }
}
