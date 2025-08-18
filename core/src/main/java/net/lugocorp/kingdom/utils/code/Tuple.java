package net.lugocorp.kingdom.utils.code;

/**
 * A simple container object for two data types
 */
public class Tuple<A, B> {
    public final A a;
    public final B b;

    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }
}
