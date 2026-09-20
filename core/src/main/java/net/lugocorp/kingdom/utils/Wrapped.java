package net.lugocorp.kingdom.utils;

/**
 * Wraps an associated primitive value so we can access/modify it from within a
 * lambda function
 */
public class Wrapped<T> {
    private T value;

    public Wrapped(T value) {
        this.value = value;
    }

    /**
     * Modifies the associated value
     */
    public void set(T value) {
        this.value = value;
    }

    /**
     * Returns the associated value
     */
    public T get() {
        return this.value;
    }
}
