package net.lugocorp.kingdom.builtin.logic;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.content.Labels;
import net.lugocorp.kingdom.context.GameViewContext;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.gameplay.combat.Damage;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.gameplay.events.StratifiedPayload;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.overlay.LabelOverlay;
import net.lugocorp.kingdom.ui.overlay.RisingOverlay;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
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
    public static StratifiedPayload<GameViewContext, Ability, Events.GetDescriptionEvent> desc(String desc) {
        return new StratifiedPayload<>(Events.GetDescriptionEvent.class,
                (GameViewContext ctx, Ability receiver, Events.GetDescriptionEvent e) -> {
                    e.desc = desc;
                    return new SideEffect();
                });
    }

    public static StratifiedPayload[] traverse(String building) {
        return new StratifiedPayload[]{new StratifiedPayload<>(Events.CanUnitMoveEvent.class,
                (GameViewContext ctx, Ability receiver, Events.CanUnitMoveEvent e) -> {
                    if (e.tile.building.map((Building b) -> b.name.equals(building)).orElse(false)) {
                        e.canWalkOnBuilding = true;
                    }
                    return new SideEffect();
                }), AbilityLogic.desc(String.format("This unit can traverse %ss", building))};
    }

    /**
     * Attack ability with variable Damage based on the target
     */
    public static SideEffect dynamicDamageAttack(GameViewContext ctx, Unit attacker, int range,
            Function<Tile, Damage> getDamage) {
        Map<Point, Entity> targets = new HashMap<>();
        Set<Point> points = new HashSet<>();

        // Grab every possible attack target within range
        Set<Point> unfiltered = Hexagons.getNeighbors(attacker.getPoint(), range);
        for (Point p : unfiltered) {
            Optional<Tile> tile = ctx.view.game.world.getTile(p);
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
        return attacker.getLeader().get().select(ctx.view, points, "No attack targets are in range", (Point p) -> {
            final Damage dmg = getDamage.apply(ctx.view.game.world.getTile(p).get());
            return new SideEffect().add(attacker.combat.attack(ctx, targets.get(p), dmg))
                    .add(() -> ctx.view.game.actions.unitHasCastSpell(ctx.view, attacker));
        }, (Tile t) -> {
            if (!t.unit.isPresent() && t.building.map((Building b) -> !b.combat.health.isVulnerable()).orElse(true)) {
                return Optional.empty();
            }
            final Damage dmg = getDamage.apply(t);
            final Entity target = (t.unit.isPresent() ? t.unit : t.building).get();
            attacker.handleEvent(new Events.AttackEvent(attacker, target, dmg));
            target.handleEvent(new Events.AttackedEvent(target, attacker, dmg));
            target.handleEvent(new Events.TakeDamageEvent(target, dmg));
            return Optional.of(new LabelOverlay(t.getPoint(),
                    new Vector3(0f, ctx.view.av.loaders.models.getModelHeight(target.getModelName()), 0f), 0xff0000,
                    dmg.toString()));
        });
    }

    /**
     * Convenience wrapper for an active Ability that implements an attack and maybe
     * an additional effect
     */
    public static SideEffect attackAndEffect(GameViewContext ctx, Unit attacker, Damage dmg, int range,
            Optional<Function<Point, SideEffect>> effect) {
        Map<Point, Entity> targets = new HashMap<>();
        Set<Point> points = new HashSet<>();

        // Grab every possible attack target within range
        Set<Point> unfiltered = Hexagons.getNeighbors(attacker.getPoint(), range);
        for (Point p : unfiltered) {
            Optional<Tile> tile = ctx.view.game.world.getTile(p);
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
        return attacker.getLeader().get().select(ctx.view, points, "No attack targets are in range", (Point p) -> {
            final SideEffect effects = new SideEffect();
            if (targets.containsKey(p)) {
                effects.add(attacker.combat.attack(ctx, targets.get(p), dmg))
                        .add(effect.map((Function<Point, SideEffect> f) -> f.apply(p)).orElse(new SideEffect()))
                        .add(() -> ctx.view.game.actions.unitHasCastSpell(ctx.view, attacker));
                if (attacker.leadership.belongsToHuman()) {
                    effects.add(() -> ctx.view.hud.bot.tileMenu.refresh());
                }
            }
            return effects;
        }, (Tile t) -> {
            if (!t.unit.isPresent() && t.building.map((Building b) -> !b.combat.health.isVulnerable()).orElse(true)) {
                return Optional.empty();
            }
            final Damage dmgLabel = new Damage(dmg);
            final Entity target = (t.unit.isPresent() ? t.unit : t.building).get();
            attacker.handleEvent(new Events.AttackEvent(attacker, target, dmgLabel));
            target.handleEvent(new Events.AttackedEvent(target, attacker, dmgLabel));
            target.handleEvent(new Events.TakeDamageEvent(target, dmgLabel));
            return Optional.of(new LabelOverlay(t.getPoint(),
                    new Vector3(0f, ctx.view.av.loaders.models.getModelHeight(target.getModelName()), 0f), 0xff0000,
                    dmgLabel.toString()));
        });
    }

    /**
     * Simpler version of attackAndEffect()
     */
    public static StratifiedPayload[] attack(Damage dmg, int range) {
        return new StratifiedPayload[]{
                new StratifiedPayload<>(Events.AbilityActivatedEvent.class,
                        (GameViewContext ctx, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic
                                .attackAndEffect(ctx, receiver.wielder, dmg, range, Optional.empty())),
                AbilityLogic.desc(String.format("Deals %s to %s", dmg,
                        range == 1
                                ? "a select melee target"
                                : String.format("a select target up to %d tiles away", range)))};
    }

    /**
     * Private helper method for common heal abilities
     */
    private static <E extends Entity> SideEffect heal(GameView view, Unit healer, Function<Point, Integer> getHitPoints,
            Function<Tile, Optional<E>> getEntity) {
        Map<Point, E> targets = new HashMap<>();
        Set<Point> points = new HashSet<>();

        // Grab every possible heal target within range
        Set<Point> unfiltered = Hexagons.getNeighbors(healer.getPoint(), 1);
        for (Point p : unfiltered) {
            final Optional<Tile> t = view.game.world.getTile(p);
            if (!t.isPresent()) {
                continue;
            }
            final Optional<E> entity = getEntity.apply(t.get());
            entity.ifPresent((E e) -> {
                targets.put(p, e);
                points.add(p);
            });
        }

        // Have the Player select which target to heal
        return healer.getLeader().get().select(view, points, "No heal targets are in range",
                (Point p) -> new SideEffect().add(healer.combat.heal(view, targets.get(p), getHitPoints.apply(p)))
                        .add(() -> view.game.actions.unitHasCastSpell(view, healer)),
                (Tile t) -> {
                    final Point p = t.getPoint();
                    if (!points.contains(p)) {
                        return Optional.empty();
                    }
                    final Entity target = targets.get(p);
                    final Events.HealEntityEvent e = new Events.HealEntityEvent(healer, target, getHitPoints.apply(p));
                    healer.handleEvent(e);
                    return Optional.of(new LabelOverlay(p,
                            new Vector3(0f, view.av.loaders.models.getModelHeight(target.getModelName()), 0f), 0x00ff00,
                            String.format("+%d", e.amount)));
                });
    }

    /**
     * Ability that heals a Unit
     */
    public static SideEffect healUnit(GameView view, Unit healer, int hitPoints) {
        return AbilityLogic.heal(view, healer, (Point p) -> hitPoints, (Tile t) -> t.unit);
    }

    /**
     * Ability that heals a Unit for some dynamic amount
     */
    public static SideEffect healUnit(GameView view, Unit healer, Function<Unit, Integer> getHitPoints) {
        return AbilityLogic.heal(view, healer,
                (Point p) -> getHitPoints.apply(view.game.world.getTile(p).get().unit.get()), (Tile t) -> t.unit);
    }

    /**
     * Ability that heals a Building which fits the given criteria
     */
    public static SideEffect healBuilding(GameView view, Unit healer, int hitPoints,
            Function<Building, Boolean> criteria) {
        return AbilityLogic.heal(view, healer, (Point p) -> hitPoints, (Tile t) -> {
            if (t.building.isPresent()) {
                Building b = t.building.get();
                if (criteria.apply(b)) {
                    return Optional.of(b);
                }
            }
            return Optional.empty();
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
     * Effect that generates gold
     */
    public static SideEffect gold(GameView view, Unit unit, int gold) {
        return new SideEffect().add(() -> {
            view.overlays.entity(unit)
                    .addRising(new RisingOverlay(view, unit, ColorScheme.GOLD.hex, String.format("+%d gold", gold)));
            unit.getLeader().get().gold += gold;
            view.hud.top.update(view.game);
        });
    }

    /**
     * Generates gold from some Tile every few turns
     */
    public static StratifiedPayload[] harvestGoldFromTile(int gold, int period, String tileDesc,
            Function<Tile, Boolean> criteria) {
        return new StratifiedPayload[]{
                AbilityLogic.desc(period == 1
                        ? String.format("Generates %d gold every turn on %s", gold, tileDesc)
                        : String.format("Generates %d gold every %d turns on %s", gold, period, tileDesc)),
                new StratifiedPayload<>(Events.SpawnEvent.class,
                        (GameViewContext ctx, Ability receiver, Events.SpawnEvent e) -> new SideEffect().add(
                                () -> ctx.view.game.future.addFutureTick("Tick", receiver, 1, true, Optional.empty()))),
                new StratifiedPayload<>(Events.RepeatedEvent.class,
                        (GameViewContext ctx, Ability receiver, Events.RepeatedEvent e) -> {
                            final SideEffect effects = new SideEffect();
                            if (receiver.canUse(ctx.view) && ctx.view.game.world.getTile(receiver.wielder.getPoint())
                                    .map((Tile t) -> criteria.apply(t)).orElse(false)) {
                                effects.add(AbilityLogic.gold(ctx.view, receiver.wielder, gold));
                                effects.add(() -> receiver.goOnCooldown(period - 1));
                            }
                            return effects;
                        }).setOverrideChannel("Tick")};
    }

    /**
     * Generates gold from some Building every few turns
     */
    public static StratifiedPayload[] harvestGoldFromBuilding(int gold, int period, String buildingDesc,
            Function<Building, Boolean> criteria) {
        return AbilityLogic.harvestGoldFromTile(gold, period, buildingDesc,
                (Tile t) -> t.building.map((Building b) -> criteria.apply(b)).orElse(false));
    }

    /**
     * Harvests an Item from some Tile every few turns
     */
    public static StratifiedPayload[] harvestFromTile(Function<GameView, String> getItem, int period, String itemDesc,
            String tileDesc, Function<Tile, Boolean> criteria) {
        return new StratifiedPayload[]{
                AbilityLogic.desc(period == 1
                        ? String.format("Generates %s every turn on %s", itemDesc, tileDesc)
                        : String.format("Generates %s every %d turns on %s", itemDesc, period, tileDesc)),
                new StratifiedPayload<>(Events.SpawnEvent.class,
                        (GameViewContext ctx, Ability receiver, Events.SpawnEvent e) -> new SideEffect().add(
                                () -> ctx.view.game.future.addFutureTick("Tick", receiver, 1, true, Optional.empty()))),
                new StratifiedPayload<>(Events.RepeatedEvent.class,
                        (GameViewContext ctx, Ability receiver, Events.RepeatedEvent e) -> {
                            final SideEffect effects = new SideEffect();
                            if (!receiver.wielder.haul.isFull() && receiver.canUse(ctx.view)
                                    && ctx.view.game.world.getTile(receiver.wielder.getPoint())
                                            .map((Tile t) -> criteria.apply(t)).orElse(false)) {
                                final Item i = ctx.view.game.generator.item(getItem.apply(ctx.view));
                                effects.add(() -> {
                                    receiver.wielder.haul.add(i);
                                    ctx.view.overlays.entity(receiver.wielder).addRising(new RisingOverlay(ctx.view,
                                            receiver.wielder, ColorScheme.WHITE.hex, i.name));
                                    receiver.goOnCooldown(period - 1);
                                });
                                effects.add(receiver.wielder.handleEvent(new Events.HarvestEvent(receiver.wielder, i)));
                            }
                            return effects;
                        }).setOverrideChannel("Tick")};
    }

    /**
     * Harvests an Item from some Tile every few turns
     */
    public static StratifiedPayload[] harvestFromTile(String item, int period, String tileDesc,
            Function<Tile, Boolean> criteria) {
        return AbilityLogic.harvestFromTile((GameView view) -> item, period, item, tileDesc, criteria);
    }

    /**
     * Harvests an Item from some Building every few turns
     */
    public static StratifiedPayload[] harvestFromBuilding(Function<GameView, String> getItem, int period,
            String itemDesc, String buildingDesc, Function<Building, Boolean> criteria) {
        return AbilityLogic.harvestFromTile(getItem, period, itemDesc, buildingDesc,
                (Tile t) -> t.building.map((Building b) -> criteria.apply(b)).orElse(false));
    }

    /**
     * Harvests an Item from some Building every few turns
     */
    public static StratifiedPayload[] harvestFromBuilding(String item, int period, String buildingDesc,
            Function<Building, Boolean> criteria) {
        return AbilityLogic.harvestFromBuilding((GameView view) -> item, period, item, buildingDesc, criteria);
    }

    /**
     * Ability that spawns a building at the caster's location
     */
    public static StratifiedPayload[] build(String building, String tileDesc, Function<Tile, Boolean> criteria) {
        return new StratifiedPayload[]{new StratifiedPayload<>(Events.AbilityActivatedEvent.class,
                (GameViewContext ctx, Ability receiver, Events.AbilityActivatedEvent e) -> {
                    final Point p = receiver.wielder.getPoint();
                    if (ctx.view.game.world.getTile(p).isPresent()) {
                        final Tile t = ctx.view.game.world.getTile(p).get();
                        if (t.building.isPresent()) {
                            return new SideEffect()
                                    .add(() -> ctx.view.hud.logger.error("Cannot place another building here"));
                        }
                        if (criteria.apply(t)) {
                            final Building b = ctx.view.game.generator.building(building, p.x, p.y);
                            return new SideEffect().add(() -> {
                                b.spawn(ctx.view);
                                ctx.view.game.actions.unitHasCastSpell(ctx.view, receiver.wielder);
                            }).add(() -> receiver.goOnCooldown(2));
                        }
                        return new SideEffect().add(() -> ctx.view.hud.logger.error("Invalid tile for this ability"));
                    }
                    return new SideEffect();
                }), AbilityLogic.desc(String.format("Spawns a %s on %s (cooldown for 2 turns)", building, tileDesc))};
    }

    /**
     * Calls into build() but it works on any Tile
     */
    public static StratifiedPayload[] build(String building) {
        return AbilityLogic.build(building, "the currently occupied tile", (Tile t) -> true);
    }

    /**
     * Ability that spawns a Building within some range of the caster
     */
    public static StratifiedPayload[] buildRemote(String building, int range, int cooldown, String tileDesc,
            Function<Tile, Boolean> criteria) {
        return new StratifiedPayload[]{new StratifiedPayload<>(Events.AbilityActivatedEvent.class,
                (GameViewContext ctx, Ability receiver, Events.AbilityActivatedEvent e) -> {
                    final Set<Point> points = Lambda.filter(
                            (Point p) -> ctx.view.game.world.getTile(p)
                                    .map((Tile t) -> !t.building.isPresent() && criteria.apply(t)).orElse(false),
                            Hexagons.getNeighbors(receiver.wielder.getPoint(), range));
                    return receiver.wielder.getLeader().map((Player player) -> player.select(ctx.view, points,
                            String.format("No valid location to place a %s", building), (Point p) -> {
                                final SideEffect effects = new SideEffect();
                                if (ctx.view.game.world.getTile(p)
                                        .map((Tile t) -> !t.building.isPresent() && criteria.apply(t)).orElse(false)) {
                                    final Building b = ctx.view.game.generator.building(building, p.x, p.y);
                                    effects.add(
                                            () -> ctx.view.game.actions.unitHasCastSpell(ctx.view, receiver.wielder))
                                            .add(() -> receiver.goOnCooldown(2)).add(() -> b.spawn(ctx.view));
                                }
                                return effects;
                            })).orElse(new SideEffect());
                }), AbilityLogic.desc(
                        String.format("Spawns a %s on %s (cooldown for %d turns)", building, tileDesc, cooldown))};
    }

    /**
     * Ability that does something while on a particular Tile
     */
    public static SideEffect doOnTile(GameView view, Unit caster, Function<Tile, Boolean> criteria,
            Supplier<SideEffect> effect) {
        boolean isOnTile = view.game.world.getTile(caster.getPoint()).map(criteria).orElse(false);
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
     * Ability that generates auction points
     */
    public static SideEffect generateAuctionPoints(GameView view, Unit caster, int points) {
        final SideEffect effects = new SideEffect();
        Events.GenerateAuctionPointsEvent event = new Events.GenerateAuctionPointsEvent(caster, points);
        return effects.add(caster.handleEvent(event))
                .add(() -> view.game.mechanics.auction.addPoints(view, caster.getPoint(), event.points));
    }

    /**
     * Returns true if Stone Form can be active at the given Point
     */
    public static SideEffect checkStoneForm(GameView view, Unit u) {
        final boolean needsStoneForm = view.game.mechanics.dayNight.isNight()
                || view.game.world.getTile(u.getPoint()).map((Tile t) -> t.name.equals(Labels.tile_rock)).orElse(false);
        final boolean hasStoneForm = u.abilities.hasStatusEffect(Labels.status_effect_stone_form_active);
        final SideEffect effects = new SideEffect();
        if (needsStoneForm && !hasStoneForm) {
            effects.add(u.abilities.addStatusEffect(view, Labels.status_effect_stone_form_active));
        }
        if (!needsStoneForm && hasStoneForm) {
            effects.add(() -> u.abilities.removeStatusEffect(view, Labels.status_effect_stone_form_active));
        }
        return effects;
    }
}
