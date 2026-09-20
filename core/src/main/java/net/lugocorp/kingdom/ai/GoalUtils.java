package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.gameplay.combat.Damage;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Path;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.prediction.Prediction;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Wrapped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Contains several shared methods between Goal subclasses
 */
public class GoalUtils {

    /**
     * Returns how many Units the CompPlayer has per Glyph
     */
    public static Map<Glyph, Integer> getUnitsByGlyph(CompPlayer player) {
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
    public static int getNumberOfBuildings(CompPlayer player, String building) {
        int count = 0;
        for (Building b : player.buildings) {
            if (b.name.equals(building)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Optionally returns the closest Point where the target Building can be found
     */
    public static Optional<Point> findNearbyBuilding(GameView view, CompPlayer player, Point focal, String building) {
        return player.memory.getClosestKnownTileWhere(view, focal,
                (Tile t) -> t.building.map((Building b) -> b.name.equals(building)).orElse(false));
    }

    /**
     * Optionally returns the closest Point where the target Tile can be found
     */
    public static Optional<Point> findNearbyTile(GameView view, CompPlayer player, Point focal, String tile) {
        return player.memory.getClosestKnownTileWhere(view, focal, (Tile t) -> t.name.equals(tile));
    }

    /**
     * Finds a nearby Tile without a Building
     */
    public static Optional<Point> findNearbyEmptyTile(GameView view, CompPlayer player, Point focal) {
        return player.memory.getClosestKnownTileWhere(view, focal, (Tile t) -> !t.building.isPresent());
    }

    /**
     * Returns true if the given Unit can spawn the given Building
     */
    public static boolean canUnitSpawnBuilding(GameView view, CompPlayer player, Unit unit, String building) {
        final Point override = GoalUtils.findNearbyEmptyTile(view, player, unit.getPoint()).orElse(unit.getPoint());
        for (Ability active : unit.abilities.getActives()) {
            if (GoalUtils.canAbilitySpawnBuilding(view, player, active, building).isPresent()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a selection Path if the given Ability spawns a the given Building
     */
    public static Optional<Path> canAbilitySpawnBuilding(GameView view, CompPlayer player, Ability ability,
            String building, Point override) {
        // TODO cache the results of this so we don't rerun it every turn
        final Optional<Prioritized<Path>> prioritized = player.actor.analyze.activeAbility(view, ability, override,
                (Prediction prediction) -> {
                    for (Event event : prediction.log) {
                        if (event instanceof Events.GenerateBuildingEvent) {
                            final Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                            if (e.blob.name.equals(building)) {
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

    /**
     * Returns a selection Path if the given Ability spawns a the given Building
     */
    public static Optional<Path> canAbilitySpawnBuilding(GameView view, CompPlayer player, Ability ability,
            String building) {
        final Point override = GoalUtils.findNearbyEmptyTile(view, player, ability.wielder.getPoint())
                .orElse(ability.wielder.getPoint());
        return GoalUtils.canAbilitySpawnBuilding(view, player, ability, building, override);
    }

    /**
     * Returns the name of a Building that the given Unit passes some passive
     * Ability check on
     */
    public static Optional<String> checkUnitOnBuilding(Unit unit, Function<Ability, Optional<String>> mapper) {
        for (Ability passive : unit.abilities.getPassives()) {
            final Optional<String> building = mapper.apply(passive);
            if (building.isPresent()) {
                return building;
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the name of a Building that the given Ability passes some passive
     * check on
     */
    public static Optional<String> checkAbilityOnBuilding(GameView view, CompPlayer player, Ability ability,
            String[] buildings, Function<Event, Boolean> criteria) {
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
                            if (criteria.apply(e)) {
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
     * Returns true if the given Unit deals extra Damage to the given Entity
     */
    public static boolean isStrongAgainst(GameView view, CompPlayer player, Unit attacker, Entity target) {
        final int baseline = 5;
        final Damage dmg = new Damage(baseline);
        final Priority result = player.actor.analyze.attack(view, attacker, dmg, target,
                (List<Event> log) -> Priority.FATAL);
        return dmg.base > baseline;
    }

    /**
     * Returns true if the given Unit deals less Damage to the given Entity
     */
    public static boolean isWeakAgainst(GameView view, CompPlayer player, Unit attacker, Entity target) {
        final int baseline = 5;
        final Damage dmg = new Damage(baseline);
        final Priority result = player.actor.analyze.attack(view, attacker, dmg, target,
                (List<Event> log) -> Priority.FATAL);
        return dmg.base < baseline;
    }

    /**
     * Returns the max Damage that the given Unit could do against the give target
     * Entity
     */
    public static int getMaxDamage(GameView view, CompPlayer player, Unit attacker, Entity target) {
        int max = 0;
        for (Ability ability : attacker.abilities.getActives()) {
            for (Point override : Hexagons.getNeighbors(target.getPoint(), 3)) {
                if (!view.game.world.isInBounds(override)) {
                    continue;
                }

                final Wrapped<Integer> damage = new Wrapped<>(0);
                player.actor.analyze.activeAbility(view, ability, override, (Prediction prediction) -> {
                    for (Event event : prediction.log) {
                        if (event instanceof Events.TakeDamageEvent) {
                            final Events.TakeDamageEvent e = (Events.TakeDamageEvent) event;
                            if (e.target == target) {
                                damage.set(damage.get() + e.dmg.total());
                            }
                        }
                    }
                    return Priority.FATAL;
                });
                if (damage.get() > max) {
                    max = damage.get();
                }
            }
        }
        return max;
    }

    /**
     * Returns the number of relevant allied or enemy Units present near the given
     * focal Point
     */
    public static int countNearbyUnits(GameView view, CompPlayer player, Point focal, boolean allies) {
        int count = 0;
        for (Point p : Hexagons.getNeighbors(focal, 5)) {
            if (view.game.world.getTile(p).flatMap((Tile t) -> t.unit)
                    .map((Unit u) -> (allies == u.leadership.belongsToPlayer(player)) && (u.glyphs.has(Glyph.BATTLE)
                            || u.glyphs.has(Glyph.DEFENSE) || u.glyphs.has(Glyph.SUPPORT)))
                    .orElse(false)) {
                count++;
            }
        }
        return count;
    }
}
