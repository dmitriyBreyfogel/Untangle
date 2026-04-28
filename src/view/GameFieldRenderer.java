package view;

import model.Game;
import model.Level;
import model.Node;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
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

    public GameFieldRenderer(
            FieldParameters fieldParameters,
            Color normalEdgeColor,
            Color intersectingEdgeColor,
            Color nodeColor,
            Color selectedNodeColor
    ) {
        this.fieldParameters = Objects.requireNonNull(fieldParameters, "fieldParameters");
        this.normalEdgeColor = Objects.requireNonNull(normalEdgeColor, "normalEdgeColor");
        this.intersectingEdgeColor = Objects.requireNonNull(intersectingEdgeColor, "intersectingEdgeColor");
        this.nodeColor = Objects.requireNonNull(nodeColor, "nodeColor");
        this.selectedNodeColor = Objects.requireNonNull(selectedNodeColor, "selectedNodeColor");
        graphRenderer = new GraphRenderer(fieldParameters);
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
            Rectangle clipBounds = graphicsCopy.getClipBounds();
            if (clipBounds == null) {
                clipBounds = new Rectangle(0, 0, 1, 1);
                graphicsCopy.setClip(clipBounds);
            }
            graphicsCopy.setPaint(new GradientPaint(
                    0,
                    0,
                    new Color(248, 244, 237),
                    0,
                    clipBounds.height,
                    new Color(236, 229, 218)
            ));
            graphicsCopy.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);

            int padding = fieldParameters.fieldPadding();
            int fieldWidth = Math.max(1, clipBounds.width - padding * 2);
            int fieldHeight = Math.max(1, clipBounds.height - padding * 2);
            int shadowOffset = 6;
            graphicsCopy.setColor(new Color(77, 60, 42, 22));
            graphicsCopy.fillRoundRect(padding + shadowOffset, padding + shadowOffset, fieldWidth, fieldHeight, 34, 34);
            graphicsCopy.setPaint(new GradientPaint(
                    0,
                    padding,
                    new Color(253, 251, 247),
                    0,
                    padding + fieldHeight,
                    new Color(241, 236, 227)
            ));
            graphicsCopy.fillRoundRect(padding, padding, fieldWidth, fieldHeight, 34, 34);
            graphicsCopy.setColor(new Color(203, 195, 183));
            graphicsCopy.drawRoundRect(padding, padding, fieldWidth, fieldHeight, 34, 34);

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
                graphicsCopy.setColor(new Color(110, 103, 95));
                graphicsCopy.setFont(new Font("Segoe UI", Font.BOLD, 20));
                String title = "Начните новую игру";
                int titleWidth = graphicsCopy.getFontMetrics().stringWidth(title);
                int titleX = clipBounds.x + (clipBounds.width - titleWidth) / 2;
                int titleY = clipBounds.y + clipBounds.height / 2 - 10;
                graphicsCopy.drawString(title, titleX, titleY);

                graphicsCopy.setColor(new Color(145, 136, 126));
                graphicsCopy.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                String hint = "Нажмите «Начать игру» и перетаскивайте узлы мышью";
                int hintWidth = graphicsCopy.getFontMetrics().stringWidth(hint);
                int hintX = clipBounds.x + (clipBounds.width - hintWidth) / 2;
                graphicsCopy.drawString(hint, hintX, titleY + 30);
            }
        } finally {
            graphicsCopy.dispose();
        }
    }
}
