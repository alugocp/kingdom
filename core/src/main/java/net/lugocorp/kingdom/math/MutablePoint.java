package net.lugocorp.kingdom.math;

/**
 * Represents a mutable point in a 2D space
 */
public class MutablePoint extends Pointlike {
    public int x;
    public int y;

    public MutablePoint(int x, int y) {
        this.set(x, y);
    }

    public MutablePoint() {
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

    /**
     * Sets the coordinates associated with this MutablePoint
     */
    public void set(int x, int y) {
        this.x = x;
        this.y = y;
        if (x >= Pointlike.X_VALUE_CAP) {
            throw new RuntimeException(String.format(
                    "We cannot initialize points where x >= %d because it disrupts the hash code contract",
                    Pointlike.X_VALUE_CAP));
        }
    }

    /**
     * Calls into set() with another Pointlike's x, y values
     */
    public void set(Pointlike p) {
        this.set(p.getX(), p.getY());
    }

    /**
     * Adds another Pointlike to this MutablePoint
     */
    public void add(Pointlike p) {
        this.x += p.getX();
        this.y += p.getY();
    }

    /**
     * Returns a deep copy of this MutablePoint
     */
    public MutablePoint copy() {
        return new MutablePoint(this.x, this.y);
    }

    /**
     * Returns an immutable version of this MutablePoint
     */
    public Point toPoint() {
        return new Point(this.x, this.y);
    }
}
