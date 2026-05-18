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
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
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
            drawBackdrop(graphicsCopy, clipBounds);

            int padding = fieldParameters.fieldPadding();
            int fieldWidth = Math.max(1, clipBounds.width - padding * 2);
            int fieldHeight = Math.max(1, clipBounds.height - padding * 2);
            drawBoardSurface(graphicsCopy, padding, fieldWidth, fieldHeight);
            drawBoardTexture(graphicsCopy, padding, fieldWidth, fieldHeight);

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
                drawEmptyState(graphicsCopy, clipBounds, gameModel);
            }
        } finally {
            graphicsCopy.dispose();
        }
    }

    private void drawBackdrop(Graphics2D graphics, Rectangle clipBounds) {
        graphics.setPaint(new GradientPaint(
                0,
                clipBounds.y,
                new Color(10, 18, 29),
                0,
                clipBounds.y + clipBounds.height,
                new Color(24, 36, 49)
        ));
        graphics.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
        graphics.setPaint(new GradientPaint(
                clipBounds.x,
                clipBounds.y,
                new Color(255, 214, 143, 34),
                clipBounds.x + clipBounds.width,
                clipBounds.y + clipBounds.height,
                new Color(255, 255, 255, 0)
        ));
        graphics.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
    }

    private void drawBoardSurface(Graphics2D graphics, int padding, int fieldWidth, int fieldHeight) {
        int arc = 42;
        graphics.setColor(new Color(0, 0, 0, 48));
        graphics.fillRoundRect(padding + 10, padding + 14, fieldWidth, fieldHeight, arc, arc);
        graphics.setColor(new Color(6, 12, 19, 26));
        graphics.fillRoundRect(padding + 5, padding + 6, fieldWidth, fieldHeight, arc, arc);
        graphics.setPaint(new GradientPaint(
                0,
                padding,
                new Color(251, 246, 236),
                0,
                padding + fieldHeight,
                new Color(227, 219, 205)
        ));
        graphics.fillRoundRect(padding, padding, fieldWidth, fieldHeight, arc, arc);
        graphics.setColor(new Color(171, 156, 136));
        graphics.drawRoundRect(padding, padding, fieldWidth, fieldHeight, arc, arc);
        graphics.setColor(new Color(255, 255, 255, 92));
        graphics.drawRoundRect(padding + 2, padding + 2, fieldWidth - 4, fieldHeight - 4, arc - 6, arc - 6);
    }

    private void drawBoardTexture(Graphics2D graphics, int padding, int fieldWidth, int fieldHeight) {
        Shape previousClip = graphics.getClip();
        RoundRectangle2D boardShape = new RoundRectangle2D.Float(padding, padding, fieldWidth, fieldHeight, 42, 42);
        graphics.clip(boardShape);
        graphics.setColor(new Color(255, 255, 255, 24));
        for (int y = padding + 22; y < padding + fieldHeight; y += 40) {
            graphics.drawLine(padding + 18, y, padding + fieldWidth - 18, y);
        }
        graphics.setColor(new Color(35, 52, 72, 14));
        for (int x = padding + 22; x < padding + fieldWidth; x += 40) {
            graphics.drawLine(x, padding + 18, x, padding + fieldHeight - 18);
        }
        graphics.setColor(new Color(255, 255, 255, 18));
        for (int x = padding + 24; x < padding + fieldWidth - 10; x += 56) {
            for (int y = padding + 28; y < padding + fieldHeight - 10; y += 56) {
                graphics.fillOval(x, y, 3, 3);
            }
        }
        graphics.setClip(previousClip);
    }

    private void drawEmptyState(Graphics2D graphics, Rectangle clipBounds, Game gameModel) {
        int centerX = clipBounds.x + clipBounds.width / 2;
        int centerY = clipBounds.y + clipBounds.height / 2;

        graphics.setColor(new Color(32, 48, 69));
        graphics.fillRoundRect(centerX - 82, centerY - 118, 164, 30, 18, 18);
        graphics.setColor(new Color(242, 233, 219));
        graphics.setFont(new Font("Segoe UI", Font.BOLD, 12));
        drawCenteredString(graphics, "НОВАЯ ПАРТИЯ", centerX, centerY - 98);

        graphics.setColor(new Color(66, 60, 54));
        graphics.setFont(new Font("Segoe UI", Font.BOLD, 24));
        drawCenteredString(graphics, "Начните игру", centerX, centerY - 26);

        graphics.setColor(new Color(121, 112, 102));
        graphics.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        drawCenteredString(graphics, "Перетаскивайте узлы и выводите линии", centerX, centerY + 10);
        drawCenteredString(graphics, "из пересечений, пока схема не очистится", centerX, centerY + 32);

        graphics.setColor(new Color(229, 218, 199));
        graphics.fillRoundRect(centerX - 146, centerY + 54, 292, 36, 18, 18);
        graphics.setColor(new Color(68, 59, 53));
        graphics.setFont(new Font("Segoe UI", Font.BOLD, 13));
        drawCenteredString(graphics, "Нажмите «Новая игра»", centerX, centerY + 77);

        if (gameModel.hasProgressToContinue()) {
            graphics.setColor(new Color(123, 111, 99));
            graphics.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            drawCenteredString(
                    graphics,
                    "Или продолжите с уровня " + formatLevelNumber(gameModel.continueLevelNumber()),
                    centerX,
                    centerY + 110
            );
        }
    }

    private void drawCenteredString(Graphics2D graphics, String text, int centerX, int baselineY) {
        int textWidth = graphics.getFontMetrics().stringWidth(text);
        graphics.drawString(text, centerX - textWidth / 2, baselineY);
    }

    private String formatLevelNumber(int value) {
        return Integer.toString(value);
    }
}
