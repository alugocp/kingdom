package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Optional;

/**
 * Behaviors are objects that can live for one or more turns and direct a
 * DecisionChannel's actions during that lifetime
 */
public interface Behavior {

    /**
     * Runs the actual logic that governs this Behavior
     */
    public void act(GameView view);

    /**
     * Returns true when this Behavior's lifetime has expired
     */
    public boolean isFinished(GameView view);

    /**
     * Returns the next Point (selection) in the queue, if one exists
     */
    public default Optional<Point> getSelection() {
        return Optional.empty();
    }
}
