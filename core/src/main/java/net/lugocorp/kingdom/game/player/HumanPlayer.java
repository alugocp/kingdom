package net.lugocorp.kingdom.game.player;

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
}
