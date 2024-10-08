package net.lugocorp.kingdom.utils;

/**
 * Lambda interface with no parameters and a return value
 */
public interface Producer<T> {
    public T run();
}
