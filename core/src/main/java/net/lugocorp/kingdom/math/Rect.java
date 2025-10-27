package net.lugocorp.kingdom.math;

/**
 * Represents a rectangle in 2D space
 */
public class Rect {
    public final int x;
    public final int y;
    public final int w;
    public final int h;

    public Rect(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /**
     * Returns true if the given Point falls within this Rect
     */
    public boolean contains(Point p) {
        return p.x >= this.x && p.x <= this.x + this.w && p.y >= this.y && p.y <= this.y + this.h;
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        return String.format("(%d, %d, %d, %d)", this.x, this.y, this.w, this.h);
    }
}
