package net.lugocorp.kingdom.ui.game;
import net.lugocorp.kingdom.utils.math.Point;

/**
 * Represents a text label rising over the GameView
 */
class Overlay {
    private static final float DURATION = 2000f;
    private static final float FADE_OUT = 0.3f;
    private float progress = 0f;
    final Point origin;
    final String label;
    final int color;

    Overlay(String label, int color, Point origin) {
        this.origin = origin;
        this.color = color;
        this.label = label;
    }

    /**
     * Returns true if this Overlay has run its course
     */
    final boolean isDone() {
        return this.progress == 1f;
    }

    /**
     * Updates this Overlay's progress through its animation
     */
    final float update(int dt) {
        this.progress = Math.min(1f, this.progress + (dt / Overlay.DURATION));
        return this.progress;
    }

    /**
     * Returns the opacity for this Overlay
     */
    final float getOpacity() {
        final float thresh = 1f - Overlay.FADE_OUT;
        return this.progress >= thresh ? 1f - ((this.progress - thresh) / Overlay.FADE_OUT) : 1f;
    }
}
