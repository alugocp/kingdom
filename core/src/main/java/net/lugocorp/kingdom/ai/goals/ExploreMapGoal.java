package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.Behavior;
import net.lugocorp.kingdom.ai.Decision;
import net.lugocorp.kingdom.ai.DecisionChannel;
import net.lugocorp.kingdom.ai.DecisionClass;
import net.lugocorp.kingdom.ai.Goal;
import net.lugocorp.kingdom.ai.Priority;
import net.lugocorp.kingdom.ai.behaviors.MoveUnitBehavior;
import net.lugocorp.kingdom.ai.behaviors.RecruitUnitBehavior;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.pathfinding.Pathfinder;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import java.util.List;
import java.util.Set;

/**
 * This causes the CompPlayer to explore the World
 */
public class ExploreMapGoal extends Goal {

    /** {@inheritdoc} */
    @Override
    protected Decision makeDecision(GameView view, CompPlayer player, DecisionChannel channel) {
        final World world = view.game.world;
        final float ratio = (float) player.memory.getKnownCells().size() / (float) world.getSize();
        final int fastUnits = this.getNumberOfFastUnits(view, player);

        // Recruit unit handler (recruit a fast Trade Glyph Unit)
        if (channel.is(DecisionClass.RECRUIT_UNIT)) {
            Priority p = Priority.NEUTRAL;
            if (fastUnits == 2) {
                p = Priority.GOOD_IDEA;
            }
            if (fastUnits == 1) {
                p = Priority.OPTIMAL;
            }
            if (fastUnits == 0) {
                p = Priority.NECESSITY;
            }
            return new Decision(channel, this, p,
                    new RecruitUnitBehavior(player, Glyph.TRADE,
                            (Unit u) -> u.movement.getMaxDistance(view) > 2 ? Priority.GOOD_IDEA : Priority.NEUTRAL,
                            (Set<Point> options) -> options.size() > 0 ? Lambda.random(options) : null));
        }

        // Unit handler
        if (channel.is(DecisionClass.UNIT)) {
            final int max = channel.getUnit().movement.getMaxDistance(view);

            // Slow Units
            if (max < 2) {
                Priority p = ratio < 0.1 ? Priority.NEUTRAL : Priority.BAD_IDEA;
                if (fastUnits > 0) {
                    p = p.decrement();
                }
                return this.getExplorationDecision(view, player, channel, p);
            }

            // Fast Units
            if (max > 2) {
                // Good or optimal idea
                Priority p = Priority.NEUTRAL;
                if (ratio < 0.1) {
                    p = Priority.NECESSITY;
                } else if (ratio < 0.35) {
                    p = Priority.OPTIMAL;
                } else if (ratio < 0.80) {
                    p = Priority.GOOD_IDEA;
                }
                if (fastUnits > 2) {
                    p = p.decrement();
                }
                return this.getExplorationDecision(view, player, channel, p);
            }

            // Average Units
            Priority p = ratio < 0.6 ? Priority.GOOD_IDEA : Priority.NEUTRAL;
            if (fastUnits > 1) {
                p = p.decrement();
            }
            return this.getExplorationDecision(view, player, channel, p);
        }

        // No decision fallback
        return this.noDecision(channel);
    }

    /**
     * Returns the number of fast Units controlled by the given CompPlayer
     */
    private int getNumberOfFastUnits(GameView view, CompPlayer player) {
        int count = 0;
        for (Unit u : player.units) {
            if (u.movement.getMaxDistance(view) > 2) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns a Decision that moves the given Unit towards some nearby, unexplored
     * part of the World
     */
    private Decision getExplorationDecision(GameView view, CompPlayer player, DecisionChannel channel,
            Priority priority) {
        Behavior behavior = this.getExplorationBehavior(view, player, channel.getUnit());
        if (behavior == null) {
            return this.noDecision(channel);
        }
        return new Decision(channel, this, priority, this.getExplorationBehavior(view, player, channel.getUnit()));
    }

    /**
     * Returns a Behavior that sets a destination for the given Unit
     */
    private Behavior getExplorationBehavior(GameView view, CompPlayer player, Unit unit) {
        final Pathfinder pathfinder = new Pathfinder(unit);
        List<Point> best = null;
        int highest = 0;
        // TODO can optimize this later to only check border known tiles
        for (Point p : player.memory.getKnownCells()) {
            if (p.equals(unit.getPoint())) {
                continue;
            }
            final int score = (this.getAdjacentUnknownTiles(view, player, p) * 5) - (unit.getPoint().distance(p) * 3);
            if (score > highest) {
                final List<Point> path = pathfinder.getPath(view, p);
                if (path.size() > 0) {
                    highest = score;
                    best = path;
                }
            }
        }
        return best == null ? null : new MoveUnitBehavior(unit, best);
    }

    /**
     * Returns the number of unknown Tiles adjacent to the given Point
     */
    private int getAdjacentUnknownTiles(GameView view, CompPlayer player, Point p) {
        int unknown = 0;
        for (Point a : Hexagons.getAdjacents(p)) {
            if (view.game.world.isInBounds(p) && !player.memory.getKnownCells().contains(a)) {
                unknown++;
            }
        }
        return unknown;
    }
}
