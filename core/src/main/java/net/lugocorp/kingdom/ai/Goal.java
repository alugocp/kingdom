package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.ai.plans.LazyNode;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Optional;

/**
 * This interface forms part of the Actor's overall strategy
 */
public abstract class Goal {

    /**
     * This method may generate a suggested PlanNode for the given Unit
     */
    public abstract Optional<Plan> suggestPlan(GameView view, Unit u);

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

    /**
     * Returns the Plan with the highest score in the given List, and filters out
     * any Plans with a score of 0
     */
    protected final Optional<Plan> getBestPlan(Iterable<Plan> plans) {
        float score = 0f;
        Plan result = null;
        for (Plan p : plans) {
            if (p.score > score) {
                score = p.score;
                result = p;
            }
        }
        return score == 0f ? Optional.empty() : Optional.of(result);
    }
}
