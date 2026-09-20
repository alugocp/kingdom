package net.lugocorp.kingdom.math;

/**
 * Represents a point in a 2D space
 */
public class Point extends Pointlike {
    public final int x;
    public final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
        this(0, 0);
    }

    /** {@inheritdoc} */
    @Override
    public int getX() {
        return this.x;
    }

    /** {@inheritdoc} */
    @Override
    public int getY() {
        return this.y;
    }
}
