package net.lugocorp.kingdom.game.player;
import net.lugocorp.kingdom.ai.Actor;
import net.lugocorp.kingdom.ai.memory.MemoryMap;
import net.lugocorp.kingdom.ai.prediction.CapturedEvents;
import net.lugocorp.kingdom.ai.prediction.SelectedTargets;
import net.lugocorp.kingdom.ai.stats.Statistics;
import net.lugocorp.kingdom.ai.wishlist.Wishlists;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * This Player is operated by an Actor (entry point to the AI system)
 */
public class CompPlayer extends Player {
    private final Actor actor = new Actor();
    public final Statistics stats = new Statistics();
    public final Wishlists wishlist;
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
    public void makeDecisions(GameView view) {
        // TODO optimize this by limiting the number of operations the AI players
        // execute per frame
        this.memory.refresh(view);
        this.actor.assessGoals(this);
        this.actor.assignUnitPlans(view, this.units);
        this.actor.executeUnitPlans(view);
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
        if (points.size() == 0) {
            return SideEffect.none;
        }

        // If we're making a prediction then we should split off
        // our prediction for each possible target from points.
        if (SelectedTargets.instance.isPrediction()) {
            final List<SideEffect> effects = new ArrayList<>();
            CapturedEvents.instance.split(points, (Point p) -> effects.add(action.apply(p)));
            return SideEffect.all(effects);
        }

        // We've selected our targets, so we're ready to execute now
        return action.apply(SelectedTargets.instance.popPath());
    }
}
