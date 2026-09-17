package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.Decision;
import net.lugocorp.kingdom.ai.DecisionChannel;
import net.lugocorp.kingdom.ai.DecisionClass;
import net.lugocorp.kingdom.ai.Goal;
import net.lugocorp.kingdom.ai.Prioritized;
import net.lugocorp.kingdom.ai.Priority;
import net.lugocorp.kingdom.ai.behaviors.ActivateAbilityBehavior;
import net.lugocorp.kingdom.ai.behaviors.ConsumeItemBehavior;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This causes the CompPlayer to gather food Items for its Units
 */
public class HarvestGoldGoal extends Goal {

    /** {@inheritdoc} */
    @Override
    protected Decision makeDecision(GameView view, CompPlayer player, DecisionChannel channel) {

        // Recruit unit handler (recruit a Trade or Mining Unit)
        if (channel.is(DecisionClass.RECRUIT_UNIT)) {
            final Map<Glyph, Integer> unitsByGlyph = this.getUnitsByGlyph(player);
            final int mines = this.getNumberOfMines(player);

            // Recruit a Unit that can build a Mine
            if (mines < 2 || mines < unitsByGlyph.get(Glyph.MINING)) {
                return new Decision(channel, this, mines < 2 ? Priority.OPTIMAL : Priority.GOOD_IDEA,
                        new RecruitUnitBehavior(player, Glyph.MINING,
                                (Unit u) -> this.canUnitSpawnMine(view, player, u)
                                        ? Priority.GOOD_IDEA
                                        : Priority.NEUTRAL,
                                (Set<Point> options) -> Lambda.random(options)));
            }

            // Recruit a Unit that can harvest gold from a Mine or Marketplace
            Priority priority = Priority.GOOD_IDEA;
            if (unitsByGlyph.get(Glyph.TRADE) + unitsByGlyph.get(Glyph.MINING) < 3) {
                priority = Priority.OPTIMAL;
            } else if (unitsByGlyph.get(Glyph.TRADE) > 2 && unitsByGlyph.get(Glyph.MINING) > 2) {
                priority = Priority.NEUTRAL;
            }

            // Return our Decision with the chosen Glyph and Priority
            return new Decision(channel, this, priority,
                    new RecruitUnitBehavior(player,
                            unitsByGlyph.get(Glyph.TRADE) < unitsByGlyph.get(Glyph.MINING) ? Glyph.TRADE : Glyph.MINING,
                            (Unit u) -> this.doesUnitGenerateGoldOnBuilding(view, player, u).isPresent()
                                    ? Priority.GOOD_IDEA
                                    : Priority.NEUTRAL,
                            (Set<Point> options) -> {
                                final Chooser<Point> chooser = new Chooser<>((Integer old, Integer next) -> next < old);
                                for (Point p : options) {
                                    final Optional<Point> mine = this.findNearbyBuilding(view, player, p,
                                            Labels.building_mine);
                                    mine.ifPresent((Point p1) -> {
                                        chooser.choice(p, p.distance(p1));
                                    });
                                }
                                return chooser.has() ? chooser.get() : Lambda.random(options);
                            }));
        }

        // Unit handler
        if (channel.is(DecisionClass.UNIT)) {
            final Unit unit = channel.getUnit();

            // Consume any valuable Items in this Unit's haul Inventory
            for (Item item : unit.haul) {
                if (unit.hunger.canEat(view, item)) {
                    continue;
                }
                final Priority priority = player.actor.analyze.itemConsumption(view, unit, item, (List<Event> log) -> {
                    for (Event e : log) {
                        if (e.getClass() == Events.YieldGoldEvent.class) {
                            return Priority.GOOD_IDEA;
                        }
                    }
                    return Priority.FATAL;
                });
                if (priority != Priority.FATAL) {
                    return new Decision(channel, this, Priority.NECESSITY, new ConsumeItemBehavior(unit, item));
                }
            }

            // Move the Unit to a Mine (or build a Mine)
            final Optional<String> building = this.doesUnitGenerateGoldOnBuilding(view, player, unit);
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
                                final Optional<Path> selections = this.canAbilitySpawnMine(view, player, active);
                                if (selections.isPresent()) {
                                    final Optional<Point> dest = this.findNearbyEmptyTile(view, player,
                                            unit.getPoint());
                                    if (dest.isPresent()) {
                                        final List<Point> tilePath = pathfinder.getPath(view, dest.get());
                                        if (dest.get().equals(unit.getPoint())
                                                || (tilePath.size() > 0 && tilePath.size() < 6)) {
                                            return new Decision(channel, this, Priority.OPTIMAL, new ListBehavior(
                                                    new MoveUnitBehavior(unit, tilePath),
                                                    new ActivateAbilityBehavior(unit, active, selections.get())));
                                        }
                                        if (tilePath.size() > 0 && tilePath.size() < 12) {
                                            return new Decision(channel, this, Priority.GOOD_IDEA, new ListBehavior(
                                                    new MoveUnitBehavior(unit, tilePath),
                                                    new ActivateAbilityBehavior(unit, active, selections.get())));
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
     * Returns how many Units the CompPlayer has per Glyph
     */
    private Map<Glyph, Integer> getUnitsByGlyph(CompPlayer player) {
        final Map<Glyph, Integer> counts = new HashMap<>();
        for (Glyph g : Glyph.values()) {
            counts.put(g, 0);
        }

        // Count the Units
        for (Unit u : player.units) {
            final Glyph[] glyphs = u.glyphs.get();
            for (int a = 0; a < glyphs.length; a++) {
                final Glyph g = glyphs[a];
                counts.put(g, counts.get(g) + 1);
            }
        }
        return counts;
    }

    /**
     * Counts the number of Mines under this Player's rule
     */
    private int getNumberOfMines(CompPlayer player) {
        int count = 0;
        for (Building b : player.buildings) {
            if (b.name.equals(Labels.building_mine)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Optionally returns the closest Point where the target Building can be found
     */
    private Optional<Point> findNearbyBuilding(GameView view, CompPlayer player, Point focal, String building) {
        return player.memory.getClosestKnownTileWhere(view, focal,
                (Tile t) -> t.building.map((Building b) -> b.name.equals(building)).orElse(false));
    }

    /**
     * Finds a nearby Tile without a Building
     */
    private Optional<Point> findNearbyEmptyTile(GameView view, CompPlayer player, Point focal) {
        return player.memory.getClosestKnownTileWhere(view, focal, (Tile t) -> !t.building.isPresent());
    }

    /**
     * Returns the name of a Building that the given Unit generates gold on
     */
    private Optional<String> doesUnitGenerateGoldOnBuilding(GameView view, CompPlayer player, Unit unit) {
        for (Ability passive : unit.abilities.getPassives()) {
            final Optional<String> building = this.doesAbilityGenerateGoldOnBuilding(view, player, passive);
            if (building.isPresent()) {
                return building;
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the name of a Building that the given Ability generates gold on
     */
    private Optional<String> doesAbilityGenerateGoldOnBuilding(GameView view, CompPlayer player, Ability ability) {
        // TODO cache the results of this so we don't rerun it every turn
        final String[] buildings = {Labels.building_marketplace, Labels.building_mine};
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
                                if (item.gold > 1) {
                                    return Priority.GOOD_IDEA;
                                }
                            } else if (e.getClass() == Events.YieldGoldEvent.class) {
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

    /**
     * Returns true if the given Unit can spawn a Mine
     */
    private boolean canUnitSpawnMine(GameView view, CompPlayer player, Unit unit) {
        final Point override = this.findNearbyEmptyTile(view, player, unit.getPoint()).orElse(unit.getPoint());
        for (Ability active : unit.abilities.getActives()) {
            if (this.canAbilitySpawnMine(view, player, active).isPresent()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the given Ability spawns a Mine
     */
    private Optional<Path> canAbilitySpawnMine(GameView view, CompPlayer player, Ability ability) {
        // TODO cache the results of this so we don't rerun it every turn
        final Point override = this.findNearbyEmptyTile(view, player, ability.wielder.getPoint())
                .orElse(ability.wielder.getPoint());
        final Optional<Prioritized<Path>> prioritized = player.actor.analyze.activeAbility(view, ability, override,
                (Prediction prediction) -> {
                    for (Event event : prediction.log) {
                        if (event instanceof Events.GenerateBuildingEvent) {
                            final Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                            if (e.blob.name.equals(Labels.building_mine)) {
                                return Priority.GOOD_IDEA;
                            }
                        }
                    }
                    return Priority.FATAL;
                });
        if (prioritized.map((Prioritized<Path> p) -> p.priority != Priority.FATAL).orElse(false)) {
            return Optional.of(prioritized.get().value);
        }
        return Optional.empty();
    }
}
