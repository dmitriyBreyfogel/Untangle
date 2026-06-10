package view.render.node;

import model.movement.FixedMovementStrategy;
import model.movement.FreeMovementStrategy;
import model.core.Game;
import model.movement.HorizontalMovementStrategy;
import model.movement.MovementContext;
import model.movement.MovementStrategy;
import model.core.Node;
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

        assertInstanceOf(DefaultNodeRenderStrategy.class, registry.resolve(FreeMovementStrategy.class));
        assertInstanceOf(FixedNodeRenderStrategy.class, registry.resolve(FixedMovementStrategy.class));
        assertInstanceOf(HorizontalNodeRenderStrategy.class, registry.resolve(HorizontalMovementStrategy.class));
    }

    @Test
    @DisplayName("Реестр возвращает стратегию по умолчанию для неизвестного движения")
    void resolvesUnknownMovementWithDefaultStrategy() {
        NodeRenderStrategy defaultStrategy = new DefaultNodeRenderStrategy();
        NodeRenderStrategyRegistry registry = new NodeRenderStrategyRegistry(defaultStrategy);

        assertSame(defaultStrategy, registry.resolve(CustomMovementStrategy.class));
    }

    @Test
    @DisplayName("Реестр поддерживает регистрацию кастомной стратегии движения")
    void supportsCustomRegistration() {
        NodeRenderStrategyRegistry registry = new NodeRenderStrategyRegistry(new DefaultNodeRenderStrategy());
        NodeRenderStrategy renderStrategy = new EmptyNodeRenderStrategy();

        registry.register(CustomMovementStrategy.class, renderStrategy);

        assertSame(renderStrategy, registry.resolve(CustomMovementStrategy.class));
    }

    @Test
    @DisplayName("Реестр находит стратегию рендера по родительскому типу движения")
    void resolvesAssignableMovementStrategy() {
        NodeRenderStrategyRegistry registry = new NodeRenderStrategyRegistry(new DefaultNodeRenderStrategy());
        NodeRenderStrategy renderStrategy = new EmptyNodeRenderStrategy();

        registry.register(MovementStrategy.class, renderStrategy);

        assertSame(renderStrategy, registry.resolve(CustomMovementStrategy.class));
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
    @DisplayName("Реестр не принимает пустые параметры")
    void rejectsNullArgs() {
        NodeRenderStrategyRegistry registry = new NodeRenderStrategyRegistry(new DefaultNodeRenderStrategy());

        assertThrows(NullPointerException.class, () -> new NodeRenderStrategyRegistry(null));
        assertThrows(NullPointerException.class, () -> registry.register(null, new DefaultNodeRenderStrategy()));
        assertThrows(NullPointerException.class, () -> registry.register(CustomMovementStrategy.class, null));
        assertThrows(NullPointerException.class, () -> registry.resolve((Class<? extends MovementStrategy>) null));
        assertThrows(NullPointerException.class, () -> registry.resolve((Node) null));
    }

    private static final class CustomMovementStrategy extends MovementStrategy {
        @Override
        protected java.awt.geom.Point2D resolveValidatedMove(MovementContext context) {
            return context.requestedPosition();
        }
    }

    private static final class EmptyNodeRenderStrategy extends NodeRenderStrategy {
        @Override
        protected void renderValidated(
                java.awt.Graphics2D graphics,
                java.awt.Point center,
                int radius,
                java.awt.Color color,
                boolean selected
        ) {
        }
    }
}
