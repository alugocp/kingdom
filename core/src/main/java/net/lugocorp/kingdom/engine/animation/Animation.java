package net.lugocorp.kingdom.engine.animation;
import net.lugocorp.kingdom.game.model.Unit;

/**
 * This class represents a single dynamic graphical action
 */
public abstract class Animation {
    private final Tweening tweening;

    public Animation(Tweening tweening) {
        this.tweening = tweening;
    }

    /**
     * Override this method to make the Animation do something
     */
    protected abstract void animate(Unit u, float value);

    /**
     * The Animation calls this when it's complete
     */
    protected void onFinish() {
        // No-op by default
    }

    /**
     * This function updates the Animation state, and returns true if the Animation
     * is done
     */
    boolean update(Unit u, int dt) {
        final float value = this.tweening.update(dt);
        this.animate(u, value);
        if (this.tweening.isComplete()) {
            this.onFinish();
            return true;
        }
        return false;
    }
}
