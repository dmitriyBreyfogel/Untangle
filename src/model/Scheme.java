package model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class Scheme {
    private static final MovementStrategy DEFAULT_MOVEMENT_STRATEGY = new FreeMovementStrategy();

    private final List<Point2D.Double> initialNodeCoordinates;
    private final Map<Integer, List<Integer>> nodeConnections;
    private final Map<Integer, MovementStrategy> nodeMovementStrategies;

    private final List<Node> nodes;
    private final List<Edge> edges;

    private GameField gameField;
    private Game game;

    private Scheme(
            List<Point2D> initialNodeCoordinates,
            Map<Integer, List<Integer>> nodeConnections,
            Map<Integer, MovementStrategy> nodeMovementStrategies
    ) {
        Objects.requireNonNull(initialNodeCoordinates, "initialNodeCoordinates");
        List<Point2D.Double> copies = new ArrayList<>(initialNodeCoordinates.size());
        for (Point2D p : initialNodeCoordinates) {
            copies.add(copyOf(p, "initialNodeCoordinates"));
        }
        this.initialNodeCoordinates = List.copyOf(copies);
        this.nodeConnections = deepCopy(Objects.requireNonNull(nodeConnections, "nodeConnections"));
        this.nodeMovementStrategies = Map.copyOf(Objects.requireNonNull(nodeMovementStrategies, "nodeMovementStrategies"));

        if (this.initialNodeCoordinates.size() < 3) {
            throw new IllegalArgumentException("Смеха должна состоять минимум из трёх узлов");
        }
        verifyMovementStrategyIndexes();

        this.nodes = new ArrayList<>(this.initialNodeCoordinates.size());
        for (int i = 0; i < this.initialNodeCoordinates.size(); i++) {
            MovementStrategy movementStrategy = this.nodeMovementStrategies.getOrDefault(i, DEFAULT_MOVEMENT_STRATEGY);
            nodes.add(new Node(this.initialNodeCoordinates.get(i), movementStrategy));
        }

        this.edges = buildEdges();

        updateSchemeGeometry();
        updateIntersections();
        verifyNoIsolatedNodes();
    }

    public static Scheme create(List<Point2D> initialNodeCoordinates, Map<Integer, List<Integer>> nodeConnections) {
        return create(initialNodeCoordinates, nodeConnections, Map.of());
    }

    public static Scheme create(
            List<Point2D> initialNodeCoordinates,
            Map<Integer, List<Integer>> nodeConnections,
            Map<Integer, MovementStrategy> nodeMovementStrategies
    ) {
        return new Scheme(initialNodeCoordinates, nodeConnections, nodeMovementStrategies);
    }

    public void reset() {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).moveDirectlyTo(initialNodeCoordinates.get(i));
        }
        updateSchemeGeometry();
        updateIntersections();
    }

    public void moveNode(Node node, Point2D destination) {
        Objects.requireNonNull(node, "node");
        Objects.requireNonNull(destination, "destination");

        Point2D resolvedDestination = node.resolveMove(destination);
        if (gameField != null && !gameField.canPlace(resolvedDestination)) {
            return;
        }
        if (node.isAt(resolvedDestination)) {
            return;
        }

        node.moveDirectlyTo(resolvedDestination);
        updateIncidentEdges(node);
        updateIntersections();

        if (game != null) {
            game.incrementMoveCounter();
            game.validateMove();
        }
    }

    public List<Edge> getIntersectingEdges() {
        List<Edge> result = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.isIntersecting()) {
                result.add(edge);
            }
        }
        return List.copyOf(result);
    }

    public boolean hasIntersections() {
        for (Edge edge : edges) {
            if (edge.isIntersecting()) {
                return true;
            }
        }
        return false;
    }

    public List<Edge> getEdgesOfNode(Node node) {
        Objects.requireNonNull(node, "node");
        List<Edge> result = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.containsNode(node)) {
                result.add(edge);
            }
        }
        return List.copyOf(result);
    }

    public List<Node> getNodes() {
        return List.copyOf(nodes);
    }

    public List<Edge> getEdges() {
        return List.copyOf(edges);
    }

    void bindGameField(GameField gameField) {
        this.gameField = Objects.requireNonNull(gameField, "gameField");
    }

    void bindGame(Game game) {
        this.game = Objects.requireNonNull(game, "game");
    }

    private void updateIncidentEdges(Node node) {
        List<Edge> incidentEdges = getEdgesOfNode(node);
        for (Edge edge : incidentEdges) {
            edge.updateGeometry();
        }
    }

    private void updateSchemeGeometry() {
        List<Node> nodes = getNodes();
        for (Node node : nodes) {
            updateIncidentEdges(node);
        }
    }

    private void updateIntersections() {
        for (Edge edge : edges) {
            edge.setIntersecting(false);
        }

        for (int i = 0; i < edges.size(); i++) {
            for (int j = i + 1; j < edges.size(); j++) {
                Edge a = edges.get(i);
                Edge b = edges.get(j);
                if (a.intersects(b)) {
                    a.setIntersecting(true);
                    b.setIntersecting(true);
                }
            }
        }
    }

    private List<Edge> buildEdges() {
        int nodeCount = nodes.size();

        Set<String> uniqueEdges = new HashSet<>();
        List<Edge> result = new ArrayList<>();

        for (Map.Entry<Integer, List<Integer>> entry : nodeConnections.entrySet()) {
            int from = entry.getKey();
            if (from < 0 || from >= nodeCount) {
                throw new IllegalArgumentException("Индекс узла вышел из диапазона списка: " + from);
            }
            for (int to : entry.getValue()) {
                if (to < 0 || to >= nodeCount) {
                    throw new IllegalArgumentException("Индекс узла вышел из диапазона списка: " + to);
                }
                if (to == from) {
                    throw new IllegalArgumentException("Узел не может соединиться с самим собой: " + from);
                }
                int a = Math.min(from, to);
                int b = Math.max(from, to);
                String key = a + ":" + b;
                if (uniqueEdges.add(key)) {
                    result.add(new Edge(nodes.get(a), nodes.get(b)));
                }
            }
        }

        if (result.size() < 3) {
            throw new IllegalArgumentException("Схема должна содержать минимум три грани");
        }

        return result;
    }

    private void verifyNoIsolatedNodes() {
        Map<Node, Integer> indexByNode = new IdentityHashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            indexByNode.put(nodes.get(i), i);
        }

        int[] degree = new int[nodes.size()];
        for (Edge edge : edges) {
            Integer a = indexByNode.get(edge.getNodeA());
            Integer b = indexByNode.get(edge.getNodeB());
            if (a != null) degree[a]++;
            if (b != null) degree[b]++;
        }
        for (int i = 0; i < degree.length; i++) {
            if (degree[i] <= 0) {
                throw new IllegalArgumentException("Узел " + i + " не имеет соединений");
            }
        }
    }

    private void verifyMovementStrategyIndexes() {
        int nodeCount = initialNodeCoordinates.size();
        for (int nodeIndex : nodeMovementStrategies.keySet()) {
            if (nodeIndex < 0 || nodeIndex >= nodeCount) {
                throw new IllegalArgumentException("Индекс стратегии движения вышел из диапазона списка: " + nodeIndex);
            }
        }
    }

    private static Map<Integer, List<Integer>> deepCopy(Map<Integer, List<Integer>> map) {
        Map<Integer, List<Integer>> copy = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            copy.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return Map.copyOf(copy);
    }

    private static Point2D.Double copyOf(Point2D p, String paramName) {
        Objects.requireNonNull(p, paramName);
        double x = p.getX();
        double y = p.getY();
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException(paramName + " должен содержать конечное значение");
        }
        return new Point2D.Double(x, y);
    }
}
