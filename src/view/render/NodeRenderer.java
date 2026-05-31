package view.render;

import model.core.Game;
import model.level.Level;
import model.core.Node;
import view.render.node.NodeRenderStrategyRegistry;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Objects;

final class NodeRenderer {
    private final FieldParameters fieldParameters;
    private final NodeRenderStrategyRegistry nodeRenderStrategyRegistry;

    NodeRenderer(FieldParameters fieldParameters) {
        this.fieldParameters = Objects.requireNonNull(fieldParameters, "fieldParameters");
        this.nodeRenderStrategyRegistry = NodeRenderStrategyRegistry.createDefault();
    }

    NodeRenderer(FieldParameters fieldParameters, NodeRenderStrategyRegistry nodeRenderStrategyRegistry) {
        this.fieldParameters = Objects.requireNonNull(fieldParameters, "fieldParameters");
        this.nodeRenderStrategyRegistry = Objects.requireNonNull(nodeRenderStrategyRegistry, "nodeRenderStrategyRegistry");
    }

    void drawNodes(Graphics2D graphics, Game gameModel) {
        drawNodes(graphics, gameModel, GraphPalette.defaultPalette());
    }

    void drawNodes(Graphics2D graphics, Game gameModel, GraphPalette graphPalette) {
        drawNodes(graphics, gameModel, null, null, graphPalette);
    }

    void drawNodes(
            Graphics2D graphics,
            Game gameModel,
            Node selectedNode,
            Point2D selectedNodePosition,
            GraphPalette graphPalette
    ) {
        Objects.requireNonNull(graphics, "graphics");
        Objects.requireNonNull(gameModel, "gameModel");
        Objects.requireNonNull(graphPalette, "graphPalette");

        Level currentLevel = gameModel.currentLevel();
        if (currentLevel == null) {
            return;
        }

        FieldCoordinateMapper mapper = mapperFor(graphics, gameModel);
        drawNodes(graphics, gameModel, mapper, selectedNode, selectedNodePosition, graphPalette);
    }

    void drawNodes(
            Graphics2D graphics,
            Game gameModel,
            FieldCoordinateMapper mapper,
            Node selectedNode,
            Point2D selectedNodePosition,
            GraphPalette graphPalette
    ) {
        Objects.requireNonNull(graphics, "graphics");
        Objects.requireNonNull(gameModel, "gameModel");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(graphPalette, "graphPalette");

        Level currentLevel = gameModel.currentLevel();
        if (currentLevel == null) {
            return;
        }

        for (Node node : currentLevel.scheme().getNodes()) {
            drawNode(
                    graphics,
                    mapper,
                    node,
                    selectedNode,
                    selectedNodePosition,
                    node == selectedNode ? graphPalette.selectedNodeColor() : graphPalette.nodeColor()
            );
        }
    }

    private void drawNode(
            Graphics2D graphics,
            FieldCoordinateMapper mapper,
            Node node,
            Node selectedNode,
            Point2D selectedNodePosition,
            Color color
    ) {
        Point screenPoint = toScreenPoint(mapper, node, selectedNode, selectedNodePosition);
        int radius = fieldParameters.nodeRadius();
        nodeRenderStrategyRegistry.resolve(node).render(graphics, screenPoint, radius, color, node == selectedNode);
    }

    private Point toScreenPoint(FieldCoordinateMapper mapper, Node node, Node selectedNode, Point2D selectedNodePosition) {
        double modelX = node.getX();
        double modelY = node.getY();
        if (node == selectedNode && selectedNodePosition != null) {
            modelX = selectedNodePosition.getX();
            modelY = selectedNodePosition.getY();
        }

        return mapper.toScreenCoordinates(new Point2D.Double(modelX, modelY));
    }

    private FieldCoordinateMapper mapperFor(Graphics2D graphics, Game gameModel) {
        return FieldCoordinateMapper.fromBounds(fieldParameters, graphics.getClipBounds(), gameModel);
    }
}
