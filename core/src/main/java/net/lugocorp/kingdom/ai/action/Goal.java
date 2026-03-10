package net.lugocorp.kingdom.ai.action;
import net.lugocorp.kingdom.ai.plans.LazyNode;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Optional;

/**
 * TODO write goals for the following:
 * GenerateGold (needs new Event)
 * GenerateUnitPoints (needs new Event)
 * GenerateAuctionPoints (needs new Event)
 * ClaimActiveBuildings
 * ClaimPatron
 * DepositToVaults
 * FeedUnits
 *
 * and then write some helper methods for PlanNode scoring logic (attacking certain targets, checking passive abilities, etc)
 */

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
     * Returns true if the given Glyph aligns with this Goal
     */
    public boolean likesGlyph(Glyph glyph) {
        return false;
    }

    /**
     * Returns true if the given Event channel aligns with this Goal
     */
    public boolean likesEventChannel(String channel) {
        return false;
    }

    /**
     * Calls into likesEventChannel() using the class name
     */
    public final <E extends Event> boolean likesEventChannel(Class<E> channel) {
        return this.likesEventChannel(channel.getSimpleName());
    }

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
        Optional<Plan> best = Optional.empty();
        for (Plan p : plans) {
            if (!best.isPresent() || p.score > best.get().score) {
                best = Optional.of(p);
            }
        }
        return best;
    }

    @Override
    public String toString() {
        return String.format("(Goal %s)", this.getClass().getSimpleName());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Goal) {
            Goal g = (Goal) o;
            return this.toString().equals(g.toString());
        }
        return false;
    }
}
