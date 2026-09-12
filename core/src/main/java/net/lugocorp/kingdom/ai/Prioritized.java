package net.lugocorp.kingdom.ai;

/**
 * Represents some value with an associated Priority
 */
public class Prioritized<T> {
    public final Priority priority;
    public final T value;

    public Prioritized(Priority priority, T value) {
        this.priority = priority;
        this.value = value;
    }
}
