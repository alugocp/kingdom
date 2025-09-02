package net.lugocorp.kingdom.mod;
import net.lugocorp.kingdom.engine.assets.SpriteLoader;
import net.lugocorp.kingdom.game.combat.Damage;
import net.lugocorp.kingdom.game.core.AbilityLogic;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.core.ItemLogic;
import net.lugocorp.kingdom.game.core.UnitLogic;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.glyph.GlyphCategory;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Entity;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Patron;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.EntityType;
import net.lugocorp.kingdom.game.properties.Inventory;
import net.lugocorp.kingdom.game.properties.Inventory.InventoryType;
import net.lugocorp.kingdom.mod.common.Defs;
import net.lugocorp.kingdom.ui.menu.ArtifactNode;
import net.lugocorp.kingdom.ui.menu.FateNode;
import net.lugocorp.kingdom.ui.menu.InventoryNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.Lambda;
import net.lugocorp.kingdom.utils.code.SideEffect;
import net.lugocorp.kingdom.utils.math.HexSide;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.mods.GameMod;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This mod defines all the content for version 1.0 of the base game
 */
public class KingdomMod implements GameMod {

    /** {@inheritdoc} */
    @Override
    public String getKey() {
        return "vanilla";
    }

    /** {@inheritdoc} */
    @Override
    public String getName() {
        return "Vanilla";
    }

    /** {@inheritdoc} */
    @Override
    public String getDescription() {
        return "Contains all of the base content for this game.";
    }

    /** {@inheritdoc} */
    @Override
    public void registerSprites(SpriteLoader sprites) {
        sprites.register(Defs.assets_placeholder, Defs.assets_icons, InventoryNode.SIDE, InventoryNode.SIDE, 0, 0);
        sprites.register(Defs.assets_potion, Defs.assets_icons, InventoryNode.SIDE, InventoryNode.SIDE, 1, 0);
        sprites.register(Defs.assets_apple, Defs.assets_icons, InventoryNode.SIDE, InventoryNode.SIDE, 2, 0);
        sprites.register(Defs.assets_pouch, Defs.assets_icons, InventoryNode.SIDE, InventoryNode.SIDE, 3, 0);
        sprites.register(Defs.assets_coin, Defs.assets_icons, InventoryNode.SIDE, InventoryNode.SIDE, 0, 1);
        sprites.register(Defs.assets_sword, Defs.assets_icons, InventoryNode.SIDE, InventoryNode.SIDE, 1, 1);
        sprites.register(Defs.assets_shield, Defs.assets_icons, InventoryNode.SIDE, InventoryNode.SIDE, 2, 1);
        sprites.register(Defs.assets_candle, Defs.assets_icons, InventoryNode.SIDE, InventoryNode.SIDE, 3, 1);
        sprites.register(Defs.assets_mushroom, Defs.assets_icons, InventoryNode.SIDE, InventoryNode.SIDE, 0, 2);
        sprites.register(Defs.assets_crystal, Defs.assets_icons, InventoryNode.SIDE, InventoryNode.SIDE, 1, 2);
        sprites.register(Defs.assets_bone, Defs.assets_icons, InventoryNode.SIDE, InventoryNode.SIDE, 2, 2);
        sprites.register(Defs.assets_fish, Defs.assets_icons, InventoryNode.SIDE, InventoryNode.SIDE, 3, 2);
        sprites.register(Defs.assets_flower, Defs.assets_icons, InventoryNode.SIDE, InventoryNode.SIDE, 0, 3);
        sprites.register(Defs.assets_seeds, Defs.assets_icons, InventoryNode.SIDE, InventoryNode.SIDE, 1, 3);
        sprites.register(Defs.assets_golden_feather, Defs.assets_artifacts, ArtifactNode.WIDTH, ArtifactNode.HEIGHT, 0,
                0);
        sprites.register(Defs.assets_raider, Defs.assets_fates, FateNode.WIDTH, FateNode.HEIGHT, 0, 0);
        sprites.register(Defs.assets_merchant, Defs.assets_fates, FateNode.WIDTH, FateNode.HEIGHT, 1, 0);
        sprites.register(Defs.assets_veteran, Defs.assets_fates, FateNode.WIDTH, FateNode.HEIGHT, 2, 0);
        sprites.register(Defs.assets_devout, Defs.assets_fates, FateNode.WIDTH, FateNode.HEIGHT, 3, 0);
        sprites.register(Defs.assets_sentinel, Defs.assets_fates, FateNode.WIDTH, FateNode.HEIGHT, 0, 1);
        sprites.register(Defs.assets_usurper, Defs.assets_fates, FateNode.WIDTH, FateNode.HEIGHT, 1, 1);
        sprites.register(Defs.assets_forager, Defs.assets_fates, FateNode.WIDTH, FateNode.HEIGHT, 2, 1);
    }

    /** {@inheritdoc} */
    @Override
    public void registerEvents(AllEventHandlers events) {

        /**
         * SECTION Default handlers
         */

        // GetVisionEvent
        events.unit.setDefaultHandler("GetVisionEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GetVisionEvent e = (Events.GetVisionEvent) event;
            e.radius = 2;
            return SideEffect.none;
        });

        // GetsHungry
        events.unit.setDefaultHandler("GetsHungry", (GameView view, Unit receiver,
                Event event) -> () -> view.game.mechanics.turns.addFutureTick("HungerStrikes", receiver, 1, true));

        // HungerStrikes
        events.unit.setDefaultHandler("HungerStrikes", (GameView view, Unit receiver, Event event) -> {
            if (receiver.getLeader().isPresent()) {
                return () -> receiver.loseLoyalty(view, 1);
            }
            ((Events.RepeatedEvent) event).repeat = false;
            return SideEffect.none;
        });

        // CanEatEvent
        events.unit.setDefaultHandler("CanEatEvent", (GameView view, Unit receiver, Event event) -> {
            Events.CanEatEvent e = (Events.CanEatEvent) event;
            e.edible = e.item.tags.has(Defs.tag_fruit);
            return SideEffect.none;
        });

        // UnitMoveDistanceEvent
        events.unit.setDefaultHandler("UnitMoveDistanceEvent", (GameView view, Unit receiver, Event event) -> {
            Events.UnitMoveDistanceEvent e = (Events.UnitMoveDistanceEvent) event;
            e.distance = 2;
            return SideEffect.none;
        });

        /**
         * SECTION Tiles
         */

        // Grass
        events.tile.addEventHandler(Defs.tile_grass, "GenerateTileEvent",
                (GameView view, Tile receiver, Event event) -> {
                    Events.GenerateTileEvent e = (Events.GenerateTileEvent) event;
                    e.blob.setModelInstance(view.av, Defs.assets_grass);
                    e.blob.setMinimapColor(0x2c9965);
                    return SideEffect.none;
                });

        // Rock
        events.tile.addEventHandler(Defs.tile_rock, "GenerateTileEvent",
                (GameView view, Tile receiver, Event event) -> {
                    Events.GenerateTileEvent e = (Events.GenerateTileEvent) event;
                    e.blob.setModelInstance(view.av, Defs.assets_grass);
                    e.blob.setMinimapColor(0x666666);
                    e.blob.setMaterial(Defs.assets_rock);
                    return SideEffect.none;
                });

        // Sand
        events.tile.addEventHandler(Defs.tile_sand, "GenerateTileEvent",
                (GameView view, Tile receiver, Event event) -> {
                    Events.GenerateTileEvent e = (Events.GenerateTileEvent) event;
                    e.blob.setModelInstance(view.av, Defs.assets_grass);
                    e.blob.setMinimapColor(0xc7c567);
                    e.blob.setMaterial(Defs.assets_sand);
                    return SideEffect.none;
                });

        // Snow
        events.tile.addEventHandler(Defs.tile_snow, "GenerateTileEvent",
                (GameView view, Tile receiver, Event event) -> {
                    Events.GenerateTileEvent e = (Events.GenerateTileEvent) event;
                    e.blob.setModelInstance(view.av, Defs.assets_grass);
                    e.blob.setMinimapColor(0xffffff);
                    e.blob.setMaterial(Defs.assets_snow);
                    return SideEffect.none;
                });

        // Water
        events.tile.addEventHandler(Defs.tile_water, "GenerateTileEvent",
                (GameView view, Tile receiver, Event event) -> {
                    Events.GenerateTileEvent e = (Events.GenerateTileEvent) event;
                    e.blob.setModelInstance(view.av, Defs.assets_water);
                    e.blob.setMinimapColor(0x20c7f7);
                    e.blob.setObstacle(true);
                    e.blob.setWave(true);
                    return SideEffect.none;
                });

        // Lava
        events.tile.addEventHandler(Defs.tile_lava, "GenerateTileEvent",
                (GameView view, Tile receiver, Event event) -> {
                    Events.GenerateTileEvent e = (Events.GenerateTileEvent) event;
                    e.blob.setModelInstance(view.av, Defs.assets_water);
                    e.blob.setMinimapColor(0xcf3b23);
                    e.blob.setMaterial(Defs.assets_lava);
                    e.blob.setObstacle(true);
                    e.blob.setWave(true);
                    return SideEffect.none;
                });

        /**
         * SECTION Buildings
         */

        // Mine
        events.building.addEventHandler(Defs.building_mine, "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.av, "mine");
                    e.blob.desc = "Mines provide valuables like gold coins";
                    e.blob.setActive();
                    return SideEffect.none;
                });

        // Vault
        events.building.addEventHandler(Defs.building_vault, "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.av, "vault");
                    e.blob.desc = "Vaults can store excess items and be used in auctions";
                    e.blob.items = Optional.of(new Inventory(InventoryType.BUILDING, 24));
                    e.blob.setActive();
                    return SideEffect.none;
                });

        // Forest
        events.building.addEventHandler(Defs.building_forest, "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.av, "forest");
                    e.blob.desc = "Don't miss the forest for the trees";
                    e.blob.setMinimapColor(0x257d53);
                    return SideEffect.none;
                });

        // Taiga
        events.building.addEventHandler(Defs.building_taiga, "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.av, "forest");
                    e.blob.desc = "The trees are pretty this time of year";
                    e.blob.setMinimapColor(0xb4c3c7);
                    e.blob.setMaterial(Defs.assets_taiga);
                    return SideEffect.none;
                });

        // Meadow
        events.building.addEventHandler(Defs.building_meadow, "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.av, "meadow");
                    e.blob.desc = "Stay a while and smell the roses";
                    e.blob.setMinimapColor(0x4dd349);
                    return SideEffect.none;
                });

        // Oasis
        events.building.addEventHandler(Defs.building_oasis, "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.av, "oasis");
                    e.blob.desc = "Moments of respite from the overbearing sun";
                    e.blob.setMinimapColor(0x2c9965);
                    return SideEffect.none;
                });

        // Shrubland
        events.building.addEventHandler(Defs.building_shrubland, "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.av, "shrubland");
                    e.blob.desc = "Meadows in the middle of the desert";
                    e.blob.setMinimapColor(0x4dd349);
                    return SideEffect.none;
                });

        // Mountain
        events.building.addEventHandler(Defs.building_mountain, "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.av, "mountain");
                    e.blob.desc = "An immovable object";
                    e.blob.setMinimapColor(0x875f9a);
                    e.blob.combat.health.invulnerable();
                    e.blob.setObstacle(true);
                    return SideEffect.none;
                });

        // Healing Fountain
        events.building.addEventHandler(Defs.building_healing_fountain, "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.av, "fountain");
                    e.blob.desc = "Heals an occupying unit each turn";
                    e.blob.setMinimapColor(0x875f9a);
                    e.blob.setActive();
                    return SideEffect.none;
                });
        events.building.addEventHandler(Defs.ability_edible, "SpawnEvent",
                (GameView view, Building receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 1, true);
                    return SideEffect.none;
                });
        events.building.addEventHandler(Defs.ability_edible, "TickEvent",
                (GameView view, Building receiver, Event event) -> {
                    Optional<Unit> u = view.game.world.getTile(receiver.getPoint()).flatMap((Tile t) -> t.unit);
                    return u.isPresent() ? () -> receiver.combat.heal(view, u.get(), 5) : SideEffect.none;
                });

        /**
         * SECTION Patrons
         */

        // Joyous Reaper
        // Great Corn Woman
        // Lord Shui, Guardian of the River
        // The Pond Troll
        events.patron.addEventHandler(Defs.patron_pond_troll, "GeneratePatronEvent",
                (GameView view, Patron receiver, Event event) -> {
                    Events.GeneratePatronEvent e = (Events.GeneratePatronEvent) event;
                    e.blob.setModelInstance(view.av, "pond-troll");
                    e.blob.desc = "The favorite player's units can traverse water tiles and have a 20% chance to fish when they do";
                    e.blob.preference = "Units that cannot swim";
                    e.blob.isPreferredUnitType = (Unit u) -> !u.hasPassiveAbility(Defs.ability_swim);
                    return SideEffect.none;
                });
        events.patron.addEventHandler(Defs.patron_pond_troll, "SpawnEvent",
                (GameView view, Patron receiver, Event event) -> {
                    view.game.events.signals.addListener("CanUnitMoveEvent", receiver);
                    view.game.events.signals.addListener("UnitMovedEvent", receiver);
                    return SideEffect.none;
                });
        events.patron.addEventHandler(Defs.patron_pond_troll, "CanUnitMoveEvent",
                (GameView view, Patron receiver, Event event) -> {
                    Events.CanUnitMoveEvent e = (Events.CanUnitMoveEvent) event;
                    if (e.unit.getLeader().equals(receiver.getFavoritePlayer())
                            && e.tile.name.equals(Defs.tile_water)) {
                        e.canWalkOnTile = true;
                    }
                    return SideEffect.none;
                });
        events.patron.addEventHandler(Defs.patron_pond_troll, "UnitMovedEvent",
                (GameView view, Patron receiver, Event event) -> {
                    Events.UnitMovedEvent e = (Events.UnitMovedEvent) event;
                    if (e.unit.getLeader().equals(receiver.getFavoritePlayer())
                            && view.game.world.getTile(e.current).get().name.equals(Defs.tile_water)
                            && !e.unit.haul.isFull() && Lambda.chance(20)) {
                        return () -> e.unit.haul.add(view.game.generator.item(Defs.item_fish));
                    }
                    return SideEffect.none;
                });

        // The Eternal Guardian
        // Flutterwing
        // Wise Mountain
        // Wise Oak
        // Ahn-Juné
        // The Shining Eyes
        events.patron.addEventHandler(Defs.patron_shining_eyes, "GeneratePatronEvent",
                (GameView view, Patron receiver, Event event) -> {
                    Events.GeneratePatronEvent e = (Events.GeneratePatronEvent) event;
                    e.blob.setModelInstance(view.av, "shining-eyes");
                    e.blob.desc = "Heals 4 random units of its favorite player each turn";
                    e.blob.preference = "Healing glyph units";
                    e.blob.isPreferredUnitType = (Unit u) -> u.glyphs.has(Glyph.HEALING);
                    return SideEffect.none;
                });
        events.patron.addEventHandler(Defs.patron_shining_eyes, "SpawnEvent",
                (GameView view, Patron receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 1, true);
                    return SideEffect.none;
                });
        events.patron.addEventHandler(Defs.patron_shining_eyes, "TickEvent",
                (GameView view, Patron receiver, Event event) -> {
                    final List<SideEffect> effects = SideEffect.list();
                    final Optional<Player> favorite = receiver.getFavoritePlayer();
                    favorite.ifPresent((Player p) -> {
                        for (Unit u : Lambda.subset(4, p.units)) {
                            effects.add(() -> u.combat.heal(view, 3));
                        }
                    });
                    return SideEffect.all(effects);
                });

        /**
         * SECTION Artifacts
         */

        // Cho's Sigil of Haste
        events.artifact.addEventHandler(Defs.artifact_chos_sigil_of_haste, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your healing glyph units get +1 movement speed";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_chos_sigil_of_haste, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("UnitMoveDistanceEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_chos_sigil_of_haste, "UnitMoveDistanceEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.UnitMoveDistanceEvent e = (Events.UnitMoveDistanceEvent) event;
                    if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.HEALING)) {
                        e.distance++;
                    }
                    return SideEffect.none;
                });

        // Urdin's Scroll of Agility
        events.artifact.addEventHandler(Defs.artifact_urdins_scroll_of_agility, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your defense glyph units get +1 movement speed";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_urdins_scroll_of_agility, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("UnitMoveDistanceEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_urdins_scroll_of_agility, "UnitMoveDistanceEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.UnitMoveDistanceEvent e = (Events.UnitMoveDistanceEvent) event;
                    if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.DEFENSE)) {
                        e.distance++;
                    }
                    return SideEffect.none;
                });

        // Sword of Aesethos
        events.artifact.addEventHandler(Defs.artifact_sword_of_aesethos, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your units have +10% critical hit chance";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_sword_of_aesethos, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("CheckCriticalHitEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_sword_of_aesethos, "CheckCriticalHitEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.CheckCriticalHitEvent e = (Events.CheckCriticalHitEvent) event;
                    if (receiver.isClaimedByLeader(e.entity)) {
                        e.chance += 10;
                    }
                    return SideEffect.none;
                });

        // Kauna's Amulet
        events.artifact.addEventHandler(Defs.artifact_kaunas_amulet, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your units within a patron's domain have extra defense";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_kaunas_amulet, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("TakeDamageEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_kaunas_amulet, "TakeDamageEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.TakeDamageEvent e = (Events.TakeDamageEvent) event;
                    if (receiver.isClaimedByLeader(e.target) && e.target.isEntityType(EntityType.UNIT)) {
                        for (Patron p : view.game.mechanics.patronage) {
                            if (p.domainContains(e.target.getPoint())) {
                                e.dmg.base -= 2;
                                break;
                            }
                        }
                    }
                    return SideEffect.none;
                });

        // Staff of Wurmdel
        events.artifact.addEventHandler(Defs.artifact_staff_of_wurmdel, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your healing spells restore +4 more health";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_staff_of_wurmdel, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("HealEntityEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_staff_of_wurmdel, "HealEntityEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.HealEntityEvent e = (Events.HealEntityEvent) event;
                    if (receiver.isClaimedByLeader(e.healer)) {
                        e.amount += 4;
                    }
                    return SideEffect.none;
                });

        // Tome of Morun
        events.artifact.addEventHandler(Defs.artifact_tome_of_morun, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "20% chance to spawn a glyph when your units kill an enemy";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_tome_of_morun, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("EntityDiedEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_tome_of_morun, "EntityDiedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.EntityDiedEvent e = (Events.EntityDiedEvent) event;
                    if (receiver.isClaimedByLeader(e.killer) && !receiver.isClaimedByLeader(e.target)) {
                        Tile t = view.game.world.getTile(e.killer.getPoint()).get();
                        if (!t.getGlyph().isPresent() && Lambda.chance(20)) {
                            t.setGlyph(Optional.of(Lambda.random(GlyphCategory.class)));
                        }
                    }
                    return SideEffect.none;
                });

        // Orb of Nerketo
        events.artifact.addEventHandler(Defs.artifact_orb_of_nerketo, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your units have +1 vision";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_orb_of_nerketo, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("GetVisionEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_orb_of_nerketo, "GetVisionEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GetVisionEvent e = (Events.GetVisionEvent) event;
                    if (receiver.isClaimedByPlayer(e.player)) {
                        e.radius++;
                    }
                    return SideEffect.none;
                });

        // Shada's Flute
        events.artifact.addEventHandler(Defs.artifact_shadas_flute, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your patrons generate 5 unit points per turn";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_shadas_flute, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 1, true);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_shadas_flute, "TickEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    for (Patron patron : view.game.mechanics.patronage) {
                        if (receiver.getOwner().equals(patron.getFavoritePlayer())) {
                            receiver.getOwner().get().unitPoints += 5;
                        }
                    }
                    return SideEffect.none;
                });

        // Stones of Thudin
        events.artifact.addEventHandler(Defs.artifact_stones_of_thudin, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your vaults have +3 defense";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_stones_of_thudin, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("TakeDamageEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_stones_of_thudin, "TakeDamageEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.TakeDamageEvent e = (Events.TakeDamageEvent) event;
                    if (e.target.isEntityType(EntityType.BUILDING)) {
                        if (receiver.isClaimedByLeader(e.target) && e.target.name.equals(Defs.building_vault)) {
                            e.dmg.base -= 3;
                        }
                    }
                    return SideEffect.none;
                });

        // The Chasi Bones
        events.artifact.addEventHandler(Defs.artifact_the_chasi_bones, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your nature glyph units have a 20% chance to harvest an additional item";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_the_chasi_bones, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("HarvestEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_the_chasi_bones, "HarvestEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.HarvestEvent e = (Events.HarvestEvent) event;
                    if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.NATURE) && !e.unit.haul.isFull()
                            && Lambda.chance(20)) {
                        e.unit.haul.add(view.game.generator.item(e.item.getStratifier()));
                    }
                    return SideEffect.none;
                });

        // Ucha's Bowl of Plenty
        events.artifact.addEventHandler(Defs.artifact_uchas_bowl_of_plenty, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "+1 option when selecting a new unit";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    e.blob.chips = 2;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_uchas_bowl_of_plenty, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    e.player.numRecruitmentOptions++;
                    return SideEffect.none;
                });

        // Nerketo's Helm
        events.artifact.addEventHandler(Defs.artifact_nerketos_helm, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Critical hits against your units are less effective";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    e.blob.chips = 2;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_nerketos_helm, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("CheckCriticalHitEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_nerketos_helm, "CheckCriticalHitEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.CheckCriticalHitEvent e = (Events.CheckCriticalHitEvent) event;
                    if (e.entity.isEntityType(EntityType.UNIT) && receiver.isClaimedByLeader(e.entity)) {
                        e.multiplier = 1.1f;
                    }
                    return SideEffect.none;
                });

        // Bounty of Ahn-June
        events.artifact.addEventHandler(Defs.artifact_bounty_of_ahn_june, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Trade glyph units on your vaults generate +2 more auction points";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    e.blob.chips = 2;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_bounty_of_ahn_june, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("GenerateAuctionPointsEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_bounty_of_ahn_june, "GenerateAuctionPointsEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateAuctionPointsEvent e = (Events.GenerateAuctionPointsEvent) event;
                    if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.TRADE)
                            && view.game.world.getTile(e.unit.getPoint()).flatMap((Tile t) -> t.building)
                                    .map((Building b) -> b.name.equals(Defs.building_vault)).orElse(false)) {
                        e.points += 2;
                    }
                    return SideEffect.none;
                });

        // Mark of Kung
        events.artifact.addEventHandler(Defs.artifact_mark_of_kung, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your battle glyph units get +1 movement speed";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    e.blob.chips = 2;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_mark_of_kung, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("UnitMoveDistanceEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_mark_of_kung, "UnitMoveDistanceEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.UnitMoveDistanceEvent e = (Events.UnitMoveDistanceEvent) event;
                    if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.BATTLE)) {
                        e.distance++;
                    }
                    return SideEffect.none;
                });

        // Chalco's Seal of Protection
        events.artifact.addEventHandler(Defs.artifact_chalcos_seal_of_protection, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your trade glyph units have +2 defense";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    e.blob.chips = 2;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_chalcos_seal_of_protection, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("TakeDamageEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_chalcos_seal_of_protection, "TakeDamageEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.TakeDamageEvent e = (Events.TakeDamageEvent) event;
                    if (e.target.isEntityType(EntityType.UNIT)) {
                        Unit u = (Unit) e.target;
                        if (receiver.isClaimedByLeader(u) && u.glyphs.has(Glyph.TRADE)) {
                            e.dmg.base -= 2;
                        }
                    }
                    return SideEffect.none;
                });

        // Poda's Elixir
        events.artifact.addEventHandler(Defs.artifact_podas_elixir, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "15% chance refresh a glyph when you recruit a unit";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    e.blob.chips = 2;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_podas_elixir, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("SpawnEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_podas_elixir, "SpawnEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.SpawnEvent e = (Events.SpawnEvent) event;
                    if (e.spawned instanceof Unit) {
                        Unit u = (Unit) e.spawned;
                        Tile t = view.game.world.getTile(u.getPoint()).get();
                        if (receiver.isClaimedByLeader(u) && !t.getGlyph().isPresent() && Lambda.chance(15)) {
                            t.setGlyph(Optional.of(Lambda.random(GlyphCategory.class)));
                        }
                    }
                    return SideEffect.none;
                });

        // Gaia's Effigy
        events.artifact.addEventHandler(Defs.artifact_gaias_effigy, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "+10 unit points each turn";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    e.blob.chips = 3;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_gaias_effigy, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 1, true);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_gaias_effigy, "TickEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    receiver.getOwner().get().unitPoints += 10;
                    return SideEffect.none;
                });

        // Rod of Adelon
        events.artifact.addEventHandler(Defs.artifact_rod_of_adelon, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "5% chance to recruit an enemy unit when you kill it";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    e.blob.chips = 3;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_rod_of_adelon, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("KilledEntityEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_rod_of_adelon, "KilledEntityEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.KilledEntityEvent e = (Events.KilledEntityEvent) event;
                    if (e.target.isEntityType(EntityType.UNIT) && receiver.isClaimedByLeader(e.killer)
                            && !e.killer.isFriendly(e.target) && Lambda.chance(5)) {
                        view.game.generator.unit(e.target.name, e.target.getX(), e.target.getY()).spawn(view);
                    }
                    return SideEffect.none;
                });

        // Blade of Sanguinor
        events.artifact.addEventHandler(Defs.artifact_blade_of_sanguinor, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your battle glyph units deal +2 damage";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    e.blob.chips = 3;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_blade_of_sanguinor, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("AttackEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_blade_of_sanguinor, "AttackEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.AttackEvent e = (Events.AttackEvent) event;
                    if (e.target.isEntityType(EntityType.UNIT) && receiver.isClaimedByLeader(e.target)
                            && ((Unit) e.target).glyphs.has(Glyph.BATTLE)) {
                        e.dmg.base += 2;
                    }
                    return SideEffect.none;
                });

        // Cask of Amonitor
        events.artifact.addEventHandler(Defs.artifact_cask_of_amonitor, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your unoccupied tiles in a patron's domain provide +1 favor";
                    e.blob.image = Optional.of(Defs.assets_golden_feather);
                    e.blob.chips = 3;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_cask_of_amonitor, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("CalculateFavorEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Defs.artifact_cask_of_amonitor, "CalculateFavorEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.CalculateFavorEvent e = (Events.CalculateFavorEvent) event;
                    if (receiver.isClaimedByPlayer(e.player)) {
                        for (Point p : e.patron.getDomain()) {
                            Tile t = view.game.world.getTile(p).get();
                            if (t.leader.map((Player p1) -> p1.equals(e.player)).orElse(false) && !t.unit.isPresent()) {
                                e.favor++;
                            }
                        }
                    }
                    return SideEffect.none;
                });

        /**
         * SECTION Fates
         */

        // The Raider
        events.fate.addEventHandler(Defs.fate_raider, "GenerateFateEvent",
                (GameView view, Fate receiver, Event event) -> {
                    Events.GenerateFateEvent e = (Events.GenerateFateEvent) event;
                    e.blob.image = Optional.of(Defs.assets_raider);
                    e.blob.desc.add("Playstyle: High-risk aggro");
                    return SideEffect.none;
                });

        // The Merchant
        events.fate.addEventHandler(Defs.fate_merchant, "GenerateFateEvent",
                (GameView view, Fate receiver, Event event) -> {
                    Events.GenerateFateEvent e = (Events.GenerateFateEvent) event;
                    e.blob.image = Optional.of(Defs.assets_merchant);
                    e.blob.desc.add("Playstyle: Market control");
                    return SideEffect.none;
                });

        // The Veteran
        events.fate.addEventHandler(Defs.fate_veteran, "GenerateFateEvent",
                (GameView view, Fate receiver, Event event) -> {
                    Events.GenerateFateEvent e = (Events.GenerateFateEvent) event;
                    e.blob.image = Optional.of(Defs.assets_veteran);
                    e.blob.desc.add("Playstyle: Military production");
                    return SideEffect.none;
                });

        // The Devout
        events.fate.addEventHandler(Defs.fate_devout, "GenerateFateEvent",
                (GameView view, Fate receiver, Event event) -> {
                    Events.GenerateFateEvent e = (Events.GenerateFateEvent) event;
                    e.blob.image = Optional.of(Defs.assets_devout);
                    e.blob.desc.add("Playstyle: Patron collection");
                    return SideEffect.none;
                });

        // The Sentinel
        events.fate.addEventHandler(Defs.fate_sentinel, "GenerateFateEvent",
                (GameView view, Fate receiver, Event event) -> {
                    Events.GenerateFateEvent e = (Events.GenerateFateEvent) event;
                    e.blob.image = Optional.of(Defs.assets_sentinel);
                    e.blob.desc.add("Playstyle: Defensive expansion");
                    return SideEffect.none;
                });

        // The Usurper
        events.fate.addEventHandler(Defs.fate_usurper, "GenerateFateEvent",
                (GameView view, Fate receiver, Event event) -> {
                    Events.GenerateFateEvent e = (Events.GenerateFateEvent) event;
                    e.blob.image = Optional.of(Defs.assets_usurper);
                    e.blob.desc.add("Playstyle: Early market bonus into unit production");
                    return SideEffect.none;
                });

        // The Forager
        events.fate.addEventHandler(Defs.fate_forager, "GenerateFateEvent",
                (GameView view, Fate receiver, Event event) -> {
                    Events.GenerateFateEvent e = (Events.GenerateFateEvent) event;
                    e.blob.image = Optional.of(Defs.assets_forager);
                    e.blob.desc.add("Playstyle: Resource accumulation");
                    return SideEffect.none;
                });

        /**
         * SECTION Units
         */

        // Knuckleheads
        // Gorax the Dragon Knight
        // Equinox
        // Elder Chumsa
        // Gemrock
        // Glittersnout
        // Sir Tlatec
        events.unit.addEventHandler(Defs.unit_sir_tlatec, "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.setModelInstance(view.av, "axolotl");
                    e.blob.desc = "Tlatec the Axolotl-man has travelled far from his home in search of worthy opponents";
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(Defs.ability_sword_slash),
                            Optional.empty());
                    e.blob.setPassiveAbilities(view.game.generator, Defs.ability_swim, Defs.ability_hunt_fish,
                            Defs.ability_plate_mail, Defs.ability_regeneration);
                    e.blob.glyphs.set(Glyph.BATTLE);
                    e.blob.species = Defs.species_salamander;
                    return SideEffect.none;
                });

        // Cenuok the Battle Grue
        // Beetlemoss
        events.unit.addEventHandler(Defs.unit_beetlemoss, "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "This nature spirit guards an ancient forest in Eaglehaven";
                    e.blob.setModelInstance(view.av, "beetlemoss");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(Defs.ability_fire_cannon),
                            Optional.of(Defs.ability_plant_forest));
                    e.blob.setPassiveAbilities(view.game.generator, Defs.ability_pick_apples, Defs.ability_mine_gems);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.NATURE);
                    e.blob.species = Defs.species_sprite;
                    return SideEffect.none;
                });

        // Gloop the Adventurer
        events.unit.addEventHandler(Defs.unit_gloop_the_adventurer, "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.setModelInstance(view.av, "gloop");
                    e.blob.desc = "This Plasmoid adventurer is eager to prove themself in the dungeons";
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(Defs.ability_sword_slash),
                            Optional.of(Defs.ability_dungeon_delve));
                    e.blob.setPassiveAbilities(view.game.generator, Defs.ability_combat_loot, Defs.ability_night_vision,
                            Defs.ability_regeneration);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.MINING);
                    e.blob.combat.health.setMax(40);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_plasmoid;
                    return SideEffect.none;
                });

        // Dominus the Lich
        // Graymaw
        // Roseris Thorn-hoof
        // Nitu Sodfoot
        // Nebaneba
        // Kamiena
        // Faustus
        // Maekuro the Mighty
        // Garudee
        // Pebbles
        // Magdalena
        // Lost Golem
        // Samara
        // Golem of the Grotto
        events.unit.addEventHandler(Defs.unit_golem_of_the_grotto, "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "This golem wanders the rocky peaks where it was forged long ago";
                    e.blob.setModelInstance(view.av, "golem-grotto");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(Defs.ability_smash),
                            Optional.of(Defs.ability_plant_meadow));
                    e.blob.setPassiveAbilities(view.game.generator, Defs.ability_mountain_strider,
                            Defs.ability_local_defender);
                    e.blob.glyphs.set(Glyph.DEFENSE, Glyph.NATURE);
                    e.blob.combat.health.setMax(80);
                    e.blob.species = Defs.species_golem;
                    return SideEffect.none;
                });

        // Puffshroom
        // Lord Tyson
        // Courrier Grog
        // Nizhaad Windwalker
        // Condylure of the Star Nose
        events.unit.addEventHandler(Defs.unit_condylure_of_the_star_nose, "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "This Brownie is blind, but traverses the subterranean world with the aid of his nose";
                    e.blob.setModelInstance(view.av, "condylure");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(Defs.ability_build_healing_fountain),
                            Optional.of(Defs.ability_dig_mine));
                    e.blob.setPassiveAbilities(view.game.generator, Defs.ability_night_vision, Defs.ability_mine_gems);
                    e.blob.glyphs.set(Glyph.HEALING, Glyph.MINING);
                    e.blob.species = Defs.species_brownie;
                    return SideEffect.none;
                });

        // Huiying the Alchemist
        // Lady Daumia
        events.unit.addEventHandler(Defs.unit_lady_daumia, "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "Elven high missionary to Surgarde";
                    e.blob.setModelInstance(view.av, "daumia");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(Defs.ability_heal_wounds),
                            Optional.of(Defs.ability_self_sacrifice));
                    e.blob.setPassiveAbilities(view.game.generator, Defs.ability_night_vision, Defs.ability_life_aura);
                    e.blob.glyphs.set(Glyph.HEALING);
                    e.blob.species = Defs.species_elf;
                    return SideEffect.none;
                });

        // Zen Hito the Kappa
        // Gibrax the Everlasting
        // Passiflor
        // Frogger the Gnome
        events.unit.addEventHandler(Defs.unit_frogger_the_gnome, "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "Just a little Gnome and his frog";
                    e.blob.setModelInstance(view.av, "frog-gnome");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(Defs.ability_heal_wounds),
                            Optional.of(Defs.ability_hungry_frog_magic));
                    e.blob.setPassiveAbilities(view.game.generator, Defs.ability_pick_flowers, Defs.ability_swim);
                    e.blob.glyphs.set(Glyph.HEALING);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_gnome;
                    return SideEffect.none;
                });

        // Teragalor
        // Stalagmus
        events.unit.addEventHandler(Defs.unit_stalagmus, "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "Enchanted waters accumulate into this Golem's bowl-shaped body";
                    e.blob.setModelInstance(view.av, "stalagmus");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(Defs.ability_dig_mine),
                            Optional.of(Defs.ability_hurl_rock));
                    e.blob.setPassiveAbilities(view.game.generator, Defs.ability_night_vision,
                            Defs.ability_stone_defense, Defs.ability_mine_gems, Defs.ability_mine_gold,
                            Defs.ability_subterranean_potions);
                    e.blob.glyphs.set(Glyph.MINING);
                    e.blob.species = Defs.species_golem;
                    return SideEffect.none;
                });

        // Glimmer
        // Grizzlemane the Mycoweaver
        // Magicad
        // The Druid
        events.unit.addEventHandler(Defs.unit_the_druid, "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "A mysterious Druid who rarely speaks";
                    e.blob.setModelInstance(view.av, "druid");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(Defs.ability_plant_forest),
                            Optional.of(Defs.ability_revenge_of_the_forest));
                    e.blob.setPassiveAbilities(view.game.generator, Defs.ability_pick_apples, Defs.ability_night_vision,
                            Defs.ability_green_fortress);
                    e.blob.glyphs.set(Glyph.NATURE);
                    e.blob.species = Defs.species_sprite;
                    UnitLogic.vision(events, e.blob, 4);
                    return SideEffect.none;
                });

        // Bluefeathers
        // Broker Quercia
        // Oystermane
        // Matilda the Merchant
        // Thoughtform
        // Akatash the Trader
        // Ansuagion the Gilded
        // Blorp the Burning
        events.unit.addEventHandler(Defs.unit_blorp_the_burning, "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "A ravenous Plasmoid with an acidic body";
                    e.blob.setModelInstance(view.av, "blob");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(Defs.ability_slime_shot),
                            Optional.empty());
                    e.blob.setPassiveAbilities(view.game.generator, Defs.ability_acid_skin,
                            Defs.ability_liquifying_presence);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.DEFENSE);
                    e.blob.combat.health.setMax(80);
                    e.blob.species = Defs.species_plasmoid;
                    return SideEffect.none;
                });
        // Sathra the Flame Caster
        // Dendra Ivy
        // Trina the Ettin
        // Prismar
        events.unit.addEventHandler(Defs.unit_prismar, "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.setModelInstance(view.av, Defs.assets_crystal);
                    e.blob.desc = "This Gemstone can focus light into powerful attacks";
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(Defs.ability_fire_laser),
                            Optional.of(Defs.ability_collapse_mine));
                    e.blob.setPassiveAbilities(view.game.generator, Defs.ability_crystal_skin,
                            Defs.ability_night_vision, Defs.ability_mine_gems);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.MINING);
                    e.blob.species = Defs.species_gemstone;
                    return SideEffect.none;
                });

        //
        // Ariala the Mage
        //
        //
        //
        // Halifax
        // Glub Glub
        // Galygos the Juggernaut
        // Defender Cuauhtli
        // Gilded Cho'chal
        // Soothing Gills
        // Pumpkin Boy
        events.unit.addEventHandler(Defs.unit_pumpkin_boy, "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "He doesn't say much, he's just a little guy";
                    e.blob.setModelInstance(view.av, "pumpkin-boy");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(Defs.ability_plant_meadow),
                            Optional.of(Defs.ability_hug));
                    e.blob.setPassiveAbilities(view.game.generator, Defs.ability_night_vision,
                            Defs.ability_regeneration, Defs.ability_running_through_nature, Defs.ability_sacred_seeds);
                    e.blob.glyphs.set(Glyph.NATURE);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_sprite;
                    return SideEffect.none;
                });

        // Barometz
        events.unit.addEventHandler(Defs.unit_barometz, "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "This sheep-like Sprite blooms with delicious fruit";
                    e.blob.setModelInstance(view.av, "barometz");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(Defs.ability_bite), Optional.empty());
                    e.blob.setPassiveAbilities(view.game.generator, Defs.ability_regeneration, Defs.ability_edible,
                            Defs.ability_deposit_seeds);
                    e.blob.glyphs.set(Glyph.NATURE);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_sprite;
                    return SideEffect.none;
                });

        // Xella the Accursed
        // Svelta Luktegress
        // Al-Fikra
        events.unit.addEventHandler(Defs.unit_al_fikra, "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "This being aids the great merchant kings of Eastern Bycidia";
                    e.blob.setModelInstance(view.av, "alfikra");
                    e.blob.setActiveAbilities(view.game.generator, Optional.empty(), Optional.empty());
                    e.blob.setPassiveAbilities(view.game.generator, Defs.ability_regeneration,
                            Defs.ability_market_indicator);
                    e.blob.glyphs.set(Glyph.TRADE);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_tulpa;
                    UnitLogic.speed(events, e.blob, 3);
                    UnitLogic.vision(events, e.blob, 4);
                    return SideEffect.none;
                });

        // Goldtooth
        // The Necromancer
        // Lurch
        // Garulax
        // Patagan
        // The Pumpkin King
        // Little Buck
        // Viraqa Under the Mountain
        // Castros Waterpaw
        // Champion Jenid
        // Badroch the Pack Grue
        // Guard Captain Sentrina
        // Barbs
        // Yalitza
        // Old Man Mosscloak
        //
        //
        // King Gargantos
        events.unit.addEventHandler(Defs.unit_king_gargantos, "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "Warrior-king of the Tortoise Kingdom";
                    e.blob.setModelInstance(view.av, "gargantos");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(Defs.ability_smash),
                            Optional.of(Defs.ability_build_vault));
                    e.blob.setPassiveAbilities(view.game.generator, Defs.ability_shell_defense,
                            Defs.ability_market_boom, Defs.ability_swim);
                    e.blob.glyphs.set(Glyph.DEFENSE, Glyph.TRADE);
                    e.blob.combat.health.setMax(80);
                    e.blob.setTimeToHunger(view, 10);
                    e.blob.species = Defs.species_tortugan;
                    return SideEffect.none;
                });

        // Sir Rootbeard
        // Wuraj the Blessed
        // Karina Brightfeather
        // Photali
        //
        // Razma
        // Theressa the Rover
        //
        // Illapa
        // Disastra
        // Chicao
        // Alaistar and Wurmdel
        // Mi'chalb Lightfoot

        /**
         * SECTION Abilities
         */

        // Acid Skin
        events.ability.addEventHandler(Defs.ability_acid_skin, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Adjacent attackers take damage");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_acid_skin, "AttackedEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.AttackedEvent e = (Events.AttackedEvent) event;
                    if (e.attacker instanceof Unit) {
                        Unit target = (Unit) e.target;
                        Unit attacker = (Unit) e.attacker;
                        return Hexagons.areNeighbors(attacker.getPoint(), target.getPoint())
                                ? () -> attacker.combat.takeDamage(view, new Damage(2), target)
                                : SideEffect.none;
                    }
                    return SideEffect.none;
                });

        // Bite
        events.ability.addEventHandler(Defs.ability_bite, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Basic attack";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_bite, "AbilityActivatedEvent", (GameView view, Ability receiver,
                Event event) -> AbilityLogic.attack(view, receiver.wielder, new Damage(4), 1));

        // Build Healing Fountain
        events.ability.addEventHandler(Defs.ability_build_healing_fountain, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Constructs a healing fountain";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_build_healing_fountain, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.build(view, receiver.wielder,
                        Defs.building_healing_fountain, (Tile t) -> true));

        // Build Vault
        events.ability.addEventHandler(Defs.ability_build_vault, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Builds a vault";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_build_vault, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.build(view, receiver.wielder,
                        Defs.building_vault, (Tile t) -> true));

        // Collapse Mine
        events.ability.addEventHandler(Defs.ability_collapse_mine, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format(
                            "Target a mine occupied by an enemy unit. The unit, mine, and any adjacent enemy units all take damage.");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_collapse_mine, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Set<Point> mines = Lambda.filter(
                            (Point p) -> view.game.world.getTile(p)
                                    .map((Tile t) -> !t.leader.equals(receiver.wielder.getLeader()) && t.building
                                            .map((Building b) -> b.name.equals(Defs.building_mine)).orElse(false)
                                            && t.unit.isPresent())
                                    .orElse(false),
                            Hexagons.getNeighbors(receiver.wielder.getPoint(), 1));
                    return receiver.wielder.getLeader().get().select(view, mines, "No mines in range", (Point p) -> {
                        Set<Entity> targets = new HashSet<>();
                        targets.add(view.game.world.getTile(p).get().unit.get());
                        targets.add(view.game.world.getTile(p).get().building.get());
                        for (Point p1 : Hexagons.getNeighbors(p, 1)) {
                            Optional<Unit> u = view.game.world.getTile(p1).flatMap((Tile t) -> t.unit);
                            if (u.map((Unit u1) -> !u1.isFriendly(receiver.wielder)).orElse(false)) {
                                targets.add(u.get());
                            }
                        }
                        return () -> {
                            for (Entity e : targets) {
                                receiver.wielder.combat.attack(view, e, new Damage(5));
                            }
                        };
                    });
                });

        // Combat Loot
        events.ability.addEventHandler(Defs.ability_combat_loot, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("+2 damage if this unit has a hauled item");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_combat_loot, "AttackEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.AttackEvent e = (Events.AttackEvent) event;
                    if (receiver.wielder.haul.hasItems()) {
                        e.dmg.base += 2;
                    }
                    return SideEffect.none;
                });

        // Crystal Skin
        events.ability.addEventHandler(Defs.ability_crystal_skin, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Extra defense");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_crystal_skin, "TakeDamageEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.defense(event, 2));

        // Deposit Seeds
        events.ability.addEventHandler(Defs.ability_deposit_seeds, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Chance to spawn a meadow when this unit moves");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_deposit_seeds, "UnitMovedEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Point p = receiver.wielder.getPoint();
                    return view.game.world.getTile(p).map((Tile t) -> !t.building.isPresent()).orElse(false)
                            && Lambda.chance(10)
                                    ? () -> view.game.generator.building(Defs.building_meadow, p.x, p.y).spawn(view)
                                    : SideEffect.none;
                });

        // Dig Mine
        events.ability.addEventHandler(Defs.ability_dig_mine, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Digs a mine";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_dig_mine, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.build(view, receiver.wielder,
                        Defs.building_mine, (Tile t) -> t.name.equals(Defs.tile_rock)));

        // Dungeon Delve
        events.ability.addEventHandler(Defs.ability_dungeon_delve, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String
                            .format("Deals 5 damage and generates loot if targeting a tile with an active building");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_dungeon_delve, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.attackAndEffect(view, receiver.wielder,
                        new Damage(5), 1, Optional.of((Point p) -> {
                            return !receiver.wielder.haul.isFull() && view.game.world.getTile(p)
                                    .flatMap((Tile t) -> t.building).map((Building b) -> b.isActive()).orElse(false)
                                            ? () -> receiver.wielder.haul.add(view.game.mechanics.loot.drop(view.game))
                                            : SideEffect.none;
                        })));

        // Edible
        events.ability.addEventHandler(Defs.ability_edible, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Generates food");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_edible, "SpawnEvent",
                (GameView view, Ability receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 4, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_edible, "TickEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.harvestFromTile(view, receiver.wielder,
                        Defs.item_apple, (Tile t) -> true));

        // Fire Cannon
        events.ability.addEventHandler(Defs.ability_fire_cannon, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String
                            .format("Deals 8 damage to a building (or 4 damage to a unit) up to 2 tiles away");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_fire_cannon, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.dynamicDamageAttack(view,
                        receiver.wielder, 2,
                        (Tile t) -> t.building.isPresent() && !t.unit.isPresent() ? new Damage(8) : new Damage(4)));

        // Fire Laser
        events.ability.addEventHandler(Defs.ability_fire_laser, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Damage up to 3 units in a line");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_fire_laser, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> {
                    final Set<Point> targets = new HashSet<>();
                    final Map<Point, HexSide> sideToPoint = new HashMap<>();
                    for (HexSide side : HexSide.values()) {
                        Point dest = Hexagons.followLine(receiver.wielder.getPoint(), side, 3);
                        targets.add(dest);
                        sideToPoint.put(dest, side);
                    }
                    return receiver.wielder.getLeader().get().select(view, targets, "No targets available",
                            (Point p) -> {
                                final List<SideEffect> effects = SideEffect.list();
                                final HexSide side = sideToPoint.get(p);
                                for (int a = 0; a < 3; a++) {
                                    Point p1 = Hexagons.followLine(receiver.wielder.getPoint(), side, a + 1);
                                    view.game.world.getTile(p1).flatMap((Tile t) -> t.unit)
                                            .ifPresent((Unit u) -> effects
                                                    .add(receiver.wielder.combat.attack(view, u, new Damage(4))));
                                }
                                return SideEffect.all(effects);
                            });
                });

        // Green Fortress
        events.ability.addEventHandler(Defs.ability_green_fortress, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Extra defense on forests");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_green_fortress, "TakeDamageEvent",
                (GameView view, Ability receiver, Event event) -> {
                    boolean isForest = view.game.world.getTile(receiver.wielder.getPoint())
                            .flatMap((Tile t) -> t.building).map((Building b) -> b.name.equals(Defs.building_forest))
                            .orElse(false);
                    return isForest ? AbilityLogic.defense(event, 2) : SideEffect.none;
                });

        // Heal Wounds
        events.ability.addEventHandler(Defs.ability_heal_wounds, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Heals 5 damage";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_heal_wounds, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.healUnit(view, receiver.wielder, 5));

        // Hug
        events.ability.addEventHandler(Defs.ability_hug, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Heals the target adjacent unit for a few hit points");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_hug, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.healUnit(view, receiver.wielder, 2));

        // Hungry Frog Magic
        events.ability.addEventHandler(Defs.ability_hungry_frog_magic, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Consumes all hauled items and heals adjacent friendly units");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_hungry_frog_magic, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> {
                    List<SideEffect> effects = SideEffect.list(() -> receiver.wielder.haul.empty());
                    Set<Point> targets = Hexagons.getNeighbors(receiver.wielder.getPoint(), 1);
                    for (Point p : targets) {
                        Optional<Unit> u = view.game.world.getTile(p).flatMap((Tile t) -> t.unit);
                        if (u.map((Unit u1) -> u1.isFriendly(receiver.wielder)).orElse(false)) {
                            effects.add(() -> receiver.wielder.combat.heal(view, u.get(), 10));
                        }
                    }
                    return SideEffect.all(effects);
                });

        // Hunt Fish
        events.ability.addEventHandler(Defs.ability_hunt_fish, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Harvests fish from water tiles");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_hunt_fish, "SpawnEvent",
                (GameView view, Ability receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 4, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_hunt_fish, "TickEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.harvestFromTile(view, receiver.wielder,
                        Defs.item_fish, (Tile t) -> t.name.equals(Defs.tile_water)));

        // Hurl Rock
        events.ability.addEventHandler(Defs.ability_hurl_rock, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Ranged attack with chance to stun");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_hurl_rock, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.attackAndEffect(view, receiver.wielder,
                        new Damage(4), 2, Optional.of((Point p) -> {
                            Optional<Unit> u = view.game.world.getTile(p).flatMap((Tile t) -> t.unit);
                            return u.isPresent()
                                    ? u.get().addStatusEffect(view, Defs.status_effect_stunned)
                                    : SideEffect.none;
                        })));

        // Life Aura
        events.ability.addEventHandler(Defs.ability_life_aura, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Generates 4 unit points per turn");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_life_aura, "SpawnEvent",
                (GameView view, Ability receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 1, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_life_aura, "TickEvent", (GameView view, Ability receiver,
                Event event) -> () -> receiver.wielder.getLeader().ifPresent((Player p) -> p.unitPoints += 4));

        // Liquifying Presence
        events.ability.addEventHandler(Defs.ability_liquifying_presence, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Deals 3 damage each turn to an occupied passive building");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_liquifying_presence, "SpawnEvent",
                (GameView view, Ability receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 1, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_liquifying_presence, "TickEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Optional<Building> b = view.game.world.getTile(receiver.wielder.getPoint())
                            .flatMap((Tile t) -> t.building);
                    return b.map((Building b1) -> !b1.isActive()).orElse(false)
                            ? () -> b.get().combat.takeDamage(view, new Damage(3), receiver.wielder)
                            : SideEffect.none;
                });

        // Local Defender
        events.ability.addEventHandler(Defs.ability_local_defender, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Adjacent buildings have +3 armor");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_local_defender, "SpawnEvent",
                (GameView view, Ability receiver, Event event) -> {
                    view.game.events.signals.addListener("AttackedEvent", receiver);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_local_defender, "AttackedEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.AttackedEvent e = (Events.AttackedEvent) event;
                    if (e.target.isEntityType(EntityType.BUILDING)
                            && receiver.wielder.getLeader().equals(e.target.getLeader())
                            && Hexagons.areNeighbors(receiver.wielder.getPoint(), e.target.getPoint())) {
                        e.dmg.base -= 3;
                    }
                    return SideEffect.none;
                });

        // Market Boom
        events.ability.addEventHandler(Defs.ability_market_boom, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Attacks generate 5 auction points");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_market_boom, "AttackEvent", (GameView view, Ability receiver,
                Event event) -> AbilityLogic.generateAuctionPoints(view, receiver.wielder, 5));

        // Market Indicator
        events.ability.addEventHandler(Defs.ability_market_indicator, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Generates 1 auction point when adjacent to a vault");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_market_indicator, "SpawnEvent",
                (GameView view, Ability receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 1, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_market_indicator, "TickEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.doWhenAdjacent(view, receiver.wielder,
                        (Tile t) -> t.building.map((Building b) -> b.name.equals(Defs.building_vault)).orElse(false),
                        () -> AbilityLogic.generateAuctionPoints(view, receiver.wielder, 1)));

        // Mine Gems
        events.ability.addEventHandler(Defs.ability_mine_gems, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Harvests gems from mines");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_mine_gems, "SpawnEvent",
                (GameView view, Ability receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 4, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_mine_gems, "TickEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.harvestFromBuilding(view,
                        receiver.wielder, Defs.item_emerald, (Building b) -> b.name.equals(Defs.building_mine)));

        // Mine Gold
        events.ability.addEventHandler(Defs.ability_mine_gold, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Harvests gold coins from mines every 4 turns";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_mine_gold, "SpawnEvent",
                (GameView view, Ability receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 4, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_mine_gold, "TickEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.harvestFromBuilding(view,
                        receiver.wielder, Defs.item_gold_coin, (Building b) -> b.name.equals(Defs.building_mine)));

        // Mountain Strider
        events.ability.addEventHandler(Defs.ability_mountain_strider, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("This unit can traverse mountains");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_mountain_strider, "CanUnitMoveEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.CanUnitMoveEvent e = (Events.CanUnitMoveEvent) event;
                    if (e.tile.building.map((Building b) -> b.name.equals(Defs.building_mountain)).orElse(false)) {
                        e.canWalkOnBuilding = true;
                    }
                    return SideEffect.none;
                });

        // Night Vision
        events.ability.addEventHandler(Defs.ability_night_vision, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("This unit can see normally at night");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_night_vision, "GetVisionEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GetVisionEvent e = (Events.GetVisionEvent) event;
                    e.canSeeAtNight = true;
                    return SideEffect.none;
                });

        // Pick Apples
        events.ability.addEventHandler(Defs.ability_pick_apples, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Harvests apples from forests every 4 turns";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_pick_apples, "SpawnEvent",
                (GameView view, Ability receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 4, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_pick_apples, "TickEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.harvestFromBuilding(view,
                        receiver.wielder, Defs.item_apple, (Building b) -> b.name.equals(Defs.building_forest)));

        // Pick Flowers
        events.ability.addEventHandler(Defs.ability_pick_flowers, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Harvests flowers from meadows every 4 turns");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_pick_flowers, "SpawnEvent",
                (GameView view, Ability receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 4, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_pick_flowers, "TickEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.harvestFromBuilding(view,
                        receiver.wielder, Defs.item_flower, (Building b) -> b.name.equals(Defs.building_meadow)));

        // Plant Forest
        events.ability.addEventHandler(Defs.ability_plant_forest, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Plants a forest";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_plant_forest, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.build(view, receiver.wielder,
                        Defs.building_forest, (Tile t) -> t.name.equals(Defs.tile_grass)));

        // Plant Meadow
        events.ability.addEventHandler(Defs.ability_plant_meadow, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Plants a meadow";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_plant_meadow, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.build(view, receiver.wielder,
                        Defs.building_meadow, (Tile t) -> t.name.equals(Defs.tile_grass)));

        // Plate Mail
        events.ability.addEventHandler(Defs.ability_plate_mail, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Extra defense");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_plate_mail, "TakeDamageEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.defense(event, 2));

        // Regeneration
        events.ability.addEventHandler(Defs.ability_regeneration, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("This unit heals a little each turn");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_regeneration, "SpawnEvent",
                (GameView view, Ability receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 1, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_regeneration, "TickEvent",
                (GameView view, Ability receiver, Event event) -> () -> receiver.wielder.combat.heal(view, 1));

        // Revenge of the Forest
        events.ability.addEventHandler(Defs.ability_revenge_of_the_forest, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Attack that deals more damage when on a forest");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_revenge_of_the_forest, "AttackEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.AttackEvent e = (Events.AttackEvent) event;
                    return AbilityLogic
                            .doOnTile(
                                    view, receiver.wielder, (Tile t) -> t.building
                                            .map((Building b) -> b.name.equals(Defs.building_forest)).orElse(false),
                                    () -> {
                                        e.dmg.base += 5;
                                        return SideEffect.none;
                                    });
                });

        // Running Through Nature
        events.ability.addEventHandler(Defs.ability_running_through_nature, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("This unit is faster on passive buildings");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_running_through_nature, "UnitMoveDistanceEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.UnitMoveDistanceEvent e = (Events.UnitMoveDistanceEvent) event;
                    boolean buildingIsPassive = view.game.world.getTile(e.unit.getPoint())
                            .flatMap((Tile t) -> t.building).map((Building b) -> !b.isActive()).orElse(false);
                    if (buildingIsPassive) {
                        e.distance++;
                    }
                    return SideEffect.none;
                });

        // Self Sacrifice
        events.ability.addEventHandler(Defs.ability_self_sacrifice, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Transfers all their health but 1 to the target unit");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_self_sacrifice, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> {
                    final int hitPoints = receiver.wielder.combat.health.get() - 1;
                    return SideEffect.all(AbilityLogic.healUnit(view, receiver.wielder, hitPoints),
                            () -> receiver.wielder.combat.health.set(1));
                });

        // Sacred Seeds
        events.ability.addEventHandler(Defs.ability_sacred_seeds, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Harvests seeds from meadows that can be consumed to generate favor");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_sacred_seeds, "SpawnEvent",
                (GameView view, Ability receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 4, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_sacred_seeds, "TickEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.harvestFromBuilding(view,
                        receiver.wielder, Defs.item_sacred_seed, (Building b) -> b.name.equals(Defs.building_meadow)));

        // Shell Defense
        events.ability.addEventHandler(Defs.ability_shell_defense, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Extra defense");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_shell_defense, "TakeDamageEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.defense(event, 2));

        // Slime Shot
        events.ability.addEventHandler(Defs.ability_slime_shot, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Ranged attack");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_slime_shot, "AbilityActivatedEvent", (GameView view,
                Ability receiver, Event event) -> AbilityLogic.attack(view, receiver.wielder, new Damage(4), 3));

        // Smash
        events.ability.addEventHandler(Defs.ability_smash, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Attack with a chance to stun");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_smash, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.attackAndEffect(view, receiver.wielder,
                        new Damage(5), 1, Optional.of((Point p) -> {
                            Optional<Unit> u = view.game.world.getTile(p).flatMap((Tile t) -> t.unit);
                            return u.isPresent()
                                    ? u.get().addStatusEffect(view, Defs.status_effect_stunned)
                                    : SideEffect.none;
                        })));

        // Stone Defense
        events.ability.addEventHandler(Defs.ability_stone_defense, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Extra defense");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_stone_defense, "TakeDamageEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.defense(event, 2));

        // Subterranean Potions
        events.ability.addEventHandler(Defs.ability_subterranean_potions, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Generates Health Potions from Mines");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_subterranean_potions, "SpawnEvent",
                (GameView view, Ability receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 4, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_subterranean_potions, "TickEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.harvestFromBuilding(view,
                        receiver.wielder, Defs.item_health_potion, (Building b) -> b.name.equals(Defs.building_mine)));

        // Swim
        events.ability.addEventHandler(Defs.ability_swim, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "This unit can swim on water tiles";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_swim, "CanUnitMoveEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.CanUnitMoveEvent e = (Events.CanUnitMoveEvent) event;
                    if (!e.canWalkOnTile && e.tile.name.equals(Defs.tile_water)) {
                        e.canWalkOnTile = true;
                    }
                    return SideEffect.none;
                });

        // Sword Slash
        events.ability.addEventHandler(Defs.ability_sword_slash, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    Damage dmg = new Damage(5);
                    e.blob.desc = String.format("Deals %s", dmg);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.ability_sword_slash, "AbilityActivatedEvent", (GameView view,
                Ability receiver, Event event) -> AbilityLogic.attack(view, receiver.wielder, new Damage(5), 1));

        /**
         * SECTION Status Effects
         */

        // Stunned
        events.ability.addEventHandler(Defs.status_effect_stunned, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("The unit cannot act for 1 turn");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.status_effect_stunned, "StatusEffectAddedEvent",
                (GameView view, Ability receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 1, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.status_effect_stunned, "TickEvent",
                (GameView view, Ability receiver, Event event) -> () -> receiver.wielder.removeStatusEffect(receiver));
        events.ability.addEventHandler(Defs.status_effect_stunned, "IsStunnedEvent",
                (GameView view, Ability receiver, Event event) -> {
                    ((Events.IsStunnedEvent) event).isStunned = true;
                    return SideEffect.none;
                });

        // More Favor
        events.ability.addEventHandler(Defs.status_effect_more_favor, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("+5 favor next time the unit generates it");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Defs.status_effect_more_favor, "GenerateFavorEvent",
                (GameView view, Ability receiver, Event event) -> {
                    ((Events.GenerateFavorEvent) event).favor += 5;
                    return SideEffect.none;
                });

        /**
         * SECTION Items
         */

        // Seeds
        events.item.addEventHandler(Defs.item_sacred_seed, "GenerateItemEvent",
                (GameView view, Item receiver, Event event) -> {
                    Events.GenerateItemEvent e = (Events.GenerateItemEvent) event;
                    e.blob.desc = "Consume to generate extra favor";
                    e.blob.icon = Optional.of(Defs.assets_seeds);
                    e.blob.gold = 1;
                    return SideEffect.none;
                });
        events.item.addEventHandler(Defs.item_sacred_seed, "ItemConsumedEvent",
                (GameView view, Item receiver, Event event) -> {
                    Events.ItemConsumedEvent e = (Events.ItemConsumedEvent) event;
                    return () -> e.consumer.addStatusEffect(view, Defs.status_effect_more_favor);
                });

        // Flower
        events.item.addEventHandler(Defs.item_flower, "GenerateItemEvent",
                (GameView view, Item receiver, Event event) -> {
                    Events.GenerateItemEvent e = (Events.GenerateItemEvent) event;
                    e.blob.desc = "Consume to smell a sweet flower";
                    e.blob.icon = Optional.of(Defs.assets_flower);
                    e.blob.gold = 1;
                    return SideEffect.none;
                });
        events.item.addEventHandler(Defs.item_flower, "ItemConsumedEvent",
                (GameView view, Item receiver, Event event) -> SideEffect.none);

        // Fish
        events.item.addEventHandler(Defs.item_fish, "GenerateItemEvent",
                (GameView view, Item receiver, Event event) -> {
                    Events.GenerateItemEvent e = (Events.GenerateItemEvent) event;
                    e.blob.desc = "Consume to stave off hunger";
                    e.blob.icon = Optional.of(Defs.assets_fish);
                    e.blob.gold = 1;
                    return SideEffect.none;
                });
        events.item.addEventHandler(Defs.item_fish, "ItemConsumedEvent",
                (GameView view, Item receiver, Event event) -> ItemLogic.food(view, event));

        // Gold Coin
        events.item.addEventHandler(Defs.item_gold_coin, "GenerateItemEvent",
                (GameView view, Item receiver, Event event) -> {
                    Events.GenerateItemEvent e = (Events.GenerateItemEvent) event;
                    e.blob.desc = "Consume to increase your gold";
                    e.blob.icon = Optional.of(Defs.assets_coin);
                    e.blob.gold = 1;
                    return SideEffect.none;
                });
        events.item.addEventHandler(Defs.item_gold_coin, "ItemConsumedEvent",
                (GameView view, Item receiver, Event event) -> ItemLogic.valuable(event));

        // Emerald
        events.item.addEventHandler(Defs.item_emerald, "GenerateItemEvent",
                (GameView view, Item receiver, Event event) -> {
                    Events.GenerateItemEvent e = (Events.GenerateItemEvent) event;
                    e.blob.desc = "Consume to increase your gold";
                    e.blob.icon = Optional.of(Defs.assets_crystal);
                    e.blob.gold = 10;
                    return SideEffect.none;
                });
        events.item.addEventHandler(Defs.item_emerald, "ItemConsumedEvent",
                (GameView view, Item receiver, Event event) -> ItemLogic.valuable(event));

        // Apple
        events.item.addEventHandler(Defs.item_apple, "GenerateItemEvent",
                (GameView view, Item receiver, Event event) -> {
                    Events.GenerateItemEvent e = (Events.GenerateItemEvent) event;
                    e.blob.desc = "Consume to stave off hunger";
                    e.blob.icon = Optional.of(Defs.assets_apple);
                    e.blob.gold = 1;
                    e.blob.tags.add(Defs.tag_natural).add(Defs.tag_fruit);
                    return SideEffect.none;
                });
        events.item.addEventHandler(Defs.item_apple, "ItemConsumedEvent",
                (GameView view, Item receiver, Event event) -> ItemLogic.food(view, event));

        // Health Potion
        events.item.addEventHandler(Defs.item_health_potion, "GenerateItemEvent",
                (GameView view, Item receiver, Event event) -> {
                    Events.GenerateItemEvent e = (Events.GenerateItemEvent) event;
                    e.blob.desc = "Consume to heal by 10 hit points";
                    e.blob.icon = Optional.of(Defs.assets_potion);
                    e.blob.gold = 1;
                    return SideEffect.none;
                });
        events.item.addEventHandler(Defs.item_health_potion, "ItemConsumedEvent",
                (GameView view, Item receiver, Event event) -> ItemLogic.potion(view, event, 10));

        // Incense
        // Sack of Gold
        // Capital
        // Shellcap Armor
        // Stones
        // Sword
        // Shield
        // Staff
        // Prayer Beads
        // Rites of the Merchant
        // Rites of the Vendor
        // Pocket
        // Raider's Mail
        // Sentinel's Helm
        // Counselor's Ring
        // Matron's Sash
        // Wizard's Cap
        // Youth's Pendant
        // General's Trousers
        // Courier's Boots
        // Rogue's Gloves
        // Scion's Beltbuckle
        // Priest's Robes
        // Acidic Solute
        // Binding Solute
        // Life-Giving Solute
        // Blessed Solute
        // Feather of Bravery
        // Iron Beak Brace
        // Hollow Bone Rattle
        // Bag of Shiny Pebbles
        // Thorny Rose Staff
        // Overgrown Shield
        // Sap of Unbreaking
        // Sacred Pollen
        // Advanced Spear
        // Sentinel's Shield
        // Healing Incantation
        // Devout Incantation
        // Great Hammer
        // Battle Armaments
        // Bandage Kit
        // Dearly Held Idol
        // Stoneshell Mace
        // Shell Salve
        // Shell-Sealing Goo
        // Sacred Shell Rattle
        // Blessed Charm
        // Bloody Totem
        // Phoenix Blossom
        // Sling and Stone
        // Life-Giving Elixir
        // Blood-Thirsty Blade
        // Blood-Soaked Mail
        // Leather Armor
        // Net Bag
        // Iron Mace
        // Guardian's Axe
        // Grizzled Polearm
        // Avenger's Blade
        // Sorcerous Robes
        // Shiny Brooch
        // Rougish Sneaker
        // Swift Cape
        // Vagabond's Gloves
        // Noble Spearhead
        // Sacrificial Dagger
        // Heavy Iron Platemail
        // Keg of Strong Brew
        // Pendant of Protection
        // Charm of Connected Life
        // Guardian's Tome
        // Fine Mesh Chainmail
        // Black Market Gauntlet
        // Alloy Greaves
        // Advanced Chainmail
        // Saintly Helm
        // Idol of Lordly Favor
        // Sacrificial Club
        // Spritely Ale
        // Necklace of the Clear Mind
        // Life-Giver's Shawl
        // Ancient Tome of Healing
        // Decorated Urn
        // Devious Magic Wand
        // Plainswalker's Cloak
        // Gauntlet of Precious Light
        // Noble Ruby Ring
        // Priesthood Vestments
        // Dwarf's Pickaxe
        // Dragonkin's Helm
        // Merfolk's Net
        // Firbolg's Cloak
        // Sprite's Gloves
        // Brownie's Boots
        // Raksha's Pendant
        // Naga's Scepter
        // Well-Crafted Bow
        // Mycelium Ring
        // Cyclical Rune
        // Mercenary's Blade
        // Wizard's Staff
        // Floral seeds
        // Arboreal seeds
        // Arctic seeds
        // Cactus seeds
        // Pioneering seeds
        // Digging Kit
        // Telescope
        // Ornate Boots
        // Satchel
        // Expertly Crafted Blade
        // Queensguard Shield
        // Arcane Wand
        // Ancient Tome
        // Vendor's Scales
        // Merfolk Slippers
        // Stygian Eye
        // Ring of Life Eternal
        // Truffle
        // Scholarly Robes
        // Sanguine Blade
        // Necrotic Tome
        // Thoughtform Sword
        // Shield of the Subconscious
        // Staff of Inner Peace
        // Rod of Psychic Devotion
        // Tree Trunk Club
        // Castle Gate Aegis
        // Vase of Sacred Waters
        // Amulet of the Progenitors
        // Sharpened Quartz
        // Igneous Armaments
        // Moss-covered Stone
        // Glyphic Geode
        // Self-Sustaining Soulstone
        // Hero's Call
    }
}
