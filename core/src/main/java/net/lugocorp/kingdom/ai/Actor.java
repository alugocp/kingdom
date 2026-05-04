package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.ai.action.ActionResult;
import net.lugocorp.kingdom.ai.action.Goal;
import net.lugocorp.kingdom.ai.action.Plan;
import net.lugocorp.kingdom.ai.action.PlanNode;
import net.lugocorp.kingdom.ai.goals.AttackEnemy;
import net.lugocorp.kingdom.ai.goals.ClaimGlyphs;
import net.lugocorp.kingdom.ai.goals.ExploreMap;
import net.lugocorp.kingdom.ai.goals.HarvestFood;
import net.lugocorp.kingdom.ai.goals.IncreaseUnitPoints;
import net.lugocorp.kingdom.ai.goals.MineGold;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.BatchCounter;
import net.lugocorp.kingdom.utils.Lambda;
import net.lugocorp.kingdom.utils.Log;
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
    private final Map<Unit, PlanNode> plans = new HashMap<>();
    private final Set<Goal> goals = new HashSet<>();

    public Actor() {
        // These Goals should never be removed
        this.goals.add(new ExploreMap());
        this.goals.add(new ClaimGlyphs());
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

        // Focus on unit points vs passive buildings
        if (comp.stats.unitPoints.getLatest() < 12) {
            this.goals.add(new IncreaseUnitPoints());
        }
        if (comp.stats.unitPoints.getLatest() > 18) {
            this.goals.removeIf((Goal g) -> g instanceof IncreaseUnitPoints);
        }
    }

    /**
     * Returns the Plan with the highest score in the given List
     */
    private Optional<Plan> getBestPlan(Iterable<Plan> plans) {
        final Set<Plan> results = new HashSet<>();
        float score = -1f;
        for (Plan p : plans) {
            if (p.score > score) {
                results.clear();
                results.add(p);
                score = p.score;
            } else if (p.score == score) {
                results.add(p);
            }
        }
        return results.size() > 0 && score > 0f ? Optional.of(Lambda.random(results)) : Optional.empty();
    }

    /**
     * Iterates through our PlanNodes to animate Units under the AI's control
     */
    public boolean executeUnitPlans(GameView view, BatchCounter<Unit> units) {
        for (Unit u : units.getBatch()) {
            // Skip this Unit if they aren't present in the plan set or no longer exist in
            // the World
            if (!this.plans.containsKey(u) || !u.doesExistInWorld()) {
                continue;
            }

            // If the given Unit has an entry in the ActionManager then let that ride before
            // executing the next PlanNode
            if (view.game.actions.unitHasAssignedAction(u)) {
                Log.log("%s already has an assigned action", u.name);
                continue;
            }

            // Kick off the next PlanNode in the Unit's Plan
            PlanNode n = this.plans.get(u);
            ActionResult result = ActionResult.RIDE;
            while (result == ActionResult.RIDE) {
                result = n.act(view);
                Log.log("Plan result for %s: %s", u.name, result);
                if (result == ActionResult.RIDE || result == ActionResult.POP) {
                    if (n.getChild().isPresent()) {
                        n = n.getChild().get();
                        this.plans.put(u, n);
                    } else {
                        this.plans.remove(u);
                        break;
                    }
                }
                if (result == ActionResult.POP_ALL) {
                    this.plans.remove(u);
                }
            }
        }
        return units.isLastBatch();
    }

    /**
     * Ensures that all Units in the Set have an assigned plan (or are currently
     * performing an Action)
     */
    public boolean assignUnitPlans(GameView view, BatchCounter<Unit> units) {
        for (Unit u : units.getBatch()) {
            if (!this.plans.containsKey(u) && !view.game.actions.unitHasAssignedAction(u)) {
                final Optional<PlanNode> plan = this.determinePlanNode(view, u);
                plan.ifPresent((PlanNode n) -> this.plans.put(u, n));
            }
        }
        return units.isLastBatch();
    }

    /**
     * This function generates a PlanNode for the given Unit
     */
    private Optional<PlanNode> determinePlanNode(GameView view, Unit u) {
        final Map<Goal, Optional<Plan>> results = Lambda.mapFromSet((Goal n) -> n.suggestPlan(view, u), this.goals);
        final List<Plan> options = Lambda.map((Optional<Plan> o) -> o.get(),
                Lambda.filter((Optional<Plan> o) -> o.isPresent(), Lambda.toList(results.values())));
        final Optional<Plan> best = options.size() > 0 ? this.getBestPlan(options) : Optional.empty();
        this.printDeterminePlanLogs(u, results, best);
        return best.map((Plan p) -> p.root);
    }

    /**
     * Prints debug information when we're determining an AI Unit's Plans
     */
    private void printDeterminePlanLogs(Unit u, Map<Goal, Optional<Plan>> results, Optional<Plan> best) {
        Log.log("Determining plan for %s", u.name);
        for (Map.Entry<Goal, Optional<Plan>> e : results.entrySet()) {
            final Optional<Plan> v = e.getValue();
            if (v.isPresent()) {
                Log.log("• %s: %s (%f)", e.getKey(), v.get(), v.get().score);
            } else {
                Log.log("• %s: ---", e.getKey());
            }
        }
        if (best.isPresent()) {
            Log.log("Chose %s", best.get());
        } else {
            Log.log("Chose nothing");
        }
    }
}
