package net.lugocorp.kingdom.game.player;
import net.lugocorp.kingdom.ai.Actor;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This Player is operated by an Actor (entry point to the AI system)
 */
public class CompPlayer extends Player {
    private final Actor actor = new Actor();

    public CompPlayer(int index, Fate fate) {
        super(String.format("Computer %i", index), fate);
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
}
