package net.lugocorp.kingdom.ai.wishlist;
import net.lugocorp.kingdom.ai.Actor;
import net.lugocorp.kingdom.ai.action.Goal;
import net.lugocorp.kingdom.game.model.Patron;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * Helps a CompPlayer decide which Patron(s) to focus on
 */
public class PatronWishlist extends Wishlist<Patron> {
    private final GameView view;

    public PatronWishlist(GameView view, Actor actor) {
        super(actor);
        this.view = view;
    }

    /** {@inheritdoc} */
    @Override
    protected int getScoreForGoal(Patron option, Goal g) {
        int score = 0;
        for (String channel : this.view.game.events.patron.getChannels(option.getStratifier())) {
            score += g.likesEventChannel(channel) ? 1 : 0;
        }
        return score;
    }
}
