package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.Decision;
import net.lugocorp.kingdom.ai.DecisionChannel;
import net.lugocorp.kingdom.ai.DecisionClass;
import net.lugocorp.kingdom.ai.Goal;
import net.lugocorp.kingdom.ai.GoalUtils;
import net.lugocorp.kingdom.ai.Priority;
import net.lugocorp.kingdom.ai.behaviors.HealBehavior;
import net.lugocorp.kingdom.ai.behaviors.RecruitUnitBehavior;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.prediction.Prediction;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Chooser;
import net.lugocorp.kingdom.utils.Lambda;
import net.lugocorp.kingdom.utils.Wrapped;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This causes the CompPlayer to heal their own Units and Buildings
 */
public class KeepEntitiesHealedGoal extends Goal {

    /** {@inheritdoc} */
    @Override
    protected Decision makeDecision(GameView view, CompPlayer player, DecisionChannel channel) {
        final Set<Entity> targets = this.getPossibleTargets(view, player);

        // Recruit unit handler (recruit a Support Glyph Unit that has a healing spell)
        if (channel.is(DecisionClass.RECRUIT_UNIT)) {
            final Map<Unit, Set<Entity>> healMap = this.getHealMap(view, player, targets);
            Priority priority = Priority.NEUTRAL;
            if (targets.size() > 0 && healMap.size() == 0) {
                priority = Priority.NECESSITY;
            } else if (targets.size() > 5 && healMap.size() < 3) {
                priority = Priority.OPTIMAL;
            }
            return new Decision(channel, this, priority, new RecruitUnitBehavior(player, Glyph.SUPPORT, (Unit u) -> {
                for (Entity e : targets) {
                    if (this.canUnitHealTarget(view, player, u, e)) {
                        return Priority.GOOD_IDEA;
                    }
                }
                return Priority.NEUTRAL;
            }, (Set<Point> options) -> {
                if (targets.size() == 0) {
                    return Lambda.random(options);
                }

                // Get the option that's the closest to any given target
                final Chooser<Point> chooser = new Chooser<>((Integer old, Integer next) -> next < old);
                for (Point p : options) {
                    int dist = 0;
                    for (Entity e : targets) {
                        dist += p.distance(e.getPoint());
                    }
                    chooser.choice(p, dist / targets.size());
                }
                return chooser.get();
            }));
        }

        // Unit handler
        if (channel.is(DecisionClass.UNIT)) {
            final Unit unit = channel.getUnit();

            if (unit.glyphs.has(Glyph.SUPPORT) && targets.size() > 0) {
                final Map<Unit, Set<Entity>> healMap = this.getHealMap(view, player, targets);
                final Chooser<Entity> chooser = new Chooser<>((Integer old, Integer next) -> next > old);
                final Point p = unit.getPoint();
                for (Entity e : healMap.get(unit)) {
                    final Point p1 = e.getPoint();
                    final int nearbyEnemies = GoalUtils.countNearbyUnits(view, player, p1, false);
                    final int score = (e.combat.health.getMax() - e.combat.health.get()) - (p.distance(p1) / 2)
                            + nearbyEnemies;
                    chooser.choice(e, score);
                }
                if (chooser.has()) {
                    final Entity target = chooser.get();
                    Priority priority = Priority.FATAL;
                    if (target.combat.health.get() < target.combat.health.getMax() / 2) {
                        priority = Priority.NECESSITY;
                    } else if (target.combat.health.get() < target.combat.health.getMax() * 3 / 4) {
                        priority = Priority.OPTIMAL;
                    } else if (target.combat.health.get() < target.combat.health.getMax()) {
                        priority = Priority.GOOD_IDEA;
                    }
                    if (priority != Priority.FATAL) {
                        return new Decision(channel, this, priority, new HealBehavior(player, unit, target));
                    }
                }
            }
        }

        // No decision fallback
        return this.noDecision(channel);
    }

    /**
     * Returns a set of Units and Buildings that need healing
     */
    private Set<Entity> getPossibleTargets(GameView view, CompPlayer player) {
        final Set<Entity> targets = new HashSet<>();
        for (Unit u : player.units) {
            if (u.combat.health.get() < u.combat.health.getMax()) {
                targets.add(u);
            }
        }
        for (Building b : player.buildings) {
            if (b.combat.health.isVulnerable() && b.combat.health.get() < b.combat.health.getMax()) {
                targets.add(b);
            }
        }
        return targets;
    }

    /**
     * Returns a map of which Entities can be healed by the given Unit
     */
    private Map<Unit, Set<Entity>> getHealMap(GameView view, CompPlayer player, Set<Entity> targets) {
        final Map<Unit, Set<Entity>> map = new HashMap<>();
        for (Unit u : player.units) {
            if (!u.glyphs.has(Glyph.SUPPORT)) {
                continue;
            }
            final Set<Entity> filtered = Lambda.filter((Entity e) -> this.canUnitHealTarget(view, player, u, e),
                    targets);
            if (filtered.size() > 0) {
                map.put(u, filtered);
            }
        }
        return map;
    }

    /**
     * Returns true if the given Unit has some spell that can heal the given target
     * Entity
     */
    private boolean canUnitHealTarget(GameView view, CompPlayer player, Unit healer, Entity target) {
        for (Ability active : healer.abilities.getActives()) {
            for (Point override : Hexagons.getNeighbors(target.getPoint(), 3)) {
                final Wrapped<Boolean> heals = new Wrapped<>(false);
                player.actor.analyze.activeAbility(view, active, override, (Prediction prediction) -> {
                    for (Event event : prediction.log) {
                        if (event instanceof Events.HealEntityEvent) {
                            final Events.HealEntityEvent e = (Events.HealEntityEvent) event;
                            if (e.healer == healer && e.target == target) {
                                heals.set(true);
                                break;
                            }
                        }
                    }
                    return Priority.FATAL;
                });
                if (heals.get()) {
                    return true;
                }
            }
        }
        return false;
    }
}
