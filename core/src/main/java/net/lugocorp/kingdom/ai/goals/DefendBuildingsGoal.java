package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.Decision;
import net.lugocorp.kingdom.ai.DecisionChannel;
import net.lugocorp.kingdom.ai.DecisionClass;
import net.lugocorp.kingdom.ai.Goal;
import net.lugocorp.kingdom.ai.Priority;
import net.lugocorp.kingdom.ai.behaviors.MoveUnitBehavior;
import net.lugocorp.kingdom.ai.behaviors.RecruitUnitBehavior;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tower;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.model.UnitDefaults;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.properties.EntityType;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.pathfinding.Pathfinder;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import java.util.List;
import java.util.Set;

/**
 * This causes the CompPlayer to defend its Buildings
 */
public class DefendBuildingsGoal extends Goal {

    /** {@inheritdoc} */
    @Override
    protected Decision makeDecision(GameView view, CompPlayer player, DecisionChannel channel) {
        final int defensiveUnits = this.getNumberOfDefensiveUnits(view, player);
        final int towers = this.getNumberOfTowers(view, player);

        // Recruit unit handler (recruit a fast Trade Glyph Unit)
        if (channel.is(DecisionClass.RECRUIT_UNIT)) {
            Priority priority = Priority.BAD_IDEA;
            if (player.units.size() > 1 && defensiveUnits == 0) {
                priority = Priority.OPTIMAL;
            } else if (defensiveUnits < towers) {
                priority = Priority.OPTIMAL;
            } else if (defensiveUnits < towers * 2) {
                priority = Priority.GOOD_IDEA;
            }
            return new Decision(channel, this, priority, new RecruitUnitBehavior(player, Glyph.DEFENSE,
                    (Unit u) -> Priority.NEUTRAL, (Set<Point> options) -> {
                        // If one of the spawn points is a Tower, then spawn there
                        for (Point p : options) {
                            final Tower tower = view.game.world.getTile(p).get().getDomainCenter();
                            if (tower.getPoint().equals(p)) {
                                return p;
                            }
                        }
                        return Lambda.random(options);
                    }));
        }

        // Unit handler
        if (channel.is(DecisionClass.UNIT)) {
            final Unit unit = channel.getUnit();
            final Pathfinder pathfinder = new Pathfinder(unit);

            // Move a defensive Unit to some Building (or a non-defensive Unit to a damaged
            // Building)
            for (Building b : player.buildings) {
                // Skip if there is already a defender
                final Point p = b.getPoint();
                if (view.game.world.getTile(p).get().unit.isPresent()) {
                    continue;
                }

                // Make an assessment for the Priority of moving this Unit
                final boolean isDire = b.isEntityType(EntityType.TOWER) || b.combat.health.atOrBelowPercent(60);
                final List<Point> path = pathfinder.getPath(view, p);
                final boolean defensive = this.isDefensiveUnit(unit);
                Priority priority = defensive ? Priority.GOOD_IDEA : Priority.NEUTRAL;
                if (path.size() > 0) {
                    if (isDire) {
                        priority = priority.increment();
                    }
                    if (path.size() > unit.movement.getMaxDistance(view) * 2) {
                        priority = priority.decrement();
                    }
                    return new Decision(channel, this, priority, new MoveUnitBehavior(unit, path));
                }
            }
        }

        // No decision fallback
        return this.noDecision(channel);
    }

    /**
     * Returns true if the given Unit has defensive capabilities
     */
    private boolean isDefensiveUnit(Unit u) {
        return u.glyphs.has(Glyph.DEFENSE) || u.combat.health.getMax() > UnitDefaults.HEALTH;
    }

    /**
     * Returns the number of defensive Units controlled by the given CompPlayer
     */
    private int getNumberOfDefensiveUnits(GameView view, CompPlayer player) {
        int count = 0;
        for (Unit u : player.units) {
            if (this.isDefensiveUnit(u)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the number of Towers that belong to this CompPlayer
     */
    private int getNumberOfTowers(GameView view, CompPlayer player) {
        int total = 0;
        for (Tower tower : view.game.towers) {
            if (tower.leadership.belongsToPlayer(player)) {
                total += tower.domain.size();
            }
        }
        return total;
    }
}
