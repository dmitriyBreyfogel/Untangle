package view;

import model.Game;
import model.Level;
import model.Node;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Objects;

public final class NodeRenderer {
    private final FieldParameters fieldParameters;

    public NodeRenderer(FieldParameters fieldParameters) {
        this.fieldParameters = Objects.requireNonNull(fieldParameters, "fieldParameters");
    }

    public void drawNodes(Graphics2D graphics, Game gameModel) {
        begin(graphics);
        try {
            drawNodes(graphics, gameModel, null, null, new Color(52, 127, 196), new Color(241, 166, 47));
        } finally {
            end();
        }
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

        for (Node node : currentLevel.scheme().getNodes()) {
            drawNode(
                    graphics,
                    gameModel,
                    node,
                    selectedNode,
                    selectedNodePosition,
                    node == selectedNode ? selectedNodeColor : nodeColor
            );
        }
    }

    private void drawNode(
            Graphics2D graphics,
            Game gameModel,
            Node node,
            Node selectedNode,
            Point2D selectedNodePosition,
            Color color
    ) {
        Point screenPoint = toScreenPoint(gameModel, node, selectedNode, selectedNodePosition);
        int diameter = fieldParameters.nodeRadius() * 2;
        int radius = fieldParameters.nodeRadius();

        graphics.setColor(new Color(4, 10, 18, 56));
        graphics.fillOval(screenPoint.x - radius + 2, screenPoint.y - radius + 4, diameter, diameter);
        if (node == selectedNode) {
            int glowRadius = radius + 8;
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 78));
            graphics.fillOval(
                    screenPoint.x - glowRadius,
                    screenPoint.y - glowRadius,
                    glowRadius * 2,
                    glowRadius * 2
            );
        }

        graphics.setColor(color);
        graphics.fillOval(screenPoint.x - radius, screenPoint.y - radius, diameter, diameter);
        graphics.setStroke(new BasicStroke(2f));
        graphics.setColor(new Color(255, 248, 238, 220));
        graphics.drawOval(screenPoint.x - radius, screenPoint.y - radius, diameter, diameter);
        graphics.setColor(new Color(255, 255, 255, 92));
        graphics.fillOval(screenPoint.x - radius + 4, screenPoint.y - radius + 3, Math.max(4, radius - 6), Math.max(4, radius - 6));
        graphics.setColor(new Color(11, 22, 35, 36));
        graphics.drawOval(screenPoint.x - radius + 1, screenPoint.y - radius + 1, diameter - 2, diameter - 2);
    }

    private Point toScreenPoint(Game gameModel, Node node, Node selectedNode, Point2D selectedNodePosition) {
        Level currentLevel = gameModel.currentLevel();
        Rectangle bounds = currentBounds();
        int padding = fieldParameters.fieldPadding();
        int drawableWidth = Math.max(1, bounds.width - padding * 2);
        int drawableHeight = Math.max(1, bounds.height - padding * 2);

        double modelX = node.getX();
        double modelY = node.getY();
        if (node == selectedNode && selectedNodePosition != null) {
            modelX = selectedNodePosition.getX();
            modelY = selectedNodePosition.getY();
        }

        double x = padding + modelX * drawableWidth / currentLevel.gameField().width();
        double y = padding + modelY * drawableHeight / currentLevel.gameField().height();
        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    private Rectangle currentBounds() {
        return graphicsBounds == null ? new Rectangle(0, 0, 1, 1) : graphicsBounds;
    }

    private Rectangle graphicsBounds;

    void begin(Graphics2D graphics) {
        Rectangle clipBounds = graphics.getClipBounds();
        graphicsBounds = clipBounds != null ? new Rectangle(clipBounds) : new Rectangle(0, 0, 1, 1);
    }

    void end() {
        graphicsBounds = null;
    }
}
