package net.lugocorp.kingdom.engine.controllers;
import java.util.HashMap;
import java.util.Map;

/**
 * This class keeps track of which keys are held down
 */
public class KeyState {
    private final Map<Integer, Runnable> keyActions = new HashMap<>();

    /**
     * Adds a new key-based action to this instance
     */
    void down(int held, int key, Runnable action) {
        if (held == key) {
            this.keyActions.put(key, action);
        }
    }

    /**
     * Removes the given key and its associated action from this instance
     */
    void up(int key) {
        this.keyActions.remove(key);
    }

    /**
     * Performs all actions associated with currently held keys
     */
    public void performActions() {
        for (Runnable r : this.keyActions.values()) {
            r.run();
        }
    }
}
