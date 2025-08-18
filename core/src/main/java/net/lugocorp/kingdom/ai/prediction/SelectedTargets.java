package net.lugocorp.kingdom.ai.prediction;
import net.lugocorp.kingdom.utils.math.Path;
import net.lugocorp.kingdom.utils.math.Point;

/**
 * A utility class that communicates between our AI prediction system and
 * multi-target spell casting
 */
public class SelectedTargets {
    private boolean prediction = true;
    private Path path = null;
    public static final SelectedTargets instance = new SelectedTargets();

    /**
     * Returns true if we're in prediction mode
     */
    public boolean isPrediction() {
        return this.prediction;
    }

    /**
     * Pops a single Point from this target Path. This is used to feed the Player
     * select() function as they cast an active Ability.
     */
    public Point popPath() {
        return this.path.popFromFront();
    }

    /**
     * Sets the target Path and turns off prediction mode
     */
    public void setActivePath(Path path) {
        this.prediction = false;
        this.path = path;
    }

    /**
     * Clears the mode back to prediction
     */
    public void clear() {
        this.prediction = true;
    }
}
