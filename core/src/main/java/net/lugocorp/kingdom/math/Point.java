package net.lugocorp.kingdom.math;

/**
 * Represents a point in a 2D space
 */
public class Point {
    private static final int X_VALUE_CAP = 10000;
    public int x;
    public int y;

    public Point(int x, int y) {
        this.set(x, y);
    }

    public Point() {
        this(0, 0);
    }

    /**
     * Sets the coordinates associated with this Point
     */
    public void set(int x, int y) {
        this.x = x;
        this.y = y;
        if (x >= Point.X_VALUE_CAP) {
            throw new RuntimeException(String.format(
                    "We cannot initialize points where x >= %d because it disrupts the hash code contract",
                    Point.X_VALUE_CAP));
        }
    }

    /**
     * Calls into set() with another Point's x, y values
     */
    public void set(Point p) {
        this.set(p.x, p.y);
    }

    /**
     * Adds another Point to this Point
     */
    public void add(Point p) {
        this.x += p.x;
        this.y += p.y;
    }

    /**
     * Returns a deep copy of this Point
     */
    public Point copy() {
        return new Point(this.x, this.y);
    }

    /**
     * Very coarse implementation of the distance formula between two Points
     */
    public int distance(Point p) {
        return (int) Math.sqrt(Math.pow(this.x - p.x, 2) + Math.pow(this.y - p.y, 2));
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
