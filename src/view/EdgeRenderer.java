package view;

import model.Edge;
import model.Game;
import model.Level;
import model.Node;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
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
        begin(graphics);
        try {
            drawEdges(graphics, gameModel, null, null, new Color(90, 90, 90), new Color(210, 70, 70));
        } finally {
            end();
        }
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

        List<Edge> edges = currentLevel.scheme().getEdges();
        Set<Edge> previewIntersectingEdges = selectedNode != null && selectedNodePosition != null
                ? collectPreviewIntersectingEdges(edges, selectedNode, selectedNodePosition)
                : null;

        graphics.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (Edge edge : edges) {
            boolean intersecting = previewIntersectingEdges == null
                    ? edge.isIntersecting()
                    : previewIntersectingEdges.contains(edge);
            drawEdge(
                    graphics,
                    gameModel,
                    edge,
                    selectedNode,
                    selectedNodePosition,
                    intersecting ? intersectingEdgeColor : normalEdgeColor
            );
        }
    }

    private Set<Edge> collectPreviewIntersectingEdges(List<Edge> edges, Node selectedNode, Point2D selectedNodePosition) {
        Set<Edge> intersectingEdges = new HashSet<>();
        for (int i = 0; i < edges.size(); i++) {
            for (int j = i + 1; j < edges.size(); j++) {
                Edge first = edges.get(i);
                Edge second = edges.get(j);
                if (previewIntersects(first, second, selectedNode, selectedNodePosition)) {
                    intersectingEdges.add(first);
                    intersectingEdges.add(second);
                }
            }
        }
        return intersectingEdges;
    }

    private boolean previewIntersects(Edge first, Edge second, Node selectedNode, Point2D selectedNodePosition) {
        if (first == second) {
            return false;
        }
        if (first.containsNode(second.getNodeA()) || first.containsNode(second.getNodeB())) {
            return false;
        }

        Point2D firstStart = modelPointOf(first.getNodeA(), selectedNode, selectedNodePosition);
        Point2D firstEnd = modelPointOf(first.getNodeB(), selectedNode, selectedNodePosition);
        Point2D secondStart = modelPointOf(second.getNodeA(), selectedNode, selectedNodePosition);
        Point2D secondEnd = modelPointOf(second.getNodeB(), selectedNode, selectedNodePosition);

        return strictlyIntersects(firstStart, firstEnd, secondStart, secondEnd);
    }

    private Point2D modelPointOf(Node node, Node selectedNode, Point2D selectedNodePosition) {
        if (node == selectedNode && selectedNodePosition != null) {
            return selectedNodePosition;
        }
        return new Point2D.Double(node.getX(), node.getY());
    }

    private static boolean strictlyIntersects(Point2D a1, Point2D a2, Point2D b1, Point2D b2) {
        int o1 = orientation(a1, a2, b1);
        int o2 = orientation(a1, a2, b2);
        int o3 = orientation(b1, b2, a1);
        int o4 = orientation(b1, b2, a2);

        if (o1 == 0 || o2 == 0 || o3 == 0 || o4 == 0) {
            return false;
        }
        return o1 != o2 && o3 != o4;
    }

    private static int orientation(Point2D a, Point2D b, Point2D c) {
        double cross = (b.getX() - a.getX()) * (c.getY() - a.getY()) - (b.getY() - a.getY()) * (c.getX() - a.getX());
        double epsilon = 1e-10;
        if (Math.abs(cross) <= epsilon) {
            return 0;
        }
        return cross > 0 ? 1 : -1;
    }

    private void drawEdge(
            Graphics2D graphics,
            Game gameModel,
            Edge edge,
            Node selectedNode,
            Point2D selectedNodePosition,
            Color color
    ) {
        Point start = toScreenPoint(gameModel, edge.getNodeA(), selectedNode, selectedNodePosition);
        Point end = toScreenPoint(gameModel, edge.getNodeB(), selectedNode, selectedNodePosition);
        graphics.setColor(color);
        graphics.drawLine(start.x, start.y, end.x, end.y);
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
