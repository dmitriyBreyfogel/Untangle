package view;

import model.Game;
import model.Level;
import model.Node;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Objects;

public final class NodeRenderer {
    private final FieldParameters fieldParameters;
    private final NodeRenderStrategyRegistry nodeRenderStrategyRegistry;

    public NodeRenderer(FieldParameters fieldParameters) {
        this.fieldParameters = Objects.requireNonNull(fieldParameters, "fieldParameters");
        this.nodeRenderStrategyRegistry = NodeRenderStrategyRegistry.createDefault();
    }

    public NodeRenderer(FieldParameters fieldParameters, NodeRenderStrategyRegistry nodeRenderStrategyRegistry) {
        this.fieldParameters = Objects.requireNonNull(fieldParameters, "fieldParameters");
        this.nodeRenderStrategyRegistry = Objects.requireNonNull(nodeRenderStrategyRegistry, "nodeRenderStrategyRegistry");
    }

    public void drawNodes(Graphics2D graphics, Game gameModel) {
        drawNodes(graphics, gameModel, null, null, new Color(52, 127, 196), new Color(241, 166, 47));
    }

    void drawNodes(
            Graphics2D graphics,
            Game gameModel,
            Node selectedNode,
            Point2D selectedNodePosition,
            Color nodeColor,
            Color selectedNodeColor
    ) {
        Objects.requireNonNull(graphics, "graphics");
        Objects.requireNonNull(gameModel, "gameModel");
        Objects.requireNonNull(nodeColor, "nodeColor");
        Objects.requireNonNull(selectedNodeColor, "selectedNodeColor");

        Level currentLevel = gameModel.currentLevel();
        if (currentLevel == null) {
            return;
        }

        FieldCoordinateMapper mapper = mapperFor(graphics, gameModel);
        drawNodes(graphics, gameModel, mapper, selectedNode, selectedNodePosition, nodeColor, selectedNodeColor);
    }

    void drawNodes(
            Graphics2D graphics,
            Game gameModel,
            FieldCoordinateMapper mapper,
            Node selectedNode,
            Point2D selectedNodePosition,
            Color nodeColor,
            Color selectedNodeColor
    ) {
        Objects.requireNonNull(graphics, "graphics");
        Objects.requireNonNull(gameModel, "gameModel");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(nodeColor, "nodeColor");
        Objects.requireNonNull(selectedNodeColor, "selectedNodeColor");

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
                    node == selectedNode ? selectedNodeColor : nodeColor
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

    void begin(Graphics2D graphics) {
        Objects.requireNonNull(graphics, "graphics");
    }

    void end() {
    }
}
