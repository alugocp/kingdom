package net.lugocorp.kingdom.engine.animation;

/**
 * This class contains logic for smooth movement
 */
public class Tween {
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
        final float line = (Tween.MAX - Tween.MIN) * p;
        if (this.backToStart) {
            final float downwards = Math.abs(Tween.MAX - (line * 2f));
            return this.increasing ? Tween.MAX - downwards : downwards;
        }
        return this.increasing ? line : Tween.MAX - line;
    }

    /**
     * Handles the passage of some milliseconds and returns the resulting animation
     * progress
     */
    public float update(int dt) {
        final float diff = (float) dt / this.runtime;
        this.progress = Math.min(Tween.MAX, Math.max(Tween.MIN, this.progress + diff));
        return this.getValue(this.progress);
    }

    /**
     * Returns true when this instance has completed its path
     */
    boolean isComplete() {
        return this.progress == Tween.MAX;
    }

    /**
     * Tells this instance to move down instead of up
     */
    public Tween desc() {
        this.increasing = false;
        return this;
    }

    /**
     * Sets how long the animation will take in total
     */
    public Tween duration(int runtime) {
        this.runtime = runtime;
        return this;
    }

    /**
     * Causes this instance to return to its starting point
     */
    public Tween goBackToStart() {
        this.backToStart = true;
        return this;
    }
}
