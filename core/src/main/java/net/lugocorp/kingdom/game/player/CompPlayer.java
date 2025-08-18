package net.lugocorp.kingdom.game.player;
import net.lugocorp.kingdom.ai.Actor;
import net.lugocorp.kingdom.ai.memory.MemoryMap;
import net.lugocorp.kingdom.ai.prediction.CapturedEvents;
import net.lugocorp.kingdom.ai.prediction.SelectedTargets;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.SideEffect;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * This Player is operated by an Actor (entry point to the AI system)
 */
public class CompPlayer extends Player {
    private final Actor actor = new Actor();
    public final MemoryMap memory;

    public CompPlayer(int index, Point world, Fate fate) {
        super(String.format("Computer %d", index), fate);
        this.memory = new MemoryMap(world);
    }

    /**
     * Runs the Actor's decision-making logic for the current turn
     */
    public void makeDecisions(GameView view) {
        // TODO optimize this by limiting the number of operations the AI players
        // execute per frame
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
    public void incrementVisibility(Tile t) {
        this.memory.incrementVisibility(t);
    }

    /** {@inheritdoc} */
    @Override
    public void decrementVisibility(Tile t) {
        this.memory.decrementVisibility(t);
    }

    /** {@inheritdoc} */
    @Override
    public SideEffect select(GameView view, Set<Point> points, String error, Function<Point, SideEffect> action) {
        if (SelectedTargets.instance.isPrediction()) {
            final List<SideEffect> effects = new ArrayList<>();
            CapturedEvents.instance.split(points, (Point p) -> effects.add(action.apply(p)));
            return SideEffect.all(effects);
        }

        // We've selected our targets, so we're ready to execute now
        return action.apply(SelectedTargets.instance.popPath());
    }
}
