package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.ai.plans.LazyNode;
import net.lugocorp.kingdom.game.model.Unit;

/**
 * This interface forms part of the Actor's overall strategy
 */
public abstract class Goal {

    /**
     * This method may generate a suggested PlanNode for the given Unit
     */
    public abstract Plan suggestPlan(Unit u);

    /**
     * Returns a score value for the given PlanNode
     */
    protected abstract float getScore(PlanNode root);

    /**
     * Wraps a PlanNode in the expected output type for suggestPlan()
     */
    protected final Plan wrapPlanNode(PlanNode n) {
        return new Plan(n, this.getScore(n));
    }

    /**
     * Returns a Plan where the Unit in question does nothing
     */
    protected final Plan emptyPlan(Unit u) {
        return new Plan(new LazyNode(u), 0f);
    }
}
