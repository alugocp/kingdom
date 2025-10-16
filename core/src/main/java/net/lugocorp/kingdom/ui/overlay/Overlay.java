package net.lugocorp.kingdom.ui.overlay;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.logic.CameraLogic;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.math.Vector3;
import java.util.Optional;

/**
 * Represents a 2D asset rising over the GameView in 3D space
 */
public abstract class Overlay {
    private final Vector3 offset;
    private final Point origin;
    private Optional<Runnable> callback = Optional.empty();

    public Overlay(Point origin, Vector3 offset) {
        this.offset = offset.add(Coords.raw.vector(0, Hexagons.HEIGHT, 0));
        this.origin = origin;
    }

    /**
     * Returns true if this Overlay has run its course
     */
    public abstract boolean isDone();

    /**
     * Updates this Overlay's progress through its animation
     */
    public abstract void update(int dt);

    /**
     * Renders this Overlay onto the Game World
     */
    public abstract void render(GameView view);

    /**
     * Adds a callback to this Overlay (will run on completion)
     */
    public Overlay then(Runnable r) {
        this.callback = Optional.of(r);
        return this;
    }

    /**
     * Runs this Overlay's callback (if any exists)
     */
    public void runCallback() {
        this.callback.ifPresent((Runnable r) -> r.run());
    }

    /**
     * Returns the current position of the Overlay
     */
    protected float[] getPosition(GameView view) {
        return CameraLogic.getScreenPointFromTileOffset(this.origin, this.offset);
    }
}
