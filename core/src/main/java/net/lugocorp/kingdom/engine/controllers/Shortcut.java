package net.lugocorp.kingdom.engine.controllers;

/**
 * This class represents a labelled keycode for keyboard shortcuts
 */
public class Shortcut {
    public final String label;
    public final int[] keycodes;

    public Shortcut(String label, int... keycodes) {
        this.keycodes = keycodes;
        this.label = label;
    }

    /**
     * Runs the given action if the given keycode is sufficient for this Shortcut
     */
    public void matches(int keycode, Runnable action) {
        for (int k : this.keycodes) {
            if (k == keycode) {
                action.run();
                break;
            }
        }
    }
}
