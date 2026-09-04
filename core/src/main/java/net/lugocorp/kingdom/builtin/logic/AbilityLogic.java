package net.lugocorp.kingdom.builtin.logic;
import net.lugocorp.kingdom.ai.prediction.CapturedEvents;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.content.Labels;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.gameplay.combat.Damage;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.gameplay.events.StratifiedPayload;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.overlay.LabelOverlay;
import net.lugocorp.kingdom.ui.overlay.RisingOverlay;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
import com.badlogic.gdx.math.Vector3;
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
     * Sets the Ability's description to a flat string
     */
    public static StratifiedPayload<Ability, Events.GetDescriptionEvent> desc(String desc) {
        return new StratifiedPayload<>(Events.GetDescriptionEvent.class,
                (GameView view, Ability receiver, Events.GetDescriptionEvent e) -> {
                    e.desc = desc;
                    return new SideEffect();
                });
    }

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
            return new SideEffect().add(attacker.combat.attack(view, targets.get(p), dmg))
                    .add(() -> view.game.actions.unitHasCastSpell(view, attacker));
        }, (Tile t) -> {
            if (!t.unit.isPresent() && !t.building.isPresent()) {
                return Optional.empty();
            }
            final Damage dmg = getDamage.apply(t);
            final Entity target = (t.unit.isPresent() ? t.unit : t.building).get();
            attacker.handleEvent(view, new Events.AttackEvent(attacker, target, dmg));
            return Optional.of(new LabelOverlay(t.getPoint(),
                    new Vector3(0f, view.av.loaders.models.getModelHeight(target.getModelName()), 0f), 0xff0000,
                    dmg.toString()));
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
        return attacker.getLeader().get().select(view, points, "No attack targets are in range", (Point p) -> {
            final SideEffect effects = new SideEffect();
            if (targets.get(p) != null) {
                effects.add(attacker.combat.attack(view, targets.get(p), dmg))
                        .add(effect.map((Function<Point, SideEffect> f) -> f.apply(p)).orElse(new SideEffect()))
                        .add(() -> view.game.actions.unitHasCastSpell(view, attacker));
                if (attacker.leadership.belongsToHuman()) {
                    effects.add(() -> view.hud.bot.tileMenu.refresh());
                }
            }
            return effects;
        }, (Tile t) -> {
            if (!t.unit.isPresent() && !t.building.isPresent()) {
                return Optional.empty();
            }
            final Damage dmgLabel = new Damage(dmg);
            final Entity target = (t.unit.isPresent() ? t.unit : t.building).get();
            attacker.handleEvent(view, new Events.AttackEvent(attacker, target, dmgLabel));
            return Optional.of(new LabelOverlay(t.getPoint(),
                    new Vector3(0f, view.av.loaders.models.getModelHeight(target.getModelName()), 0f), 0xff0000,
                    dmgLabel.toString()));
        });
    }

    /**
     * Simpler version of attackAndEffect()
     */
    public static StratifiedPayload[] attack(Damage dmg, int range) {
        return new StratifiedPayload[]{
                new StratifiedPayload<Ability, Events.AbilityActivatedEvent>(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic
                                .attackAndEffect(view, receiver.wielder, dmg, range, Optional.empty())),
                AbilityLogic.desc(String.format("Deals %s to %s", dmg,
                        range == 1
                                ? "a select melee target"
                                : String.format("a select target up to %d tiles away", range)))};
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
                (Point p) -> new SideEffect().add(healer.combat.heal(view, targets.get(p), hitPoints))
                        .add(() -> view.game.actions.unitHasCastSpell(view, healer)));
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
        return new SideEffect();
    }

    /**
     * Ability that spawns a building at the caster's location
     */
    public static SideEffect build(GameView view, Unit caster, String building, Function<Tile, Boolean> criteria) {
        Point p = caster.getPoint();
        if (view.game.world.getTile(p).isPresent()) {
            Tile t = view.game.world.getTile(p).get();

            if (t.building.isPresent()) {
                return new SideEffect().add(() -> view.hud.logger.error("Cannot place another building here"));
            }
            if (criteria.apply(t)) {
                Building b = view.game.generator.building(building, p.x, p.y);
                return new SideEffect().add(() -> {
                    b.spawn(view);
                    view.game.actions.unitHasCastSpell(view, caster);
                }).add(caster.abilities.addStatusEffect(view, Labels.status_effect_exhausted));
            }
            return new SideEffect().add(() -> view.hud.logger.error("Invalid tile for this ability"));
        }
        return new SideEffect();
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
        return isOnTile ? effect.get() : new SideEffect();
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
        return new SideEffect();
    }

    /**
     * Ability that harvests an Item from some Tile
     */
    public static SideEffect harvestFromTile(GameView view, Unit caster, String item,
            Function<Tile, Boolean> criteria) {
        return AbilityLogic.doOnTile(view, caster, criteria, () -> {
            final SideEffect effects = new SideEffect();
            if (!caster.haul.isFull()) {
                Item i = view.game.generator.item(item);
                effects.add(() -> {
                    caster.haul.add(i);
                    view.overlays.entity(caster)
                            .addRising(new RisingOverlay(view, caster, ColorScheme.WHITE.hex, i.name));
                });
                effects.add(caster.handleEvent(view, new Events.HarvestEvent(caster, i)));
            }
            return effects;
        });
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
        final SideEffect effects = new SideEffect();
        Events.GenerateAuctionPointsEvent event = new Events.GenerateAuctionPointsEvent(caster, points);
        return effects.add(caster.handleEvent(view, event))
                .add(() -> view.game.mechanics.auction.addPoints(view, caster.getPoint(), event.points));
    }
}
