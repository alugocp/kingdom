package net.lugocorp.kingdom.utils.math;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

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
        // Calculate the expected number of neighbors
        int expected = 0;
        for (int a = 1; a <= r; a++) {
            expected += a * 6;
        }

        // Collect the neighboring Points
        final List<Point> visited = new ArrayList<>();
        visited.add(p);
        int curr = 0;
        while (curr < visited.size() && visited.size() < expected) {
            Set<Point> adjs = Hexagons.getAdjacents(visited.get(curr));
            for (Point p1 : adjs) {
                if (!visited.contains(p1)) {
                    visited.add(p1);
                }
            }
            curr++;
        }

        // Collect the Points into a set and return
        Set<Point> coords = new HashSet<>();
        coords.addAll(visited);
        return coords;
    }

    /**
     * Returns a set of Points that are adjacent to the given Point in the grid
     */
    public static Set<Point> getAdjacents(Point p) {
        final Set<Point> coords = new HashSet<>();
        if (p.y % 2 == 0) {
            coords.add(new Point(p.x - 1, p.y - 1));
            coords.add(new Point(p.x, p.y - 1));
            coords.add(new Point(p.x - 1, p.y));
            coords.add(new Point(p.x + 1, p.y));
            coords.add(new Point(p.x - 1, p.y + 1));
            coords.add(new Point(p.x, p.y + 1));
        } else {
            coords.add(new Point(p.x, p.y - 1));
            coords.add(new Point(p.x + 1, p.y - 1));
            coords.add(new Point(p.x - 1, p.y));
            coords.add(new Point(p.x + 1, p.y));
            coords.add(new Point(p.x, p.y + 1));
            coords.add(new Point(p.x + 1, p.y + 1));
        }
        return coords;
    }

    /**
     * Returns true if the two given Points are adjacent
     */
    public static boolean areNeighbors(Point p1, Point p2) {
        return (p1.y == p2.y && Math.abs(p1.x - p2.x) == 1)
                || (Math.abs(p1.y - p2.y) == 1 && ((p1.y % 2 == 0 && (p2.x == p1.x || p2.x == p1.x - 1))
                        || (p1.y % 2 == 1 && (p2.x == p1.x || p2.x == p1.x + 1))));
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
                    borders += p.x < p1.x ? HexSide.RIGHT.value : HexSide.LEFT.value;
                } else {
                    boolean right = (p.y % 2 == 0 && p.x == p1.x) || (p.y % 2 == 1 && p.x == p1.x - 1);
                    if (p.y < p1.y) {
                        borders += right ? HexSide.BOT_RIGHT.value : HexSide.BOT_LEFT.value;
                    } else {
                        borders += right ? HexSide.TOP_RIGHT.value : HexSide.TOP_LEFT.value;
                    }
                }
            }
        }
        return borders;
    }

    /**
     * Returns the coordinate translation needed to move in the given direction, as
     * a Point
     */
    public static Point getDirectionTranslation(Point start, HexSide direction) {
        final boolean even = start.y % 2 == 0;
        switch (direction) {
            case TOP_RIGHT :
                return even ? new Point(0, -1) : new Point(1, -1);
            case BOT_RIGHT :
                return even ? new Point(0, 1) : new Point(1, 1);
            case TOP_LEFT :
                return even ? new Point(-1, -1) : new Point(0, -1);
            case BOT_LEFT :
                return even ? new Point(-1, 1) : new Point(0, 1);
            case RIGHT :
                return even ? new Point(1, 0) : new Point(1, 0);
            case LEFT :
            default :
                return even ? new Point(-1, 0) : new Point(-1, 0);
        }
    }

    /**
     * Calculates which Point you'll end up at from some starting Point, direction
     * and distance
     */
    public static Point followLine(Point origin, HexSide direction, int distance) {
        final Point p = new Point(origin.x, origin.y);
        for (int a = 0; a < distance; a++) {
            p.add(Hexagons.getDirectionTranslation(p, direction));
        }
        return p;
    }
}
