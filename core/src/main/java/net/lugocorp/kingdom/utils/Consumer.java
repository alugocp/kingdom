package net.lugocorp.kingdom.utils;

/**
 * Lambda interface with one parameter and no return value
 */
// TODO replace this with java.util.function.Consumer (Consumer.accept())
public interface Consumer<T> {
    public void run(T t);
}
