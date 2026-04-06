package model;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

public final class LevelFactory {
    private static final double FIELD_WIDTH = 100.0;
    private static final double FIELD_HEIGHT = 100.0;

    public Level createLevel(int number) {
        return switch (number) {
            case 1 -> new Level(
                    1,
                    List.of(
                            new Point2D.Double(10, 10),
                            new Point2D.Double(90, 90),
                            new Point2D.Double(10, 90),
                            new Point2D.Double(90, 10)
                    ),
                    Map.of(
                            0, List.of(1, 2),
                            2, List.of(3),
                            1, List.of(3)
                    ),
                    FIELD_WIDTH,
                    FIELD_HEIGHT
            );
            case 2 -> new Level(
                    2,
                    List.of(
                            new Point2D.Double(20, 20),
                            new Point2D.Double(80, 80),
                            new Point2D.Double(20, 80),
                            new Point2D.Double(80, 20),
                            new Point2D.Double(50, 50)
                    ),
                    Map.of(
                            0, List.of(1, 4),
                            1, List.of(2),
                            2, List.of(3, 4),
                            3, List.of(0)
                    ),
                    FIELD_WIDTH,
                    FIELD_HEIGHT
            );
            default -> throw new IllegalArgumentException("Unknown level number: " + number);
        };
    }
}
