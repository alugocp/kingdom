package net.lugocorp.kingdom.game.core;
import net.lugocorp.kingdom.game.combat.Damage;
import net.lugocorp.kingdom.game.combat.HitPoints;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Patron;
import net.lugocorp.kingdom.game.model.Player;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * This class contains utility functions for writing new Ability effects
 */
public class AbilityLogic {

    /**
     * Convenience wrapper for an active Ability that implements an attack
     */
    public static void attack(GameView view, Unit attacker, Damage dmg) {
        Map<Point, HitPoints> targets = new HashMap<>();
        Set<Point> points = new HashSet<>();

        // Grab every possible attack target within range
        int range = attacker.getAttackRange(view);
        Set<Point> unfiltered = Hexagons.getNeighbors(attacker.getPoint(), range);
        for (Point p : unfiltered) {
            Optional<Tile> tile = view.game.world.getTile(p);
            if (!tile.isPresent()) {
                continue;
            }
            Tile t = tile.get();
            if (t.unit.isPresent()) {
                if (t.unit.get().health.isVulnerable()) {
                    targets.put(p, t.unit.get().health);
                    points.add(p);
                }
            } else if (t.building.isPresent() && t.building.get().health().isVulnerable()) {
                targets.put(p, t.building.get().health());
                points.add(p);
            }
        }

        // Have the human Player select which target to attack
        view.selector.select(points, "No attack targets are in range", (Point p) -> {
            attacker.health.attack(view, targets.get(p), dmg);
            view.game.mechanics.turns.unitHasActed(view, attacker);
        });
    }

    /**
     * Ability that heals a Unit
     */
    public static void healUnit(GameView view, Unit healer, int hitPoints) {
        Map<Point, HitPoints> targets = new HashMap<>();
        Set<Point> points = new HashSet<>();

        // Grab every possible heal target within range
        Set<Point> unfiltered = Hexagons.getNeighbors(healer.getPoint(), 1);
        for (Point p : unfiltered) {
            Optional<Tile> t = view.game.world.getTile(p);
            if (!t.isPresent()) {
                continue;
            }
            if (t.get().unit.isPresent()) {
                targets.put(p, t.get().unit.get().health);
                points.add(p);
            }
        }

        // Have the human Player select which target to heal
        view.selector.select(points, "No heal targets are in range", (Point p) -> {
            targets.get(p).heal(hitPoints);
            view.game.mechanics.turns.unitHasActed(view, healer);
        });
    }

    /**
     * Ability that heals a Building
     */
    public static void healBuilding(GameView view, Unit healer, int hitPoints, Function<Building, Boolean> criteria) {
        Map<Point, HitPoints> targets = new HashMap<>();
        Set<Point> points = new HashSet<>();

        // Grab every possible heal target within range
        Set<Point> unfiltered = Hexagons.getNeighbors(healer.getPoint(), 1);
        for (Point p : unfiltered) {
            Optional<Tile> t = view.game.world.getTile(p);
            if (!t.isPresent()) {
                continue;
            }
            if (t.get().building.map(criteria).orElse(false)) {
                targets.put(p, t.get().building.get().health());
                points.add(p);
            }
        }

        // Have the human Player select which target to heal
        view.selector.select(points, "No heal targets are in range", (Point p) -> {
            targets.get(p).heal(hitPoints);
            view.game.mechanics.turns.unitHasActed(view, healer);
        });
    }

    /**
     * Ability that spawns a building at the caster's location
     */
    public static void build(GameView view, Unit caster, String building, Function<Tile, Boolean> criteria) {
        Point p = caster.getPoint();
        view.game.world.getTile(p).ifPresent((Tile t) -> {
            if (t.building.isPresent()) {
                view.logger.log("Cannot place another building here");
            } else if (criteria.apply(t)) {
                view.game.generator.building(building, p.x, p.y).spawn(view);
                view.game.mechanics.turns.unitHasActed(view, caster);
            } else {
                view.logger.log("Invalid tile for this ability");
            }
        });
    }

    /**
     * Ability that does something while on a particular Building
     */
    public static void doOnBuilding(GameView view, Unit caster, Function<Building, Boolean> criteria, Runnable thing) {
        boolean isOnBuilding = view.game.world.getTile(caster.getPoint()).flatMap((Tile t) -> t.building).map(criteria)
                .orElse(false);
        if (isOnBuilding) {
            thing.run();
        }
    }

    /**
     * Ability that harvests an Item from some Tile
     */
    public static void harvest(GameView view, Unit caster, String item, Function<Building, Boolean> criteria) {
        AbilityLogic.doOnBuilding(view, caster, criteria, () -> {
            if (!caster.haul.isFull()) {
                caster.haul.add(view.game.generator.item(item));
            }
        });
    }

    /**
     * Generates some favor with a Patron if the Unit is in its domain
     */
    public static void worship(GameView view, Unit caster, int points) {
        caster.leader.ifPresent((Player player) -> view.game.getPatronByDomain(caster.getPoint())
                .ifPresent((Patron p) -> p.addFavor(player, points)));
    }
}
