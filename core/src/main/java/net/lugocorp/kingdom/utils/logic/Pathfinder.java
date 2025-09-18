package net.lugocorp.kingdom.utils.logic;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.Lambda;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class implements the A* pathfinding algorithm
 */
public class Pathfinder {
    private static final int INFINITY = (int) Float.POSITIVE_INFINITY;

    public static final List<Point> pathfind(GameView view, Unit unit, Point dest) {
        // TODO optimize this for larger map sizes one day
        final Map<Point, Integer> gScore = new HashMap<>();
        final Map<Point, Point> cameFrom = new HashMap<>();
        final Set<Point> openSet = new HashSet<>();
        gScore.put(unit.getPoint(), 0);
        openSet.add(unit.getPoint());

        // If the destination Point is occupied (or this Unit otherwise cannot move
        // there) then we can return early
        if (!unit.movement.canMoveToPoint(view, dest)) {
            return new ArrayList<Point>();
        }

        // A* algorithm iteration
        while (openSet.size() > 0) {
            final Point current = Pathfinder.getLikelyClosestPoint(openSet, gScore);
            if (current.equals(dest)) {
                return Pathfinder.reconstructPath(cameFrom, unit.getPoint(), current);
            }

            // Haven't found the path yet, keep going
            openSet.remove(current);
            for (Point neighbor : Pathfinder.getNeighbors(view, unit, current)) {
                final int score = gScore.get(current) + 1;
                if (score < gScore.getOrDefault(neighbor, Pathfinder.INFINITY)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, score);
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        // No complete path found
        return new ArrayList<Point>();
    }

    /**
     * Returns the discovered path as a List of Points
     */
    private static final List<Point> reconstructPath(Map<Point, Point> cameFrom, Point origin, Point dest) {
        final List<Point> path = new ArrayList<>();
        Point p = dest;
        path.add(p);
        while (!cameFrom.get(p).equals(origin)) {
            p = cameFrom.get(p);
            path.add(0, p);
        }
        return path;
    }

    /**
     * Returns the adjacent Points that the Unit can traverse to
     */
    private static final Set<Point> getNeighbors(GameView view, Unit unit, Point p) {
        return Lambda.filter((Point p1) -> unit.movement.canMoveToPoint(view, p1), Hexagons.getAdjacents(p));
    }

    /**
     * Returns the next Point we should iterate on in the A* algorithm
     */
    private static final Point getLikelyClosestPoint(Set<Point> openSet, Map<Point, Integer> gScore) {
        int thresh = Pathfinder.INFINITY;
        Point lowest = null;
        for (Point p : openSet) {
            final int score = gScore.getOrDefault(p, Pathfinder.INFINITY);
            if (score < thresh) {
                thresh = score;
                lowest = p;
            }
        }
        return lowest;
    }
}
