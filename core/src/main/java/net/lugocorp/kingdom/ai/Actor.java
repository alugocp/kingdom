package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.ai.goals.ExploreMap;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This class contains all the logic for a non-human (AI) player
 */
public class Actor {
    private final Map<Unit, PlanNode> unitPlans = new HashMap<>();
    private final Set<Goal> overall = new HashSet<>();

    public Actor() {
        overall.add(new ExploreMap());
    }

    /**
     * Iterates through our PlanNodes to animate Units under the AI's control
     */
    public void executeUnitPlans(GameView view) {
        for (Map.Entry<Unit, PlanNode> entry : this.unitPlans.entrySet()) {
            PlanNode n = entry.getValue();
            ActionResult result = ActionResult.RIDE;
            while (result == ActionResult.RIDE) {
                result = n.act(view);
                if (result == ActionResult.RIDE || result == ActionResult.POP) {
                    if (n.getChild().isPresent()) {
                        n = n.getChild().get();
                        this.unitPlans.put(entry.getKey(), n);
                    } else {
                        this.unitPlans.remove(entry.getKey());
                    }
                }
                if (result == ActionResult.POP_ALL) {
                    this.unitPlans.remove(entry.getKey());
                }
            }
        }
    }

    /**
     * Ensures that all Units in the Set have an assigned plan
     */
    public void assignUnitPlans(Set<Unit> units) {
        for (Unit u : units) {
            if (!this.unitPlans.containsKey(u)) {
                Optional<PlanNode> plan = this.createUnitPlan(u);
                plan.ifPresent((PlanNode n) -> this.unitPlans.put(u, n));
            }
        }
    }

    /**
     * This function generates a plan for the given Unit
     */
    private Optional<PlanNode> createUnitPlan(Unit u) {
        // Suggest some actions that align with our overall goals
        List<PlanNode> heads = Lambda.flatMap(Lambda.map((Goal n) -> n.suggestPlanNodes(u), this.overall));

        // Generate full plan trees from PlanNodes
        List<PlanNode> trees = Lambda.flatMap(Lambda.map((PlanNode n) -> n.generateTrees(), heads));

        // Find highest scoring PlanNode path
        Optional<PlanNode> highest = Optional.empty();
        for (PlanNode path : trees) {
            if (path.getScore() > highest.map((PlanNode n) -> n.getScore()).orElse(0f)) {
                highest = Optional.of(path);
            }
        }
        return highest;
    }
}
