package net.lugocorp.kingdom.game.player;
import net.lugocorp.kingdom.game.model.Tile;

/**
 * This Player is operated by a real life human being
 */
public class HumanPlayer extends Player {

    public HumanPlayer() {
        super("you", null);
    }

    /** {@inheritdoc} */
    @Override
    public boolean isHumanPlayer() {
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public void incrementVisibility(Tile t) {
        t.incrementVisibility();
    }

    /** {@inheritdoc} */
    @Override
    public void decrementVisibility(Tile t) {
        t.decrementVisibility();
    }
}
