package view;

import model.Game;
import model.Level;
import model.Node;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Objects;

public final class GameFieldRenderer {
    private final FieldParameters fieldParameters;
    private final Color normalEdgeColor;
    private final Color intersectingEdgeColor;
    private final Color nodeColor;
    private final Color selectedNodeColor;
    private final GraphRenderer graphRenderer;
    private final BoardRenderer boardRenderer;
    private final EmptyFieldRenderer emptyFieldRenderer;

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
        graphRenderer = new GraphRenderer(fieldParameters, nodeRenderStrategyRegistry);
        boardRenderer = new BoardRenderer();
        emptyFieldRenderer = new EmptyFieldRenderer();
    }

    public void drawField(Graphics2D graphics, Game gameModel, Node selectedNode) {
        drawField(graphics, gameModel, selectedNode, null);
    }

    void drawField(Graphics2D graphics, Game gameModel, Node selectedNode, java.awt.geom.Point2D selectedNodePosition) {
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
            if (currentLevel != null) {
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
            } else {
                emptyFieldRenderer.draw(graphicsCopy, clipBounds, gameModel);
            }
        } finally {
            graphicsCopy.dispose();
        }
    }
}
