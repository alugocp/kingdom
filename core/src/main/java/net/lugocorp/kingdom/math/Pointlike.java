package net.lugocorp.kingdom.math;

/**
 * Parent class for mutable and immutable integer tuples
 */
public abstract class Pointlike {
    static final int X_VALUE_CAP = 10000;

    /**
     * Returns the x value
     */
    public abstract int getX();

    /**
     * Returns the y value
     */
    public abstract int getY();

    /**
     * Very coarse implementation of the distance formula between two Points
     */
    public int distance(Pointlike p) {
        return (int) Math.sqrt(Math.pow(this.getX() - p.getX(), 2) + Math.pow(this.getY() - p.getY(), 2));
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        return String.format("(%d, %d)", this.getX(), this.getY());
    }

    /** {@inheritdoc} */
    @Override
    public int hashCode() {
        return (this.getY() * Pointlike.X_VALUE_CAP) + this.getX();
    }

    /** {@inheritdoc} */
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Pointlike) {
            final Pointlike p = (Pointlike) o;
            return p.getX() == this.getX() && p.getY() == this.getY();
        }
        return false;
    }
}
