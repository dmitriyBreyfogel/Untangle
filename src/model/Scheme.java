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
    private final List<Point2D.Double> initialNodeCoordinates;
    private final Map<Integer, List<Integer>> nodeConnections;

    private final List<Node> nodes;
    private final List<Edge> edges;

    private GameField gameField;
    private Game game;

    private Scheme(List<Point2D> initialNodeCoordinates, Map<Integer, List<Integer>> nodeConnections) {
        Objects.requireNonNull(initialNodeCoordinates, "initialNodeCoordinates");
        List<Point2D.Double> copies = new ArrayList<>(initialNodeCoordinates.size());
        for (Point2D p : initialNodeCoordinates) {
            copies.add(copyOf(p, "initialNodeCoordinates"));
        }
        this.initialNodeCoordinates = List.copyOf(copies);
        this.nodeConnections = deepCopy(Objects.requireNonNull(nodeConnections, "nodeConnections"));

        if (this.initialNodeCoordinates.size() < 3) {
            throw new IllegalArgumentException("Scheme must contain at least 3 nodes.");
        }

        this.nodes = new ArrayList<>(this.initialNodeCoordinates.size());
        for (Point2D p : this.initialNodeCoordinates) {
            nodes.add(new Node(p));
        }

        this.edges = buildEdges();

        updateSchemeGeometry();
        updateIntersections();
        verifyNoIsolatedNodes();
    }

    public static Scheme create(List<Point2D> initialNodeCoordinates, Map<Integer, List<Integer>> nodeConnections) {
        return new Scheme(initialNodeCoordinates, nodeConnections);
    }

    public void reset() {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).moveTo(initialNodeCoordinates.get(i));
        }
        updateSchemeGeometry();
        updateIntersections();
    }

    public void moveNode(Node node, Point2D destination) {
        Objects.requireNonNull(node, "node");
        Objects.requireNonNull(destination, "destination");

        if (gameField != null && !gameField.canPlace(destination)) {
            return;
        }

        node.moveTo(destination);
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

    List<Node> getNodes() {
        return List.copyOf(nodes);
    }

    List<Edge> getEdges() {
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

        Set<Long> uniqueEdges = new HashSet<>();
        List<Edge> result = new ArrayList<>();

        for (Map.Entry<Integer, List<Integer>> entry : nodeConnections.entrySet()) {
            int from = entry.getKey();
            if (from < 0 || from >= nodeCount) {
                throw new IllegalArgumentException("Node index out of range: " + from);
            }
            for (int to : entry.getValue()) {
                if (to < 0 || to >= nodeCount) {
                    throw new IllegalArgumentException("Node index out of range: " + to);
                }
                if (to == from) {
                    throw new IllegalArgumentException("Node cannot connect to itself: " + from);
                }
                int a = Math.min(from, to);
                int b = Math.max(from, to);
                long key = (((long) a) << 32) | (b & 0xffffffffL);
                if (uniqueEdges.add(key)) {
                    result.add(new Edge(nodes.get(a), nodes.get(b)));
                }
            }
        }

        if (result.size() < 3) {
            throw new IllegalArgumentException("Scheme must contain at least 3 edges.");
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
                throw new IllegalArgumentException("Node " + i + " has no connections.");
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
            throw new IllegalArgumentException(paramName + " must contain only finite coordinates.");
        }
        return new Point2D.Double(x, y);
    }
}
