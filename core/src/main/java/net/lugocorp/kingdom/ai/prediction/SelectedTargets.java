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

    public boolean isPrediction() {
        return this.prediction;
    }

    public Point popPath() {
        return this.path.popFromFront();
    }

    public void setActivePath(Path path) {
        this.prediction = false;
        this.path = path;
    }

    public void clear() {
        this.prediction = true;
    }
}
