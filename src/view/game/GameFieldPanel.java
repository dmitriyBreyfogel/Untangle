package view.game;

import model.core.Game;
import model.core.Node;
import view.render.FieldParameters;
import view.render.GameFieldRenderer;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
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

final class GameFieldPanel extends JPanel {
    private final FieldParameters fieldParameters;
    private final Game gameModel;
    private Node selectedNode;
    private final GameFieldRenderer gameFieldRenderer;
    private final GameFieldNavigator gameFieldNavigator;
    private Point2D selectedNodePreviewPosition;

    GameFieldPanel(Game gameModel) {
        this.gameModel = Objects.requireNonNull(gameModel, "gameModel");
        fieldParameters = new FieldParameters(12, 28);
        gameFieldRenderer = new GameFieldRenderer(fieldParameters);
        gameFieldNavigator = new GameFieldNavigator(fieldParameters, gameModel, this::getSize);
        configurePanel();
        attachMouseHandlers();
    }

    void refreshField() {
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
        setBackground(new Color(14, 22, 32));
        setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText("Перетаскивайте узлы, чтобы распутать схему");
        setPreferredSize(new Dimension(620, 620));
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
        selectedNodePreviewPosition = gameFieldNavigator.previewMove(selectedNode, screenPoint);
        refreshField();
    }

    private void handleMouseRelease() {
        if (selectedNode != null && selectedNodePreviewPosition != null && hasPendingMove()) {
            gameModel.moveNode(selectedNode, selectedNodePreviewPosition);
        }

        gameFieldNavigator.clearSelectedNode();
        selectedNode = null;
        selectedNodePreviewPosition = null;
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshField();
    }

    private boolean hasPendingMove() {
        double dx = selectedNodePreviewPosition.getX() - selectedNode.getX();
        double dy = selectedNodePreviewPosition.getY() - selectedNode.getY();
        return Math.abs(dx) > 1e-9 || Math.abs(dy) > 1e-9;
    }

}
