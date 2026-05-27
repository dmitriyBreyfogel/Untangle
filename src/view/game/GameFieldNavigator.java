package view.game;

import model.core.Game;
import model.level.Level;
import model.core.Node;
import view.render.FieldCoordinateMapper;
import view.render.FieldParameters;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Objects;
import java.util.function.Supplier;

public final class GameFieldNavigator {
    private final FieldParameters fieldParameters;
    private final Game gameModel;
    private final Supplier<Dimension> panelSizeSupplier;

    private Node selectedNode;

    public GameFieldNavigator(FieldParameters fieldParameters, Game gameModel, Supplier<Dimension> panelSizeSupplier) {
        this.fieldParameters = Objects.requireNonNull(fieldParameters, "fieldParameters");
        this.gameModel = Objects.requireNonNull(gameModel, "gameModel");
        this.panelSizeSupplier = Objects.requireNonNull(panelSizeSupplier, "panelSizeSupplier");
    }

    public Node findNodeAtScreenPoint(Point screenPoint) {
        Objects.requireNonNull(screenPoint, "screenPoint");
        Level currentLevel = gameModel.currentLevel();
        if (currentLevel == null) {
            return null;
        }

        Node nearestNode = null;
        double nearestDistance = Double.POSITIVE_INFINITY;
        FieldCoordinateMapper mapper = currentMapper();
        for (Node node : currentLevel.scheme().getNodes()) {
            Point nodeScreenPoint = mapper.toScreenCoordinates(new Point2D.Double(node.getX(), node.getY()));
            double distance = nodeScreenPoint.distance(screenPoint);
            if (distance <= fieldParameters.nodeRadius() && distance < nearestDistance) {
                nearestNode = node;
                nearestDistance = distance;
            }
        }
        return nearestNode;
    }

    public void selectNode(Point screenPoint) {
        selectedNode = findNodeAtScreenPoint(screenPoint);
    }

    public void moveSelectedNode(Point screenPoint) {
        Objects.requireNonNull(screenPoint, "screenPoint");
        Level currentLevel = gameModel.currentLevel();
        if (selectedNode == null || currentLevel == null) {
            return;
        }
        Point2D modelPoint = currentMapper().toModelCoordinates(screenPoint);
        currentLevel.scheme().moveNode(selectedNode, modelPoint);
    }

    public void clearSelectedNode() {
        selectedNode = null;
    }

    public Point convertToScreenCoordinates(Point2D modelPoint) {
        return currentMapper().toScreenCoordinates(modelPoint);
    }

    public Point2D convertToModelCoordinates(Point screenPoint) {
        return currentMapper().toModelCoordinates(screenPoint);
    }

    public Point2D previewMove(Node node, Point screenPoint) {
        Objects.requireNonNull(node, "node");
        Objects.requireNonNull(screenPoint, "screenPoint");
        return node.resolveMove(convertToModelCoordinates(screenPoint));
    }

    Node selectedNode() {
        return selectedNode;
    }

    private FieldCoordinateMapper currentMapper() {
        return FieldCoordinateMapper.fromPanel(fieldParameters, currentPanelSize(), gameModel);
    }

    private Dimension currentPanelSize() {
        Dimension panelSize = Objects.requireNonNull(panelSizeSupplier.get(), "panelSize");
        int width = Math.max(1, panelSize.width);
        int height = Math.max(1, panelSize.height);
        return new Dimension(width, height);
    }
}
