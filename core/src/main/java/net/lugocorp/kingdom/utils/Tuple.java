package net.lugocorp.kingdom.utils;

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

    /** {@inheritdoc} */
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    /** {@inheritdoc} */
    @Override
    public boolean equals(Object o) {
        return this.toString().equals(o.toString());
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        return String.format("(%s, %s)", this.a.toString(), this.b.toString());
    }
}
