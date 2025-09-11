package net.lugocorp.kingdom.engine.animation;

/**
 * This class contains logic for smooth movement
 */
public class Tweening {
    private static final float MIN = 0f;
    private static final float MAX = 1f;
    private boolean backToStart = false;
    private boolean increasing = true;
    private float progress = 0f;
    private int runtime = 1000;

    /**
     * Returns the resulting value given a progress value
     */
    private float getValue(float p) {
        final float line = (Tweening.MAX - Tweening.MIN) * p;
        if (this.backToStart) {
            final float downwards = Math.abs(Tweening.MAX - (line * 2f));
            return this.increasing ? Tweening.MAX - downwards : downwards;
        }
        return this.increasing ? line : Tweening.MAX - line;
    }

    /**
     * Handles the passage of some milliseconds and returns the resulting animation
     * progress
     */
    public float update(int dt) {
        final float diff = (float) dt / this.runtime;
        this.progress = Math.min(Tweening.MAX, Math.max(Tweening.MIN, this.progress + diff));
        return this.getValue(this.progress);
    }

    /**
     * Returns true when this instance has completed its path
     */
    boolean isComplete() {
        return this.progress == Tweening.MAX;
    }

    /**
     * Tells this instance to move down instead of up
     */
    public Tweening desc() {
        this.increasing = false;
        return this;
    }

    /**
     * Sets how long the animation will take in total
     */
    public Tweening duration(int runtime) {
        this.runtime = runtime;
        return this;
    }

    /**
     * Causes this instance to return to its starting point
     */
    public Tweening goBackToStart() {
        this.backToStart = true;
        return this;
    }
}
