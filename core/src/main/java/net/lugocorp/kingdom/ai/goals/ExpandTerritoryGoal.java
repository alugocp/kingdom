package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.Decision;
import net.lugocorp.kingdom.ai.DecisionChannel;
import net.lugocorp.kingdom.ai.DecisionClass;
import net.lugocorp.kingdom.ai.Goal;
import net.lugocorp.kingdom.ai.GoalUtils;
import net.lugocorp.kingdom.ai.Priority;
import net.lugocorp.kingdom.ai.behaviors.AttackBehavior;
import net.lugocorp.kingdom.ai.behaviors.RecruitUnitBehavior;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Tower;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.properties.EntityType;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Chooser;
import net.lugocorp.kingdom.utils.Lambda;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * This causes the CompPlayer to take Towers
 */
public class ExpandTerritoryGoal extends Goal {

    /** {@inheritdoc} */
    @Override
    protected Decision makeDecision(GameView view, CompPlayer player, DecisionChannel channel) {
        final Set<Tower> targets = this.getPossibleTargets(view, player);

        // Recruit unit handler (recruit a Battle Glyph Unit that is strong against
        // Towers)
        if (channel.is(DecisionClass.RECRUIT_UNIT)) {
            final int battleUnits = GoalUtils.getUnitsByGlyph(player).get(Glyph.BATTLE);
            Priority priority = Priority.NEUTRAL;
            if (targets.size() > 0 && battleUnits == 0) {
                priority = Priority.NECESSITY;
            } else if (battleUnits < targets.size()) {
                priority = Priority.OPTIMAL;
            }
            return new Decision(channel, this, priority, new RecruitUnitBehavior(player, Glyph.BATTLE, (Unit u) -> {
                int bonus = 0;
                for (Tower t : targets) {
                    if (GoalUtils.isWeakAgainst(view, player, u, t)) {
                        bonus--;
                    }
                    if (GoalUtils.isStrongAgainst(view, player, u, t)) {
                        bonus++;
                    }
                }
                return bonus > 2
                        ? Priority.OPTIMAL
                        : (bonus > 0 ? Priority.GOOD_IDEA : (bonus < 0 ? Priority.BAD_IDEA : Priority.NEUTRAL));
            }, (Set<Point> options) -> {
                if (targets.size() == 0) {
                    return Lambda.random(options);
                }

                // Get the option that's the closest to any given target
                final Chooser<Point> chooser = new Chooser<>((Integer old, Integer next) -> next < old);
                for (Tower t : targets) {
                    Point p = t.getPoint();
                    for (Point p1 : options) {
                        chooser.choice(p1, p.distance(p1));
                    }
                }
                return chooser.get();
            }));
        }

        // Unit handler
        if (channel.is(DecisionClass.UNIT)) {
            final Unit unit = channel.getUnit();

            if (unit.glyphs.has(Glyph.BATTLE) && targets.size() > 0) {
                final Chooser<Tower> chooser = new Chooser<>((Integer old, Integer next) -> next < old);
                final Point p = unit.getPoint();
                for (Tower t : targets) {
                    chooser.choice(t, p.distance(t.getPoint()));
                }

                // Calculate the Priority and have our Unit attack the target
                final Tower target = chooser.get();
                Priority priority = Priority.GOOD_IDEA;
                if (GoalUtils.isStrongAgainst(view, player, unit, target)) {
                    priority = Priority.OPTIMAL;
                } else if (GoalUtils.isWeakAgainst(view, player, unit, target)) {
                    priority = Priority.NEUTRAL;
                }
                return new Decision(channel, this, priority, new AttackBehavior(player, unit, target));
            }
        }

        // No decision fallback
        return this.noDecision(channel);
    }

    /**
     * Returns a set of Towers that the CompPlayer knows of
     */
    private Set<Tower> getPossibleTargets(GameView view, CompPlayer player) {
        final Set<Tower> targets = new HashSet<>();
        for (Point p : player.memory.getKnownCells()) {
            final Tile tile = view.game.world.getTile(p).get();
            final Optional<Tower> tower = tile.building
                    .flatMap((Building b) -> b.leadership.belongsToNobody() && b.isEntityType(EntityType.TOWER)
                            ? Optional.of((Tower) b)
                            : Optional.empty());
            tower.ifPresent((Tower t) -> targets.add(t));
        }
        return targets;
    }
}
