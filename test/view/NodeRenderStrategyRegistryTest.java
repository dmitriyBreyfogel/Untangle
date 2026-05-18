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
    @DisplayName("Реестр находит стратегии рендера для стандартных стратегий движения")
    void resolvesDefaultMovementStrategies() {
        NodeRenderStrategyRegistry registry = NodeRenderStrategyRegistry.createDefault();

        assertInstanceOf(DefaultNodeRenderStrategy.class, registry.resolve(new FreeMovementStrategy()));
        assertInstanceOf(FixedNodeRenderStrategy.class, registry.resolve(new FixedMovementStrategy()));
        assertInstanceOf(HorizontalNodeRenderStrategy.class, registry.resolve(new HorizontalMovementStrategy()));
    }

    @Test
    @DisplayName("Реестр возвращает стратегию по умолчанию для неизвестного движения")
    void resolvesUnknownMovementWithDefaultStrategy() {
        NodeRenderStrategy defaultStrategy = new DefaultNodeRenderStrategy();
        NodeRenderStrategyRegistry registry = new NodeRenderStrategyRegistry(defaultStrategy);

        assertSame(defaultStrategy, registry.resolve(new CustomMovementStrategy()));
    }

    @Test
    @DisplayName("Реестр поддерживает регистрацию кастомной стратегии движения")
    void supportsCustomRegistration() {
        NodeRenderStrategyRegistry registry = new NodeRenderStrategyRegistry(new DefaultNodeRenderStrategy());
        NodeRenderStrategy renderStrategy = (graphics, center, radius, color, selected) -> {
        };

        registry.register(CustomMovementStrategy.class, renderStrategy);

        assertSame(renderStrategy, registry.resolve(new CustomMovementStrategy()));
    }

    @Test
    @DisplayName("Реестр находит стратегию рендера по узлу")
    void resolvesStrategyFromNode() {
        NodeRenderStrategyRegistry registry = NodeRenderStrategyRegistry.createDefault();
        Game game = new Game();
        game.start();
        Node node = game.currentLevel().scheme().getNodes().getFirst();

        assertInstanceOf(DefaultNodeRenderStrategy.class, registry.resolve(node));
    }

    @Test
    @DisplayName("Реестр не принимает null параметры")
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
        public java.awt.geom.Point2D resolveMove(java.awt.geom.Point2D currentPosition, java.awt.geom.Point2D requestedPosition) {
            return requestedPosition;
        }
    }
}
