package net.lugocorp.kingdom.engine.animation;
import java.util.Optional;

/**
 * Represents a chain of Animations
 */
public class AnimationChain {
    private Optional<Animation> first = Optional.empty();
    private Optional<Animation> last = Optional.empty();

    /**
     * Adds an Animation to the chain
     */
    public void add(Animation a) {
        if (!this.first.isPresent()) {
            this.first = Optional.of(a);
            this.last = Optional.of(a);
        } else {
            this.last = Optional.of(this.last.get().then(a));
        }
    }

    /**
     * Returns the first Animation in the chain
     */
    public Animation get() {
        return this.first.get();
    }
}
