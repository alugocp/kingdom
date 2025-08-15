package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.game.model.Player;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import net.lugocorp.kingdom.utils.SideEffect;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * This class allows the human and AI players to navigate the turn structure
 * under the same umbrella
 */
public class PlayerInterface {

    /**
     * Returns true if the given Player is present and human
     */
    private static boolean isHuman(Optional<Player> player) {
        return player.map((Player p) -> p.isHumanPlayer()).orElse(false);
    }

    /**
     * Wraps select logic for the human player (leverages the UI) and for AI players
     * (leverages SideEffects)
     */
    public static SideEffect select(GameView view, Optional<Player> player, Set<Point> points, String error,
            Function<Point, SideEffect> action) {
        if (PlayerInterface.isHuman(player)) {
            view.selector.select(points, error, (Point p) -> action.apply(p).execute());
            return SideEffect.none;
        }
        // TODO find a way to differentiate the side effects of this
        // by the target so the AI can make an actual decision here
        return SideEffect.all(Lambda.map((Point p) -> action.apply(p), points));
    }
}
