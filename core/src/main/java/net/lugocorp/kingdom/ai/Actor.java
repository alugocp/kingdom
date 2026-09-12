package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.gameplay.actions.SkipAction;
import net.lugocorp.kingdom.math.Path;
import net.lugocorp.kingdom.prediction.Prediction;
import net.lugocorp.kingdom.prediction.SelectionTree;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Log;
import net.lugocorp.kingdom.utils.LogSys;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * This class provides access to all of the Decision-making logic for a
 * CompPlayer
 */
public class Actor {
    private final Map<String, Decision> decisions = new HashMap<>();
    public final ActorState state = new ActorState();

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

            final Consideration<Decision> consideration = new Consideration<>();
            for (Goal goal : goals) {
                final Decision d = goal.getDecision(view, player, channel);
                consideration.consider(d.priority, d);
            }
            consideration.getValue().ifPresent((Decision d) -> {
                Log.log(LogSys.AI, "Set decision %s", d);
                this.decisions.put(channel.toString(), d);
            });
        }

        // Make Decisions for miscellaneous DecisionChannels
        final DecisionChannel[] misc = new DecisionChannel[]{DecisionChannel.recruitUnit(),
                DecisionChannel.recruitArtifact(), DecisionChannel.auctionEntry()};
        for (DecisionChannel channel : misc) {
            if (this.decisions.containsKey(channel.toString())) {
                continue;
            }

            final Consideration<Decision> consideration = new Consideration<>();
            for (Goal goal : goals) {
                final Decision d = goal.getDecision(view, player, channel);
                consideration.consider(d.priority, d);
            }
            consideration.getValue().ifPresent((Decision d) -> {
                Log.log(LogSys.AI, "Set decision %s", d);
                this.decisions.put(channel.toString(), d);
            });
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
            this.state.enact(d.behavior);
            d.behavior.act(view);
        }
        if (isFatal || d.behavior.isFinished(view)) {
            Log.log(LogSys.AI, "Decision has been removed");
            this.decisions.remove(channel.toString());
        }
        return true;
    }

    /**
     * Makes a Prediction for each possible selection Path after activating the
     * given Ability, then returns the highest Priority Path
     */
    public Optional<Prioritized<Path>> analyzeActiveAbility(GameView view, Ability ability,
            Function<Prediction, Priority> prioritize) {
        final Consideration<Path> consideration = new Consideration<>();
        final SelectionTree tree = this.state.deliberate();
        ability.activate(view);
        tree.iteratePredictions((Prediction prediction) -> {
            final Priority priority = prioritize.apply(prediction);
            consideration.consider(priority, prediction.path);
        });
        return consideration.getState();
    }
}
