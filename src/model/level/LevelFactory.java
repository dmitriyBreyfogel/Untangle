package model.level;

import model.movement.FixedMovementStrategy;
import model.movement.HorizontalMovementStrategy;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class LevelFactory {
    private static final double FIELD_WIDTH = 100.0;
    private static final double FIELD_HEIGHT = 100.0;
    private static final int LAST_LEVEL_NUMBER = 10;

    public List<Integer> availableLevelNumbers() {
        List<Integer> numbers = new ArrayList<>();
        for (int number = 1; number <= LAST_LEVEL_NUMBER; number++) {
            numbers.add(number);
        }
        return List.copyOf(numbers);
    }

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
                    Map.of(
                            2, new FixedMovementStrategy(),
                            3, new HorizontalMovementStrategy()
                    ),
                    FIELD_WIDTH,
                    FIELD_HEIGHT
            );
            case 2 -> new Level(
                    2,
                    List.of(
                            new Point2D.Double(15, 15),
                            new Point2D.Double(85, 85),
                            new Point2D.Double(85, 15),
                            new Point2D.Double(15, 85),
                            new Point2D.Double(50, 50)
                    ),
                    Map.ofEntries(
                            Map.entry(0, List.of(1, 3, 4)),
                            Map.entry(1, List.of(2, 4)),
                            Map.entry(2, List.of(3, 4)),
                            Map.entry(3, List.of(4))
                    ),
                    FIELD_WIDTH,
                    FIELD_HEIGHT
            );
            case 3 -> new Level(
                    3,
                    List.of(
                            new Point2D.Double(50, 8),
                            new Point2D.Double(88, 80),
                            new Point2D.Double(12, 80),
                            new Point2D.Double(50, 28),
                            new Point2D.Double(68, 68),
                            new Point2D.Double(32, 68)
                    ),
                    Map.ofEntries(
                            Map.entry(0, List.of(2, 4, 1)),
                            Map.entry(2, List.of(4, 3)),
                            Map.entry(4, List.of(5)),
                            Map.entry(1, List.of(3, 5)),
                            Map.entry(3, List.of(5))
                    ),
                    FIELD_WIDTH,
                    FIELD_HEIGHT
            );
            case 4 -> new Level(
                    4,
                    List.of(
                            new Point2D.Double(50, 8),
                            new Point2D.Double(50, 92),
                            new Point2D.Double(85, 25),
                            new Point2D.Double(15, 70),
                            new Point2D.Double(85, 70),
                            new Point2D.Double(15, 25),
                            new Point2D.Double(50, 50)
                    ),
                    Map.ofEntries(
                            Map.entry(0, List.of(1, 5, 6)),
                            Map.entry(1, List.of(2, 6)),
                            Map.entry(2, List.of(3, 6)),
                            Map.entry(3, List.of(4, 6)),
                            Map.entry(4, List.of(5, 6))
                    ),
                    FIELD_WIDTH,
                    FIELD_HEIGHT
            );
            case 5 -> new Level(
                    5,
                    List.of(
                            new Point2D.Double(12, 80),
                            new Point2D.Double(88, 20),
                            new Point2D.Double(37, 80),
                            new Point2D.Double(63, 20),
                            new Point2D.Double(12, 20),
                            new Point2D.Double(88, 80),
                            new Point2D.Double(37, 20),
                            new Point2D.Double(63, 80)
                    ),
                    Map.ofEntries(
                            Map.entry(0, List.of(1, 4, 5)),
                            Map.entry(1, List.of(2, 5, 6)),
                            Map.entry(2, List.of(3, 6, 7)),
                            Map.entry(3, List.of(7)),
                            Map.entry(4, List.of(5)),
                            Map.entry(5, List.of(6)),
                            Map.entry(6, List.of(7))
                    ),
                    Map.of(1, new HorizontalMovementStrategy()),
                    FIELD_WIDTH,
                    FIELD_HEIGHT
            );
            case 6 -> new Level(
                    6,
                    List.of(
                            new Point2D.Double(85, 85),
                            new Point2D.Double(15, 85),
                            new Point2D.Double(85, 50),
                            new Point2D.Double(15, 15),
                            new Point2D.Double(50, 50),
                            new Point2D.Double(50, 15),
                            new Point2D.Double(50, 85),
                            new Point2D.Double(15, 50),
                            new Point2D.Double(85, 15)
                    ),
                    Map.ofEntries(
                            Map.entry(0, List.of(1, 3, 4)),
                            Map.entry(1, List.of(2, 4, 5)),
                            Map.entry(2, List.of(5)),
                            Map.entry(3, List.of(4, 6, 7)),
                            Map.entry(4, List.of(5, 7, 8)),
                            Map.entry(5, List.of(8)),
                            Map.entry(6, List.of(7)),
                            Map.entry(7, List.of(8))
                    ),
                    Map.of(
                            4, new FixedMovementStrategy(),
                            6, new HorizontalMovementStrategy()
                    ),
                    FIELD_WIDTH,
                    FIELD_HEIGHT
            );
            case 7 -> new Level(
                    7,
                    List.of(
                            new Point2D.Double(50, 6),
                            new Point2D.Double(75, 88),
                            new Point2D.Double(10, 35),
                            new Point2D.Double(90, 35),
                            new Point2D.Double(25, 88),
                            new Point2D.Double(61, 67),
                            new Point2D.Double(32, 43),
                            new Point2D.Double(68, 43),
                            new Point2D.Double(39, 67),
                            new Point2D.Double(50, 30)
                    ),
                    Map.ofEntries(
                            Map.entry(0, List.of(1, 4, 5, 6)),
                            Map.entry(1, List.of(2, 6, 7)),
                            Map.entry(2, List.of(3, 7)),
                            Map.entry(3, List.of(4, 8)),
                            Map.entry(4, List.of(9)),
                            Map.entry(5, List.of(6, 9)),
                            Map.entry(6, List.of(7)),
                            Map.entry(7, List.of(8)),
                            Map.entry(8, List.of(9))
                    ),
                    Map.of(
                            0, new FixedMovementStrategy(),
                            6, new HorizontalMovementStrategy(),
                            8, new FixedMovementStrategy()
                    ),
                    FIELD_WIDTH,
                    FIELD_HEIGHT
            );
            case 8 -> new Level(
                    8,
                    List.of(
                            new Point2D.Double(50, 94),
                            new Point2D.Double(6, 50),
                            new Point2D.Double(50, 6),
                            new Point2D.Double(18, 82),
                            new Point2D.Double(82, 18),
                            new Point2D.Double(18, 18),
                            new Point2D.Double(82, 82),
                            new Point2D.Double(94, 50),
                            new Point2D.Double(65, 52),
                            new Point2D.Double(65, 35),
                            new Point2D.Double(50, 50)
                    ),
                    Map.ofEntries(
                            Map.entry(0, List.of(1, 7, 8)),
                            Map.entry(1, List.of(2, 8)),
                            Map.entry(2, List.of(3, 8)),
                            Map.entry(3, List.of(4, 8)),
                            Map.entry(4, List.of(5, 9)),
                            Map.entry(5, List.of(6, 9)),
                            Map.entry(6, List.of(7, 9)),
                            Map.entry(7, List.of(9)),
                            Map.entry(8, List.of(10)),
                            Map.entry(9, List.of(10))
                    ),
                    Map.of(
                            3, new HorizontalMovementStrategy(),
                            10, new FixedMovementStrategy()
                    ),
                    FIELD_WIDTH,
                    FIELD_HEIGHT
            );
            case 9 -> new Level(
                    9,
                    List.of(
                            new Point2D.Double(90, 88),
                            new Point2D.Double(10, 12),
                            new Point2D.Double(37, 88),
                            new Point2D.Double(63, 12),
                            new Point2D.Double(90, 50),
                            new Point2D.Double(10, 50),
                            new Point2D.Double(37, 12),
                            new Point2D.Double(63, 88),
                            new Point2D.Double(90, 12),
                            new Point2D.Double(10, 88),
                            new Point2D.Double(37, 50),
                            new Point2D.Double(63, 50)
                    ),
                    Map.ofEntries(
                            Map.entry(0, List.of(1, 4, 5)),
                            Map.entry(1, List.of(2, 5, 6)),
                            Map.entry(2, List.of(3, 6, 7)),
                            Map.entry(3, List.of(7)),
                            Map.entry(4, List.of(5, 8, 9)),
                            Map.entry(5, List.of(6, 9, 10)),
                            Map.entry(6, List.of(7, 10, 11)),
                            Map.entry(7, List.of(11)),
                            Map.entry(8, List.of(9)),
                            Map.entry(9, List.of(10)),
                            Map.entry(10, List.of(11))
                    ),
                    Map.of(
                            1, new HorizontalMovementStrategy(),
                            4, new HorizontalMovementStrategy(),
                            9, new HorizontalMovementStrategy()
                    ),
                    FIELD_WIDTH,
                    FIELD_HEIGHT
            );
            case 10 -> new Level(
                    10,
                    List.of(
                            new Point2D.Double(50, 95),
                            new Point2D.Double(11, 72),
                            new Point2D.Double(11, 28),
                            new Point2D.Double(50, 5),
                            new Point2D.Double(28, 89),
                            new Point2D.Double(5, 50),
                            new Point2D.Double(28, 11),
                            new Point2D.Double(72, 11),
                            new Point2D.Double(95, 50),
                            new Point2D.Double(72, 89),
                            new Point2D.Double(89, 28),
                            new Point2D.Double(89, 72),
                            new Point2D.Double(50, 50)
                    ),
                    Map.ofEntries(
                            Map.entry(0, List.of(1, 11, 12, 2, 10)),
                            Map.entry(1, List.of(2)),
                            Map.entry(2, List.of(3, 12, 4)),
                            Map.entry(3, List.of(4)),
                            Map.entry(4, List.of(5, 12, 6)),
                            Map.entry(5, List.of(6)),
                            Map.entry(6, List.of(7, 12, 8)),
                            Map.entry(7, List.of(8)),
                            Map.entry(8, List.of(9, 12, 10)),
                            Map.entry(9, List.of(10)),
                            Map.entry(10, List.of(11, 12))
                    ),
                    Map.of(
                            2, new HorizontalMovementStrategy(),
                            10, new HorizontalMovementStrategy(),
                            12, new FixedMovementStrategy()
                    ),
                    FIELD_WIDTH,
                    FIELD_HEIGHT
            );
            default -> throw new IllegalArgumentException("Неизвестный номер уровня: " + number);
        };
    }
}
