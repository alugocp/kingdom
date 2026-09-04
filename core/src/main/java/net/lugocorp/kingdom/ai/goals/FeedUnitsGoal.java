package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.Decision;
import net.lugocorp.kingdom.ai.DecisionChannel;
import net.lugocorp.kingdom.ai.DecisionClass;
import net.lugocorp.kingdom.ai.Goal;
import net.lugocorp.kingdom.ai.Priority;
import net.lugocorp.kingdom.ai.behaviors.FollowEntityBehavior;
import net.lugocorp.kingdom.ai.behaviors.GiveItemBehavior;
import net.lugocorp.kingdom.ai.behaviors.ListBehavior;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.properties.Inventory;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.pathfinding.Pathfinder;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This causes the CompPlayer to keep its Units fed
 */
public class FeedUnitsGoal extends Goal {

    /** {@inheritdoc} */
    @Override
    protected Decision makeDecision(GameView view, CompPlayer player, DecisionChannel channel) {
        // UNIT handler
        if (channel.is(DecisionClass.UNIT)) {
            final Unit unit = channel.getUnit();
            final int hunger = unit.hunger.get(view);
            final int speed = unit.movement.getMaxDistance(view);
            if (hunger > unit.hunger.getTurnsBeforeHunger() / 2 || unit.haul.getEdibleItems(view, unit).size() > 0) {
                return this.noDecision(channel);
            }

            // Find something that we can path to
            int distance = 0;
            Entity best = null;
            final Pathfinder pathfinder = new Pathfinder(unit);
            for (Building b : player.buildings) {
                final Set<Item> food = b.items.map((Inventory i) -> i.getEdibleItems(view, unit))
                        .orElse(new HashSet<Item>());
                if (food.size() > 0) {
                    final List<Point> path = pathfinder.getPath(view, b.getPoint());
                    if (path.size() > 0 && path.size() < speed * (hunger - 1)) {
                        if (path.size() <= speed) {
                            return this.getFeedDecision(view, channel, unit, b);
                        }
                        if (best == null || path.size() < distance) {
                            distance = path.size();
                            best = b;
                        }
                    }
                }
            }
            for (Unit u : player.units) {
                final Set<Item> food = u.haul.getEdibleItems(view, unit);
                if (food.size() > 0) {
                    final List<Point> path = pathfinder.getPath(view, u.getPoint());
                    if (path.size() > 0 && path.size() < speed * (hunger - 3)) {
                        if (best == null || path.size() < distance) {
                            distance = path.size();
                            best = u;
                        }
                    }
                }
            }
            if (best != null) {
                return this.getFeedDecision(view, channel, unit, best);
            }
        }

        // No decision fallback
        return this.noDecision(channel);
    }

    /**
     * Returns the Behavior that allows the Unit to feed from the target Entity
     */
    private Decision getFeedDecision(GameView view, DecisionChannel channel, Unit unit, Entity target) {
        return new Decision(channel, this, Priority.OPTIMAL, new ListBehavior(new FollowEntityBehavior(unit, target),
                new GiveItemBehavior(target, unit, (Item i) -> unit.hunger.canEat(view, i))));
    }
}
