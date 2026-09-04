package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.gameplay.actions.SkipAction;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Log;
import net.lugocorp.kingdom.utils.LogSys;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides access to all of the Decision-making logic for a
 * CompPlayer
 */
public class Actor {
    private final Map<String, Decision> decisions = new HashMap<>();

    /**
     * Assigns Decisions for each DecisionChannel associated with this Actor's
     * CompPlayer
     */
    public void makeDecisions(GameView view, CompPlayer player, GoalSet goals, List<Unit> units) {
        // Make Decisions regarding Units
        for (Unit unit : units) {
            final DecisionChannel channel = DecisionChannel.unit(unit);
            if (this.decisions.containsKey(channel.toString())) {
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
            if (this.decisions.containsKey(channel.toString())) {
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
        boolean accept = false;
        if (this.decisions.containsKey(channel.toString())) {
            final int incumbent = this.decisions.get(channel.toString()).priority.value;
            final int incoming = d.priority.value;
            if (incoming > incumbent || (incoming == incumbent && Math.random() < 0.3)) {
                accept = true;
            }
        } else {
            accept = true;
        }
        if (accept) {
            Log.log(LogSys.AI, "Set decision %s", d);
            this.decisions.put(channel.toString(), d);
        }
    }

    /**
     * Acts out the Behavior associated with the given DecisionChannel, and returns
     * true if there was a Behavior to enact
     */
    public boolean enactDecision(GameView view, DecisionChannel channel) {
        final Decision d = this.decisions.get(channel.toString());
        if (d == null) {
            Log.log(LogSys.AI, "Decision not found");
            return false;
        }
        final boolean isFatal = d.priority == Priority.FATAL;
        if (isFatal) {
            if (channel.hasUnit()) {
                Log.log(LogSys.AI, "Decision would be fatal, skipping turn...");
                view.game.actions.unitHasActed(view, channel.getUnit(),
                        new SkipAction("This unit's player has skipped its turn", () -> true));
            }
        } else {
            Log.log(LogSys.AI, "Enacting...");
            d.behavior.act(view);
        }
        if (isFatal || d.behavior.isFinished(view)) {
            Log.log(LogSys.AI, "Decision has been removed");
            this.decisions.remove(channel.toString());
        }
        return true;
    }
}
