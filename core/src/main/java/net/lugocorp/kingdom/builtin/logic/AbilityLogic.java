package net.lugocorp.kingdom.builtin.logic;
import net.lugocorp.kingdom.ai.prediction.CapturedEvents;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.actions.ActivateAction;
import net.lugocorp.kingdom.game.combat.Damage;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.ui.overlay.EntityRisingOverlay;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.SideEffect;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
     * Attack ability with variable Damage based on the target
     */
    public static SideEffect dynamicDamageAttack(GameView view, Unit attacker, int range,
            Function<Tile, Damage> getDamage) {
        Map<Point, Entity> targets = new HashMap<>();
        Set<Point> points = new HashSet<>();

        // Grab every possible attack target within range
        Set<Point> unfiltered = Hexagons.getNeighbors(attacker.getPoint(), range);
        for (Point p : unfiltered) {
            Optional<Tile> tile = view.game.world.getTile(p);
            if (!tile.isPresent()) {
                continue;
            }
            Tile t = tile.get();
            if (t.unit.isPresent()) {
                if (t.unit.get().combat.health.isVulnerable()) {
                    targets.put(p, t.unit.get());
                    points.add(p);
                }
            } else if (t.building.isPresent() && t.building.get().combat.health.isVulnerable()) {
                targets.put(p, t.building.get());
                points.add(p);
            }
        }

        // Use overridden method from Player to determine how targets are selected
        return attacker.getLeader().get().select(view, points, "No attack targets are in range", (Point p) -> {
            final Damage dmg = getDamage.apply(view.game.world.getTile(p).get());
            return SideEffect.all(attacker.combat.attack(view, targets.get(p), dmg),
                    () -> view.game.actions.unitHasActed(view, attacker, new ActivateAction()));
        });
    }

    /**
     * Convenience wrapper for an active Ability that implements an attack and maybe
     * an additional effect
     */
    public static SideEffect attackAndEffect(GameView view, Unit attacker, Damage dmg, int range,
            Optional<Function<Point, SideEffect>> effect) {
        Map<Point, Entity> targets = new HashMap<>();
        Set<Point> points = new HashSet<>();

        // Grab every possible attack target within range
        Set<Point> unfiltered = Hexagons.getNeighbors(attacker.getPoint(), range);
        for (Point p : unfiltered) {
            Optional<Tile> tile = view.game.world.getTile(p);
            if (!tile.isPresent()) {
                continue;
            }
            Tile t = tile.get();
            if (t.unit.isPresent()) {
                if (t.unit.get().combat.health.isVulnerable()) {
                    targets.put(p, t.unit.get());
                    points.add(p);
                }
            } else if (t.building.isPresent() && t.building.get().combat.health.isVulnerable()) {
                targets.put(p, t.building.get());
                points.add(p);
            }
        }

        // Use overridden method from Player to determine how targets are selected
        return attacker.getLeader().get().select(view, points, "No attack targets are in range",
                (Point p) -> SideEffect.all(attacker.combat.attack(view, targets.get(p), dmg),
                        effect.map((Function<Point, SideEffect> f) -> f.apply(p)).orElse(SideEffect.none),
                        () -> view.game.actions.unitHasActed(view, attacker, new ActivateAction())));
    }

    /**
     * Simpler version of attackAndEffect()
     */
    public static SideEffect attack(GameView view, Unit attacker, Damage dmg, int range) {
        return AbilityLogic.attackAndEffect(view, attacker, dmg, range, Optional.empty());
    }

    /**
     * Private helper method for common heal abilities
     */
    private static SideEffect heal(GameView view, Unit healer, int hitPoints, Function<Tile, Entity> getEntity) {
        Map<Point, Entity> targets = new HashMap<>();
        Set<Point> points = new HashSet<>();

        // Grab every possible heal target within range
        Set<Point> unfiltered = Hexagons.getNeighbors(healer.getPoint(), 1);
        for (Point p : unfiltered) {
            Optional<Tile> t = view.game.world.getTile(p);
            if (!t.isPresent()) {
                continue;
            }
            Entity entity = getEntity.apply(t.get());
            if (entity != null) {
                targets.put(p, entity);
                points.add(p);
            }
        }

        // Have the Player select which target to heal
        return healer.getLeader().get().select(view, points, "No heal targets are in range",
                (Point p) -> SideEffect.all(healer.combat.heal(view, targets.get(p), hitPoints),
                        () -> view.game.actions.unitHasActed(view, healer, new ActivateAction())));
    }

    /**
     * Ability that heals a Unit
     */
    public static SideEffect healUnit(GameView view, Unit healer, int hitPoints) {
        return AbilityLogic.heal(view, healer, hitPoints, (Tile t) -> t.unit.orElse(null));
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
                    return b;
                }
            }
            return null;
        });
    }

    /**
     * Effect that decreases the damage taken by a TakeDamageEvent
     */
    public static SideEffect defense(Event event, int points) {
        Events.TakeDamageEvent e = (Events.TakeDamageEvent) event;
        e.dmg.base -= points;
        return SideEffect.none;
    }

    /**
     * Ability that spawns a building at the caster's location
     */
    public static SideEffect build(GameView view, Unit caster, String building, Function<Tile, Boolean> criteria) {
        Point p = caster.getPoint();
        if (view.game.world.getTile(p).isPresent()) {
            Tile t = view.game.world.getTile(p).get();

            if (t.building.isPresent()) {
                return () -> view.hud.logger.error("Cannot place another building here");
            }
            if (criteria.apply(t)) {
                Building b = view.game.generator.building(building, p.x, p.y);
                return () -> {
                    b.spawn(view);
                    view.game.actions.unitHasActed(view, caster, new ActivateAction());
                };
            }
            return () -> view.hud.logger.error("Invalid tile for this ability");
        }
        return SideEffect.none;
    }

    /**
     * Ability that does something while on a particular Tile
     */
    public static SideEffect doOnTile(GameView view, Unit caster, Function<Tile, Boolean> criteria,
            Supplier<SideEffect> effect) {
        Point p = CapturedEvents.instance.isActive()
                ? CapturedEvents.instance.getFakePoint().map((Point p1) -> p1).orElse(caster.getPoint())
                : caster.getPoint();
        boolean isOnTile = view.game.world.getTile(p).map(criteria).orElse(false);
        return isOnTile ? effect.get() : SideEffect.none;
    }

    /**
     * Ability that does something while on a particular Building
     */
    public static SideEffect doOnBuilding(GameView view, Unit caster, Function<Building, Boolean> criteria,
            Supplier<SideEffect> effect) {
        return AbilityLogic.doOnTile(view, caster,
                (Tile t) -> t.building.map((Building b) -> criteria.apply(b)).orElse(false), effect);
    }

    /**
     * Does something when the Unit is adjacent to some criteria
     */
    public static SideEffect doWhenAdjacent(GameView view, Unit wielder, Function<Tile, Boolean> criteria,
            Supplier<SideEffect> effect) {
        final Set<Point> coords = Hexagons.getNeighbors(wielder.getPoint(), 1);
        for (Point p : coords) {
            final Optional<Tile> t = view.game.world.getTile(p);
            if (t.isPresent() && criteria.apply(t.get())) {
                return effect.get();
            }
        }
        return SideEffect.none;
    }

    /**
     * Ability that harvests an Item from some Tile
     */
    public static SideEffect harvestFromTile(GameView view, Unit caster, String item,
            Function<Tile, Boolean> criteria) {
        final List<SideEffect> effects = SideEffect.list();
        if (!caster.haul.isFull()) {
            Item i = view.game.generator.item(item);
            effects.add(caster.handleEvent(view, new Events.HarvestEvent(caster, i)));
            effects.add(() -> {
                caster.haul.add(i);
                view.overlays.add(new EntityRisingOverlay(view, caster, ColorScheme.WHITE.hex, i.name));
                if (caster.getLeader().map((Player p) -> !p.isHumanPlayer()).orElse(false)) {
                    CompPlayer comp = (CompPlayer) caster.getLeader().get();
                    if (i.hasTag("natural") || i.hasTag("fruit")) {
                        comp.stats.naturalHarvest.add(1);
                    } else {
                        comp.stats.otherHarvest.add(1);
                    }
                }
            });
        }
        return AbilityLogic.doOnTile(view, caster, criteria, () -> SideEffect.all(effects));
    }

    /**
     * Ability that harvests an Item from some Building
     */
    public static SideEffect harvestFromBuilding(GameView view, Unit caster, String item,
            Function<Building, Boolean> criteria) {
        return AbilityLogic.harvestFromTile(view, caster, item,
                (Tile t) -> t.building.map((Building b) -> criteria.apply(b)).orElse(false));
    }

    /**
     * Ability that generates auction points
     */
    public static SideEffect generateAuctionPoints(GameView view, Unit caster, int points) {
        Events.GenerateAuctionPointsEvent event = new Events.GenerateAuctionPointsEvent(caster, points);
        caster.handleEvent(view, event);
        return () -> view.game.mechanics.auction.addPoints(view, caster.getPoint(), event.points);
    }
}
