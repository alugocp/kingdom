package net.lugocorp.kingdom.game.core;
import net.lugocorp.kingdom.ai.prediction.CapturedEvents;
import net.lugocorp.kingdom.game.combat.Combat;
import net.lugocorp.kingdom.game.combat.Damage;
import net.lugocorp.kingdom.game.combat.HitPoints;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class contains utility functions for writing new Ability effects
 */
public class AbilityLogic {

    /**
     * Convenience wrapper for an active Ability that implements an attack
     */
    public static SideEffect attack(GameView view, Unit attacker, Damage dmg) {
        Map<Point, Combat> targets = new HashMap<>();
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
                if (t.unit.get().combat.health.isVulnerable()) {
                    targets.put(p, t.unit.get().combat);
                    points.add(p);
                }
            } else if (t.building.isPresent() && t.building.get().combat.health.isVulnerable()) {
                targets.put(p, t.building.get().combat);
                points.add(p);
            }
        }

        // Have the human Player select which target to attack
        view.selector.select(points, "No attack targets are in range", (Point p) -> {
            attacker.combat.attack(view, targets.get(p), dmg);
            view.game.mechanics.turns.unitHasActed(view, attacker);
        });
        return attacker.getLeader().get().select(view, points, "No attack targets are in range",
                (Point p) -> SideEffect.all(attacker.combat.attack(view, targets.get(p), dmg),
                        () -> view.game.mechanics.turns.unitHasActed(view, attacker)));
    }

    /**
     * Private helper method for common heal abilities
     */
    private static SideEffect heal(GameView view, Unit healer, int hitPoints,
            Function<Tile, Optional<HitPoints>> getHitPoints) {
        Map<Point, HitPoints> targets = new HashMap<>();
        Set<Point> points = new HashSet<>();

        // Grab every possible heal target within range
        Set<Point> unfiltered = Hexagons.getNeighbors(healer.getPoint(), 1);
        for (Point p : unfiltered) {
            Optional<Tile> t = view.game.world.getTile(p);
            if (!t.isPresent()) {
                continue;
            }
            Optional<HitPoints> hp = getHitPoints.apply(t.get());
            if (hp.isPresent()) {
                targets.put(p, hp.get());
                points.add(p);
            }
        }

        // Have the Player select which target to heal
        return healer.getLeader().get().select(view, points, "No heal targets are in range", (Point p) -> () -> {
            targets.get(p).heal(hitPoints);
            view.game.mechanics.turns.unitHasActed(view, healer);
        });
    }

    /**
     * Ability that heals a Unit
     */
    public static SideEffect healUnit(GameView view, Unit healer, int hitPoints) {
        return AbilityLogic.heal(view, healer, hitPoints, (Tile t) -> t.unit.map((Unit u) -> u.combat.health));
    }

    /**
     * Ability that heals a Building which fits the given criteria
     */
    public static SideEffect healBuilding(GameView view, Unit healer, int hitPoints,
            Function<Building, Boolean> criteria) {
        return AbilityLogic.heal(view, healer, hitPoints, (Tile t) -> {
            if (t.building.isPresent()) {
                Building b = t.building.get();
                if (criteria.apply(b)) {
                    return Optional.of(b.combat.health);
                }
            }
            return Optional.empty();
        });
    }

    /**
     * Ability that spawns a building at the caster's location
     */
    public static SideEffect build(GameView view, Unit caster, String building, Function<Tile, Boolean> criteria) {
        Point p = caster.getPoint();
        if (view.game.world.getTile(p).isPresent()) {
            Tile t = view.game.world.getTile(p).get();

            if (t.building.isPresent()) {
                return () -> view.logger.log("Cannot place another building here");
            }
            if (criteria.apply(t)) {
                Building b = view.game.generator.building(building, p.x, p.y);
                return () -> {
                    b.spawn(view);
                    view.game.mechanics.turns.unitHasActed(view, caster);
                };
            }
            return () -> view.logger.log("Invalid tile for this ability");
        }
        return SideEffect.none;
    }

    /**
     * Ability that does something while on a particular Building
     */
    public static SideEffect doOnBuilding(GameView view, Unit caster, Function<Building, Boolean> criteria,
            Supplier<SideEffect> effect) {
        Point p = CapturedEvents.instance.isActive()
                ? CapturedEvents.instance.getFakePoint().map((Point p1) -> p1).orElse(caster.getPoint())
                : caster.getPoint();
        boolean isOnBuilding = view.game.world.getTile(p).flatMap((Tile t) -> t.building).map(criteria).orElse(false);
        return isOnBuilding ? effect.get() : SideEffect.none;
    }

    /**
     * Ability that harvests an Item from some Tile
     */
    public static SideEffect harvest(GameView view, Unit caster, String item, Function<Building, Boolean> criteria) {
        return AbilityLogic.doOnBuilding(view, caster, criteria,
                () -> caster.haul.isFull() ? SideEffect.none : () -> caster.haul.add(view.game.generator.item(item)));
    }
}
