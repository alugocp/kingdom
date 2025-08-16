package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.game.model.Unit;
import java.util.List;

/**
 * This interface forms part of the Actor's overall strategy
 */
public interface Goal {

    /**
     * This method will generate suggested plans for a Unit
     */
    public List<PlanNode> suggestPlanNodes(Unit u);

    /**
     * Scores a PlanNode leaf
     */
    public float scoreNode(PlanNode leaf);
}
