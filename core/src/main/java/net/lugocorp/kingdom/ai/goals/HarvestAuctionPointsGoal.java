package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.Decision;
import net.lugocorp.kingdom.ai.DecisionChannel;
import net.lugocorp.kingdom.ai.DecisionClass;
import net.lugocorp.kingdom.ai.Goal;
import net.lugocorp.kingdom.ai.Priority;
import net.lugocorp.kingdom.ai.behaviors.MoveUnitBehavior;
import net.lugocorp.kingdom.ai.behaviors.RecruitUnitBehavior;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.content.Labels;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.pathfinding.Pathfinder;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This causes the CompPlayer to generate auction points from markets
 */
public class HarvestAuctionPointsGoal extends Goal {

    /** {@inheritdoc} */
    @Override
    protected Decision makeDecision(GameView view, CompPlayer player, DecisionChannel channel) {
        // Recruit unit handler (recruit a Trade Unit)
        if (channel.is(DecisionClass.RECRUIT_UNIT)) {
            return new Decision(channel, this, Priority.GOOD_IDEA,
                    new RecruitUnitBehavior(player, Glyph.TRADE,
                            (Unit u) -> this.doesUnitGeneratePointsOnBuilding(view, player, u).isPresent()
                                    ? Priority.GOOD_IDEA
                                    : Priority.NEUTRAL,
                            (Set<Point> options) -> {
                                final Set<Point> marketplaces = Lambda
                                        .filter((Point p) -> view.game.world.getTile(p).get().building
                                                .map((Building b) -> b.name.equals(Labels.building_marketplace))
                                                .orElse(false), options);
                                return marketplaces.size() > 0 ? Lambda.random(marketplaces) : Lambda.random(options);
                            }));
        }

        // Unit handler
        if (channel.is(DecisionClass.UNIT)) {
            final Unit unit = channel.getUnit();
            final Optional<String> building = this.doesUnitGeneratePointsOnBuilding(view, player, unit);
            if (building.isPresent()) {
                final Optional<Point> closest = this.findNearbyBuilding(view, player, unit.getPoint(), building.get());
                if (closest.isPresent()) {
                    final Pathfinder pathfinder = new Pathfinder(unit);
                    final List<Point> path = pathfinder.getPath(view, closest.get());
                    if (path.size() > 0) {
                        return new Decision(channel, this, Priority.OPTIMAL, new MoveUnitBehavior(unit, path));
                    }
                }
            }
        }

        // No decision fallback
        return this.noDecision(channel);
    }

    /**
     * Optionally returns the closest Point where the target Building can be found
     */
    private Optional<Point> findNearbyBuilding(GameView view, CompPlayer player, Point focal, String building) {
        return player.memory.getClosestKnownTileWhere(view, focal,
                (Tile t) -> t.building.map((Building b) -> b.name.equals(building)).orElse(false));
    }

    /**
     * Returns the name of a Building that the given Unit generates auction points
     * on
     */
    private Optional<String> doesUnitGeneratePointsOnBuilding(GameView view, CompPlayer player, Unit unit) {
        for (Ability passive : unit.abilities.getPassives()) {
            final Optional<String> building = this.doesAbilityGeneratePointsOnBuilding(view, player, passive);
            if (building.isPresent()) {
                return building;
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the name of a Building that the given Ability generates auction
     * points on
     */
    private Optional<String> doesAbilityGeneratePointsOnBuilding(GameView view, CompPlayer player, Ability ability) {
        // TODO cache the results of this so we don't rerun it every turn
        final String[] buildings = {Labels.building_marketplace};
        for (int a = 0; a < buildings.length; a++) {
            final Optional<Point> override = view.game.world.findBuilding(buildings[a])
                    .map((Building b) -> b.getPoint());
            if (!override.isPresent()) {
                continue;
            }
            final Events.RepeatedEvent event = new Events.RepeatedEvent("Tick", 0, false);
            final Priority priority = player.actor.analyze.passiveAbility(view, ability, event, override.get(),
                    (List<Event> log) -> {
                        for (Event e : log) {
                            if (e.getClass() == Events.GenerateAuctionPointsEvent.class) {
                                return Priority.GOOD_IDEA;
                            }
                        }
                        return Priority.FATAL;
                    });
            if (priority != Priority.FATAL) {
                return Optional.of(buildings[a]);
            }
        }
        return Optional.empty();
    }
}
