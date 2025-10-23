package net.lugocorp.kingdom.utils.pathfinding;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.Lambda;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class implements the A* pathfinding algorithm and can be iterated on
 * multiple times
 */
public class Pathfinder {
    static final int INFINITY = (int) Float.POSITIVE_INFINITY;
    private final PathData data;
    private final Unit unit;
    // TODO optimize this for larger map sizes one day

    public Pathfinder(Unit unit) {
        this.data = new PathData(unit.getPoint());
        this.unit = unit;
    }

    /**
     * Returns the cached path to the given Point, or calculates such a path on the
     * fly
     */
    public final List<Point> getPath(GameView view, Point dest) {
        // Return a path that has already been found by a previous iteration
        if (this.data.hasAlreadySeenPoint(dest)) {
            return this.data.reconstructPath(dest);
        }

        // Or, kick off a new iteration if no such path exists yet
        if (this.data.openSet.size() == 0) {
            this.data.openSet.add(this.data.getBestKickoffPoint(dest));
        }
        return this.pathfind(view, dest);
    }

    /**
     * Returns the adjacent Points that the Unit can traverse to
     */
    private final Set<Point> getNeighbors(GameView view, Point p) {
        return Lambda.filter((Point p1) -> this.unit.movement.canMoveToPoint(view, p1), Hexagons.getAdjacents(p));
    }

    /**
     * This method implements another iteration of the A* algorithm for a new
     * destination Point
     */
    private final List<Point> pathfind(GameView view, Point dest) {
        // If the destination Point is occupied (or this Unit otherwise cannot move
        // there) then we can return early
        if (!unit.movement.canMoveToPoint(view, dest)) {
            return new ArrayList<Point>();
        }

        // A* algorithm iteration
        while (this.data.openSet.size() > 0) {
            final Point current = this.data.getLikelyClosestPoint();
            if (current.equals(dest)) {
                return this.data.reconstructPath(dest);
            }

            // Haven't found the path yet, keep going
            this.data.openSet.remove(current);
            for (Point neighbor : this.getNeighbors(view, current)) {
                this.data.checkNewNeighbor(current, neighbor);
            }
        }

        // No complete path found
        return new ArrayList<Point>();
    }
}
