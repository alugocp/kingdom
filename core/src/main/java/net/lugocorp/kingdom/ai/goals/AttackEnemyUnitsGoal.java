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
 * This causes the CompPlayer to attack enemy Units
 */
public class AttackEnemyUnitsGoal extends Goal {

    /** {@inheritdoc} */
    @Override
    protected Decision makeDecision(GameView view, CompPlayer player, DecisionChannel channel) {
        final Set<Unit> targets = this.getPossibleTargets(view, player);

        // Recruit unit handler (recruit a Battle Glyph Unit that is strong against
        // Buildings)
        if (channel.is(DecisionClass.RECRUIT_UNIT)) {
            final int battleUnits = GoalUtils.getUnitsByGlyph(player).get(Glyph.BATTLE);
            Priority priority = Priority.NEUTRAL;
            if (targets.size() > 0 && battleUnits == 0) {
                priority = Priority.NECESSITY;
            } else if (targets.size() > 5 && battleUnits < 3) {
                priority = Priority.OPTIMAL;
            }
            return new Decision(channel, this, priority, new RecruitUnitBehavior(player, Glyph.BATTLE, (Unit u) -> {
                int bonus = 0;
                for (Unit u1 : targets) {
                    if (GoalUtils.isWeakAgainst(view, player, u, u1)) {
                        bonus--;
                    }
                    if (GoalUtils.isStrongAgainst(view, player, u, u1)) {
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
                for (Unit u : targets) {
                    Point p = u.getPoint();
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
                final Chooser<Unit> chooser = new Chooser<>((Integer old, Integer next) -> next > old);
                final Point p = unit.getPoint();
                for (Unit u : targets) {
                    final Optional<Building> occupiedBuilding = view.game.world.getTile(u.getPoint()).get().building;
                    final int maxDamage = GoalUtils.getMaxDamage(view, player, unit, u);
                    final int damageTaken = unit.combat.health.getMax() - unit.combat.health.get();
                    final int nearbyEnemies = GoalUtils.countNearbyUnits(view, player, unit.getPoint(), false);
                    final int nearbyAllies = GoalUtils.countNearbyUnits(view, player, unit.getPoint(), true);
                    final int dist = p.distance(u.getPoint());
                    final int health = u.combat.health.get();
                    if (maxDamage == 0) {
                        continue;
                    }

                    Priority priority = Priority.GOOD_IDEA;
                    if (occupiedBuilding.map((Building b) -> b.isEntityType(EntityType.TOWER)).orElse(false)) {
                        priority = priority.increment();
                    } else if (occupiedBuilding.isPresent()) {
                        priority = priority.decrement();
                    }
                    if (nearbyEnemies == 0) {
                        priority = priority.increment();
                    } else {
                        if (damageTaken > 0) {
                            priority = priority.decrement();
                        }
                        if (nearbyAllies > nearbyEnemies) {
                            priority = priority.increment();
                        } else if (nearbyEnemies > 2) {
                            priority = priority.decrement();
                            if (nearbyEnemies > 4) {
                                priority = priority.decrement();
                            }
                        }
                    }
                    if (dist > 4) {
                        priority = priority.decrement();
                    }
                    if (health / maxDamage > 5) {
                        priority = priority.decrement();
                    } else {
                        priority = priority.increment();
                        if (health / maxDamage <= 2) {
                            priority = priority.increment();
                        }
                    }
                    chooser.choice(u, priority.value);
                }

                // Calculate the Priority and have our Unit attack the target
                if (chooser.getScore() > Priority.FATAL.value) {
                    final Unit target = chooser.get();
                    final Priority priority = Priority.getByValue(chooser.getScore());
                    return new Decision(channel, this, priority, new AttackBehavior(player, unit, target));
                }
            }
        }

        // No decision fallback
        return this.noDecision(channel);
    }

    /**
     * Returns a set of enemy Units that the CompPlayer knows of
     */
    private Set<Unit> getPossibleTargets(GameView view, CompPlayer player) {
        final Set<Unit> targets = new HashSet<>();
        for (Point p : player.memory.getKnownCells()) {
            final Tile tile = view.game.world.getTile(p).get();
            final Optional<Unit> target = tile.unit
                    .flatMap((Unit u) -> u.leadership.belongsToPlayer(player) ? Optional.empty() : Optional.of(u));
            target.ifPresent((Unit u) -> targets.add(u));
        }
        return targets;
    }
}
