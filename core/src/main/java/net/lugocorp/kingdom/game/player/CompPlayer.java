package net.lugocorp.kingdom.game.player;
import net.lugocorp.kingdom.ai.Actor;
import net.lugocorp.kingdom.ai.memory.MemoryMap;
import net.lugocorp.kingdom.ai.prediction.CapturedEvents;
import net.lugocorp.kingdom.ai.prediction.SelectedTargets;
import net.lugocorp.kingdom.ai.stats.Statistics;
import net.lugocorp.kingdom.ai.wishlist.Wishlists;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.BatchCounter;
import net.lugocorp.kingdom.utils.SideEffect;
import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * This Player is operated by an Actor (entry point to the AI system)
 */
public class CompPlayer extends Player {
    private final Actor actor = new Actor();
    public final Statistics stats = new Statistics();
    public final Wishlists wishlist;
    private Optional<BatchCounter<Unit>> unitsForDecisionMaking = Optional.empty();
    private boolean executingUnitPlans = false;
    public MemoryMap memory = null;

    public CompPlayer(GameView view, int index, Fate fate, Color color) {
        super(String.format("Computer %d", index), fate, color);
        this.wishlist = new Wishlists(view, this);
        this.getFate().setPlayer(this);
    }

    /**
     * Returns this CompPlayer's Actor instance
     */
    public Actor getActor() {
        return this.actor;
    }

    /**
     * Runs the Actor's decision-making logic for the current turn
     */
    public boolean makeDecisions(GameView view) {
        // Initiate decision-making process
        if (!this.unitsForDecisionMaking.isPresent()) {
            final List<Unit> unitsCopy = new ArrayList<>();
            this.memory.refresh(view);
            this.actor.assessGoals(this);
            unitsCopy.addAll(this.units);
            this.unitsForDecisionMaking = Optional.of(new BatchCounter(10, unitsCopy));
        }

        // Handle the plan execution phase
        if (this.executingUnitPlans) {
            final boolean result = this.actor.executeUnitPlans(view, this.unitsForDecisionMaking.get());
            if (result) {
                // Terminate the decision-making process
                this.unitsForDecisionMaking = Optional.empty();
                this.executingUnitPlans = false;
            }
            return result;
        }

        // Handle the plan assignment phase
        if (this.actor.assignUnitPlans(view, this.unitsForDecisionMaking.get())) {
            // Transition to the execution phase
            this.unitsForDecisionMaking.get().reset();
            this.executingUnitPlans = true;
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
}
