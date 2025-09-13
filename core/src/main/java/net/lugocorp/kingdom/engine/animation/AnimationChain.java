package net.lugocorp.kingdom.engine.animation;

/**
 * Represents a chain of Animations
 */
public class AnimationChain {
    private Animation first = null;
    private Animation last = null;

    /**
     * Adds an Animation to the chain
     */
    public void add(Animation a) {
        if (this.first == null) {
            this.first = a;
            this.last = a;
        } else {
            this.last = this.last.then(a);
        }
    }

    /**
     * Returns the first Animation in the chain
     */
    public Animation get() {
        return this.first;
    }
}
