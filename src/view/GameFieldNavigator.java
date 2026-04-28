package view;

import model.Game;
import model.Level;
import model.Node;

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
        for (Node node : currentLevel.scheme().getNodes()) {
            Point nodeScreenPoint = convertToScreenCoordinates(new Point2D.Double(node.getX(), node.getY()));
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
        Point2D modelPoint = convertToModelCoordinates(screenPoint);
        currentLevel.scheme().moveNode(selectedNode, modelPoint);
    }

    public void clearSelectedNode() {
        selectedNode = null;
    }

    public Point convertToScreenCoordinates(Point2D modelPoint) {
        Objects.requireNonNull(modelPoint, "modelPoint");
        Dimension panelSize = currentPanelSize();
        int padding = fieldParameters.fieldPadding();
        int drawableWidth = Math.max(1, panelSize.width - padding * 2);
        int drawableHeight = Math.max(1, panelSize.height - padding * 2);

        double screenX = padding + modelPoint.getX() * drawableWidth / currentFieldWidth();
        double screenY = padding + modelPoint.getY() * drawableHeight / currentFieldHeight();
        return new Point((int) Math.round(screenX), (int) Math.round(screenY));
    }

    public Point2D convertToModelCoordinates(Point screenPoint) {
        Objects.requireNonNull(screenPoint, "screenPoint");
        Dimension panelSize = currentPanelSize();
        int padding = fieldParameters.fieldPadding();
        int drawableWidth = Math.max(1, panelSize.width - padding * 2);
        int drawableHeight = Math.max(1, panelSize.height - padding * 2);

        double clampedX = Math.max(padding, Math.min(screenPoint.x, padding + drawableWidth));
        double clampedY = Math.max(padding, Math.min(screenPoint.y, padding + drawableHeight));
        double modelX = (clampedX - padding) * currentFieldWidth() / drawableWidth;
        double modelY = (clampedY - padding) * currentFieldHeight() / drawableHeight;
        return new Point2D.Double(modelX, modelY);
    }

    Node selectedNode() {
        return selectedNode;
    }

    private Dimension currentPanelSize() {
        Dimension panelSize = Objects.requireNonNull(panelSizeSupplier.get(), "panelSize");
        int width = Math.max(1, panelSize.width);
        int height = Math.max(1, panelSize.height);
        return new Dimension(width, height);
    }

    private double currentFieldWidth() {
        Level currentLevel = gameModel.currentLevel();
        return currentLevel == null ? 100.0 : currentLevel.gameField().width();
    }

    private double currentFieldHeight() {
        Level currentLevel = gameModel.currentLevel();
        return currentLevel == null ? 100.0 : currentLevel.gameField().height();
    }
}
