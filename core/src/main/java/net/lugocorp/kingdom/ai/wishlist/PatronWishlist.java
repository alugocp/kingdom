package net.lugocorp.kingdom.ai.wishlist;
import net.lugocorp.kingdom.ai.Actor;
import net.lugocorp.kingdom.ai.action.Goal;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Patron;

/**
 * Helps a CompPlayer decide which Patron(s) to focus on
 */
public class PatronWishlist extends Wishlist<Patron> {
    private final Game game;

    public PatronWishlist(Game game, Actor actor) {
        super(actor);
        this.game = game;
    }

    /** {@inheritdoc} */
    @Override
    protected int getScoreForGoal(Patron option, Goal g) {
        int score = 0;
        for (String channel : this.game.events.patron.getChannels(option.getStratifier())) {
            score += g.likesEventChannel(channel) ? 1 : 0;
        }
        return score;
    }
}
