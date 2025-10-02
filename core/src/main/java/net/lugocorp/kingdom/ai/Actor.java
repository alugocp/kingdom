package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.ai.action.ActionResult;
import net.lugocorp.kingdom.ai.action.Goal;
import net.lugocorp.kingdom.ai.action.Plan;
import net.lugocorp.kingdom.ai.action.PlanNode;
import net.lugocorp.kingdom.ai.goals.AttackEnemy;
import net.lugocorp.kingdom.ai.goals.ClaimGlyphs;
import net.lugocorp.kingdom.ai.goals.ClaimPassiveBuildings;
import net.lugocorp.kingdom.ai.goals.ExploreMap;
import net.lugocorp.kingdom.ai.goals.HarvestFood;
import net.lugocorp.kingdom.ai.goals.IncreaseUnitPoints;
import net.lugocorp.kingdom.ai.goals.MineGold;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.Lambda;
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
        // These Goals should never be removed
        this.goals.add(new ExploreMap());
        this.goals.add(new ClaimGlyphs());
        this.goals.add(new ClaimPassiveBuildings());
        this.goals.add(new IncreaseUnitPoints());
        this.goals.add(new HarvestFood());
    }

    /**
     * Returns all of this Actor's Goals
     */
    public Set<Goal> getGoals() {
        return this.goals;
    }

    /**
     * Determines which Goals the CompPlayer should focus on right now
     */
    public void assessGoals(CompPlayer comp) {
        // Add Fate-based Goals
        this.goals.addAll(comp.getFate().strategicGoals);

        // Mine gold
        if (comp.stats.income.getMean() < 4.0) {
            this.goals.add(new MineGold());
        }
        if (comp.stats.income.getMean() > 8.0) {
            this.goals.removeIf((Goal g) -> g instanceof MineGold);
        }

        // Attack enemies
        if (comp.stats.enemiesKilled.getMean() < 0.1) {
            this.goals.add(new AttackEnemy());
        }
        if (comp.stats.enemiesKilled.getMean() >= 0.3 && comp.stats.unitsLost.getMean() >= 0.5) {
            this.goals.removeIf((Goal g) -> g instanceof AttackEnemy);
        }
    }

    /**
     * Returns the Plan with the highest score in the given List
     */
    private Optional<Plan> getBestPlan(Iterable<Plan> plans) {
        float score = -1f;
        Optional<Plan> result = Optional.empty();
        for (Plan p : plans) {
            if (p.score > score) {
                result = Optional.of(p);
                score = p.score;
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
        Set<Plan> options = Lambda.map((Optional<Plan> o) -> o.get(), Lambda.filter((Optional<Plan> o) -> o.isPresent(),
                Lambda.map((Goal n) -> n.suggestPlan(view, u), this.goals)));
        return options.size() > 0 ? this.getBestPlan(options).map((Plan p) -> p.root) : Optional.empty();
    }
}
