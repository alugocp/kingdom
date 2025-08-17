package net.lugocorp.kingdom.game.player;
import net.lugocorp.kingdom.ai.Actor;
import net.lugocorp.kingdom.ai.MemoryMap;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;

/**
 * This Player is operated by an Actor (entry point to the AI system)
 */
public class CompPlayer extends Player {
    private final Actor actor = new Actor();
    private final MemoryMap map;

    public CompPlayer(int index, Point world, Fate fate) {
        super(String.format("Computer %i", index), fate);
        this.map = new MemoryMap(world);
    }

    /**
     * Runs the Actor's decision-making logic for the current turn
     */
    public void makeDecisions(GameView view) {
        // TODO optimize this by limiting the number of operations the AI players
        // execute per frame
        this.actor.assignUnitPlans(this.units);
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
        this.map.incrementVisibility(t.getPoint());
    }

    /** {@inheritdoc} */
    @Override
    public void decrementVisibility(Tile t) {
        this.map.decrementVisibility(t.getPoint());
    }
}
