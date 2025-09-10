package net.lugocorp.kingdom.engine.animation;

/**
 * This class contains logic for smooth movement
 */
public class Tweening {
    private static final float MIN = 0f;
    private static final float MAX = 1f;
    private float direction = 1f;
    private int runtime = 1000;
    private float value = 0f;

    /**
     * Handles the passage of some milliseconds and returns the resulting animation
     * progress
     */
    public float update(int dt) {
        this.value += this.direction * ((float) dt / this.runtime);
        this.value = Math.min(Tweening.MAX, Math.max(Tweening.MIN, this.value));
        return this.value;
    }

    /**
     * Returns true when this instance has completed its path
     */
    boolean isComplete() {
        return this.value == (this.direction > 0 ? Tweening.MAX : Tweening.MIN);
    }

    /**
     * Tells this instance to move down instead of up
     */
    public Tweening desc() {
        this.value = Tweening.MAX;
        this.direction = -1f;
        return this;
    }

    /**
     * Sets how long the animation will take in total
     */
    public Tweening duration(int runtime) {
        this.runtime = runtime;
        return this;
    }
}
