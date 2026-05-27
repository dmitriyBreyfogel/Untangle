package view.render;

import model.core.Edge;
import model.core.Game;
import model.level.Level;
import model.core.Node;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class EdgeRenderer {
    private final FieldParameters fieldParameters;

    public EdgeRenderer(FieldParameters fieldParameters) {
        this.fieldParameters = Objects.requireNonNull(fieldParameters, "fieldParameters");
    }

    public void drawEdges(Graphics2D graphics, Game gameModel) {
        drawEdges(graphics, gameModel, null, null, new Color(90, 90, 90), new Color(210, 70, 70));
    }

    void drawEdges(Graphics2D graphics, Game gameModel, Color normalEdgeColor, Color intersectingEdgeColor) {
        drawEdges(graphics, gameModel, null, null, normalEdgeColor, intersectingEdgeColor);
    }

    void drawEdges(
            Graphics2D graphics,
            Game gameModel,
            Node selectedNode,
            Point2D selectedNodePosition,
            Color normalEdgeColor,
            Color intersectingEdgeColor
    ) {
        Objects.requireNonNull(graphics, "graphics");
        Objects.requireNonNull(gameModel, "gameModel");
        Objects.requireNonNull(normalEdgeColor, "normalEdgeColor");
        Objects.requireNonNull(intersectingEdgeColor, "intersectingEdgeColor");

        Level currentLevel = gameModel.currentLevel();
        if (currentLevel == null) {
            return;
        }

        FieldCoordinateMapper mapper = mapperFor(graphics, gameModel);
        drawEdges(graphics, gameModel, mapper, selectedNode, selectedNodePosition, normalEdgeColor, intersectingEdgeColor);
    }

    void drawEdges(
            Graphics2D graphics,
            Game gameModel,
            FieldCoordinateMapper mapper,
            Node selectedNode,
            Point2D selectedNodePosition,
            Color normalEdgeColor,
            Color intersectingEdgeColor
    ) {
        Objects.requireNonNull(graphics, "graphics");
        Objects.requireNonNull(gameModel, "gameModel");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(normalEdgeColor, "normalEdgeColor");
        Objects.requireNonNull(intersectingEdgeColor, "intersectingEdgeColor");

        Level currentLevel = gameModel.currentLevel();
        if (currentLevel == null) {
            return;
        }

        List<Edge> edges = currentLevel.scheme().getEdges();
        Set<Edge> previewIntersectingEdges = selectedNode != null && selectedNodePosition != null
                ? new HashSet<>(currentLevel.scheme().getIntersectingEdgesAfterMove(selectedNode, selectedNodePosition))
                : null;

        for (Edge edge : edges) {
            boolean intersecting = previewIntersectingEdges == null
                    ? edge.isIntersecting()
                    : previewIntersectingEdges.contains(edge);
            drawEdge(
                    graphics,
                    mapper,
                    edge,
                    selectedNode,
                    selectedNodePosition,
                    intersecting ? intersectingEdgeColor : normalEdgeColor,
                    intersecting
            );
        }
    }

    private void drawEdge(
            Graphics2D graphics,
            FieldCoordinateMapper mapper,
            Edge edge,
            Node selectedNode,
            Point2D selectedNodePosition,
            Color color,
            boolean intersecting
    ) {
        Point start = toScreenPoint(mapper, edge.getNodeA(), selectedNode, selectedNodePosition);
        Point end = toScreenPoint(mapper, edge.getNodeB(), selectedNode, selectedNodePosition);
        Line2D edgeLine = new Line2D.Float(start.x, start.y, end.x, end.y);
        Line2D shadowLine = new Line2D.Float(start.x + 2.5f, start.y + 3.5f, end.x + 2.5f, end.y + 3.5f);

        graphics.setStroke(new BasicStroke(9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.setColor(new Color(4, 10, 18, 52));
        graphics.draw(shadowLine);

        if (intersecting) {
            graphics.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphics.setColor(withAlpha(color, 72));
            graphics.draw(edgeLine);
        }

        graphics.setStroke(new BasicStroke(4.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.setColor(color);
        graphics.draw(edgeLine);
    }

    private Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
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
