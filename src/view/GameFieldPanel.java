package view;

import model.Game;
import model.Level;
import model.Node;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Objects;

public final class GameFieldPanel extends JPanel {
    private final FieldParameters fieldParameters;
    private final Game gameModel;
    private Node selectedNode;
    private final Color normalEdgeColor;
    private final Color intersectingEdgeColor;
    private final Color nodeColor;
    private final Color selectedNodeColor;
    private final GameFieldRenderer gameFieldRenderer;
    private final GameFieldNavigator gameFieldNavigator;
    private Point2D selectedNodePreviewPosition;

    public GameFieldPanel(Game gameModel) {
        this.gameModel = Objects.requireNonNull(gameModel, "gameModel");
        fieldParameters = new FieldParameters(12, 28);
        normalEdgeColor = new Color(66, 63, 60);
        intersectingEdgeColor = new Color(198, 59, 59);
        nodeColor = new Color(53, 111, 179);
        selectedNodeColor = new Color(230, 155, 44);
        gameFieldRenderer = new GameFieldRenderer(
                fieldParameters,
                normalEdgeColor,
                intersectingEdgeColor,
                nodeColor,
                selectedNodeColor
        );
        gameFieldNavigator = new GameFieldNavigator(fieldParameters, gameModel, this::getSize);
        configurePanel();
        attachMouseHandlers();
    }

    public void refreshField() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        gameFieldRenderer.drawField((Graphics2D) graphics, gameModel, selectedNode, selectedNodePreviewPosition);
    }

    private void configurePanel() {
        setOpaque(true);
        setDoubleBuffered(true);
        setBackground(new Color(243, 239, 233));
        setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText("Перетаскивайте узлы, чтобы убрать пересечения");
        setPreferredSize(new Dimension(700, 700));
        setMinimumSize(new Dimension(320, 320));
    }

    private void attachMouseHandlers() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                handleMousePress(event.getPoint());
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                handleMouseDrag(event.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                handleMouseRelease();
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private void handleMousePress(Point screenPoint) {
        gameFieldNavigator.selectNode(screenPoint);
        selectedNode = gameFieldNavigator.selectedNode();
        selectedNodePreviewPosition = selectedNode == null
                ? null
                : new Point2D.Double(selectedNode.getX(), selectedNode.getY());
        setCursor(Cursor.getPredefinedCursor(selectedNode == null ? Cursor.HAND_CURSOR : Cursor.MOVE_CURSOR));
        refreshField();
    }

    private void handleMouseDrag(Point screenPoint) {
        if (selectedNode == null || gameModel.currentLevel() == null) {
            return;
        }
        selectedNodePreviewPosition = gameFieldNavigator.convertToModelCoordinates(screenPoint);
        refreshField();
    }

    private void handleMouseRelease() {
        Level levelBeforeMove = gameModel.currentLevel();
        int previousLevelNumber = gameModel.currentLevelNumber();
        int previousMaxCompletedLevelNumber = gameModel.maxCompletedLevelNumber();

        if (levelBeforeMove != null && selectedNode != null && selectedNodePreviewPosition != null && hasPendingMove()) {
            levelBeforeMove.scheme().moveNode(selectedNode, selectedNodePreviewPosition);
        }

        gameFieldNavigator.clearSelectedNode();
        selectedNode = null;
        selectedNodePreviewPosition = null;
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        handleMoveResult(previousLevelNumber, previousMaxCompletedLevelNumber);
    }

    private boolean hasPendingMove() {
        double dx = selectedNodePreviewPosition.getX() - selectedNode.getX();
        double dy = selectedNodePreviewPosition.getY() - selectedNode.getY();
        return Math.abs(dx) > 1e-9 || Math.abs(dy) > 1e-9;
    }

    private void handleMoveResult(int previousLevelNumber, int previousMaxCompletedLevelNumber) {
        java.awt.Window ownerWindow = SwingUtilities.getWindowAncestor(this);
        if (ownerWindow instanceof GameWindow gameWindow) {
            gameWindow.handleMoveResult(previousLevelNumber, previousMaxCompletedLevelNumber);
            return;
        }
        refreshField();
    }
}
