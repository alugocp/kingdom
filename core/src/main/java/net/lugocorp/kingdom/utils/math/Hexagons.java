package net.lugocorp.kingdom.utils.math;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles all math related to the game's hexagonal grid space
 */
public class Hexagons {
    public static final float SIDE = 0.5773505f;
    public static final float DEPTH = 1.154701f;
    public static final float WIDTH = 1f;
    public static final float HEIGHT = 0.5f;
    public static final float DEPTH_DIFF = 0.288675134595f;

    /**
     * Retrieves a Set of all hexagons within some radius of the specified Point on
     * the grid
     */
    public static Set<Point> getNeighbors(Point p, int r) {
        double thresh = Math.pow(Hexagons.WIDTH * r, 2) * 1.01; // 1.01 accounts for rounding errors
        float[] p1 = Coords.grid.coordinates(p.x, p.y);
        Set<Point> coords = new HashSet<>();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                float[] p2 = Coords.grid.coordinates(p.x + dx, p.y + dy);
                if (Math.pow(p1[0] - p2[0], 2) + Math.pow(p1[1] - p2[1], 2) <= thresh) {
                    coords.add(new Point(p.x + dx, p.y + dy));
                }
            }
        }
        return coords;
    }
}
