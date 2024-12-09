package net.lugocorp.kingdom.utils.math;

/**
 * Represents a point in a 2D space
 */
public class Point {
    private static final int X_VALUE_CAP = 10000;
    public final int x;
    public final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        if (x >= Point.X_VALUE_CAP) {
            throw new RuntimeException(String.format(
                    "We cannot initialize points where x >= %d because it disrupts the hash code contract",
                    Point.X_VALUE_CAP));
        }
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        return String.format("(%d, %d)", this.x, this.y);
    }

    /** {@inheritdoc} */
    @Override
    public int hashCode() {
        return (this.y * Point.X_VALUE_CAP) + this.x;
    }

    /** {@inheritdoc} */
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Point) {
            Point p = (Point) o;
            return p.x == this.x && p.y == this.y;
        }
        return false;
    }
}
