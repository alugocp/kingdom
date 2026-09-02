package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.game.model.Unit;
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
    public void makeDecisions(GoalSet goals, List<Unit> units) {
        // Make Decisions regarding Units
        for (Unit unit : units) {
            final DecisionChannel channel = DecisionChannel.unit(unit);
            if (this.decisions.containsKey(channel)) {
                continue;
            }
            for (Goal goal : goals) {
                this.consider(goal, channel);
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
                this.consider(goal, channel);
            }
        }
    }

    /**
     * Generate a Decision for the given Goal and DecisionChannel and assign it if
     * it has the highest Priority
     */
    private void consider(Goal goal, DecisionChannel channel) {
        final Decision d = goal.getDecision(this, channel);
        if (!this.decisions.containsKey(channel) || d.priority.value > this.decisions.get(channel).priority.value) {
            this.decisions.put(channel, d);
        }
    }

    /**
     * Acts out the Behavior associated with the given DecisionChannel, and returns
     * true if there was a Behavior to enact
     */
    public boolean enactDecision(DecisionChannel channel) {
        final Decision d = this.decisions.get(channel);
        if (d == null) {
            return false;
        }
        d.behavior.act();
        if (d.behavior.isFinished()) {
            this.decisions.remove(channel);
        }
        return true;
    }
}
