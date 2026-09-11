package net.lugocorp.kingdom.math;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a series of Points in a particular order
 */
public class Path implements Iterable<Point> {
    private final List<Point> points = new ArrayList<>();

    /**
     * Returns true if there are no Points in this Path yet
     */
    public boolean isEmpty() {
        return this.points.size() == 0;
    }

    /**
     * Adds a Point to this Path
     */
    public Path add(Point p) {
        this.points.add(p);
        return this;
    }

    /**
     * Prepends a Point to this Path
     */
    public Path prepend(Point p) {
        this.points.add(0, p);
        return this;
    }

    /**
     * Removes and returns the first Point in this Path
     */
    public Point popFromFront() {
        return this.points.remove(0);
    }

    /**
     * Removes and returns the last Point in this Path
     */
    public Point pop() {
        return this.points.remove(this.points.size() - 1);
    }

    /**
     * Concatenates another Path onto this one
     */
    public Path concat(Path path) {
        for (Point p : path.points) {
            this.add(p);
        }
        return this;
    }

    /** {@inheritdoc} */
    @Override
    public Iterator<Point> iterator() {
        return this.points.iterator();
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        if (this.points.size() == 0) {
            return "";
        }
        final StringBuilder builder = new StringBuilder(this.points.get(0).toString());
        for (int a = 1; a < this.points.size(); a++) {
            builder.append(", ");
            builder.append(this.points.get(a).toString());
        }
        return builder.toString();
    }

    /** {@inheritdoc} */
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    /** {@inheritdoc} */
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Path) {
            Path p = (Path) o;
            return this.toString().equals(p.toString());
        }
        return false;
    }
}
