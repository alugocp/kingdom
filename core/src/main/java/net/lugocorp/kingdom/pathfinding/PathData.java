package net.lugocorp.kingdom.pathfinding;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class stores persisted data for the A* algorithm across multiple
 * iterations
 */
class PathData {
    private final Point origin;
    private final Map<Point, Integer> gScore = new HashMap<>();
    private final Map<Point, Point> cameFrom = new HashMap<>();
    final Set<Point> openSet = new HashSet<>();

    PathData(Point origin) {
        this.gScore.put(origin, 0);
        this.openSet.add(origin);
        this.origin = origin;
    }

    /**
     * Returns true if the given Point exists within this data
     */
    final boolean hasAlreadySeenPoint(Point p) {
        return this.cameFrom.containsKey(p) || p.equals(this.origin);
    }

    /**
     * Checks the new neighbor Point relative to the given current Point and adds it
     * to state according to A*
     */
    final void checkNewNeighbor(Point current, Point neighbor) {
        final int score = this.gScore.get(current) + 1;
        if (score < this.gScore.getOrDefault(neighbor, Pathfinder.INFINITY)) {
            // TODO there may be some case here where we want lower scoring nodes from
            // previous iterations to return to the openSet (to get the absolute shortest
            // point)
            this.cameFrom.put(neighbor, current);
            this.gScore.put(neighbor, score);
            if (!this.openSet.contains(neighbor)) {
                this.openSet.add(neighbor);
            }
        }
    }

    /**
     * Returns the discovered path as a List of Points
     */
    final List<Point> reconstructPath(Point dest) {
        final List<Point> path = new ArrayList<>();
        Point p = dest;
        path.add(p);

        // Return early if we're already at the origin
        if (p.equals(this.origin)) {
            return path;
        }

        // Go back through the cameFrom map to build the path
        while (!this.cameFrom.get(p).equals(this.origin)) {
            p = this.cameFrom.get(p);
            path.add(0, p);
        }
        return path;
    }

    /**
     * Returns the next Point we should iterate on in the A* algorithm
     */
    final Point getLikelyClosestPoint() {
        int thresh = Pathfinder.INFINITY;
        Point lowest = null;
        for (Point p : this.openSet) {
            final int score = this.gScore.getOrDefault(p, Pathfinder.INFINITY);
            if (score < thresh) {
                thresh = score;
                lowest = p;
            }
        }
        return lowest;
    }

    /**
     * Returns the best Point to kick off the next iteration of A*
     */
    final Point getBestKickoffPoint(Point dest) {
        final Vector3 v = Coords.grid.vector(dest.x, dest.y);
        int d = Pathfinder.INFINITY;
        Point best = null;
        for (Point p : this.gScore.keySet()) {
            final Vector3 v1 = Coords.grid.vector(p.x, p.y);
            final int d1 = (int) Math.sqrt(Math.pow(v.x - v1.x, 2) + Math.pow(v.z - v1.z, 2));
            if (d1 < d) {
                best = p;
                d = d1;
            }
        }
        return best;
    }
}
