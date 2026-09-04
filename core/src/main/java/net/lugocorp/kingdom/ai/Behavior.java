package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.ui.views.GameView;

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
}
