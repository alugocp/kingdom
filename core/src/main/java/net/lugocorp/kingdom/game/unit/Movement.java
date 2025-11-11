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
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public SideEffect move(GameView view, List<Point> path) {
        // Check the Unit's remaining move distance for this turn
        final int distance = Math.min(path.size(), view.game.actions.getRemainingMoveDistance(view, this.unit));
        if (distance < 1) {
            return this.unit.leadership.belongsToHuman()
                    ? () -> view.hud.logger.error("The unit cannot move anymore this turn")
                    : SideEffect.none;
        }

        // Do the actual movements
        final List<Point> previous = this.getPreviousPath(path, distance);
        final Events.UnitMovedEvent before = new Events.UnitMovedEvent(this.unit, path.get(distance - 1), previous);
        final Events.UnitMovedEvent after = new Events.AfterUnitMovedEvent(this.unit, path.get(distance - 1), previous);
        return SideEffect.all(this.unit.handleEvent(view, before), () -> {
            final AnimationChain chain = new AnimationChain();
            Point prev = this.unit.getPoint();
            for (int a = 0; a < distance; a++) {
                chain.add(new MoveAnimation(this.unit, prev, path.get(a),
                        a == distance - 1 ? Optional.of(after) : Optional.empty()));
                prev = path.get(a);
            }
            view.animations.add(chain.get());
            view.game.actions.unitHasActed(view, this.unit, new MoveAction(view, this.unit, path, distance));
        });
    }

    /**
     * Returns the previous Points taken in a Unit's movement
     */
    private List<Point> getPreviousPath(List<Point> path, int distance) {
        final List<Point> previous = new ArrayList<>();
        previous.add(new Point(this.unit.getX(), this.unit.getY()));
        for (int a = 0; a < distance - 1; a++) {
            previous.add(path.get(a));
        }
        return previous;
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
}
