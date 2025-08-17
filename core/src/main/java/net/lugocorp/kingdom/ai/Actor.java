package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.ai.goals.ExploreMap;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This class contains all the logic for a non-human (AI) player
 */
public class Actor {
    private final Map<Unit, PlanNode> plans = new HashMap<>();
    private final Set<Goal> goals = new HashSet<>();

    public Actor() {
        this.goals.add(new ExploreMap());
    }

    /**
     * Returns the Plan with the highest score in the given List
     */
    public static Plan getBestPlan(Iterable<Plan> plans) {
        float score = -1f;
        Plan result = null;
        for (Plan p : plans) {
            if (p.score > score) {
                score = p.score;
                result = p;
            }
        }
        return result;
    }

    /**
     * Iterates through our PlanNodes to animate Units under the AI's control
     */
    public void executeUnitPlans(GameView view) {
        for (Map.Entry<Unit, PlanNode> entry : this.plans.entrySet()) {
            PlanNode n = entry.getValue();
            ActionResult result = ActionResult.RIDE;
            while (result == ActionResult.RIDE) {
                result = n.act(view);
                if (result == ActionResult.RIDE || result == ActionResult.POP) {
                    if (n.getChild().isPresent()) {
                        n = n.getChild().get();
                        this.plans.put(entry.getKey(), n);
                    } else {
                        this.plans.remove(entry.getKey());
                    }
                }
                if (result == ActionResult.POP_ALL) {
                    this.plans.remove(entry.getKey());
                }
            }
        }
    }

    /**
     * Ensures that all Units in the Set have an assigned plan
     */
    public void assignUnitPlans(GameView view, Set<Unit> units) {
        for (Unit u : units) {
            if (!this.plans.containsKey(u)) {
                Optional<PlanNode> plan = this.determinePlanNode(view, u);
                plan.ifPresent((PlanNode n) -> this.plans.put(u, n));
            }
        }
    }

    /**
     * This function generates a PlanNode for the given Unit
     */
    private Optional<PlanNode> determinePlanNode(GameView view, Unit u) {
        Set<Plan> options = Lambda.map((Goal n) -> n.suggestPlan(view, u), this.goals);
        return options.size() > 0 ? Optional.of(Actor.getBestPlan(options).root) : Optional.empty();
    }
}
