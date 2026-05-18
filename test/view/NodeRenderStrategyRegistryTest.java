package view;

import model.FixedMovementStrategy;
import model.FreeMovementStrategy;
import model.Game;
import model.HorizontalMovementStrategy;
import model.MovementStrategy;
import model.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NodeRenderStrategyRegistryTest {
    @Test
    @DisplayName("Registry resolves default movement strategies")
    void resolvesDefaultMovementStrategies() {
        NodeRenderStrategyRegistry registry = NodeRenderStrategyRegistry.createDefault();

        assertInstanceOf(DefaultNodeRenderStrategy.class, registry.resolve(new FreeMovementStrategy()));
        assertInstanceOf(FixedNodeRenderStrategy.class, registry.resolve(new FixedMovementStrategy()));
        assertInstanceOf(HorizontalNodeRenderStrategy.class, registry.resolve(new HorizontalMovementStrategy()));
    }

    @Test
    @DisplayName("Registry returns default strategy for unknown movement")
    void resolvesUnknownMovementWithDefaultStrategy() {
        NodeRenderStrategy defaultStrategy = new DefaultNodeRenderStrategy();
        NodeRenderStrategyRegistry registry = new NodeRenderStrategyRegistry(defaultStrategy);

        assertSame(defaultStrategy, registry.resolve(new CustomMovementStrategy()));
    }

    @Test
    @DisplayName("Registry supports custom movement strategy registration")
    void supportsCustomRegistration() {
        NodeRenderStrategyRegistry registry = new NodeRenderStrategyRegistry(new DefaultNodeRenderStrategy());
        NodeRenderStrategy renderStrategy = (graphics, center, radius, color, selected) -> {
        };

        registry.register(CustomMovementStrategy.class, renderStrategy);

        assertSame(renderStrategy, registry.resolve(new CustomMovementStrategy()));
    }

    @Test
    @DisplayName("Registry resolves strategy from node")
    void resolvesStrategyFromNode() {
        NodeRenderStrategyRegistry registry = NodeRenderStrategyRegistry.createDefault();
        Game game = new Game();
        game.start();
        Node node = game.currentLevel().scheme().getNodes().getFirst();

        assertInstanceOf(DefaultNodeRenderStrategy.class, registry.resolve(node));
    }

    @Test
    @DisplayName("Registry rejects null parameters")
    void rejectsNullArgs() {
        NodeRenderStrategyRegistry registry = new NodeRenderStrategyRegistry(new DefaultNodeRenderStrategy());

        assertThrows(NullPointerException.class, () -> new NodeRenderStrategyRegistry(null));
        assertThrows(NullPointerException.class, () -> registry.register(null, new DefaultNodeRenderStrategy()));
        assertThrows(NullPointerException.class, () -> registry.register(CustomMovementStrategy.class, null));
        assertThrows(NullPointerException.class, () -> registry.resolve((MovementStrategy) null));
        assertThrows(NullPointerException.class, () -> registry.resolve((Node) null));
    }

    private static final class CustomMovementStrategy implements MovementStrategy {
        @Override
        public java.awt.geom.Point2D resolveMove(Node node, java.awt.geom.Point2D requestedPosition) {
            return requestedPosition;
        }
    }
}
