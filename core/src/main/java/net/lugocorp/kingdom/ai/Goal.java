package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.ai.plans.LazyNode;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This interface forms part of the Actor's overall strategy
 */
public abstract class Goal {

    /**
     * This method may generate a suggested PlanNode for the given Unit
     */
    public abstract Plan suggestPlan(GameView view, Unit u);

    /**
     * Returns a score value for the given PlanNode
     */
    protected abstract float getScore(GameView view, PlanNode root);

    /**
     * Wraps a PlanNode in the expected output type for suggestPlan()
     */
    protected final Plan wrapPlanNode(GameView view, PlanNode n) {
        return new Plan(n, this.getScore(view, n));
    }

    /**
     * Returns a Plan where the Unit in question does nothing
     */
    protected final Plan emptyPlan(Unit u) {
        return new Plan(new LazyNode(u), 0f);
    }
}
