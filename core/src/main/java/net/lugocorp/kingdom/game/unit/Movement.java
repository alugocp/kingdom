package net.lugocorp.kingdom.game.unit;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.builtin.animation.MoveAnimation;
import net.lugocorp.kingdom.engine.animation.AnimationChain;
import net.lugocorp.kingdom.engine.userdata.CoordUserData;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.Lambda;
import net.lugocorp.kingdom.utils.code.SideEffect;
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
     * Returns the maximum distance that this Unit can move in a turn
     */
    private int getMaxDistance(GameView view) {
        Events.UnitMoveDistanceEvent event = new Events.UnitMoveDistanceEvent(this.unit);
        this.unit.handleEvent(view, event);
        return event.distance;
    }

    /**
     * Returns the list of Points that this Unit can move to
     */
    public Set<Point> getTargets(GameView view) {
        return this.getPotentialMoveGraph(view).keySet();
    }

    /**
     * Returns a map where each key is a potential move target and each value is the
     * Point we arrive there from
     */
    public Map<Point, Point> getPotentialMoveGraph(GameView view) {
        // Returns nothing if this Unit cannot move
        final int max = this.getMaxDistance(view);
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
                    if (!view.game.world.isInBounds(p1)) {
                        continue;
                    }
                    Tile t = view.game.world.getTile(p1).get();
                    if (t.unit.isPresent()) {
                        continue;
                    }
                    Events.CanUnitMoveEvent event = new Events.CanUnitMoveEvent(this.unit, t);
                    this.unit.handleEvent(view, event);
                    if (!event.possible()) {
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

    /**
     * Returns a list of Points to take you from one to another
     */
    private List<Point> getMovePath(GameView view, Point dest) {
        final Map<Point, Point> graph = this.getPotentialMoveGraph(view);
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
     * Moves this Unit to another Tile in the grid
     */
    public SideEffect move(GameView view, Point p) {
        return SideEffect.all(() -> {
            final List<Point> path = this.getMovePath(view, p);
            final int duration = 1000 / path.size();
            Point prev = this.unit.getPoint();
            AnimationChain chain = new AnimationChain();
            for (Point p1 : path) {
                chain.add(new MoveAnimation(this.unit, duration, prev, p1));
                prev = p1;
            }
            view.animations.add(chain.get());
        }, this.unit.handleEvent(view,
                new Events.UnitMovedEvent(this.unit, this.unit.getX(), this.unit.getY(), p.x, p.y)));
    }
}
