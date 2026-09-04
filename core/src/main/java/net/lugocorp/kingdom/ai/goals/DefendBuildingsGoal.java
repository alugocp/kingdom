package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.Decision;
import net.lugocorp.kingdom.ai.DecisionChannel;
import net.lugocorp.kingdom.ai.DecisionClass;
import net.lugocorp.kingdom.ai.Goal;
import net.lugocorp.kingdom.ai.Priority;
import net.lugocorp.kingdom.ai.analysis.UnitAnalysis;
import net.lugocorp.kingdom.ai.behaviors.MoveUnitBehavior;
import net.lugocorp.kingdom.ai.behaviors.RecruitUnitBehavior;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tower;
import net.lugocorp.kingdom.game.model.Unit;
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
        final int defensiveUnits = Lambda.count(player.units, (Unit u) -> UnitAnalysis.isDefensive(view, u));
        final int towers = Lambda.count(view.game.towers, (Tower t) -> t.leadership.belongsToPlayer(player));

        // Recruit unit handler (recruit a defensive Unit)
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
                final boolean defensive = UnitAnalysis.isDefensive(view, unit);
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
}
