package net.lugocorp.kingdom.math;

/**
 * Represents a point in a 2D space
 */
public class Point {
    public final int x;
    public final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        return String.format("(%d, %d)", this.x, this.y);
    }
}
