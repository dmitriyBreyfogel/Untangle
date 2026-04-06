package model;

import java.awt.geom.Point2D;
import java.util.Objects;

public final class GameField {
    private final double width;
    private final double height;
    private final Scheme scheme;

    public GameField(double width, double height, Scheme scheme) {
        if (!Double.isFinite(width) || !Double.isFinite(height) || width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive finite numbers.");
        }
        this.width = width;
        this.height = height;
        this.scheme = Objects.requireNonNull(scheme, "scheme");
        this.scheme.bindGameField(this);
    }

    public boolean canPlace(Point2D point) {
        Objects.requireNonNull(point, "point");
        double x = point.getX();
        double y = point.getY();
        return Double.isFinite(x) && Double.isFinite(y) && x >= 0 && x <= width && y >= 0 && y <= height;
    }

    double width() {
        return width;
    }

    double height() {
        return height;
    }

    Scheme scheme() {
        return scheme;
    }
}
