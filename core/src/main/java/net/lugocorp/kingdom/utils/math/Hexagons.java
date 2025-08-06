package net.lugocorp.kingdom.utils.math;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Handles all math related to the game's hexagonal grid space
 */
public class Hexagons {
    public static final int BORDER_LEFT = 1;
    public static final int BORDER_RIGHT = 2;
    public static final int BORDER_TOP_LEFT = 4;
    public static final int BORDER_TOP_RIGHT = 8;
    public static final int BORDER_BOT_LEFT = 16;
    public static final int BORDER_BOT_RIGHT = 32;
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

    /**
     * Returns an integer representing borders based on some criteria for the given
     * Point
     */
    public static int getBorderInteger(Point p, Function<Point, Boolean> criteria) {
        Set<Point> neighbors = Hexagons.getNeighbors(p, 1);
        int borders = 0;
        for (Point p1 : neighbors) {
            if (criteria.apply(p1)) {
                if (p.y == p1.y) {
                    borders += p.x < p1.x ? Hexagons.BORDER_RIGHT : Hexagons.BORDER_LEFT;
                } else {
                    boolean right = (p.y % 2 == 0 && p.x == p1.x) || (p.y % 2 == 1 && p.x == p1.x - 1);
                    if (p.y < p1.y) {
                        borders += right ? Hexagons.BORDER_BOT_RIGHT : Hexagons.BORDER_BOT_LEFT;
                    } else {
                        borders += right ? Hexagons.BORDER_TOP_RIGHT : Hexagons.BORDER_TOP_LEFT;
                    }
                }
            }
        }
        return borders;
    }
}
