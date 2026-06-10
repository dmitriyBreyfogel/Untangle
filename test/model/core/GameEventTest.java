package model.core;

import model.event.GameEvent;
import model.event.GameFinishedEvent;
import model.event.GameEventListener;
import model.event.GameStartedEvent;
import model.event.LevelCompletedEvent;
import model.event.LevelRestartedEvent;
import model.event.LevelStartedEvent;
import model.event.NodeMovedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameEventTest {
    @Test
    @DisplayName("Старт игры публикует события после загрузки уровня")
    void startPublishesEventsAfterLevelIsLoaded() {
        Game game = new Game();
        List<GameEvent> events = new ArrayList<>();

        game.addEventListener(event -> {
            events.add(event);
            assertTrue(game.started());
            assertNotNull(game.currentLevel());
            assertEquals(1, game.currentLevelNumber());
            assertEquals(0, game.moveCounter());
        });

        game.start();

        assertEventTypes(events, GameStartedEvent.class, LevelStartedEvent.class);
        assertEquals(1, ((GameStartedEvent) events.get(0)).levelNumber());
        assertEquals(1, ((LevelStartedEvent) events.get(1)).levelNumber());
    }

    @Test
    @DisplayName("Старт с выбранного уровня публикует события выбранного уровня после его загрузки")
    void startAtLevelPublishesSelectedLevelEventsAfterLevelIsLoaded() {
        Game game = new Game();
        List<GameEvent> events = new ArrayList<>();

        game.addEventListener(event -> {
            events.add(event);
            assertTrue(game.started());
            assertNotNull(game.currentLevel());
            assertEquals(5, game.currentLevelNumber());
            assertEquals(0, game.moveCounter());
        });

        game.startAtLevel(5);

        assertEventTypes(events, GameStartedEvent.class, LevelStartedEvent.class);
        assertEquals(5, ((GameStartedEvent) events.get(0)).levelNumber());
        assertEquals(5, ((LevelStartedEvent) events.get(1)).levelNumber());
    }

    @Test
    @DisplayName("Старт с неизвестного уровня не публикует события и не меняет состояние игры")
    void startAtUnknownLevelPublishesNoEventsAndLeavesGameStopped() {
        Game game = new Game();
        List<GameEvent> events = new ArrayList<>();
        game.addEventListener(events::add);

        assertThrows(IllegalArgumentException.class, () -> game.startAtLevel(999));

        assertTrue(events.isEmpty());
        assertFalse(game.started());
        assertNull(game.currentLevel());
        assertEquals(0, game.moveCounter());
        assertEquals(0, game.maxCompletedLevelNumber());
    }

    @Test
    @DisplayName("Продолжение игры публикует события после загрузки доступного уровня")
    void continuePublishesEventsAfterUnlockedLevelIsLoaded() {
        Game game = startedGame();
        game.moveNode(game.currentLevel().scheme().getNodes().get(1), point(90, 5));
        game.finish();
        List<GameEvent> events = new ArrayList<>();

        game.addEventListener(event -> {
            events.add(event);
            assertTrue(game.started());
            assertNotNull(game.currentLevel());
            assertEquals(2, game.currentLevelNumber());
            assertEquals(0, game.moveCounter());
        });

        game.continueGame();

        assertEventTypes(events, GameStartedEvent.class, LevelStartedEvent.class);
        assertEquals(2, ((GameStartedEvent) events.get(0)).levelNumber());
        assertEquals(2, ((LevelStartedEvent) events.get(1)).levelNumber());
    }

    @Test
    @DisplayName("Действия без изменения состояния не публикуют события")
    void noOpActionsPublishNoEvents() {
        Game game = new Game();
        List<GameEvent> events = new ArrayList<>();
        game.addEventListener(events::add);

        game.continueGame();
        game.restartCurrentLevel();
        game.finish();

        assertTrue(events.isEmpty());

        game.start();
        events.clear();
        game.start();
        game.startAtLevel(2);
        game.continueGame();

        assertTrue(events.isEmpty());
    }

    @Test
    @DisplayName("Повторный старт активной игры не публикует события и не заменяет текущий уровень")
    void startedGameActionsPublishNoEventsAndKeepCurrentState() {
        Game game = startedGame();
        Object levelBefore = game.currentLevel();
        int levelNumberBefore = game.currentLevelNumber();
        int maxCompletedBefore = game.maxCompletedLevelNumber();
        int moveCounterBefore = game.moveCounter();
        List<GameEvent> events = new ArrayList<>();
        game.addEventListener(events::add);

        game.start();
        game.startAtLevel(2);
        game.continueGame();

        assertTrue(events.isEmpty());
        assertTrue(game.started());
        assertSame(levelBefore, game.currentLevel());
        assertEquals(levelNumberBefore, game.currentLevelNumber());
        assertEquals(maxCompletedBefore, game.maxCompletedLevelNumber());
        assertEquals(moveCounterBefore, game.moveCounter());
    }

    @Test
    @DisplayName("Обычный ход публикует перемещение после изменения узла и счётчика")
    void regularMovePublishesNodeMovedAfterStateChange() {
        Game game = startedGame();
        Node node = game.currentLevel().scheme().getNodes().getFirst();
        List<GameEvent> events = new ArrayList<>();

        game.addEventListener(event -> {
            events.add(event);
            NodeMovedEvent nodeMovedEvent = assertInstanceOf(NodeMovedEvent.class, event);
            assertEquals(1, game.currentLevelNumber());
            assertEquals(1, game.moveCounter());
            assertEquals(node, nodeMovedEvent.node());
            assertEquals(10, nodeMovedEvent.previousX());
            assertEquals(10, nodeMovedEvent.previousY());
            assertEquals(12, nodeMovedEvent.currentX());
            assertEquals(12, nodeMovedEvent.currentY());
            assertEquals(12, node.getX());
            assertEquals(12, node.getY());
        });

        boolean moved = game.moveNode(node, point(12, 12));

        assertTrue(moved);
        assertEventTypes(events, NodeMovedEvent.class);
    }

    @Test
    @DisplayName("Недопустимый ход не публикует события")
    void invalidMovePublishesNoEvents() {
        Game game = startedGame();
        Node node = game.currentLevel().scheme().getNodes().getFirst();
        double xBefore = node.getX();
        double yBefore = node.getY();
        int moveCounterBefore = game.moveCounter();
        int levelNumberBefore = game.currentLevelNumber();
        List<GameEvent> events = new ArrayList<>();
        game.addEventListener(events::add);

        boolean moved = game.moveNode(node, point(-1, 0));

        assertFalse(moved);
        assertTrue(events.isEmpty());
        assertEquals(xBefore, node.getX());
        assertEquals(yBefore, node.getY());
        assertEquals(moveCounterBefore, game.moveCounter());
        assertEquals(levelNumberBefore, game.currentLevelNumber());
    }

    @Test
    @DisplayName("Ход до старта игры не публикует события")
    void moveBeforeStartPublishesNoEvents() {
        Game game = new Game();
        Node node = new Node(point(0, 0));
        List<GameEvent> events = new ArrayList<>();
        game.addEventListener(events::add);

        boolean moved = game.moveNode(node, point(1, 1));

        assertFalse(moved);
        assertTrue(events.isEmpty());
        assertFalse(game.started());
        assertNull(game.currentLevel());
    }

    @Test
    @DisplayName("Ход без фактического изменения позиции не публикует события")
    void unchangedResolvedMovePublishesNoEvents() {
        Game game = startedGame();
        Node fixedNode = game.currentLevel().scheme().getNodes().get(2);
        List<GameEvent> events = new ArrayList<>();
        game.addEventListener(events::add);

        boolean moved = game.moveNode(fixedNode, point(30, 40));

        assertFalse(moved);
        assertTrue(events.isEmpty());
        assertEquals(0, game.moveCounter());
        assertEquals(10, fixedNode.getX());
        assertEquals(90, fixedNode.getY());
    }

    @Test
    @DisplayName("Победный ход публикует перемещение, прохождение и старт следующего уровня в правильном порядке")
    void winningMovePublishesEventsInOrder() {
        Game game = startedGame();
        Node node = game.currentLevel().scheme().getNodes().get(1);
        List<GameEvent> events = new ArrayList<>();

        game.addEventListener(event -> {
            events.add(event);
            if (event instanceof NodeMovedEvent nodeMovedEvent) {
                assertEquals(1, game.currentLevelNumber());
                assertEquals(1, game.moveCounter());
                assertEquals(node, nodeMovedEvent.node());
                assertEquals(90, node.getX());
                assertEquals(5, node.getY());
                return;
            }
            if (event instanceof LevelCompletedEvent levelCompletedEvent) {
                assertEquals(1, levelCompletedEvent.levelNumber());
                assertEquals(1, levelCompletedEvent.maxCompletedLevelNumber());
                assertFalse(levelCompletedEvent.lastLevel());
                assertEquals(1, game.currentLevelNumber());
                assertEquals(1, game.maxCompletedLevelNumber());
                assertEquals(1, game.moveCounter());
                assertTrue(game.currentLevel().isCompleted());
                return;
            }
            LevelStartedEvent levelStartedEvent = assertInstanceOf(LevelStartedEvent.class, event);
            assertEquals(2, levelStartedEvent.levelNumber());
            assertEquals(2, game.currentLevelNumber());
            assertEquals(0, game.moveCounter());
            assertNotNull(game.currentLevel());
        });

        boolean moved = game.moveNode(node, point(90, 5));

        assertTrue(moved);
        assertEventTypes(events, NodeMovedEvent.class, LevelCompletedEvent.class, LevelStartedEvent.class);
    }

    @Test
    @DisplayName("Победный ход на последнем уровне завершает игру после события прохождения")
    void winningLastLevelPublishesGameFinishedAfterLevelCompleted() {
        Game game = new Game();
        game.startAtLevel(10);
        List<GameEvent> events = new ArrayList<>();

        game.addEventListener(event -> {
            events.add(event);
            if (event instanceof LevelCompletedEvent levelCompletedEvent) {
                assertEquals(10, levelCompletedEvent.levelNumber());
                assertEquals(10, levelCompletedEvent.maxCompletedLevelNumber());
                assertTrue(levelCompletedEvent.lastLevel());
                assertTrue(game.started());
                assertNotNull(game.currentLevel());
                assertEquals(12, game.moveCounter());
                assertTrue(game.currentLevel().isCompleted());
            }
            if (event instanceof NodeMovedEvent nodeMovedEvent) {
                assertEquals(events.size(), nodeMovedEvent.moveCounter());
                assertEquals(nodeMovedEvent.moveCounter(), game.moveCounter());
                assertEquals(nodeMovedEvent.currentX(), nodeMovedEvent.node().getX());
                assertEquals(nodeMovedEvent.currentY(), nodeMovedEvent.node().getY());
            }
            if (event instanceof GameFinishedEvent gameFinishedEvent) {
                assertEquals(10, gameFinishedEvent.maxCompletedLevelNumber());
                assertFalse(game.started());
                assertNull(game.currentLevel());
                assertEquals(0, game.moveCounter());
            }
        });

        solveLevel10(game);

        assertFalse(game.started());
        assertEventTypes(events, repeatedNodeMovedEventsThen(12, LevelCompletedEvent.class, GameFinishedEvent.class));
        assertInstanceOf(LevelCompletedEvent.class, events.get(events.size() - 2));
        assertInstanceOf(GameFinishedEvent.class, events.getLast());
    }

    @Test
    @DisplayName("Перезапуск уровня публикует событие после сброса состояния")
    void restartPublishesEventAfterStateReset() {
        Game game = startedGame();
        game.moveNode(game.currentLevel().scheme().getNodes().getFirst(), point(12, 12));
        Object previousLevel = game.currentLevel();
        List<GameEvent> events = new ArrayList<>();

        game.addEventListener(event -> {
            events.add(event);
            LevelRestartedEvent levelRestartedEvent = assertInstanceOf(LevelRestartedEvent.class, event);
            assertEquals(1, levelRestartedEvent.levelNumber());
            assertEquals(1, game.currentLevelNumber());
            assertEquals(0, game.moveCounter());
            assertNotSame(previousLevel, game.currentLevel());
            assertEquals(10, game.currentLevel().scheme().getNodes().getFirst().getX());
            assertEquals(10, game.currentLevel().scheme().getNodes().getFirst().getY());
        });

        game.restartCurrentLevel();

        assertEventTypes(events, LevelRestartedEvent.class);
        assertEquals(1, game.currentLevelNumber());
        assertEquals(0, game.moveCounter());
        assertNotSame(previousLevel, game.currentLevel());
    }

    @Test
    @DisplayName("Завершение игры публикует событие после очистки состояния")
    void finishPublishesEventAfterStateIsCleared() {
        Game game = startedGame();
        List<GameEvent> events = new ArrayList<>();

        game.addEventListener(event -> {
            events.add(event);
            GameFinishedEvent gameFinishedEvent = assertInstanceOf(GameFinishedEvent.class, event);
            assertEquals(0, gameFinishedEvent.maxCompletedLevelNumber());
            assertFalse(game.started());
            assertNull(game.currentLevel());
            assertEquals(0, game.moveCounter());
        });

        game.finish();

        assertEventTypes(events, GameFinishedEvent.class);
    }

    @Test
    @DisplayName("Завершение игры публикует событие с уже сохранённым прогрессом")
    void finishPublishesCompletedProgressAfterStateIsCleared() {
        Game game = startedGame();
        game.moveNode(game.currentLevel().scheme().getNodes().get(1), point(90, 5));
        List<GameEvent> events = new ArrayList<>();

        game.addEventListener(event -> {
            events.add(event);
            GameFinishedEvent gameFinishedEvent = assertInstanceOf(GameFinishedEvent.class, event);
            assertEquals(1, gameFinishedEvent.maxCompletedLevelNumber());
            assertEquals(1, game.maxCompletedLevelNumber());
            assertFalse(game.started());
            assertNull(game.currentLevel());
            assertEquals(0, game.moveCounter());
        });

        game.finish();

        assertEventTypes(events, GameFinishedEvent.class);
    }

    @Test
    @DisplayName("Слушатели событий вызываются в порядке регистрации")
    void listenersAreCalledInRegistrationOrder() {
        Game game = new Game();
        List<String> calls = new ArrayList<>();

        game.addEventListener(event -> calls.add("first:" + event.getClass().getSimpleName()));
        game.addEventListener(event -> calls.add("second:" + event.getClass().getSimpleName()));

        game.start();

        assertEquals(List.of(
                "first:GameStartedEvent",
                "second:GameStartedEvent",
                "first:LevelStartedEvent",
                "second:LevelStartedEvent"
        ), calls);
    }

    @Test
    @DisplayName("Удалённый слушатель событий больше не вызывается")
    void removedListenerIsNotCalled() {
        Game game = new Game();
        List<GameEvent> events = new ArrayList<>();
        GameEventListener listener = events::add;

        game.addEventListener(listener);
        game.removeEventListener(listener);

        game.start();

        assertTrue(events.isEmpty());
    }

    @Test
    @DisplayName("Слушатель, удалённый во время публикации, получает текущее событие, но не получает следующие")
    void listenerRemovedDuringPublicationReceivesCurrentEventOnly() {
        Game game = new Game();
        List<String> calls = new ArrayList<>();
        GameEventListener secondListener = event -> calls.add("second:" + event.getClass().getSimpleName());

        game.addEventListener(event -> {
            calls.add("first:" + event.getClass().getSimpleName());
            game.removeEventListener(secondListener);
        });
        game.addEventListener(secondListener);

        game.start();

        assertEquals(List.of(
                "first:GameStartedEvent",
                "second:GameStartedEvent",
                "first:LevelStartedEvent"
        ), calls);
    }

    @Test
    @DisplayName("Слушатель, добавленный во время публикации, начинает получать только следующие события")
    void listenerAddedDuringPublicationReceivesOnlyNextEvents() {
        Game game = new Game();
        List<String> calls = new ArrayList<>();
        GameEventListener addedListener = event -> calls.add("added:" + event.getClass().getSimpleName());

        game.addEventListener(event -> {
            calls.add("first:" + event.getClass().getSimpleName());
            if (event instanceof GameStartedEvent) {
                game.addEventListener(addedListener);
            }
        });

        game.start();

        assertEquals(List.of(
                "first:GameStartedEvent",
                "first:LevelStartedEvent",
                "added:LevelStartedEvent"
        ), calls);
    }

    @Test
    @DisplayName("События отклоняют некорректное состояние при создании")
    void eventRecordsRejectInvalidState() {
        Node node = new Node(point(0, 0));

        assertThrows(IllegalArgumentException.class, () -> new GameStartedEvent(0));
        assertThrows(IllegalArgumentException.class, () -> new LevelStartedEvent(0));
        assertThrows(IllegalArgumentException.class, () -> new LevelRestartedEvent(0));
        assertThrows(NullPointerException.class, () -> new NodeMovedEvent(1, null, 0, 0, 1, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new NodeMovedEvent(0, node, 0, 0, 1, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new NodeMovedEvent(1, node, Double.NaN, 0, 1, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new NodeMovedEvent(1, node, 0, Double.NaN, 1, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new NodeMovedEvent(1, node, 0, 0, Double.NaN, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new NodeMovedEvent(1, node, 0, 0, 1, Double.POSITIVE_INFINITY, 1));
        assertThrows(IllegalArgumentException.class, () -> new NodeMovedEvent(1, node, 0, 0, 1, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new LevelCompletedEvent(0, 0, false));
        assertThrows(IllegalArgumentException.class, () -> new LevelCompletedEvent(2, 1, false));
        assertThrows(IllegalArgumentException.class, () -> new GameFinishedEvent(-1));
    }

    private static Game startedGame() {
        Game game = new Game();
        game.start();
        return game;
    }

    private static void solveLevel10(Game game) {
        Point2D[] positions = {
                point(50, 5),
                point(72, 11),
                point(89, 28),
                point(95, 50),
                point(89, 72),
                point(72, 89),
                point(50, 95),
                point(28, 89),
                point(11, 72),
                point(5, 50),
                point(11, 28),
                point(28, 11),
                point(50, 50)
        };

        int levelNumber = game.currentLevelNumber();
        Scheme scheme = game.currentLevel().scheme();
        for (int i = 0; i < positions.length && game.started() && game.currentLevelNumber() == levelNumber; i++) {
            game.moveNode(scheme.getNodes().get(i), positions[i]);
        }
    }

    private static void assertEventTypes(List<GameEvent> events, Class<?>... eventTypes) {
        assertEquals(eventTypes.length, events.size());
        for (int i = 0; i < eventTypes.length; i++) {
            assertEquals(eventTypes[i], events.get(i).getClass());
        }
    }

    private static Class<?>[] repeatedNodeMovedEventsThen(int nodeMovedEventCount, Class<?>... tailEventTypes) {
        Class<?>[] eventTypes = new Class<?>[nodeMovedEventCount + tailEventTypes.length];
        Arrays.fill(eventTypes, 0, nodeMovedEventCount, NodeMovedEvent.class);
        System.arraycopy(tailEventTypes, 0, eventTypes, nodeMovedEventCount, tailEventTypes.length);
        return eventTypes;
    }

    private static Point2D point(double x, double y) {
        return new Point2D.Double(x, y);
    }
}
