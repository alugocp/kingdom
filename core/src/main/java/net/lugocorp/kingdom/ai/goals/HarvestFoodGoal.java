package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.Decision;
import net.lugocorp.kingdom.ai.DecisionChannel;
import net.lugocorp.kingdom.ai.DecisionClass;
import net.lugocorp.kingdom.ai.Goal;
import net.lugocorp.kingdom.ai.Prioritized;
import net.lugocorp.kingdom.ai.Priority;
import net.lugocorp.kingdom.ai.behaviors.ActivateAbilityBehavior;
import net.lugocorp.kingdom.ai.behaviors.ListBehavior;
import net.lugocorp.kingdom.ai.behaviors.MoveUnitBehavior;
import net.lugocorp.kingdom.ai.behaviors.RecruitUnitBehavior;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.content.Labels;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.math.Path;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.pathfinding.Pathfinder;
import net.lugocorp.kingdom.prediction.Prediction;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Chooser;
import net.lugocorp.kingdom.utils.Lambda;
import net.lugocorp.kingdom.utils.Tuple;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This causes the CompPlayer to gather food Items for its Units
 */
public class HarvestFoodGoal extends Goal {

    /** {@inheritdoc} */
    @Override
    protected Decision makeDecision(GameView view, CompPlayer player, DecisionChannel channel) {
        final Set<Unit> hungryUnits = this.getHungryUnits(view, player);

        // Recruit unit handler (recruit a Nature or Mining Unit)
        if (channel.is(DecisionClass.RECRUIT_UNIT)) {
            final Map<Unit, Integer> support = this.getHarvesterSupport(view, player, hungryUnits);

            // Get the correct Glyph and Priority for our ideal harvester
            Optional<Glyph> targetGlyph = Optional.empty();
            Optional<Unit> targetUnit = Optional.empty();
            Priority priority = Priority.GOOD_IDEA;
            for (Map.Entry<Unit, Integer> e : support.entrySet()) {
                if (e.getValue() < 4) {
                    targetGlyph = Optional.of(e.getKey().abilities.hasPassive(Labels.ability_rock_appetite)
                            ? Glyph.MINING
                            : Glyph.NATURE);
                    targetUnit = Optional.of(e.getKey());
                    if (e.getValue() < 1) {
                        priority = Priority.NECESSITY;
                    } else if (e.getValue() < 2) {
                        priority = Priority.OPTIMAL;
                    }
                    break;
                }
            }

            // Return our Decision if there is indeed an ideal harvester
            if (targetGlyph.isPresent()) {
                final Unit finalTargetUnit = targetUnit.get();
                return new Decision(channel, this, priority, new RecruitUnitBehavior(player, targetGlyph.get(),
                        (Unit u) -> this.doesUnitGenerateFoodOnBuilding(view, player, u, finalTargetUnit).isPresent()
                                ? Priority.GOOD_IDEA
                                : Priority.NEUTRAL,
                        (Set<Point> options) -> {
                            final Chooser<Point> chooser = new Chooser<>((Integer old, Integer next) -> next < old);
                            for (Point p : options) {
                                chooser.choice(p, finalTargetUnit.getPoint().distance(p));
                            }
                            return chooser.get();
                        }));
            }
        }

        // Unit handler
        if (channel.is(DecisionClass.UNIT)) {
            final Unit unit = channel.getUnit();
            final Optional<String> building = this.doesUnitGenerateFoodOnBuilding(view, player, unit, hungryUnits);
            if (building.isPresent()) {

                // Find a nearby Building that interacts with this Unit
                final Optional<Point> closest = this.findNearbyBuilding(view, player, unit.getPoint(), building.get());
                if (closest.isPresent()) {

                    // Check if there's a path to the closest Building
                    final Pathfinder pathfinder = new Pathfinder(unit);
                    final List<Point> path = pathfinder.getPath(view, closest.get());
                    if (path.size() > 0) {

                        // We can go to the Building if it's close by
                        if (path.size() < 6) {
                            return new Decision(channel, this, Priority.OPTIMAL, new MoveUnitBehavior(unit, path));
                        } else {
                            // Try to spawn the Building if one is not close enough
                            for (Ability active : unit.abilities.getActives()) {
                                final Optional<Tuple<String, Path>> tile = this.doesAbilitySpawnBuildingOnTile(view,
                                        player, active, building.get());
                                if (tile.isPresent()) {
                                    final Optional<Point> dest = this.findNearbyTile(view, player, unit.getPoint(),
                                            tile.get().a);
                                    if (dest.isPresent()) {
                                        final List<Point> tilePath = pathfinder.getPath(view, dest.get());
                                        if (dest.get().equals(unit.getPoint())
                                                || (tilePath.size() > 0 && tilePath.size() < 6)) {
                                            return new Decision(channel, this, Priority.OPTIMAL,
                                                    new ListBehavior(new MoveUnitBehavior(unit, tilePath),
                                                            new ActivateAbilityBehavior(unit, active, tile.get().b)));
                                        }
                                        if (tilePath.size() > 0 && tilePath.size() < 12) {
                                            return new Decision(channel, this, Priority.GOOD_IDEA,
                                                    new ListBehavior(new MoveUnitBehavior(unit, tilePath),
                                                            new ActivateAbilityBehavior(unit, active, tile.get().b)));
                                        }
                                    }
                                }
                            }

                            // Go to the Building anyways
                            return new Decision(channel, this, Priority.GOOD_IDEA, new MoveUnitBehavior(unit, path));
                        }
                    }
                }
            }
        }

        // No decision fallback
        return this.noDecision(channel);
    }

    /**
     * Returns a Set of Units that will need food soon
     */
    private Set<Unit> getHungryUnits(GameView view, CompPlayer player) {
        return Lambda.filter((Unit u) -> u.hunger.get(view) <= 6 && u.haul.getEdibleItems(view, u).size() == 0,
                player.units);
    }

    /**
     * Counts how many food harvesters we have per given Unit
     */
    private Map<Unit, Integer> getHarvesterSupport(GameView view, CompPlayer player, Set<Unit> units) {
        final Map<Unit, Integer> results = new HashMap<>();
        for (Unit target : units) {
            results.put(target, 0);
            for (Unit harvester : player.units) {
                final Optional<String> building = this.doesUnitGenerateFoodOnBuilding(view, player, harvester, target);
                if (building.isPresent()) {
                    results.put(target, results.get(target) + 1);
                }
            }
        }
        return results;
    }

    /**
     * Optionally returns the closest Point where the target Building can be found
     */
    private Optional<Point> findNearbyBuilding(GameView view, CompPlayer player, Point focal, String building) {
        return player.memory.getClosestKnownTileWhere(view, focal,
                (Tile t) -> t.building.map((Building b) -> b.name.equals(building)).orElse(false));
    }

    /**
     * Optionally returns the closest Point where the target Tile can be found
     */
    private Optional<Point> findNearbyTile(GameView view, CompPlayer player, Point focal, String tile) {
        return player.memory.getClosestKnownTileWhere(view, focal, (Tile t) -> t.name.equals(tile));
    }

    /**
     * Returns the name of a Building that the given Unit generates a food Item on
     * for any of the given target Units
     */
    private Optional<String> doesUnitGenerateFoodOnBuilding(GameView view, CompPlayer player, Unit unit,
            Set<Unit> targets) {
        for (Unit target : targets) {
            final Optional<String> building = this.doesUnitGenerateFoodOnBuilding(view, player, unit, target);
            if (building.isPresent()) {
                return building;
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the name of a Building that the given Unit generates a food Item on
     * for the given target Unit
     */
    private Optional<String> doesUnitGenerateFoodOnBuilding(GameView view, CompPlayer player, Unit unit, Unit target) {
        for (Ability passive : unit.abilities.getPassives()) {
            final Optional<String> building = this.doesAbilityGenerateFoodOnBuilding(view, player, passive, target);
            if (building.isPresent()) {
                return building;
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the name of a Building that the given Ability generates a food Item
     * on for the given Unit
     */
    private Optional<String> doesAbilityGenerateFoodOnBuilding(GameView view, CompPlayer player, Ability ability,
            Unit target) {
        // TODO cache the results of this so we don't rerun it every turn
        final String[] buildings = {Labels.building_dense_forest, Labels.building_forest, Labels.building_meadow,
                Labels.building_oasis, Labels.building_shrubland, Labels.building_mine};
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
                            if (e.getClass() == Events.GenerateItemEvent.class) {
                                final Item item = ((Events.GenerateItemEvent) e).blob;
                                if (target.hunger.canEat(view, item)) {
                                    return Priority.GOOD_IDEA;
                                }
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

    /**
     * Returns the name of a Tile that the given Ability spawns a Building on
     */
    private Optional<Tuple<String, Path>> doesAbilitySpawnBuildingOnTile(GameView view, CompPlayer player,
            Ability ability, String target) {
        // TODO cache the results of this so we don't rerun it every turn
        final String[] tiles = {Labels.tile_grass, Labels.tile_rock, Labels.tile_sand, Labels.tile_snow};
        for (int a = 0; a < tiles.length; a++) {
            final Optional<Point> override = view.game.world.findTile(tiles[a]).map((Tile t) -> t.getPoint());
            if (!override.isPresent()) {
                continue;
            }
            final Optional<Prioritized<Path>> prioritized = player.actor.analyze.activeAbility(view, ability,
                    override.get(), (Prediction prediction) -> {
                        for (Event e : prediction.log) {
                            if (e.getClass() == Events.GenerateBuildingEvent.class) {
                                final Building building = ((Events.GenerateBuildingEvent) e).blob;
                                if (building.name.equals(target)) {
                                    return Priority.GOOD_IDEA;
                                }
                            }
                        }
                        return Priority.FATAL;
                    });
            if (prioritized.map((Prioritized<Path> p) -> p.priority != Priority.FATAL).orElse(false)) {
                return Optional.of(new Tuple<String, Path>(tiles[a], prioritized.get().value));
            }
        }
        return Optional.empty();
    }
}
