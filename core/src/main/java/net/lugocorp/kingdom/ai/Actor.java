package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides access to all of the Decision-making logic for a
 * CompPlayer
 */
public class Actor {
    private final Map<DecisionChannel, Decision> decisions = new HashMap<>();

    /**
     * Assigns Decisions for each DecisionChannel associated with this Actor's
     * CompPlayer
     */
    public void makeDecisions(GameView view, CompPlayer player, GoalSet goals, List<Unit> units) {
        // Make Decisions regarding Units
        for (Unit unit : units) {
            final DecisionChannel channel = DecisionChannel.unit(unit);
            if (this.decisions.containsKey(channel)) {
                continue;
            }
            for (Goal goal : goals) {
                this.consider(view, player, goal, channel);
            }
        }

        // Make Decisions for miscellaneous DecisionChannels
        final DecisionChannel[] misc = new DecisionChannel[]{DecisionChannel.recruitUnit(),
                DecisionChannel.recruitArtifact(), DecisionChannel.auctionEntry()};
        for (DecisionChannel channel : misc) {
            if (this.decisions.containsKey(channel)) {
                continue;
            }
            for (Goal goal : goals) {
                this.consider(view, player, goal, channel);
            }
        }
    }

    /**
     * Generate a Decision for the given Goal and DecisionChannel and assign it if
     * it has the highest Priority (or if it ties the highest Priority and we pass
     * by random chance)
     */
    private void consider(GameView view, CompPlayer player, Goal goal, DecisionChannel channel) {
        final Decision d = goal.getDecision(view, player, channel);
        if (this.decisions.containsKey(channel)) {
            final int incumbent = this.decisions.get(channel).priority.value;
            final int incoming = d.priority.value;
            if (incoming > incumbent || (incoming == incumbent && Math.random() < 0.3)) {
                this.decisions.put(channel, d);
            }
        } else {
            this.decisions.put(channel, d);
        }
    }

    /**
     * Acts out the Behavior associated with the given DecisionChannel, and returns
     * true if there was a Behavior to enact
     */
    public boolean enactDecision(GameView view, DecisionChannel channel) {
        final Decision d = this.decisions.get(channel);
        if (d == null) {
            return false;
        }
        final boolean isFatal = d.priority == Priority.FATAL;
        if (!isFatal) {
            d.behavior.act(view);
        }
        if (isFatal || d.behavior.isFinished(view)) {
            this.decisions.remove(channel);
        }
        return true;
    }
}
