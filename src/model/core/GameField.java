package model.core;

import java.awt.geom.Point2D;
import java.util.Objects;

public final class GameField {
    private final double width;
    private final double height;

    public GameField(double width, double height) {
        if (!Double.isFinite(width) || !Double.isFinite(height) || width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Ширина и высота должны быть конечными положительными величинами");
        }
        this.width = width;
        this.height = height;
    }

    public boolean canPlace(Point2D point) {
        Objects.requireNonNull(point, "point");
        double x = point.getX();
        double y = point.getY();
        return Double.isFinite(x) && Double.isFinite(y) && x >= 0 && x <= width && y >= 0 && y <= height;
    }

    public double width() {
        return width;
    }

    public double height() {
        return height;
    }
}
