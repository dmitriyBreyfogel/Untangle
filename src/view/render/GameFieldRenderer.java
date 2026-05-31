package view.render;

import model.core.Game;
import model.level.Level;
import model.core.Node;
import view.render.node.NodeRenderStrategyRegistry;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Objects;

public final class GameFieldRenderer {
    private static final Color DEFAULT_NORMAL_EDGE_COLOR = new Color(106, 126, 152);
    private static final Color DEFAULT_INTERSECTING_EDGE_COLOR = new Color(226, 97, 76);
    private static final Color DEFAULT_NODE_COLOR = new Color(74, 163, 180);
    private static final Color DEFAULT_SELECTED_NODE_COLOR = new Color(243, 188, 65);

    private final FieldParameters fieldParameters;
    private final Color normalEdgeColor;
    private final Color intersectingEdgeColor;
    private final Color nodeColor;
    private final Color selectedNodeColor;
    private final GraphRenderer graphRenderer;
    private final BoardRenderer boardRenderer;

    public GameFieldRenderer(FieldParameters fieldParameters) {
        this(fieldParameters, NodeRenderStrategyRegistry.createDefault());
    }

    public GameFieldRenderer(
            FieldParameters fieldParameters,
            NodeRenderStrategyRegistry nodeRenderStrategyRegistry
    ) {
        this(
                fieldParameters,
                DEFAULT_NORMAL_EDGE_COLOR,
                DEFAULT_INTERSECTING_EDGE_COLOR,
                DEFAULT_NODE_COLOR,
                DEFAULT_SELECTED_NODE_COLOR,
                nodeRenderStrategyRegistry
        );
    }

    public GameFieldRenderer(
            FieldParameters fieldParameters,
            Color normalEdgeColor,
            Color intersectingEdgeColor,
            Color nodeColor,
            Color selectedNodeColor
    ) {
        this(
                fieldParameters,
                normalEdgeColor,
                intersectingEdgeColor,
                nodeColor,
                selectedNodeColor,
                NodeRenderStrategyRegistry.createDefault()
        );
    }

    public GameFieldRenderer(
            FieldParameters fieldParameters,
            Color normalEdgeColor,
            Color intersectingEdgeColor,
            Color nodeColor,
            Color selectedNodeColor,
            NodeRenderStrategyRegistry nodeRenderStrategyRegistry
    ) {
        this.fieldParameters = Objects.requireNonNull(fieldParameters, "fieldParameters");
        this.normalEdgeColor = Objects.requireNonNull(normalEdgeColor, "normalEdgeColor");
        this.intersectingEdgeColor = Objects.requireNonNull(intersectingEdgeColor, "intersectingEdgeColor");
        this.nodeColor = Objects.requireNonNull(nodeColor, "nodeColor");
        this.selectedNodeColor = Objects.requireNonNull(selectedNodeColor, "selectedNodeColor");
        graphRenderer = new GraphRenderer(
                fieldParameters,
                Objects.requireNonNull(nodeRenderStrategyRegistry, "nodeRenderStrategyRegistry")
        );
        boardRenderer = new BoardRenderer();
    }

    public void drawField(Graphics2D graphics, Game gameModel, Node selectedNode) {
        drawField(graphics, gameModel, selectedNode, null);
    }

    public void drawField(Graphics2D graphics, Game gameModel, Node selectedNode, java.awt.geom.Point2D selectedNodePosition) {
        Objects.requireNonNull(graphics, "graphics");
        Objects.requireNonNull(gameModel, "gameModel");

        Graphics2D graphicsCopy = (Graphics2D) graphics.create();
        try {
            graphicsCopy.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphicsCopy.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            Rectangle clipBounds = graphicsCopy.getClipBounds();
            if (clipBounds == null) {
                clipBounds = new Rectangle(0, 0, 1, 1);
                graphicsCopy.setClip(clipBounds);
            }
            boardRenderer.draw(graphicsCopy, clipBounds, fieldParameters);

            Level currentLevel = gameModel.currentLevel();
            if (currentLevel == null) {
                return;
            }
            graphRenderer.drawGraph(
                    graphicsCopy,
                    gameModel,
                    selectedNode,
                    selectedNodePosition,
                    normalEdgeColor,
                    intersectingEdgeColor,
                    nodeColor,
                    selectedNodeColor
            );
        } finally {
            graphicsCopy.dispose();
        }
    }
}
