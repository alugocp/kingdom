package net.lugocorp.kingdom.game.player;
import net.lugocorp.kingdom.ai.Actor;
import net.lugocorp.kingdom.ai.DecisionChannel;
import net.lugocorp.kingdom.ai.GoalSet;
import net.lugocorp.kingdom.ai.memory.MemoryMap;
import net.lugocorp.kingdom.ai.prediction.CapturedEvents;
import net.lugocorp.kingdom.ai.prediction.SelectedTargets;
import net.lugocorp.kingdom.ai.stats.Statistics;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.gameplay.actions.SkipAction;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.overlay.LabelOverlay;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.BatchCounter;
import net.lugocorp.kingdom.utils.SideEffect;
import com.badlogic.gdx.graphics.Color;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * This Player is operated by an Actor (entry point to the AI system)
 */
public class CompPlayer extends Player {
    public final Actor actor = new Actor();
    public final Statistics stats = new Statistics();
    private Optional<BatchCounter<Unit>> unitsForDecisionMaking = Optional.empty();
    public MemoryMap memory = null;

    public CompPlayer(GameView view, int index, Fate fate, Color color) {
        super(String.format("Computer %d", index), fate, color);
        this.getFate().setPlayer(this);
    }

    // This is for Kryo purposes only
    public CompPlayer() {
        super("", null, null);
    }

    /**
     * Runs the CompPlayer's decision-making logic and returns false when complete
     */
    public boolean makeUnitDecisions(GameView view) {
        final List<Unit> units = view.game.actions.getUnitsWithAndWithoutActions(this).b;

        // Assign and enact a Behavior to all Units without an Action
        this.actor.makeDecisions(GoalSet.singleton, units);
        for (Unit u : units) {
            if (!this.actor.enactDecision(DecisionChannel.unit(u))) {
                view.game.actions.unitHasActed(view, u,
                        new SkipAction("There is no behavior set for this unit", () -> true));
            }
        }

        // Return true if we can still act again
        for (Unit u : units) {
            // TODO replace with canUnitDoThis() so that we support double actions
            if (!view.game.actions.unitHasAssignedAction(u)) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public boolean isHumanPlayer() {
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public void incrementVision(Tile t) {
        this.memory.incrementVision(t);
    }

    /** {@inheritdoc} */
    @Override
    public void decrementVision(Tile t) {
        this.memory.decrementVision(t);
    }

    /** {@inheritdoc} */
    @Override
    public SideEffect select(GameView view, Set<Point> points, String error, Function<Point, SideEffect> action) {
        final SideEffect effects = new SideEffect();
        if (points.size() == 0) {
            return effects;
        }

        // If we're making a prediction then we should split off
        // our prediction for each possible target from points.
        if (SelectedTargets.instance.isPrediction()) {
            CapturedEvents.instance.split(points, (Point p) -> effects.add(action.apply(p)));
            return effects;
        }

        // We've selected our targets, so we're ready to execute now
        return action.apply(SelectedTargets.instance.popPath());
    }

    /** {@inheritdoc} */
    @Override
    public SideEffect select(GameView view, Set<Point> points, String error, Function<Point, SideEffect> action,
            Function<Tile, Optional<LabelOverlay>> hover) {
        return this.select(view, points, error, action);
    }
}
