package model.movement;

import model.core.Node;

import java.util.List;

public interface NodeAwareMovementStrategy extends MovementStrategy {
    void setNodes(List<Node> nodes);
}
