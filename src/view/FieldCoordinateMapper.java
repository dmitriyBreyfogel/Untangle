package view;

import model.core.Game;
import model.level.Level;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Objects;

public final class FieldCoordinateMapper {
    private static final double DEFAULT_FIELD_WIDTH = 100.0;
    private static final double DEFAULT_FIELD_HEIGHT = 100.0;

    private final FieldParameters fieldParameters;
    private final Dimension panelSize;
    private final double fieldWidth;
    private final double fieldHeight;

    public FieldCoordinateMapper(FieldParameters fieldParameters, Dimension panelSize, double fieldWidth, double fieldHeight) {
        this.fieldParameters = Objects.requireNonNull(fieldParameters, "fieldParameters");
        Objects.requireNonNull(panelSize, "panelSize");
        if (!Double.isFinite(fieldWidth) || !Double.isFinite(fieldHeight) || fieldWidth <= 0 || fieldHeight <= 0) {
            throw new IllegalArgumentException("Размеры поля должны быть конечными положительными величинами");
        }
        this.panelSize = new Dimension(Math.max(1, panelSize.width), Math.max(1, panelSize.height));
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
    }

    public static FieldCoordinateMapper fromPanel(FieldParameters fieldParameters, Dimension panelSize, Game gameModel) {
        Objects.requireNonNull(gameModel, "gameModel");
        Level currentLevel = gameModel.currentLevel();
        double width = currentLevel == null ? DEFAULT_FIELD_WIDTH : currentLevel.gameField().width();
        double height = currentLevel == null ? DEFAULT_FIELD_HEIGHT : currentLevel.gameField().height();
        return new FieldCoordinateMapper(fieldParameters, panelSize, width, height);
    }

    public static FieldCoordinateMapper fromBounds(FieldParameters fieldParameters, Rectangle bounds, Game gameModel) {
        Rectangle safeBounds = bounds == null ? new Rectangle(0, 0, 1, 1) : bounds;
        return fromPanel(fieldParameters, new Dimension(safeBounds.width, safeBounds.height), gameModel);
    }

    public Point toScreenCoordinates(Point2D modelPoint) {
        Objects.requireNonNull(modelPoint, "modelPoint");
        double screenX = fieldParameters.fieldPadding() + modelPoint.getX() * drawableWidth() / fieldWidth;
        double screenY = fieldParameters.fieldPadding() + modelPoint.getY() * drawableHeight() / fieldHeight;
        return new Point((int) Math.round(screenX), (int) Math.round(screenY));
    }

    public Point2D toModelCoordinates(Point screenPoint) {
        Objects.requireNonNull(screenPoint, "screenPoint");
        int padding = fieldParameters.fieldPadding();
        double clampedX = Math.max(padding, Math.min(screenPoint.x, padding + drawableWidth()));
        double clampedY = Math.max(padding, Math.min(screenPoint.y, padding + drawableHeight()));
        double modelX = (clampedX - padding) * fieldWidth / drawableWidth();
        double modelY = (clampedY - padding) * fieldHeight / drawableHeight();
        return new Point2D.Double(modelX, modelY);
    }

    private int drawableWidth() {
        return Math.max(1, panelSize.width - fieldParameters.fieldPadding() * 2);
    }

    private int drawableHeight() {
        return Math.max(1, panelSize.height - fieldParameters.fieldPadding() * 2);
    }
}
