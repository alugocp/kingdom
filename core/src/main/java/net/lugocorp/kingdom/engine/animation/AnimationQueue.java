package net.lugocorp.kingdom.engine.animation;
import net.lugocorp.kingdom.game.model.Unit;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles switching between Animations for a Unit
 */
public class AnimationQueue {
    private final List<Animation> animations = new ArrayList<>();

    /**
     * Adds a new Animation to the queue
     */
    public AnimationQueue add(Animation a) {
        this.animations.add(a);
        return this;
    }

    /**
     * Progresses the state of the current Animation (if there is one)
     */
    public void update(Unit u, int dt) {
        if (this.animations.size() == 0) {
            return;
        }

        // Update the current Animation
        final Animation a = this.animations.get(0);
        if (a.update(u, dt)) {
            this.animations.remove(0);
        }
    }
}
