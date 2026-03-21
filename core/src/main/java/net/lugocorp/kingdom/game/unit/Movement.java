package net.lugocorp.kingdom.game.unit;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.builtin.animation.MoveAnimation;
import net.lugocorp.kingdom.engine.animation.AnimationChain;
import net.lugocorp.kingdom.engine.userdata.CoordUserData;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.gameplay.actions.MoveAction;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.math.HexSide;
import net.lugocorp.kingdom.math.Hexagons;
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
    private boolean skipPreCheck = false;

    public Movement(Unit unit, CoordUserData userData) {
        this.userData = userData;
        this.unit = unit;
    }

    /**
     * Skips the move function's pre-check (useful for having Units blindly follow
     * unorthodox movement logic)
     */
    public void turnOffPreCheck() {
        this.skipPreCheck = true;
    }

    /**
     * Unskips the move function's pre-check
     */
    public void turnOnPreCheck() {
        this.skipPreCheck = false;
    }

    /**
     * Moves this Unit to another Tile in the grid. The parallel flag causes this
     * method to handle game state logic (e.g. vision and tile leadership) instead
     * of putting it on the AnimationQueue. This is good when the human player isn't
     * directing individual units' movement (like at the end of the turn or for AI
     * players)
     */
    public SideEffect move(GameView view, List<Point> path, boolean parallel) {
        final SideEffect effects = new SideEffect();

        // Check the Unit's remaining move distance for this turn
        final int distance = Math.min(path.size(), view.game.actions.getRemainingMoveDistance(view, this.unit));
        if (distance < 1) {
            if (this.unit.leadership.belongsToHuman()) {
                effects.add(() -> view.hud.logger.error("The unit cannot move anymore this turn"));
            }
            return effects;
        }

        // Make sure we can still take this path
        if (!this.skipPreCheck) {
            for (int a = 0; a < distance; a++) {
                if (!this.canMoveToPoint(view, path.get(a))) {
                    return effects;
                }
            }
        }

        // Do the actual movements
        final List<Point> previous = this.getPreviousPath(path, distance);
        final Events.UnitMovedEvent before = new Events.UnitMovedEvent(this.unit, path.get(distance - 1), previous,
                parallel);
        final Events.UnitMovedEvent after = new Events.AfterUnitMovedEvent(this.unit, path.get(distance - 1), previous,
                parallel);
            effects.add(this.unit.handleEvent(view, before));
        effects.add(() -> {
            final Point start = this.unit.getPoint().copy();
            final boolean wasOnUnit = view.hud.bot.tileMenu.get().equals(start);
            final AnimationChain chain = new AnimationChain();
            Point prev = start;
            for (int a = 0; a < distance; a++) {
                final Point dest = path.get(a);
                final boolean isLast = a == distance - 1;
                final Optional<HexSide> direction = Hexagons.getDirection(prev, dest);
                if (!direction.isPresent()) {
                    throw new RuntimeException("Should not be here - cannot find vision offset direction");
                }
                // Do some logic after the animation (or right now if we're running parallel
                // movement between different Units)
                final Runnable logic = () -> {
                    this.unit.getLeader()
                            .ifPresent((Player l) -> this.unit.vision.translate(l, view.game.world, direction.get()));
                    this.removeFromPosition(view.game);
                    this.setPosition(view, dest.x, dest.y);
                    if (isLast) {
                        this.unit.handleEvent(view, after).execute();
                        if (wasOnUnit && view.hud.bot.tileMenu.get().equals(start)
                                && this.unit.leadership.belongsToHuman()) {
                            view.hud.bot.minimap.refresh(view.game.world);
                            view.hud.bot.tileMenu.set(dest);
                        }
                    }
                };
                if (parallel) {
                    logic.run();
                } else {
                    chain.add(new MoveAnimation(this.unit, prev, dest, logic));
                }
                prev = dest;
            }
            if (!parallel) {
                view.animations.add(chain.get());
            }
            view.game.actions.unitHasActed(view, this.unit, new MoveAction(view, this.unit, path, distance));
        });
        return effects;
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
