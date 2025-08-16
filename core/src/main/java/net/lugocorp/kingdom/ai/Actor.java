package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.ai.high.ExploreMapNode;
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
    private final Map<Unit, LowNode> unitPlans = new HashMap<>();
    private final Set<HighNode> overall = new HashSet<>();

    public Actor() {
        overall.add(new ExploreMapNode());
    }

    /**
     * Iterates through our LowNodes to animate Units under the AI's control
     */
    public void executeUnitPlans(GameView view) {
        for (Map.Entry<Unit, LowNode> entry : this.unitPlans.entrySet()) {
            LowNode n = entry.getValue();
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
                Optional<LowNode> plan = this.createUnitPlan(u);
                plan.ifPresent((LowNode n) -> this.unitPlans.put(u, n));
            }
        }
    }

    /**
     * This function generates a plan for the given Unit
     */
    private Optional<LowNode> createUnitPlan(Unit u) {
        // Suggest some actions that align with our overall goals
        List<LowNode> heads = Lambda.flatMap(Lambda.map((HighNode n) -> n.suggestLowNodes(u), this.overall));

        // Generate full plan trees from LowNodes
        List<LowNode> trees = Lambda.flatMap(Lambda.map((LowNode n) -> n.generateTrees(), heads));

        // Find highest scoring LowNode path
        Optional<LowNode> highest = Optional.empty();
        for (LowNode path : trees) {
            if (path.getScore() > highest.map((LowNode n) -> n.getScore()).orElse(0f)) {
                highest = Optional.of(path);
            }
        }
        return highest;
    }
}
