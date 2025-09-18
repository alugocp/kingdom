package net.lugocorp.kingdom.game.unit;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.builtin.animation.MoveAnimation;
import net.lugocorp.kingdom.engine.animation.AnimationChain;
import net.lugocorp.kingdom.engine.userdata.CoordUserData;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.actions.MoveAction;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.Lambda;
import net.lugocorp.kingdom.utils.code.SideEffect;
import net.lugocorp.kingdom.utils.logic.Pathfinder;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This class captures all movement logic for a Unit
 */
public class Movement {
    private final CoordUserData userData;
    private final Unit unit;

    public Movement(Unit unit, CoordUserData userData) {
        this.userData = userData;
        this.unit = unit;
    }

    /**
     * Moves this Unit to another Tile in the grid
     */
    public SideEffect move(GameView view, Point p) {
        final int max = this.getMaxDistance(view);
        final Map<Point, Point> graph = this.getPotentialMoveGraph(view, max);

        // If we can move to the destination this turn then do so
        if (graph.containsKey(p)) {
            return SideEffect.all(() -> {
                final List<Point> path = this.getMovePath(view, graph, p);
                Point prev = this.unit.getPoint();
                AnimationChain chain = new AnimationChain();
                for (Point p1 : path) {
                    chain.add(new MoveAnimation(this.unit, prev, p1));
                    prev = p1;
                }
                view.animations.add(chain.get());
                view.game.actions.unitHasActed(view, this.unit, new MoveAction(view, this.unit, path, path.size()));
            }, this.unit.handleEvent(view,
                    new Events.UnitMovedEvent(this.unit, this.unit.getX(), this.unit.getY(), p.x, p.y)));
        }

        // If we cannot move to the destination this single turn, then use a more
        // expensive pathfinding algorithm
        final List<Point> path = Pathfinder.pathfind(view, this.unit, p);
        if (path.size() < max) {
            // If the path is shorter than our max move distance (it should only ever be 0
            // in this case)
            // then there is no possible path here. Alert the player if they're human.
            if (path.size() > 0) {
                throw new RuntimeException("A* found too short a path, this should not happen");
            }
            return this.unit.leadership.belongsToHuman()
                    ? () -> view.logger.error("Unit cannot move there")
                    : SideEffect.none;
        }
        return SideEffect.all(
                () -> view.game.actions.unitHasActed(view, this.unit, new MoveAction(view, this.unit, path, 0)),
                this.move(view, path.get(max - 1)));
    }

    /**
     * Sets this Unit's position in the World. Useful for spawning or movement.
     */
    public void setPosition(GameView view, int x, int y) {
        Tile destin = view.game.world.getTile(x, y).get();
        destin.unit = Optional.of(this.unit);
        this.unit.setX(x);
        this.unit.setY(y);
        this.unit.resetModelPosition();
        view.game.setLeader(view, destin, this.unit.getLeader());
        destin.building.ifPresent((Building b) -> b.setAlpha(0.5f));
        this.userData.point.x = x;
        this.userData.point.y = y;
    }

    /**
     * Removes this Unit from its current position in the World
     */
    public void removeFromPosition(Game g) {
        Tile origin = g.world.getTile(this.unit.getX(), this.unit.getY()).get();
        origin.building.ifPresent((Building b) -> b.setAlpha(1f));
        origin.unit = Optional.empty();
    }

    /**
     * Returns the maximum distance that this Unit can move in a turn
     */
    public int getMaxDistance(GameView view) {
        Events.UnitMoveDistanceEvent event = new Events.UnitMoveDistanceEvent(this.unit);
        this.unit.handleEvent(view, event);
        return event.distance;
    }

    /**
     * Returns the list of Points that this Unit can move to
     */
    public Set<Point> getTargets(GameView view, int max) {
        return this.getPotentialMoveGraph(view, max).keySet();
    }

    /**
     * Returns the list of Points that this Unit can move to
     */
    public Set<Point> getTargets(GameView view) {
        return this.getPotentialMoveGraph(view, this.getMaxDistance(view)).keySet();
    }

    /**
     * Returns true if the given Unit can move to the given Point
     */
    public boolean canMoveToPoint(GameView view, Point p) {
        // Cannot move out of bounds
        if (!view.game.world.isInBounds(p)) {
            return false;
        }

        // Cannot move if a Unit already exists there
        Tile t = view.game.world.getTile(p).get();
        if (t.unit.isPresent()) {
            return false;
        }

        // Check if this Unit is allowed to move there (Tile and Building logic)
        Events.CanUnitMoveEvent event = new Events.CanUnitMoveEvent(this.unit, t);
        this.unit.handleEvent(view, event);
        return event.possible();
    }

    /**
     * Returns a list of Points to take you from one to another
     */
    private List<Point> getMovePath(GameView view, Map<Point, Point> graph, Point dest) {
        if (!graph.containsKey(dest)) {
            throw new RuntimeException(String.format("Invalid destination %s", dest.toString()));
        }

        List<Point> path = new ArrayList<>();
        path.add(dest);
        while (!Hexagons.areNeighbors(path.get(0), this.unit.getPoint())) {
            path.add(0, graph.get(path.get(0)));
        }
        return path;
    }

    /**
     * Returns a map where each key is a potential move target and each value is the
     * Point we arrive there from
     */
    private Map<Point, Point> getPotentialMoveGraph(GameView view, int max) {
        // Returns nothing if this Unit cannot move
        if (max == 0) {
            return new HashMap<Point, Point>();
        }

        // Set up the algorithm's variables
        final Point origin = new Point(this.unit.getX(), this.unit.getY());
        final Map<Point, Integer> distance = new HashMap<>();
        final Map<Point, Point> graph = new HashMap<>();
        distance.put(origin, 0);

        // Iterate for each distance up to the max
        for (int a = 0; a < max; a++) {
            // Do this for each Point with the current distance value
            final int steps = a;
            final Set<Point> outside = Lambda.filter((Point p) -> distance.get(p) == steps, distance.keySet());
            for (Point p : outside) {
                // Do this for each of the current Point's adjacents,
                // unless if they've already been processed
                final Set<Point> adjs = Hexagons.getAdjacents(p);
                for (Point p1 : adjs) {
                    // Skip this adjacent Point if we've already processed it
                    if (distance.containsKey(p1)) {
                        continue;
                    }

                    // Check if we can move to this adjacent Point
                    if (!this.canMoveToPoint(view, p1)) {
                        continue;
                    }

                    // If we can move to this adjacent Point then record it in the graph
                    distance.put(p1, a + 1);
                    graph.put(p1, p);
                }
            }
        }
        return graph;
    }
}
