package net.lugocorp.kingdom.utils;

/**
 * Wraps an associated integer value
 */
public class WrapInt {
    private int value = 0;

    /**
     * Modifies the associated value
     */
    public void add(int v) {
        this.value += v;
    }

    /**
     * Returns the associated value
     */
    public int get() {
        return this.value;
    }
}
