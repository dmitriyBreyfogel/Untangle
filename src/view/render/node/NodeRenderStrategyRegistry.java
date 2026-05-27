package view.render.node;

import model.movement.FixedMovementStrategy;
import model.movement.FreeMovementStrategy;
import model.movement.HorizontalMovementStrategy;
import model.movement.MovementStrategy;
import model.core.Node;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class NodeRenderStrategyRegistry {
    private final Map<Class<? extends MovementStrategy>, NodeRenderStrategy> renderStrategies = new LinkedHashMap<>();
    private final NodeRenderStrategy defaultRenderStrategy;

    public NodeRenderStrategyRegistry(NodeRenderStrategy defaultRenderStrategy) {
        this.defaultRenderStrategy = Objects.requireNonNull(defaultRenderStrategy, "defaultRenderStrategy");
    }

    public static NodeRenderStrategyRegistry createDefault() {
        NodeRenderStrategyRegistry registry = new NodeRenderStrategyRegistry(new DefaultNodeRenderStrategy());
        registry.register(FreeMovementStrategy.class, new DefaultNodeRenderStrategy());
        registry.register(FixedMovementStrategy.class, new FixedNodeRenderStrategy());
        registry.register(HorizontalMovementStrategy.class, new HorizontalNodeRenderStrategy());
        return registry;
    }

    public void register(Class<? extends MovementStrategy> movementStrategyType, NodeRenderStrategy renderStrategy) {
        renderStrategies.put(
                Objects.requireNonNull(movementStrategyType, "movementStrategyType"),
                Objects.requireNonNull(renderStrategy, "renderStrategy")
        );
    }

    public NodeRenderStrategy resolve(Node node) {
        Objects.requireNonNull(node, "node");
        return resolve(node.getMovementStrategy());
    }

    public NodeRenderStrategy resolve(MovementStrategy movementStrategy) {
        Objects.requireNonNull(movementStrategy, "movementStrategy");
        NodeRenderStrategy exactStrategy = renderStrategies.get(movementStrategy.getClass());
        if (exactStrategy != null) {
            return exactStrategy;
        }

        for (Map.Entry<Class<? extends MovementStrategy>, NodeRenderStrategy> entry : renderStrategies.entrySet()) {
            if (entry.getKey().isAssignableFrom(movementStrategy.getClass())) {
                return entry.getValue();
            }
        }
        return defaultRenderStrategy;
    }
}
