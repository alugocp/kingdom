package net.lugocorp.kingdom.engine.animation;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Optional;

/**
 * This class represents a single dynamic graphical action
 */
public abstract class Animation {
    private final Tween tween;
    private Optional<Animation> next = Optional.empty();
    private FrameType frame = FrameType.FIRST;

    public Animation(Tween tween) {
        this.tween = tween;
    }

    /**
     * Override this method to make the Animation do something
     */
    protected abstract void animate(float value);

    /**
     * The Animation calls this when it's complete
     */
    protected void onFinish(GameView view) {
        // No-op by default
    }

    /**
     * Sets another Animation to happen after this one
     */
    public final Animation then(Animation a) {
        this.next = Optional.of(a);
        return a;
    }

    /**
     * Returns the next Animation to play after this one
     */
    public final Optional<Animation> getFollowup() {
        return this.next;
    }

    /**
     * Returns true if we're in the first frame
     */
    protected final boolean isFirstFrame() {
        return this.frame == FrameType.FIRST;
    }

    /**
     * Returns true if we're in the last frame
     */
    protected final boolean isLastFrame() {
        return this.frame == FrameType.LAST;
    }

    /**
     * This function updates the Animation state, and returns true if the Animation
     * is done
     */
    final boolean update(GameView view, int dt) {
        final float value = this.tween.update(dt);
        if (this.tween.isComplete()) {
            this.frame = FrameType.LAST;
        }
        this.animate(value);
        if (this.frame == FrameType.FIRST) {
            this.frame = FrameType.OTHER;
        }
        if (this.frame == FrameType.LAST) {
            this.onFinish(view);
            return true;
        }
        return false;
    }
}
