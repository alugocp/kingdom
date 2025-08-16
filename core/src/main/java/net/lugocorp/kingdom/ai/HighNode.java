package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.game.model.Unit;
import java.util.List;

/**
 * This interface forms part of the Actor's overall strategy
 */
public interface HighNode {

    /**
     * This method will generate suggested plans for a Unit
     */
    public List<LowNode> suggestLowNodes(Unit u);

    /**
     * Scores a LowNode leaf
     */
    public float scoreNode(LowNode leaf);
}
