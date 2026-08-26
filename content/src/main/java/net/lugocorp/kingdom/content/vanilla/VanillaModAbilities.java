package net.lugocorp.kingdom.content.vanilla;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.builtin.logic.AbilityLogic;
import net.lugocorp.kingdom.content.Labels;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.EntityType;
import net.lugocorp.kingdom.gameplay.combat.Damage;
import net.lugocorp.kingdom.gameplay.events.AllEventHandlers;
import net.lugocorp.kingdom.gameplay.events.Stratified;
import net.lugocorp.kingdom.math.HexSide;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import net.lugocorp.kingdom.utils.SideEffect;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * SECTION Abilities
 */
class VanillaModAbilities {

    /**
     * Registers all Abilities for the Vanilla mod
     */
    static void registerEvents(AllEventHandlers events) {
        // Acid Skin
        new Stratified<Ability>(events.ability, Labels.ability_acid_skin).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_acid_skin);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Adjacent attackers take damage"))
                .add(Events.AttackedEvent.class, (GameView view, Ability receiver, Events.AttackedEvent e) -> {
                    if (e.attacker instanceof Unit) {
                        Unit target = (Unit) e.target;
                        Unit attacker = (Unit) e.attacker;
                        return Hexagons.areNeighbors(attacker.getPoint(), target.getPoint())
                                ? attacker.combat.takeDamage(view, new Damage(1), target)
                                : new SideEffect();
                    }
                    return new SideEffect();
                });

        // Bash
        new Stratified<Ability>(events.ability, Labels.ability_bash).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_bash);
                    return new SideEffect();
                }).add(AbilityLogic.attack(new Damage(3), 1));

        // Bite
        new Stratified<Ability>(events.ability, Labels.ability_bite).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_bite);
                    return new SideEffect();
                }).add(AbilityLogic.attack(new Damage(2), 1));

        // Build Healing Fountain
        new Stratified<Ability>(events.ability, Labels.ability_build_healing_fountain)
                .add(Events.GenerateAbilityEvent.class,
                        (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                            e.blob.setIcon(Labels.asset_build_healing_fountain);
                            return new SideEffect();
                        })
                .add(AbilityLogic.desc("Constructs a healing fountain")).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.build(view,
                                receiver.wielder, Labels.building_healing_fountain, (Tile t) -> true));

        // Build Marketplace
        new Stratified<Ability>(events.ability, Labels.ability_build_marketplace).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_build_vault);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Builds a marketplace")).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.build(view,
                                receiver.wielder, Labels.building_marketplace, (Tile t) -> true));

        // Collapse Mine
        new Stratified<Ability>(events.ability, Labels.ability_collapse_mine).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_collapse_mine);
                    return new SideEffect();
                })
                .add(AbilityLogic.desc(
                        "Target a mine occupied by an enemy unit. The unit, mine, and any adjacent enemy units all take damage."))
                .add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            Set<Point> mines = Lambda.filter((Point p) -> view.game.world.getTile(p)
                                    .map((Tile t) -> !t.getLeader().equals(receiver.wielder.getLeader()) && t.building
                                            .map((Building b) -> b.name.equals(Labels.building_mine)).orElse(false)
                                            && t.unit.isPresent())
                                    .orElse(false), Hexagons.getNeighbors(receiver.wielder.getPoint(), 2));
                            return receiver.wielder.getLeader().get().select(view, mines, "No mines in range",
                                    (Point p) -> {
                                        Set<Entity> targets = new HashSet<>();
                                        final SideEffect effects = new SideEffect();
                                        targets.add(view.game.world.getTile(p).get().unit.get());
                                        targets.add(view.game.world.getTile(p).get().building.get());
                                        for (Point p1 : Hexagons.getNeighbors(p, 1)) {
                                            Optional<Unit> u = view.game.world.getTile(p1).flatMap((Tile t) -> t.unit);
                                            if (u.map((Unit u1) -> !u1.isFriendly(receiver.wielder)).orElse(false)) {
                                                targets.add(u.get());
                                            }
                                        }
                                        for (Entity t : targets) {
                                            effects.add(receiver.wielder.combat.attack(view, t, new Damage(2)));
                                        }
                                        return effects;
                                    });
                        });

        // Combat Loot
        new Stratified<Ability>(events.ability, Labels.ability_combat_loot).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_combat_loot);
                    return new SideEffect();
                }).add(AbilityLogic.desc("+2 damage if this unit has a stored item"))
                .add(Events.AttackEvent.class, (GameView view, Ability receiver, Events.AttackEvent e) -> {
                    if (receiver.wielder.haul.hasItems()) {
                        e.dmg.base += 2;
                    }
                    return new SideEffect();
                });

        // Craft Golden Spear
        new Stratified<Ability>(events.ability, Labels.ability_craft_golden_spear)
                .add(Events.GenerateAbilityEvent.class,
                        (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                            e.blob.setIcon(Labels.asset_golden_spear);
                            return new SideEffect();
                        })
                .add(AbilityLogic.desc("Gives the target adjacent ally a golden spear (+2 damage)"))
                .add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            final Set<Point> targets = Lambda.filter((Point p) -> view.game.world.getTile(p)
                                    .flatMap((Tile t) -> t.unit)
                                    .map((Unit u) -> u.leadership.sameLeader(receiver.wielder) && !u.haul.isFull())
                                    .orElse(false), Hexagons.getNeighbors(receiver.wielder.getPoint(), 1));
                            return receiver.wielder.getLeader().get().select(view, targets, "No allies in range",
                                    (Point p) -> {
                                        return new SideEffect().add(() -> {
                                            view.game.world.getUnit(p).ifPresent((Unit u) -> u.haul
                                                    .add(view.game.generator.item(Labels.item_golden_spear)));
                                            view.game.actions.unitHasCastSpell(view, receiver.wielder);
                                        });
                                    });
                        });

        // Craft Slime Armor
        new Stratified<Ability>(events.ability, Labels.ability_craft_slime_armor).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_slime_armor);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Consumes one goo item and gives the target ally slime armor (+2 defense)"))
                .add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            if (!receiver.wielder.haul.hasItemWithTag(Labels.tag_goo)) {
                                view.hud.logger.error("Cannot craft slime armor without a goo item");
                                return new SideEffect();
                            }
                            final Set<Point> targets = Lambda.filter((Point p) -> view.game.world.getTile(p)
                                    .flatMap((Tile t) -> t.unit)
                                    .map((Unit u) -> u.leadership.sameLeader(receiver.wielder) && !u.haul.isFull())
                                    .orElse(false), Hexagons.getNeighbors(receiver.wielder.getPoint(), 2));
                            return receiver.wielder.getLeader().get().select(view, targets, "No allies in range",
                                    (Point p) -> {
                                        return new SideEffect().add(() -> {
                                            receiver.wielder.haul.removeItemWithTag(Labels.tag_goo);
                                            view.game.world.getUnit(p).ifPresent((Unit u) -> u.haul
                                                    .add(view.game.generator.item(Labels.item_slime_armor)));
                                            view.game.actions.unitHasCastSpell(view, receiver.wielder);
                                        });
                                    });
                        });

        // Crystal Skin
        new Stratified<Ability>(events.ability, Labels.ability_crystal_skin).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_acid_skin, 0x34a33b, 0x318ec0);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Extra defense")).add(Events.TakeDamageEvent.class,
                        (GameView view, Ability receiver, Events.TakeDamageEvent e) -> AbilityLogic.defense(e, 2));

        // Defensive Bloom
        new Stratified<Ability>(events.ability, Labels.ability_defensive_bloom).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_defensive_blossom);
                    return new SideEffect();
                }).add(AbilityLogic.desc("15% chance to generate a natural item when the unit is attacked"))
                .add(Events.AttackedEvent.class, (GameView view, Ability receiver, Events.AttackedEvent e) -> {
                    return !receiver.wielder.haul.isFull() && Lambda.chance(15)
                            ? new SideEffect().add(() -> receiver.wielder.haul
                                    .add(view.game.mechanics.loot.dropByTag(view.game, Labels.tag_natural)))
                            : new SideEffect();
                });

        // Deposit Seeds
        new Stratified<Ability>(events.ability, Labels.ability_deposit_seeds).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_deposit_seeds);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Chance to spawn a meadow when this unit moves"))
                .add(Events.UnitMovedEvent.class, (GameView view, Ability receiver, Events.UnitMovedEvent e) -> {
                    Point p = receiver.wielder.getPoint();
                    return view.game.world.getTile(p).map((Tile t) -> !t.building.isPresent()).orElse(false)
                            && Lambda.chance(10)
                                    ? new SideEffect().add(() -> view.game.generator
                                            .building(Labels.building_meadow, p.x, p.y).spawn(view))
                                    : new SideEffect();
                });

        // Dig Mine
        new Stratified<Ability>(events.ability, Labels.ability_dig_mine).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_dig_mine);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Digs a mine")).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.build(view,
                                receiver.wielder, Labels.building_mine, (Tile t) -> t.name.equals(Labels.tile_rock)));

        // Dungeon Delve
        new Stratified<Ability>(events.ability, Labels.ability_dungeon_delve).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_dungeon_delve);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Deals 3 damage and generates loot if targeting a tile with a building"))
                .add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic
                                .attackAndEffect(view, receiver.wielder, new Damage(3), 1,
                                        Optional.of((Point p) -> !receiver.wielder.haul.isFull()
                                                && view.game.world.getTile(p).map((Tile t) -> t.building).isPresent()
                                                        ? new SideEffect().add(() -> receiver.wielder.haul
                                                                .add(view.game.mechanics.loot.drop(view.game)))
                                                        : new SideEffect())));

        // Economic Activity
        new Stratified<Ability>(events.ability, Labels.ability_economic_activity).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_economic_activity);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Generates 3 auction points when occupying a marketplace"))
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver, Events.SpawnEvent e) -> new SideEffect()
                                .add(() -> view.game.future.addFutureTick("Tick", receiver, 1, true, Optional.empty())))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.doOnBuilding(view,
                                receiver.wielder, (Building b) -> b.name.equals(Labels.building_marketplace),
                                () -> AbilityLogic.generateAuctionPoints(view, receiver.wielder, 1)));

        // Edible
        new Stratified<Ability>(events.ability, Labels.ability_edible).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_edible);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Generates food"))
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver, Events.SpawnEvent e) -> new SideEffect()
                                .add(() -> view.game.future.addFutureTick("Tick", receiver, 4, true, Optional.empty())))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromTile(view,
                                receiver.wielder, view.game.mechanics.loot.getByTag(Labels.tag_fruit),
                                (Tile t) -> true));

        // Efficient Stomach
        new Stratified<Ability>(events.ability, Labels.ability_efficient_stomach).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_stomach);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Can cast an additional spell above 50% hunger"))
                .add(Events.GetMaxActivationsEvent.class,
                        (GameView view, Ability receiver, Events.GetMaxActivationsEvent e) -> {
                            if (receiver.wielder.hunger.get(view) * 2 > receiver.wielder.hunger
                                    .getTurnsBeforeHunger()) {
                                e.max++;
                            }
                            return new SideEffect();
                        });

        // Entrenched
        new Stratified<Ability>(events.ability, Labels.ability_entrenched).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_local_defender);
                    return new SideEffect();
                }).add(AbilityLogic.desc("+2 armor when on a building"))
                .add(Events.TakeDamageEvent.class, (GameView view, Ability receiver, Events.TakeDamageEvent e) -> {
                    final boolean isOnActiveBuilding = view.game.world.getTile(receiver.wielder.getPoint())
                            .map((Tile t) -> t.building).isPresent();
                    return isOnActiveBuilding ? AbilityLogic.defense(e, 2) : new SideEffect();
                });

        // Fast
        new Stratified<Ability>(events.ability, Labels.ability_fast).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_running_through_nature, 0x246719, 0xff9525);
                    return new SideEffect();
                }).add(AbilityLogic.desc("This unit can normally move 3 spaces per turn"))
                .add(Events.UnitMoveDistanceEvent.class,
                        (GameView view, Ability receiver, Events.UnitMoveDistanceEvent e) -> {
                            e.distance = 3;
                            return new SideEffect();
                        });

        // Fireball
        new Stratified<Ability>(events.ability, Labels.ability_fireball).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_fireball);
                    return new SideEffect();
                }).add(AbilityLogic.attack(new Damage(2), 3));

        // Fire Cannon
        new Stratified<Ability>(events.ability, Labels.ability_fire_cannon).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_fire_cannon);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Deals 2 damage (or 5 damage to a building)"))
                .add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic
                                .dynamicDamageAttack(view, receiver.wielder, 2,
                                        (Tile t) -> t.building.isPresent() && !t.unit.isPresent()
                                                ? new Damage(5)
                                                : new Damage(2)));

        // Fire Laser
        new Stratified<Ability>(events.ability, Labels.ability_fire_laser).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_fire_laser);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Damage up to 3 units in a line")).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            final Set<Point> targets = new HashSet<>();
                            final Map<Point, HexSide> sideToPoint = new HashMap<>();
                            for (HexSide side : HexSide.values()) {
                                Point dest = Hexagons.followLine(receiver.wielder.getPoint(), side, 3);
                                targets.add(dest);
                                sideToPoint.put(dest, side);
                            }
                            return receiver.wielder.getLeader().get().select(view, targets, "No targets available",
                                    (Point p) -> {
                                        final SideEffect effects = new SideEffect();
                                        final HexSide side = sideToPoint.get(p);
                                        for (int a = 0; a < 3; a++) {
                                            Point p1 = Hexagons.followLine(receiver.wielder.getPoint(), side, a + 1);
                                            view.game.world.getTile(p1).flatMap((Tile t) -> t.unit)
                                                    .ifPresent((Unit u) -> effects.add(
                                                            receiver.wielder.combat.attack(view, u, new Damage(2))));
                                        }
                                        effects.add(() -> view.game.actions.unitHasCastSpell(view, receiver.wielder));
                                        if (receiver.wielder.leadership.belongsToHuman()) {
                                            effects.add(() -> view.hud.bot.tileMenu.refresh());
                                        }
                                        return effects;
                                    });
                        });

        // Forage in Meadow
        new Stratified<Ability>(events.ability, Labels.ability_forage_in_meadow).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_pick_flowers);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Harvests natural items from meadows every 4 turns"))
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver, Events.SpawnEvent e) -> new SideEffect()
                                .add(() -> view.game.future.addFutureTick("Tick", receiver, 4, true, Optional.empty())))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(
                                view, receiver.wielder, view.game.mechanics.loot.getByTag(Labels.tag_natural),
                                (Building b) -> b.name.equals(Labels.building_meadow)));

        // Ghastly Thrall
        new Stratified<Ability>(events.ability, Labels.ability_ghastly_thrall).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_raise_undead);
                    return new SideEffect();
                })
                .add(AbilityLogic.desc(
                        "This unit can only move to tiles adjacent to The Necromancer, and will follow The Necromancer as it moves"))
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver, Events.SpawnEvent e) -> new SideEffect().add(
                                () -> view.game.events.signals.addListener(Events.AfterUnitMovedEvent.class, receiver)))
                .add(Events.AfterUnitMovedEvent.class,
                        (GameView view, Ability receiver, Events.AfterUnitMovedEvent e) -> {
                            if (e.unit.name.equals(Labels.unit_necromancer)
                                    && e.unit.leadership.sameLeader(receiver.wielder)) {
                                receiver.wielder.movement.turnOffPreCheck();
                                SideEffect se = receiver.wielder.movement.move(view, e.previous, e.parallel);
                                receiver.wielder.movement.turnOnPreCheck();
                                return se;
                            }
                            return new SideEffect();
                        })
                .add(Events.CanUnitMoveEvent.class, (GameView view, Ability receiver, Events.CanUnitMoveEvent e) -> {
                    final Set<Point> radius = Lambda.filter(
                            (Point p) -> view.game.world.getUnit(p)
                                    .map((Unit u) -> u.name.equals(Labels.unit_necromancer)).orElse(false),
                            Hexagons.getNeighbors(e.tile.getPoint(), 1));
                    if (radius.size() == 0) {
                        e.canWalkOnBuilding = false;
                    }
                    return new SideEffect();
                });

        // Gilded Strike
        new Stratified<Ability>(events.ability, Labels.ability_gilded_strike).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_gilded_strike);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Deals 2 damage and generates 10 gold"))
                .add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic
                                .attackAndEffect(view, receiver.wielder, new Damage(2), 1, Optional.of((Point p) -> {
                                    return new SideEffect().add(() -> {
                                        receiver.wielder.getLeader().ifPresent((Player l) -> {
                                            l.gold += 10;
                                        });
                                        view.hud.top.update(view.game);
                                    });
                                })));

        // Green Fortress
        new Stratified<Ability>(events.ability, Labels.ability_green_fortress).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_green_fortress);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Extra defense on forests"))
                .add(Events.TakeDamageEvent.class, (GameView view, Ability receiver, Events.TakeDamageEvent e) -> {
                    boolean isForest = view.game.world.getTile(receiver.wielder.getPoint())
                            .flatMap((Tile t) -> t.building).map((Building b) -> b.name.equals(Labels.building_forest))
                            .orElse(false);
                    return isForest ? AbilityLogic.defense(e, 2) : new SideEffect();
                });

        // Harvest Goo
        new Stratified<Ability>(events.ability, Labels.ability_harvest_goo).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_harvest_slime);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Harvests goo from mines every 4 turns"))
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver, Events.SpawnEvent e) -> new SideEffect()
                                .add(() -> view.game.future.addFutureTick("Tick", receiver, 4, true, Optional.empty())))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(
                                view, receiver.wielder, view.game.mechanics.loot.getByTag(Labels.tag_goo),
                                (Building b) -> b.name.equals(Labels.building_mine)));

        // Harvest Mushrooms
        new Stratified<Ability>(events.ability, Labels.ability_harvest_mushrooms).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_harvest_mushroom);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Harvests mushrooms from forests and mines every 4 turns"))
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver, Events.SpawnEvent e) -> new SideEffect()
                                .add(() -> view.game.future.addFutureTick("Tick", receiver, 4, true, Optional.empty())))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(
                                view, receiver.wielder, view.game.mechanics.loot.getByTag(Labels.tag_mushroom),
                                (Building b) -> b.name.equals(Labels.building_forest)
                                        || b.name.equals(Labels.building_mine)));

        // Heal Wounds
        new Stratified<Ability>(events.ability, Labels.ability_heal_wounds).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_heal_wounds);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Heals 5 damage"))
                .add(Events.AbilityActivatedEvent.class, (GameView view, Ability receiver,
                        Events.AbilityActivatedEvent e) -> AbilityLogic.healUnit(view, receiver.wielder, 5));

        // High Vision
        new Stratified<Ability>(events.ability, Labels.ability_high_vision).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_night_vision, 0xa18f1e, 0xffffff);
                    return new SideEffect();
                }).add(AbilityLogic.desc("This unit can normally see in a 4 tile radius around it"))
                .add(Events.GetVisionEvent.class, (GameView view, Ability receiver, Events.GetVisionEvent e) -> {
                    e.radius = 4;
                    return new SideEffect();
                });

        // Hug
        new Stratified<Ability>(events.ability, Labels.ability_hug).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_hug);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Heals the target adjacent unit for a few hit points"))
                .add(Events.AbilityActivatedEvent.class, (GameView view, Ability receiver,
                        Events.AbilityActivatedEvent e) -> AbilityLogic.healUnit(view, receiver.wielder, 2));

        // Hungry Frog Magic
        new Stratified<Ability>(events.ability, Labels.ability_hungry_frog_magic).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_hungry_frog_magic);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Consumes all stored items and heals adjacent friendly units"))
                .add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            final SideEffect effects = new SideEffect().add(() -> receiver.wielder.haul.empty());
                            Set<Point> targets = Hexagons.getNeighbors(receiver.wielder.getPoint(), 1);
                            for (Point p : targets) {
                                Optional<Unit> u = view.game.world.getUnit(p);
                                if (u.map((Unit u1) -> u1.isFriendly(receiver.wielder)).orElse(false)) {
                                    effects.add(receiver.wielder.combat.heal(view, u.get(), 10));
                                }
                            }
                            return effects;
                        });

        // Hunt Fish
        new Stratified<Ability>(events.ability, Labels.ability_hunt_fish).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_hunt_fish);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Harvests fish from water tiles"))
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver, Events.SpawnEvent e) -> new SideEffect()
                                .add(() -> view.game.future.addFutureTick("Tick", receiver, 4, true, Optional.empty())))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromTile(view,
                                receiver.wielder, Labels.item_fish, (Tile t) -> t.name.equals(Labels.tile_water)));

        // Hurl Rock
        new Stratified<Ability>(events.ability, Labels.ability_hurl_rock).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_fire_cannon);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Deals 2 damage with a 15% chance to stun"))
                .add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic
                                .attackAndEffect(view, receiver.wielder, new Damage(2), 2, Optional.of((Point p) -> {
                                    Optional<Unit> u = view.game.world.getUnit(p);
                                    return u.isPresent() && Lambda.chance(15)
                                            ? u.get().abilities.addStatusEffect(view, Labels.status_effect_stunned)
                                            : new SideEffect();
                                })));

        // Inject Poison
        new Stratified<Ability>(events.ability, Labels.ability_inject_poison).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_bite, 0xffffff, 0x3dac2a);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Deals 1 damage and poisons the target"))
                .add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic
                                .attackAndEffect(view, receiver.wielder, new Damage(1), 1, Optional.of((Point p) -> {
                                    final Optional<Unit> u = view.game.world.getUnit(p);
                                    return u.isPresent()
                                            ? u.get().abilities.addStatusEffect(view, Labels.status_effect_poisoned)
                                            : new SideEffect();
                                })));

        // Life Aura
        new Stratified<Ability>(events.ability, Labels.ability_life_aura).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_life_aura);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Generates 3 unit points per turn"))
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver, Events.SpawnEvent e) -> new SideEffect()
                                .add(() -> view.game.future.addFutureTick("Tick", receiver, 1, true, Optional.empty())))
                .add("Tick", (GameView view, Ability receiver, Events.RepeatedEvent e) -> new SideEffect()
                        .add(() -> receiver.wielder.getLeader()
                                .ifPresent((Player p) -> p.addUnitPoints(view, receiver.wielder.getPoint(), 3))));

        // Liquifying Presence
        new Stratified<Ability>(events.ability, Labels.ability_liquifying_presence)
                .add(Events.GenerateAbilityEvent.class,
                        (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                            e.blob.setIcon(Labels.asset_liquifying_presence);
                            return new SideEffect();
                        })
                .add(AbilityLogic.desc("Deals 1 damage each turn to an occupied building"))
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver, Events.SpawnEvent e) -> new SideEffect()
                                .add(() -> view.game.future.addFutureTick("Tick", receiver, 1, true, Optional.empty())))
                .add("Tick", (GameView view, Ability receiver, Events.RepeatedEvent e) -> {
                    Optional<Building> b = view.game.world.getTile(receiver.wielder.getPoint())
                            .flatMap((Tile t) -> t.building);
                    return b.isPresent()
                            ? b.get().combat.takeDamage(view, new Damage(1), receiver.wielder)
                            : new SideEffect();
                });

        // Local Defender
        new Stratified<Ability>(events.ability, Labels.ability_local_defender).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_local_defender);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Adjacent buildings have +3 armor"))
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver, Events.SpawnEvent e) -> new SideEffect()
                                .add(() -> view.game.events.signals.addListener(Events.AttackedEvent.class, receiver)))
                .add(Events.AttackedEvent.class, (GameView view, Ability receiver, Events.AttackedEvent e) -> {
                    if (e.target.isEntityType(EntityType.BUILDING)
                            && receiver.wielder.getLeader().equals(e.target.getLeader())
                            && Hexagons.areNeighbors(receiver.wielder.getPoint(), e.target.getPoint())) {
                        e.dmg.base -= 3;
                    }
                    return new SideEffect();
                });

        // Loose Gems
        new Stratified<Ability>(events.ability, Labels.ability_loose_gems).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_gems);
                    return new SideEffect();
                }).add(AbilityLogic.desc("15% chance to generate an emerald when the unit attacks"))
                .add(Events.AttackEvent.class, (GameView view, Ability receiver, Events.AttackEvent e) -> {
                    return !receiver.wielder.haul.isFull() && Lambda.chance(15)
                            ? new SideEffect().add(() -> receiver.wielder.haul
                                    .add(view.game.mechanics.loot.dropByTag(view.game, Labels.tag_gem)))
                            : new SideEffect();
                });

        // Market Boom
        new Stratified<Ability>(events.ability, Labels.ability_market_boom).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_market_boom);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Attacks generate 4 auction points"))
                .add(Events.AttackEvent.class, (GameView view, Ability receiver, Events.AttackEvent e) -> AbilityLogic
                        .generateAuctionPoints(view, receiver.wielder, 4));

        // Market Indicator
        new Stratified<Ability>(events.ability, Labels.ability_market_indicator).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_market_indicator);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Generates 3 auction points when adjacent to a marketplace"))
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver, Events.SpawnEvent e) -> new SideEffect()
                                .add(() -> view.game.future.addFutureTick("Tick", receiver, 1, true, Optional.empty())))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.doWhenAdjacent(view,
                                receiver.wielder,
                                (Tile t) -> t.building.map((Building b) -> b.name.equals(Labels.building_marketplace))
                                        .orElse(false),
                                () -> AbilityLogic.generateAuctionPoints(view, receiver.wielder, 1)));

        // Market Value Goo
        new Stratified<Ability>(events.ability, Labels.ability_market_value_goo).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_market_value_goo);
                    return new SideEffect();
                })
                .add(AbilityLogic
                        .desc("20% chance to spawn goo when this unit moves. This goo generates auction points."))
                .add(Events.UnitMovedEvent.class, (GameView view, Ability receiver, Events.UnitMovedEvent e) -> {
                    final Point p = receiver.wielder.getPoint();
                    return view.game.world.getTile(p).map((Tile t) -> !t.building.isPresent()).orElse(false)
                            && Lambda.chance(20)
                                    ? new SideEffect().add(() -> view.game.generator
                                            .building(Labels.building_market_value_goo, p.x, p.y).spawn(view))
                                    : new SideEffect();
                });

        // Metabolize
        new Stratified<Ability>(events.ability, Labels.ability_metabolize).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_eat);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Consumes 1 random hauled item to move faster for the next 2 turns"))
                .add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            if (!receiver.wielder.haul.hasItems()) {
                                if (receiver.wielder.leadership.belongsToHuman()) {
                                    view.hud.logger.error("No items to metabolize");
                                }
                                return new SideEffect();
                            }
                            return new SideEffect()
                                    .add(() -> receiver.wielder.haul.remove(receiver.wielder.haul.random()))
                                    .add(receiver.wielder.abilities.addStatusEffect(view, Labels.status_effect_swift));
                        });

        // Mine Gems
        new Stratified<Ability>(events.ability, Labels.ability_mine_gems).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_mine_gems);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Harvests gems from mines"))
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver, Events.SpawnEvent e) -> new SideEffect()
                                .add(() -> view.game.future.addFutureTick("Tick", receiver, 4, true, Optional.empty())))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(
                                view, receiver.wielder, view.game.mechanics.loot.getByTag(Labels.tag_gem),
                                (Building b) -> b.name.equals(Labels.building_mine)));

        // Mine Gold
        new Stratified<Ability>(events.ability, Labels.ability_mine_gold).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_mine_gold);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Harvests gold coins from mines every 4 turns"))
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver, Events.SpawnEvent e) -> new SideEffect()
                                .add(() -> view.game.future.addFutureTick("Tick", receiver, 4, true, Optional.empty())))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(
                                view, receiver.wielder, Labels.item_gold_coin,
                                (Building b) -> b.name.equals(Labels.building_mine)));

        // Mountain Strider
        new Stratified<Ability>(events.ability, Labels.ability_mountain_strider).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_mountain_strider);
                    return new SideEffect();
                }).add(AbilityLogic.desc("This unit can traverse mountains"))
                .add(Events.CanUnitMoveEvent.class, (GameView view, Ability receiver, Events.CanUnitMoveEvent e) -> {
                    if (e.tile.building.map((Building b) -> b.name.equals(Labels.building_mountain)).orElse(false)) {
                        e.canWalkOnBuilding = true;
                    }
                    return new SideEffect();
                });

        // Necrotic Blast
        new Stratified<Ability>(events.ability, Labels.ability_necrotic_blast).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_fireball, 0xdbc626, 0x53cb51);
                    return new SideEffect();
                }).add(AbilityLogic.attack(new Damage(3), 3));

        // Night Vision
        new Stratified<Ability>(events.ability, Labels.ability_night_vision).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_night_vision);
                    return new SideEffect();
                }).add(AbilityLogic.desc("This unit can see normally at night"))
                .add(Events.GetVisionEvent.class, (GameView view, Ability receiver, Events.GetVisionEvent e) -> {
                    e.canSeeAtNight = true;
                    return new SideEffect();
                });

        // Pebble Shot
        new Stratified<Ability>(events.ability, Labels.ability_pebble_shot).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_fire_cannon);
                    return new SideEffect();
                }).add(AbilityLogic.attack(new Damage(1), 3));

        // Pick Apples
        new Stratified<Ability>(events.ability, Labels.ability_pick_apples).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_pick_apples);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Harvests apples from forests every 4 turns"))
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver, Events.SpawnEvent e) -> new SideEffect()
                                .add(() -> view.game.future.addFutureTick("Tick", receiver, 4, true, Optional.empty())))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(
                                view, receiver.wielder, Labels.item_apple,
                                (Building b) -> b.name.equals(Labels.building_forest)));

        // Pious
        new Stratified<Ability>(events.ability, Labels.ability_pious).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_worship_glyph);
                    return new SideEffect();
                }).add(AbilityLogic.desc("This unit generates +1 favor")).add(Events.GenerateFavorEvent.class,
                        (GameView view, Ability receiver, Events.GenerateFavorEvent e) -> {
                            e.favor += 1;
                            return new SideEffect();
                        });

        // Plant Forest
        new Stratified<Ability>(events.ability, Labels.ability_plant_forest).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_plant_forest);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Plants a forest")).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.build(view,
                                receiver.wielder, Labels.building_forest,
                                (Tile t) -> t.name.equals(Labels.tile_grass)));

        // Plant Meadow
        new Stratified<Ability>(events.ability, Labels.ability_plant_meadow).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_plant_meadow);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Plants a meadow")).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.build(view,
                                receiver.wielder, Labels.building_meadow,
                                (Tile t) -> t.name.equals(Labels.tile_grass)));

        // Plate Mail
        new Stratified<Ability>(events.ability, Labels.ability_plate_mail).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_defense);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Extra defense")).add(Events.TakeDamageEvent.class,
                        (GameView view, Ability receiver, Events.TakeDamageEvent e) -> AbilityLogic.defense(e, 2));

        // Protective Spores
        new Stratified<Ability>(events.ability, Labels.ability_protective_spores).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_spores);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Grants the target +2 defense for the next 2 turns"))
                .add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            final Set<Point> points = Lambda.filter((Point p) -> view.game.world.getUnit(p).isPresent(),
                                    Hexagons.getNeighbors(receiver.wielder.getPoint(), 1));
                            return receiver.wielder.getLeader().get().select(view, points,
                                    "No valid targets for extra defense", (Point p) -> {
                                        final Unit target = view.game.world.getUnit(p).get();
                                        return new SideEffect()
                                                .add(target.abilities.addStatusEffect(view,
                                                        Labels.status_effect_extra_defense))
                                                .add(() -> view.game.actions.unitHasCastSpell(view, receiver.wielder));
                                    });
                        });

        // Pummel
        new Stratified<Ability>(events.ability, Labels.ability_pummel).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_smash);
                    return new SideEffect();
                }).add(AbilityLogic.attack(new Damage(2), 1));

        // Raise Undead
        new Stratified<Ability>(events.ability, Labels.ability_raise_undead).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_raise_undead);
                    return new SideEffect();
                })
                .add(AbilityLogic.desc(
                        "Consumes 5 health to spawn a Ghastly Thrall (max one at a time, remains adjacent to this unit)"))
                .add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            // Check for any existing Ghastly Thrall
                            for (Point p : Hexagons.getNeighbors(receiver.wielder.getPoint(), 1)) {
                                if (view.game.world.getUnit(p)
                                        .map((Unit u) -> u.name.equals(Labels.unit_ghastly_thrall)).orElse(false)) {
                                    if (receiver.wielder.leadership.belongsToHuman()) {
                                        view.hud.logger.error("You can only raise one Ghastly Thrall at a time");
                                    }
                                    return new SideEffect();
                                }
                            }

                            // Select a Tile to spawn the Unit on
                            final Set<Point> points = Lambda.filter((Point p) -> view.game.world.getTile(p)
                                    .map((Tile t) -> !t.unit.isPresent()).orElse(false),
                                    Hexagons.getNeighbors(receiver.wielder.getPoint(), 1));
                            return receiver.wielder.getLeader().get().select(view, points, "Nowhere to spawn unit",
                                    (Point p) -> {
                                        return new SideEffect().add(receiver.wielder.combat.takeDamage(view,
                                                new Damage(5), receiver.wielder)).add(() -> {
                                                    view.game.actions.unitHasCastSpell(view, receiver.wielder);
                                                    final Unit u = view.game.generator.unit(Labels.unit_ghastly_thrall,
                                                            p.x, p.y);
                                                    u.spawn(view);
                                                    view.game.setLeader(view, u, receiver.wielder.getLeader());
                                                });
                                    });
                        });

        // Regeneration
        new Stratified<Ability>(events.ability, Labels.ability_regeneration).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_regeneration);
                    return new SideEffect();
                }).add(AbilityLogic.desc("This unit heals a little each turn"))
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver, Events.SpawnEvent e) -> new SideEffect()
                                .add(() -> view.game.future.addFutureTick("Tick", receiver, 1, true, Optional.empty())))
                .add("Tick", (GameView view, Ability receiver, Events.RepeatedEvent e) -> receiver.wielder.combat
                        .heal(view, 1));

        // Remove Poison
        new Stratified<Ability>(events.ability, Labels.ability_remove_poison).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_heal_wounds, 0xaa2007, 0x3dac2a);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Removes a Poisoned effect from the target and heals both units"))
                .add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            final Set<Point> targets = Lambda.filter((Point p) -> view.game.world.getUnit(p)
                                    .map((Unit u) -> u.leadership.sameLeader(receiver.wielder)
                                            && u.abilities.hasStatusEffect(Labels.status_effect_poisoned))
                                    .orElse(false), Hexagons.getNeighbors(receiver.wielder.getPoint(), 1));
                            return receiver.wielder.getLeader().get().select(view, targets,
                                    "No poisoned targets in range", (Point p) -> {
                                        return new SideEffect().add(() -> {
                                            view.game.world.getUnit(p).ifPresent((Unit u) -> u.abilities
                                                    .removeStatusEffect(view, Labels.status_effect_poisoned));
                                            view.game.actions.unitHasCastSpell(view, receiver.wielder);
                                        });
                                    });
                        });

        // Revenge of the Forest
        new Stratified<Ability>(events.ability, Labels.ability_revenge_of_the_forest)
                .add(Events.GenerateAbilityEvent.class,
                        (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                            e.blob.setIcon(Labels.asset_sword_slash, 0xffffff, 0x00ff00);
                            return new SideEffect();
                        })
                .add(AbilityLogic.desc("Deals 2 damage (or 4 damage when on a forest)"))
                .add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver,
                                Events.AbilityActivatedEvent e) -> AbilityLogic
                                        .dynamicDamageAttack(view, receiver.wielder, 1,
                                                (Tile t) -> new Damage(t.building
                                                        .map((Building b) -> b.name.equals(Labels.building_forest))
                                                        .orElse(false) ? 4 : 2)));

        // Rock Appetite
        new Stratified<Ability>(events.ability, Labels.ability_rock_appetite).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_crystal);
                    return new SideEffect();
                }).add(AbilityLogic.desc("This unit can only eat gem items"))
                .add(Events.CanEatItemEvent.class, (GameView view, Ability receiver, Events.CanEatItemEvent e) -> {
                    e.edible = e.item.tags.has(Labels.tag_gem);
                    return new SideEffect();
                });

        // Running Through Nature
        new Stratified<Ability>(events.ability, Labels.ability_running_through_nature)
                .add(Events.GenerateAbilityEvent.class,
                        (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                            e.blob.setIcon(Labels.asset_running_through_nature);
                            return new SideEffect();
                        })
                .add(AbilityLogic.desc("This unit is faster on buildings")).add(Events.UnitMoveDistanceEvent.class,
                        (GameView view, Ability receiver, Events.UnitMoveDistanceEvent e) -> {
                            boolean buildingIsPassive = view.game.world.getTile(e.unit.getPoint())
                                    .flatMap((Tile t) -> t.building).isPresent();
                            if (buildingIsPassive) {
                                e.distance++;
                            }
                            return new SideEffect();
                        });

        // Self Sacrifice
        new Stratified<Ability>(events.ability, Labels.ability_self_sacrifice).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_self_sacrifice);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Transfers all their health but 1 to the target unit"))
                .add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            final int hitPoints = receiver.wielder.combat.health.get() - 1;
                            return new SideEffect().add(AbilityLogic.healUnit(view, receiver.wielder, hitPoints))
                                    .add(() -> receiver.wielder.combat.health.set(1));
                        });

        // Sacred Seeds
        new Stratified<Ability>(events.ability, Labels.ability_sacred_seeds).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_deposit_seeds);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Harvests seeds from meadows that can be consumed to generate favor"))
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver, Events.SpawnEvent e) -> new SideEffect()
                                .add(() -> view.game.future.addFutureTick("Tick", receiver, 4, true, Optional.empty())))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(
                                view, receiver.wielder, Labels.item_sacred_seed,
                                (Building b) -> b.name.equals(Labels.building_meadow)));

        // Shell Defense
        new Stratified<Ability>(events.ability, Labels.ability_shell_defense).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_defense);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Extra defense")).add(Events.TakeDamageEvent.class,
                        (GameView view, Ability receiver, Events.TakeDamageEvent e) -> AbilityLogic.defense(e, 2));

        // Shield Defense
        new Stratified<Ability>(events.ability, Labels.ability_shield_defense).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_defense);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Extra defense")).add(Events.TakeDamageEvent.class,
                        (GameView view, Ability receiver, Events.TakeDamageEvent e) -> AbilityLogic.defense(e, 2));

        // Slime Shot
        new Stratified<Ability>(events.ability, Labels.ability_slime_shot).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_slime_shot);
                    return new SideEffect();
                }).add(AbilityLogic.attack(new Damage(2), 3));

        // Slow
        new Stratified<Ability>(events.ability, Labels.ability_slow).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_running_through_nature, 0x246719, 0xffd9b1);
                    return new SideEffect();
                }).add(AbilityLogic.desc("This unit can normally move 1 space per turn"))
                .add(Events.UnitMoveDistanceEvent.class,
                        (GameView view, Ability receiver, Events.UnitMoveDistanceEvent e) -> {
                            e.distance = 1;
                            return new SideEffect();
                        });

        // Smash
        new Stratified<Ability>(events.ability, Labels.ability_smash).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_smash);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Deals 3 damage with a 15% chance to stun"))
                .add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic
                                .attackAndEffect(view, receiver.wielder, new Damage(3), 1, Optional.of((Point p) -> {
                                    Optional<Unit> u = view.game.world.getUnit(p);
                                    return u.isPresent() && Lambda.chance(15)
                                            ? u.get().abilities.addStatusEffect(view, Labels.status_effect_stunned)
                                            : new SideEffect();
                                })));

        // Stomp
        new Stratified<Ability>(events.ability, Labels.ability_stomp).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_stomp);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Deals 2 damage with a 15% chance to stun"))
                .add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic
                                .attackAndEffect(view, receiver.wielder, new Damage(2), 1, Optional.of((Point p) -> {
                                    Optional<Unit> u = view.game.world.getUnit(p);
                                    return u.isPresent() && Lambda.chance(15)
                                            ? u.get().abilities.addStatusEffect(view, Labels.status_effect_stunned)
                                            : new SideEffect();
                                })));

        // Stone Defense
        new Stratified<Ability>(events.ability, Labels.ability_stone_defense).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_defense);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Extra defense")).add(Events.TakeDamageEvent.class,
                        (GameView view, Ability receiver, Events.TakeDamageEvent e) -> AbilityLogic.defense(e, 2));

        // Subterranean Potions
        new Stratified<Ability>(events.ability, Labels.ability_subterranean_potions)
                .add(Events.GenerateAbilityEvent.class,
                        (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                            e.blob.setIcon(Labels.asset_subterranean_potions);
                            return new SideEffect();
                        })
                .add(AbilityLogic.desc("Generates Health Potions from Mines"))
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver, Events.SpawnEvent e) -> new SideEffect()
                                .add(() -> view.game.future.addFutureTick("Tick", receiver, 4, true, Optional.empty())))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(
                                view, receiver.wielder, Labels.item_health_potion,
                                (Building b) -> b.name.equals(Labels.building_mine)));

        // Swim
        new Stratified<Ability>(events.ability, Labels.ability_swim).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_swim);
                    return new SideEffect();
                }).add(AbilityLogic.desc("This unit can swim on water tiles"))
                .add(Events.CanUnitMoveEvent.class, (GameView view, Ability receiver, Events.CanUnitMoveEvent e) -> {
                    if (!e.canWalkOnTile && e.tile.name.equals(Labels.tile_water)) {
                        e.canWalkOnTile = true;
                    }
                    return new SideEffect();
                });

        // Swing Axe
        new Stratified<Ability>(events.ability, Labels.ability_swing_axe).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_axe_swing);
                    return new SideEffect();
                }).add(AbilityLogic.attack(new Damage(3), 1));

        // Sword Slash
        new Stratified<Ability>(events.ability, Labels.ability_sword_slash).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_sword_slash);
                    return new SideEffect();
                }).add(AbilityLogic.attack(new Damage(2), 1));

        // Thorny Skin
        new Stratified<Ability>(events.ability, Labels.ability_thorny_skin).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_thorny_skin);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Adjacent attackers take damage"))
                .add(Events.AttackedEvent.class, (GameView view, Ability receiver, Events.AttackedEvent e) -> {
                    if (e.attacker instanceof Unit) {
                        Unit target = (Unit) e.target;
                        Unit attacker = (Unit) e.attacker;
                        return Hexagons.areNeighbors(attacker.getPoint(), target.getPoint())
                                ? attacker.combat.takeDamage(view, new Damage(1), target)
                                : new SideEffect();
                    }
                    return new SideEffect();
                });

        // Total Appetite
        new Stratified<Ability>(events.ability, Labels.ability_total_appetite).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_stomach);
                    return new SideEffect();
                }).add(AbilityLogic.desc("This unit can eat any item"))
                .add(Events.CanEatItemEvent.class, (GameView view, Ability receiver, Events.CanEatItemEvent e) -> {
                    e.edible = true;
                    return new SideEffect();
                });

        // Trade
        new Stratified<Ability>(events.ability, Labels.ability_trade).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_trade);
                    return new SideEffect();
                }).add(AbilityLogic.desc("Harvests gold coins from a marketplace every other turn"))
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver, Events.SpawnEvent e) -> new SideEffect()
                                .add(() -> view.game.future.addFutureTick("Tick", receiver, 2, true, Optional.empty())))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(
                                view, receiver.wielder, Labels.item_gold_coin,
                                (Building b) -> b.name.equals(Labels.building_marketplace)));
    }
}
