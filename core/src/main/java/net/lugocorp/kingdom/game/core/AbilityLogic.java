package net.lugocorp.kingdom.game.core;
import net.lugocorp.kingdom.game.combat.Damage;
import net.lugocorp.kingdom.game.combat.HitPoints;
import net.lugocorp.kingdom.game.model.Building;
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
            Optional<Tile> t = view.game.world.getTile(p);
            if (!t.isPresent()) {
                continue;
            }
            if (t.get().unit.isPresent()) {
                targets.put(p, t.get().unit.get().health);
                points.add(p);
            } else if (t.get().building.isPresent()) {
                targets.put(p, t.get().building.get().health);
                points.add(p);
            }
        }

        // Have the human Player select which target to attack
        view.selector.select(points, "No attack targets are in range", (Point p) -> {
            attacker.health.attack(view, targets.get(p), dmg);
            view.game.mechanics.turns.unitHasActed(attacker);
        });
    }

    /**
     * Ability that spawns a building at the caster's location
     */
    public static void build(GameView view, Unit caster, String building) {
        Point p = caster.getPoint();
        view.game.world.getTile(p).ifPresent((Tile t) -> {
            if (t.building.isPresent()) {
                view.logger.log("Cannot place another building here");
            } else {
                view.game.generator.building(building, p.x, p.y).spawn(view);
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
}
