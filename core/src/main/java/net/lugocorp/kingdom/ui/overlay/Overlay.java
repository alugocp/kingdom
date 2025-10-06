package net.lugocorp.kingdom.ui.overlay;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.logic.CameraLogic;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.math.Vector3;

/**
 * Represents a 2D asset rising over the GameView in 3D space
 */
public abstract class Overlay {
    protected final Vector3 offset;
    private final Point origin;

    public Overlay(Point origin, Vector3 offset) {
        this.offset = offset;
        this.origin = origin;
    }

    /**
     * Returns true if this Overlay has run its course
     */
    public abstract boolean isDone();

    /**
     * Updates this Overlay's progress through its animation
     */
    public abstract float update(int dt);

    /**
     * Renders this Overlay onto the Game World
     */
    public abstract void render(GameView view);

    /**
     * Returns the current position of the Overlay
     */
    protected float[] getPosition(GameView view) {
        return CameraLogic.getScreenPointFromTileOffset(view.getCamera(), this.origin, this.offset);
    }
}
