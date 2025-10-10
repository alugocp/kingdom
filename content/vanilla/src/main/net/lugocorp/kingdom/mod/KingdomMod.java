package net.lugocorp.kingdom.mod;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.builtin.logic.AbilityLogic;
import net.lugocorp.kingdom.builtin.logic.ItemLogic;
import net.lugocorp.kingdom.builtin.logic.UnitLogic;
import net.lugocorp.kingdom.engine.assets.SpriteLoader;
import net.lugocorp.kingdom.game.actions.ActionType;
import net.lugocorp.kingdom.game.combat.Damage;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.glyph.GlyphCategory;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.model.Building;
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
import net.lugocorp.kingdom.mod.common.Labels;
import net.lugocorp.kingdom.mods.GameMod;
import net.lugocorp.kingdom.mods.ModProfile;
import net.lugocorp.kingdom.ui.nodes.ArtifactNode;
import net.lugocorp.kingdom.ui.nodes.FateNode;
import net.lugocorp.kingdom.ui.nodes.InventoryNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.Lambda;
import net.lugocorp.kingdom.utils.code.Semver;
import net.lugocorp.kingdom.utils.code.SideEffect;
import net.lugocorp.kingdom.utils.math.HexSide;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
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
    public ModProfile getProfile() {
        return new ModProfile("vanilla", "Vanilla", "Contains all of the base content for this game.",
                new Semver(1, 0, 0), new String[]{"Alex Lugo"});
    }

    /** {@inheritdoc} */
    @Override
    public void registerSprites(SpriteLoader sprites) {
        // Item sprites
        sprites.register(Labels.asset_placeholder, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 0, 0);
        sprites.register(Labels.asset_potion, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 1, 0);
        sprites.register(Labels.asset_apple, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 2, 0);
        sprites.register(Labels.asset_pouch, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 3, 0);
        sprites.register(Labels.asset_stone, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 4, 0);
        sprites.register(Labels.asset_staff, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 5, 0);
        sprites.register(Labels.asset_beads, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 6, 0);
        sprites.register(Labels.asset_chestplate, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 7, 0);
        sprites.register(Labels.asset_coin, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 0, 1);
        sprites.register(Labels.asset_sword, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 1, 1);
        sprites.register(Labels.asset_shield, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 2, 1);
        sprites.register(Labels.asset_candle, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 3, 1);
        sprites.register(Labels.asset_ring, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 4, 1);
        sprites.register(Labels.asset_robe, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 5, 1);
        sprites.register(Labels.asset_wizard_hat, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 6, 1);
        sprites.register(Labels.asset_pendant, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 7, 1);
        sprites.register(Labels.asset_mushroom, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 0, 2);
        sprites.register(Labels.asset_crystal, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 1, 2);
        sprites.register(Labels.asset_bone, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 2, 2);
        sprites.register(Labels.asset_fish, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 3, 2);
        sprites.register(Labels.asset_pants, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 4, 2);
        sprites.register(Labels.asset_boots, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 5, 2);
        sprites.register(Labels.asset_glove, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 6, 2);
        sprites.register(Labels.asset_belt, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 7, 2);
        sprites.register(Labels.asset_flower, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 0, 3);
        sprites.register(Labels.asset_seeds, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 1, 3);
        sprites.register(Labels.asset_paper, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 2, 3);
        sprites.register(Labels.asset_helmet, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 3, 3);
        sprites.register(Labels.asset_slime, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 4, 3);
        sprites.register(Labels.asset_feather, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 5, 3);
        sprites.register(Labels.asset_rattle, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 6, 3);
        sprites.register(Labels.asset_powder, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 7, 3);
        sprites.register(Labels.asset_spear, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 0, 4);
        sprites.register(Labels.asset_hammer, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 1, 4);
        sprites.register(Labels.asset_doll, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 2, 4);
        sprites.register(Labels.asset_mace, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 3, 4);
        sprites.register(Labels.asset_carving, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 4, 4);
        sprites.register(Labels.asset_slingshot, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 5, 4);
        sprites.register(Labels.asset_net, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 6, 4);
        sprites.register(Labels.asset_axe, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 7, 4);
        sprites.register(Labels.asset_brooch, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 0, 5);
        sprites.register(Labels.asset_dagger, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 1, 5);
        sprites.register(Labels.asset_tankard, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 2, 5);
        sprites.register(Labels.asset_book, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 3, 5);
        sprites.register(Labels.asset_club, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 4, 5);
        sprites.register(Labels.asset_vase, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 5, 5);
        sprites.register(Labels.asset_wand, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 6, 5);
        sprites.register(Labels.asset_pickaxe, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 7, 5);
        sprites.register(Labels.asset_bow, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 0, 6);
        sprites.register(Labels.asset_rune, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 1, 6);
        sprites.register(Labels.asset_shovel, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 2, 6);
        sprites.register(Labels.asset_telescope, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 3, 6);
        sprites.register(Labels.asset_scales, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 4, 6);
        sprites.register(Labels.asset_eye, Labels.asset_icons, InventoryNode.SIDE, InventoryNode.SIDE, 5, 6);

        // Artifact sprites
        sprites.register(Labels.asset_chos_sigil_of_haste, Labels.asset_artifacts, ArtifactNode.WIDTH,
                ArtifactNode.HEIGHT, 0, 0);
        sprites.register(Labels.asset_urdins_scroll_of_agility, Labels.asset_artifacts, ArtifactNode.WIDTH,
                ArtifactNode.HEIGHT, 1, 0);
        sprites.register(Labels.asset_sword_of_aesethos, Labels.asset_artifacts, ArtifactNode.WIDTH,
                ArtifactNode.HEIGHT, 2, 0);
        sprites.register(Labels.asset_kaunas_amulet, Labels.asset_artifacts, ArtifactNode.WIDTH, ArtifactNode.HEIGHT, 3,
                0);
        sprites.register(Labels.asset_staff_of_wurmdel, Labels.asset_artifacts, ArtifactNode.WIDTH, ArtifactNode.HEIGHT,
                4, 0);
        sprites.register(Labels.asset_tome_of_morun, Labels.asset_artifacts, ArtifactNode.WIDTH, ArtifactNode.HEIGHT, 0,
                1);
        sprites.register(Labels.asset_orb_of_nerketo, Labels.asset_artifacts, ArtifactNode.WIDTH, ArtifactNode.HEIGHT,
                1, 1);
        sprites.register(Labels.asset_shadas_flute, Labels.asset_artifacts, ArtifactNode.WIDTH, ArtifactNode.HEIGHT, 2,
                1);
        sprites.register(Labels.asset_stones_of_thudin, Labels.asset_artifacts, ArtifactNode.WIDTH, ArtifactNode.HEIGHT,
                3, 1);
        sprites.register(Labels.asset_chasi_bones, Labels.asset_artifacts, ArtifactNode.WIDTH, ArtifactNode.HEIGHT, 4,
                1);
        sprites.register(Labels.asset_uchas_bowl_of_plenty, Labels.asset_artifacts, ArtifactNode.WIDTH,
                ArtifactNode.HEIGHT, 0, 2);
        sprites.register(Labels.asset_nerketos_helm, Labels.asset_artifacts, ArtifactNode.WIDTH, ArtifactNode.HEIGHT, 1,
                2);
        sprites.register(Labels.asset_bounty_of_ahn_june, Labels.asset_artifacts, ArtifactNode.WIDTH,
                ArtifactNode.HEIGHT, 2, 2);
        sprites.register(Labels.asset_mark_of_kung, Labels.asset_artifacts, ArtifactNode.WIDTH, ArtifactNode.HEIGHT, 3,
                2);
        sprites.register(Labels.asset_chalcos_seal_of_protection, Labels.asset_artifacts, ArtifactNode.WIDTH,
                ArtifactNode.HEIGHT, 4, 2);
        sprites.register(Labels.asset_podas_elixir, Labels.asset_artifacts, ArtifactNode.WIDTH, ArtifactNode.HEIGHT, 0,
                3);
        sprites.register(Labels.asset_gaias_effigy, Labels.asset_artifacts, ArtifactNode.WIDTH, ArtifactNode.HEIGHT, 1,
                3);
        sprites.register(Labels.asset_rod_of_adelon, Labels.asset_artifacts, ArtifactNode.WIDTH, ArtifactNode.HEIGHT, 2,
                3);
        sprites.register(Labels.asset_blade_of_sanguinor, Labels.asset_artifacts, ArtifactNode.WIDTH,
                ArtifactNode.HEIGHT, 3, 3);
        sprites.register(Labels.asset_cask_of_amontior, Labels.asset_artifacts, ArtifactNode.WIDTH, ArtifactNode.HEIGHT,
                4, 3);

        // Fate sprites
        sprites.register(Labels.asset_raider, Labels.asset_fates, FateNode.WIDTH, FateNode.HEIGHT, 0, 0);
        sprites.register(Labels.asset_merchant, Labels.asset_fates, FateNode.WIDTH, FateNode.HEIGHT, 1, 0);
        sprites.register(Labels.asset_veteran, Labels.asset_fates, FateNode.WIDTH, FateNode.HEIGHT, 2, 0);
        sprites.register(Labels.asset_devout, Labels.asset_fates, FateNode.WIDTH, FateNode.HEIGHT, 3, 0);
        sprites.register(Labels.asset_sentinel, Labels.asset_fates, FateNode.WIDTH, FateNode.HEIGHT, 0, 1);
        sprites.register(Labels.asset_usurper, Labels.asset_fates, FateNode.WIDTH, FateNode.HEIGHT, 1, 1);
        sprites.register(Labels.asset_forager, Labels.asset_fates, FateNode.WIDTH, FateNode.HEIGHT, 2, 1);
    }

    /** {@inheritdoc} */
    @Override
    public void registerEvents(AllEventHandlers events) {

        /**
         * SECTION Default handlers
         */

        // GetVisionEvent
        events.unit.setDefaultHandler(Events.GetVisionEvent.class,
                (GameView view, Unit receiver, Events.GetVisionEvent e) -> {
                    e.radius = 2;
                    return SideEffect.none;
                });

        // GetsHungry
        events.unit.setDefaultHandler("GetsHungry",
                (GameView view, Unit receiver, Event event) -> () -> receiver.hunger.gotHungry(view));

        // HungerStrikes
        events.unit.setDefaultHandler("HungerStrikes", (GameView view, Unit receiver, Event event) -> {
            if (receiver.getLeader().isPresent()) {
                return () -> receiver.loyalty.decrease(view, 1);
            }
            ((Events.RepeatedEvent) event).repeat = false;
            return SideEffect.none;
        });

        // CanEatEvent
        events.unit.setDefaultHandler(Events.CanEatEvent.class,
                (GameView view, Unit receiver, Events.CanEatEvent e) -> {
                    e.edible = e.item.tags.has(Labels.tag_fruit);
                    return SideEffect.none;
                });

        // UnitMoveDistanceEvent
        events.unit.setDefaultHandler(Events.UnitMoveDistanceEvent.class,
                (GameView view, Unit receiver, Events.UnitMoveDistanceEvent e) -> {
                    e.distance = 2;
                    return SideEffect.none;
                });

        /**
         * SECTION Tiles
         */

        // Grass
        events.tile.addEventHandler(Labels.tile_grass, Events.GenerateTileEvent.class,
                (GameView view, Tile receiver, Events.GenerateTileEvent e) -> {
                    e.blob.setModelInstance(view.av, Labels.asset_grass);
                    e.blob.setMinimapColor(0x2c9965);
                    e.blob.desc = "The seeds to spring new life lay dormant beneath this place";
                    return SideEffect.none;
                });

        // Rock
        events.tile.addEventHandler(Labels.tile_rock, Events.GenerateTileEvent.class,
                (GameView view, Tile receiver, Events.GenerateTileEvent e) -> {
                    e.blob.setModelInstance(view.av, Labels.asset_grass);
                    e.blob.setMinimapColor(0x666666);
                    e.blob.setMaterial(Labels.asset_rock);
                    e.blob.desc = "The rocky mountainscape is home to many creatures";
                    return SideEffect.none;
                });

        // Sand
        events.tile.addEventHandler(Labels.tile_sand, Events.GenerateTileEvent.class,
                (GameView view, Tile receiver, Events.GenerateTileEvent e) -> {
                    e.blob.setModelInstance(view.av, Labels.asset_grass);
                    e.blob.setMinimapColor(0xc7c567);
                    e.blob.setMaterial(Labels.asset_sand);
                    e.blob.desc = "The hot sands seem to stretch on forever";
                    return SideEffect.none;
                });

        // Snow
        events.tile.addEventHandler(Labels.tile_snow, Events.GenerateTileEvent.class,
                (GameView view, Tile receiver, Events.GenerateTileEvent e) -> {
                    e.blob.setModelInstance(view.av, Labels.asset_grass);
                    e.blob.setMinimapColor(0xffffff);
                    e.blob.setMaterial(Labels.asset_snow);
                    e.blob.desc = "Dense and cold";
                    return SideEffect.none;
                });

        // Water
        events.tile.addEventHandler(Labels.tile_water, Events.GenerateTileEvent.class,
                (GameView view, Tile receiver, Events.GenerateTileEvent e) -> {
                    e.blob.setModelInstance(view.av, Labels.asset_water);
                    e.blob.setMinimapColor(0x20c7f7);
                    e.blob.setObstacle(true);
                    e.blob.setWave(true);
                    e.blob.desc = "Only certain units can swim";
                    return SideEffect.none;
                });

        // Lava
        events.tile.addEventHandler(Labels.tile_lava, Events.GenerateTileEvent.class,
                (GameView view, Tile receiver, Events.GenerateTileEvent e) -> {
                    e.blob.setModelInstance(view.av, Labels.asset_water);
                    e.blob.setMinimapColor(0xcf3b23);
                    e.blob.setMaterial(Labels.asset_lava);
                    e.blob.setObstacle(true);
                    e.blob.setWave(true);
                    e.blob.desc = "Watch your step!";
                    return SideEffect.none;
                });

        /**
         * SECTION Buildings
         */

        // Mine
        events.building.addEventHandler(Labels.building_mine, Events.GenerateBuildingEvent.class,
                (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance(view.av, "mine");
                    e.blob.desc = "Mines provide valuables like gold coins";
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.setMinimapColor(0x555555);
                    e.blob.setActive();
                    return SideEffect.none;
                });

        // Vault
        events.building.addEventHandler(Labels.building_vault, Events.GenerateBuildingEvent.class,
                (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance(view.av, "vault");
                    e.blob.desc = "Vaults can store excess items and be used in auctions";
                    e.blob.items = Optional.of(new Inventory(InventoryType.BUILDING, 24));
                    e.blob.combat.health.setMaxAndValue(40);
                    e.blob.setMinimapColor(0x000000);
                    e.blob.setActive();
                    return SideEffect.none;
                });

        // Forest
        events.building.addEventHandler(Labels.building_forest, Events.GenerateBuildingEvent.class,
                (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance(view.av, "forest");
                    e.blob.desc = "Don't miss the forest for the trees";
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.setMinimapColor(0x257d53);
                    return SideEffect.none;
                });

        // Taiga
        events.building.addEventHandler(Labels.building_taiga, Events.GenerateBuildingEvent.class,
                (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance(view.av, "forest");
                    e.blob.desc = "The trees are pretty this time of year";
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.setMinimapColor(0xb4c3c7);
                    e.blob.setMaterial(Labels.asset_taiga);
                    return SideEffect.none;
                });

        // Meadow
        events.building.addEventHandler(Labels.building_meadow, Events.GenerateBuildingEvent.class,
                (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance(view.av, "meadow");
                    e.blob.desc = "Stay a while and smell the roses";
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.setMinimapColor(0x4dd349);
                    return SideEffect.none;
                });

        // Oasis
        events.building.addEventHandler(Labels.building_oasis, Events.GenerateBuildingEvent.class,
                (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance(view.av, "oasis");
                    e.blob.desc = "Moments of respite from the overbearing sun";
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.setMinimapColor(0x2c9965);
                    return SideEffect.none;
                });

        // Shrubland
        events.building.addEventHandler(Labels.building_shrubland, Events.GenerateBuildingEvent.class,
                (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance(view.av, "shrubland");
                    e.blob.desc = "Meadows in the middle of the desert";
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.setMinimapColor(0x4dd349);
                    return SideEffect.none;
                });

        // Mountain
        events.building.addEventHandler(Labels.building_mountain, Events.GenerateBuildingEvent.class,
                (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance(view.av, "mountain");
                    e.blob.desc = "An immovable object";
                    e.blob.combat.health.invulnerable();
                    e.blob.setMinimapColor(0x875f9a);
                    e.blob.setObstacle(true);
                    return SideEffect.none;
                });

        // Healing Fountain
        events.building.addEventHandler(Labels.building_healing_fountain, Events.GenerateBuildingEvent.class,
                (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance(view.av, "fountain");
                    e.blob.desc = "Heals an occupying unit each turn";
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.setMinimapColor(0x875f9a);
                    e.blob.setActive();
                    return SideEffect.none;
                });
        events.building.addEventHandler(Labels.building_healing_fountain, Events.SpawnEvent.class,
                (GameView view, Building receiver,
                        Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 1, true));
        events.building.addEventHandler(Labels.building_healing_fountain, "Tick",
                (GameView view, Building receiver, Events.RepeatedEvent e) -> {
                    Optional<Unit> u = view.game.world.getTile(receiver.getPoint()).flatMap((Tile t) -> t.unit);
                    return u.isPresent() ? receiver.combat.heal(view, u.get(), 5) : SideEffect.none;
                });

        /**
         * SECTION Patrons
         */

        // Joyous Reaper
        // Great Corn Woman
        // Lord Shui, Guardian of the River
        // The Pond Troll
        events.patron.addEventHandler(Labels.patron_pond_troll, Events.GeneratePatronEvent.class,
                (GameView view, Patron receiver, Events.GeneratePatronEvent e) -> {
                    e.blob.setModelInstance(view.av, "pond-troll");
                    e.blob.desc = "The favorite player's units can traverse water tiles and have a 20% chance to fish when they do";
                    e.blob.preference = "Units that cannot swim";
                    e.blob.isPreferredUnitType = (Unit u) -> !u.abilities.hasPassive(Labels.ability_swim);
                    return SideEffect.none;
                });
        events.patron.addEventHandler(Labels.patron_pond_troll, Events.SpawnEvent.class,
                (GameView view, Patron receiver, Events.SpawnEvent e) -> () -> {
                    view.game.events.signals.addListener(Events.CanUnitMoveEvent.class, receiver);
                    view.game.events.signals.addListener(Events.UnitMovedEvent.class, receiver);
                });
        events.patron.addEventHandler(Labels.patron_pond_troll, Events.CanUnitMoveEvent.class,
                (GameView view, Patron receiver, Events.CanUnitMoveEvent e) -> {
                    if (e.unit.getLeader().equals(receiver.getFavoritePlayer())
                            && e.tile.name.equals(Labels.tile_water)) {
                        e.canWalkOnTile = true;
                    }
                    return SideEffect.none;
                });
        events.patron.addEventHandler(Labels.patron_pond_troll, Events.UnitMovedEvent.class,
                (GameView view, Patron receiver, Events.UnitMovedEvent e) -> {
                    if (e.unit.getLeader().equals(receiver.getFavoritePlayer())
                            && view.game.world.getTile(e.current).get().name.equals(Labels.tile_water)
                            && !e.unit.haul.isFull() && Lambda.chance(20)) {
                        return () -> e.unit.haul.add(view.game.generator.item(Labels.item_fish));
                    }
                    return SideEffect.none;
                });

        // The Eternal Guardian
        // Flutterwing
        // Wise Mountain
        // Wise Oak
        // Ahn-Juné
        // The Shining Eyes
        events.patron.addEventHandler(Labels.patron_shining_eyes, Events.GeneratePatronEvent.class,
                (GameView view, Patron receiver, Events.GeneratePatronEvent e) -> {
                    e.blob.setModelInstance(view.av, "shining-eyes");
                    e.blob.desc = "Heals 4 random units of its favorite player each turn";
                    e.blob.preference = "Healing glyph units";
                    e.blob.isPreferredUnitType = (Unit u) -> u.glyphs.has(Glyph.HEALING);
                    return SideEffect.none;
                });
        events.patron.addEventHandler(Labels.patron_shining_eyes, Events.SpawnEvent.class,
                (GameView view, Patron receiver,
                        Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 1, true));
        events.patron.addEventHandler(Labels.patron_shining_eyes, "Tick",
                (GameView view, Patron receiver, Events.RepeatedEvent e) -> {
                    final List<SideEffect> effects = SideEffect.list();
                    final Optional<Player> favorite = receiver.getFavoritePlayer();
                    favorite.ifPresent((Player p) -> {
                        for (Unit u : Lambda.subset(4, p.units)) {
                            effects.add(u.combat.heal(view, 3));
                        }
                    });
                    return SideEffect.all(effects);
                });

        /**
         * SECTION Artifacts
         */

        // Cho's Sigil of Haste
        events.artifact.addEventHandler(Labels.artifact_chos_sigil_of_haste, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your healing glyph units get +1 movement speed";
                    e.blob.image = Optional.of(Labels.asset_chos_sigil_of_haste);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_chos_sigil_of_haste, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    view.game.events.signals.addListener(Events.UnitMoveDistanceEvent.class, e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_chos_sigil_of_haste, Events.UnitMoveDistanceEvent.class,
                (GameView view, Artifact receiver, Events.UnitMoveDistanceEvent e) -> {
                    if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.HEALING)) {
                        e.distance++;
                    }
                    return SideEffect.none;
                });

        // Urdin's Scroll of Agility
        events.artifact.addEventHandler(Labels.artifact_urdins_scroll_of_agility, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your defense glyph units get +1 movement speed";
                    e.blob.image = Optional.of(Labels.asset_urdins_scroll_of_agility);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_urdins_scroll_of_agility, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    view.game.events.signals.addListener(Events.UnitMoveDistanceEvent.class, e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_urdins_scroll_of_agility, Events.UnitMoveDistanceEvent.class,
                (GameView view, Artifact receiver, Events.UnitMoveDistanceEvent e) -> {
                    if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.DEFENSE)) {
                        e.distance++;
                    }
                    return SideEffect.none;
                });

        // Sword of Aesethos
        events.artifact.addEventHandler(Labels.artifact_sword_of_aesethos, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your units have +10% critical hit chance";
                    e.blob.image = Optional.of(Labels.asset_sword_of_aesethos);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_sword_of_aesethos, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    view.game.events.signals.addListener(Events.CheckCriticalHitEvent.class, e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_sword_of_aesethos, Events.CheckCriticalHitEvent.class,
                (GameView view, Artifact receiver, Events.CheckCriticalHitEvent e) -> {
                    if (receiver.isClaimedByLeader(e.entity)) {
                        e.chance += 10;
                    }
                    return SideEffect.none;
                });

        // Kauna's Amulet
        events.artifact.addEventHandler(Labels.artifact_kaunas_amulet, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your units within a patron's domain have extra defense";
                    e.blob.image = Optional.of(Labels.asset_kaunas_amulet);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_kaunas_amulet, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    view.game.events.signals.addListener(Events.TakeDamageEvent.class, e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_kaunas_amulet, Events.TakeDamageEvent.class,
                (GameView view, Artifact receiver, Events.TakeDamageEvent e) -> {
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
        events.artifact.addEventHandler(Labels.artifact_staff_of_wurmdel, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your healing spells restore +4 more health";
                    e.blob.image = Optional.of(Labels.asset_staff_of_wurmdel);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_staff_of_wurmdel, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    view.game.events.signals.addListener(Events.HealEntityEvent.class, e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_staff_of_wurmdel, Events.HealEntityEvent.class,
                (GameView view, Artifact receiver, Events.HealEntityEvent e) -> {
                    if (receiver.isClaimedByLeader(e.healer)) {
                        e.amount += 4;
                    }
                    return SideEffect.none;
                });

        // Tome of Morun
        events.artifact.addEventHandler(Labels.artifact_tome_of_morun, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "20% chance to spawn a glyph when your units kill an enemy";
                    e.blob.image = Optional.of(Labels.asset_tome_of_morun);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_tome_of_morun, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    view.game.events.signals.addListener(Events.EntityDiedEvent.class, e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_tome_of_morun, Events.EntityDiedEvent.class,
                (GameView view, Artifact receiver, Events.EntityDiedEvent e) -> {
                    if (receiver.isClaimedByLeader(e.killer) && !receiver.isClaimedByLeader(e.target)) {
                        Tile t = view.game.world.getTile(e.killer.getPoint()).get();
                        if (!t.getGlyph().isPresent() && Lambda.chance(20)) {
                            t.setGlyph(Optional.of(Lambda.random(GlyphCategory.class)));
                        }
                    }
                    return SideEffect.none;
                });

        // Orb of Nerketo
        events.artifact.addEventHandler(Labels.artifact_orb_of_nerketo, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your units have +1 vision";
                    e.blob.image = Optional.of(Labels.asset_orb_of_nerketo);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_orb_of_nerketo, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    view.game.events.signals.addListener(Events.GetVisionEvent.class, e.artifact);
                    for (Unit u : e.player.units) {
                        u.vision.set(view, e.player, u, u.getPoint());
                    }
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_orb_of_nerketo, Events.GetVisionEvent.class,
                (GameView view, Artifact receiver, Events.GetVisionEvent e) -> {
                    if (receiver.isClaimedByPlayer(e.player)) {
                        e.radius++;
                    }
                    return SideEffect.none;
                });

        // Shada's Flute
        events.artifact.addEventHandler(Labels.artifact_shadas_flute, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your patrons generate 5 unit points per turn";
                    e.blob.image = Optional.of(Labels.asset_shadas_flute);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_shadas_flute, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    view.game.future.addFutureTick("Tick", receiver, 1, true);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_shadas_flute, "Tick",
                (GameView view, Artifact receiver, Events.RepeatedEvent e) -> {
                    for (Patron patron : view.game.mechanics.patronage) {
                        if (receiver.getOwner().equals(patron.getFavoritePlayer())) {
                            receiver.getOwner().get().addUnitPoints(view, 5);
                        }
                    }
                    return SideEffect.none;
                });

        // Stones of Thudin
        events.artifact.addEventHandler(Labels.artifact_stones_of_thudin, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your vaults have +3 defense";
                    e.blob.image = Optional.of(Labels.asset_stones_of_thudin);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_stones_of_thudin, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    view.game.events.signals.addListener(Events.TakeDamageEvent.class, e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_stones_of_thudin, Events.TakeDamageEvent.class,
                (GameView view, Artifact receiver, Events.TakeDamageEvent e) -> {
                    if (e.target.isEntityType(EntityType.BUILDING)) {
                        if (receiver.isClaimedByLeader(e.target) && e.target.name.equals(Labels.building_vault)) {
                            e.dmg.base -= 3;
                        }
                    }
                    return SideEffect.none;
                });

        // The Chasi Bones
        events.artifact.addEventHandler(Labels.artifact_chasi_bones, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your nature glyph units have a 20% chance to harvest an additional item";
                    e.blob.image = Optional.of(Labels.asset_chasi_bones);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_chasi_bones, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    view.game.events.signals.addListener(Events.HarvestEvent.class, e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_chasi_bones, Events.HarvestEvent.class,
                (GameView view, Artifact receiver, Events.HarvestEvent e) -> {
                    if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.NATURE) && !e.unit.haul.isFull()
                            && Lambda.chance(20)) {
                        e.unit.haul.add(view.game.generator.item(e.item.getStratifier()));
                    }
                    return SideEffect.none;
                });

        // Ucha's Bowl of Plenty
        events.artifact.addEventHandler(Labels.artifact_uchas_bowl_of_plenty, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "+1 option when selecting a new unit";
                    e.blob.image = Optional.of(Labels.asset_uchas_bowl_of_plenty);
                    e.blob.chips = 2;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_uchas_bowl_of_plenty, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    e.player.numRecruitmentOptions++;
                    return SideEffect.none;
                });

        // Nerketo's Helm
        events.artifact.addEventHandler(Labels.artifact_nerketos_helm, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Critical hits against your units are less effective";
                    e.blob.image = Optional.of(Labels.asset_nerketos_helm);
                    e.blob.chips = 2;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_nerketos_helm, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    view.game.events.signals.addListener(Events.CheckCriticalHitEvent.class, e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_nerketos_helm, Events.CheckCriticalHitEvent.class,
                (GameView view, Artifact receiver, Events.CheckCriticalHitEvent e) -> {
                    if (e.entity.isEntityType(EntityType.UNIT) && receiver.isClaimedByLeader(e.entity)) {
                        e.multiplier = 1.1f;
                    }
                    return SideEffect.none;
                });

        // Bounty of Ahn-June
        events.artifact.addEventHandler(Labels.artifact_bounty_of_ahn_june, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Trade glyph units on your vaults generate +2 more auction points";
                    e.blob.image = Optional.of(Labels.asset_bounty_of_ahn_june);
                    e.blob.chips = 2;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_bounty_of_ahn_june, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    view.game.events.signals.addListener(Events.GenerateAuctionPointsEvent.class, e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_bounty_of_ahn_june, Events.GenerateAuctionPointsEvent.class,
                (GameView view, Artifact receiver, Events.GenerateAuctionPointsEvent e) -> {
                    if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.TRADE)
                            && view.game.world.getTile(e.unit.getPoint()).flatMap((Tile t) -> t.building)
                                    .map((Building b) -> b.name.equals(Labels.building_vault)).orElse(false)) {
                        e.points += 2;
                    }
                    return SideEffect.none;
                });

        // Mark of Kung
        events.artifact.addEventHandler(Labels.artifact_mark_of_kung, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your battle glyph units get +1 movement speed";
                    e.blob.image = Optional.of(Labels.asset_mark_of_kung);
                    e.blob.chips = 2;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_mark_of_kung, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    view.game.events.signals.addListener(Events.UnitMoveDistanceEvent.class, e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_mark_of_kung, Events.UnitMoveDistanceEvent.class,
                (GameView view, Artifact receiver, Events.UnitMoveDistanceEvent e) -> {
                    if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.BATTLE)) {
                        e.distance++;
                    }
                    return SideEffect.none;
                });

        // Chalco's Seal of Protection
        events.artifact.addEventHandler(Labels.artifact_chalcos_seal_of_protection, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your trade glyph units have +2 defense";
                    e.blob.image = Optional.of(Labels.asset_chalcos_seal_of_protection);
                    e.blob.chips = 2;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_chalcos_seal_of_protection, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    view.game.events.signals.addListener(Events.TakeDamageEvent.class, e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_chalcos_seal_of_protection, Events.TakeDamageEvent.class,
                (GameView view, Artifact receiver, Events.TakeDamageEvent e) -> {
                    if (e.target.isEntityType(EntityType.UNIT)) {
                        Unit u = (Unit) e.target;
                        if (receiver.isClaimedByLeader(u) && u.glyphs.has(Glyph.TRADE)) {
                            e.dmg.base -= 2;
                        }
                    }
                    return SideEffect.none;
                });

        // Poda's Elixir
        events.artifact.addEventHandler(Labels.artifact_podas_elixir, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "15% chance refresh a glyph when you recruit a unit";
                    e.blob.image = Optional.of(Labels.asset_podas_elixir);
                    e.blob.chips = 2;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_podas_elixir, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    return () -> view.game.events.signals.addListener(Events.SpawnEvent.class, e.artifact);
                });
        events.artifact.addEventHandler(Labels.artifact_podas_elixir, Events.SpawnEvent.class,
                (GameView view, Artifact receiver, Events.SpawnEvent e) -> {
                    if (e.spawned instanceof Unit) {
                        Unit u = (Unit) e.spawned;
                        Tile t = view.game.world.getTile(u.getPoint()).get();
                        if (receiver.isClaimedByLeader(u) && !t.getGlyph().isPresent() && Lambda.chance(15)) {
                            return () -> t.setGlyph(Optional.of(Lambda.random(GlyphCategory.class)));
                        }
                    }
                    return SideEffect.none;
                });

        // Gaia's Effigy
        events.artifact.addEventHandler(Labels.artifact_gaias_effigy, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "+10 unit points each turn";
                    e.blob.image = Optional.of(Labels.asset_gaias_effigy);
                    e.blob.chips = 3;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_gaias_effigy, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    view.game.future.addFutureTick("Tick", receiver, 1, true);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_gaias_effigy, "Tick",
                (GameView view, Artifact receiver, Events.RepeatedEvent e) -> {
                    receiver.getOwner().get().addUnitPoints(view, 10);
                    return SideEffect.none;
                });

        // Rod of Adelon
        events.artifact.addEventHandler(Labels.artifact_rod_of_adelon, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "5% chance to recruit an enemy unit when you kill it";
                    e.blob.image = Optional.of(Labels.asset_rod_of_adelon);
                    e.blob.chips = 3;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_rod_of_adelon, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    view.game.events.signals.addListener(Events.KilledEntityEvent.class, e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_rod_of_adelon, Events.KilledEntityEvent.class,
                (GameView view, Artifact receiver, Events.KilledEntityEvent e) -> {
                    if (e.target.isEntityType(EntityType.UNIT) && receiver.isClaimedByLeader(e.killer)
                            && !e.killer.isFriendly(e.target) && Lambda.chance(5)) {
                        view.game.generator.unit(e.target.name, e.target.getX(), e.target.getY()).spawn(view);
                    }
                    return SideEffect.none;
                });

        // Blade of Sanguinor
        events.artifact.addEventHandler(Labels.artifact_blade_of_sanguinor, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your battle glyph units deal +2 damage";
                    e.blob.image = Optional.of(Labels.asset_blade_of_sanguinor);
                    e.blob.chips = 3;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_blade_of_sanguinor, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    view.game.events.signals.addListener(Events.AttackEvent.class, e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_blade_of_sanguinor, Events.AttackEvent.class,
                (GameView view, Artifact receiver, Events.AttackEvent e) -> {
                    if (e.target.isEntityType(EntityType.UNIT) && receiver.isClaimedByLeader(e.target)
                            && ((Unit) e.target).glyphs.has(Glyph.BATTLE)) {
                        e.dmg.base += 2;
                    }
                    return SideEffect.none;
                });

        // Cask of Amonitor
        events.artifact.addEventHandler(Labels.artifact_cask_of_amontior, Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your unoccupied tiles in a patron's domain provide +1 favor";
                    e.blob.image = Optional.of(Labels.asset_cask_of_amontior);
                    e.blob.chips = 3;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_cask_of_amontior, Events.ArtifactClaimedEvent.class,
                (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                    view.game.events.signals.addListener(Events.CalculateFavorEvent.class, e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(Labels.artifact_cask_of_amontior, Events.CalculateFavorEvent.class,
                (GameView view, Artifact receiver, Events.CalculateFavorEvent e) -> {
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
        events.fate.addEventHandler(Labels.fate_raider, Events.GenerateFateEvent.class,
                (GameView view, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_raider);
                    e.blob.desc.add("Playstyle: High-risk aggro");
                    e.blob.desc.add("> Your first unit will have the battle glyph");
                    e.blob.desc.add("> Your units always deal critical hits at or below 25% of their max health");
                    e.blob.desc
                            .add("> 15% chance for your units to fully heal themselves when they kill an enemy unit");
                    // TODO add strategicGoals
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_raider, Events.GetInitialGlyphEvent.class,
                (GameView view, Fate receiver, Events.GetInitialGlyphEvent e) -> {
                    e.glyph = Optional.of(Glyph.BATTLE);
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_raider, Events.GameStartEvent.class,
                (GameView view, Fate receiver, Events.GameStartEvent e) -> {
                    view.game.events.signals.addListener(Events.CheckCriticalHitEvent.class, receiver);
                    view.game.events.signals.addListener(Events.KilledEntityEvent.class, receiver);
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_raider, Events.CheckCriticalHitEvent.class,
                (GameView view, Fate receiver, Events.CheckCriticalHitEvent e) -> {
                    if (e.entity.isEntityType(EntityType.UNIT)) {
                        final Unit u = (Unit) e.entity;
                        if (u.leadership.hasFate(receiver) && u.combat.health.atOrBelowPercent(25)) {
                            e.chance = 100;
                        }
                    }
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_raider, Events.KilledEntityEvent.class,
                (GameView view, Fate receiver, Events.KilledEntityEvent e) -> {
                    if (e.killer.isEntityType(EntityType.UNIT) && e.target.isEntityType(EntityType.UNIT)) {
                        final Unit k = (Unit) e.killer;
                        final Unit t = (Unit) e.target;
                        if (k.leadership.hasFate(receiver) && !k.leadership.sameLeader(t) && Lambda.chance(15)) {
                            k.combat.health.set(k.combat.health.getMax());
                        }
                    }
                    return SideEffect.none;
                });

        // The Merchant
        events.fate.addEventHandler(Labels.fate_merchant, Events.GenerateFateEvent.class,
                (GameView view, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_merchant);
                    e.blob.desc.add("Playstyle: Market control");
                    e.blob.desc.add("> Your first unit will have the trade glyph");
                    e.blob.desc.add("> Your vault buildings generate 5 unit points each turn");
                    e.blob.desc.add("> Your units generate 150% auction points");
                    // TODO add strategicGoals
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_merchant, Events.GetInitialGlyphEvent.class,
                (GameView view, Fate receiver, Events.GetInitialGlyphEvent e) -> {
                    e.glyph = Optional.of(Glyph.TRADE);
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_merchant, Events.EndOfTurnEvent.class,
                (GameView view, Fate receiver, Events.EndOfTurnEvent e) -> {
                    for (Point p : view.game.getVaultBuildings(receiver.getPlayer())) {
                        receiver.getPlayer().addUnitPoints(view, p, 5);
                    }
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_merchant, Events.GenerateAuctionPointsEvent.class,
                (GameView view, Fate receiver, Events.GenerateAuctionPointsEvent e) -> {
                    if (e.unit.leadership.hasFate(receiver)) {
                        e.points += e.points / 2;
                    }
                    return SideEffect.none;
                });

        // The Veteran
        events.fate.addEventHandler(Labels.fate_veteran, Events.GenerateFateEvent.class,
                (GameView view, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_veteran);
                    e.blob.desc.add("Playstyle: Military production");
                    e.blob.desc.add("> Your battle glyph units heal for 3 damage when they don't act in a turn");
                    e.blob.desc.add("> Recruiting a battle glyph unit gives you 20 unit points");
                    // TODO add strategicGoals
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_veteran, Events.EndOfTurnEvent.class,
                (GameView view, Fate receiver, Events.EndOfTurnEvent e) -> {
                    for (Unit u : receiver.getPlayer().units) {
                        if (view.game.actions.getUnitActionType(u).map((ActionType at) -> at == ActionType.SKIP)
                                .orElse(false)) {
                            u.combat.heal(view, 3);
                        }
                    }
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_veteran, Events.RecruitNewUnitEvent.class,
                (GameView view, Fate receiver, Events.RecruitNewUnitEvent e) -> {
                    if (e.unit.glyphs.has(Glyph.BATTLE)) {
                        receiver.getPlayer().addUnitPoints(view, e.unit.getPoint(), 20);
                    }
                    return SideEffect.none;
                });

        // The Devout
        events.fate.addEventHandler(Labels.fate_devout, Events.GenerateFateEvent.class,
                (GameView view, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_devout);
                    e.blob.desc.add("Playstyle: Patron collection");
                    e.blob.desc.add("> Your active patrons generate +10 unit points");
                    e.blob.desc.add("> Your units generate +3 favor");
                    // TODO add strategicGoals
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_sentinel, Events.GameStartEvent.class,
                (GameView view, Fate receiver, Events.GameStartEvent e) -> {
                    view.game.events.signals.addListener(Events.GenerateFavorEvent.class, receiver);
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_devout, Events.EndOfTurnEvent.class,
                (GameView view, Fate receiver, Events.EndOfTurnEvent e) -> {
                    for (Patron p : view.game.mechanics.patronage) {
                        if (p.getFavoritePlayer().map((Player p1) -> receiver.getPlayer().equals(p1)).orElse(false)) {
                            receiver.getPlayer().addUnitPoints(view, p.getPoint(), 10);
                        }
                    }
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_devout, Events.GenerateFavorEvent.class,
                (GameView view, Fate receiver, Events.GenerateFavorEvent e) -> {
                    if (e.unit.leadership.hasFate(receiver)) {
                        e.favor += 3;
                    }
                    return SideEffect.none;
                });

        // The Sentinel
        events.fate.addEventHandler(Labels.fate_sentinel, Events.GenerateFateEvent.class,
                (GameView view, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_sentinel);
                    e.blob.desc.add("Playstyle: Defensive expansion");
                    e.blob.desc.add("> Your buildings take 15% less damage");
                    e.blob.desc.add("> When you create a building the occupying unit gains 3 attack and defense");
                    e.blob.desc.add("> Recruiting a defense glyph unit gives you 25 unit points");
                    // TODO add strategicGoals
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_sentinel, Events.GameStartEvent.class,
                (GameView view, Fate receiver, Events.GameStartEvent e) -> {
                    view.game.events.signals.addListener(Events.TakeDamageEvent.class, receiver);
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_sentinel, Events.TakeDamageEvent.class,
                (GameView view, Fate receiver, Events.TakeDamageEvent e) -> {
                    if (e.target.getEntityType() == EntityType.BUILDING
                            && e.target.getLeader().map((Player p) -> p.equals(receiver.getPlayer())).orElse(false)) {
                        e.dmg.base -= (int) (e.dmg.base * 0.15);
                    }
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_sentinel, Events.SpawnEvent.class,
                (GameView view, Fate receiver, Events.SpawnEvent e) -> {
                    if (e.spawned instanceof Building) {
                        final Building b = (Building) e.spawned;
                        if (b.getLeader().map((Player p) -> p.equals(receiver.getPlayer())).orElse(false)) {
                            return () -> view.game.world.getTile(b.getPoint()).flatMap((Tile t) -> t.unit)
                                    .ifPresent((Unit u) -> {
                                        u.abilities.addStatusEffect(view, Labels.status_effect_proud_builder);
                                    });
                        }
                    }
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_sentinel, Events.RecruitNewUnitEvent.class,
                (GameView view, Fate receiver, Events.RecruitNewUnitEvent e) -> {
                    if (e.unit.glyphs.has(Glyph.DEFENSE)) {
                        receiver.getPlayer().addUnitPoints(view, e.unit.getPoint(), 25);
                    }
                    return SideEffect.none;
                });

        // The Usurper
        events.fate.addEventHandler(Labels.fate_usurper, Events.GenerateFateEvent.class,
                (GameView view, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_usurper);
                    e.blob.desc.add("Playstyle: Early market bonus into unit production");
                    e.blob.desc.add("> Your first unit will have the trade glyph");
                    e.blob.desc.add("> You get a free auction chip at the start of the game");
                    e.blob.desc.add("> You get 25 unit points when you do not win an auction");
                    // TODO add strategicGoals
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_usurper, Events.GetInitialGlyphEvent.class,
                (GameView view, Fate receiver, Events.GetInitialGlyphEvent e) -> {
                    e.glyph = Optional.of(Glyph.TRADE);
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_usurper, Events.GameStartEvent.class,
                (GameView view, Fate receiver, Events.GameStartEvent e) -> {
                    e.player.auctionChips++;
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_usurper, Events.LostAuctionEvent.class,
                (GameView view, Fate receiver, Events.LostAuctionEvent e) -> {
                    e.player.addUnitPoints(view, 25);
                    return SideEffect.none;
                });

        // The Forager
        events.fate.addEventHandler(Labels.fate_forager, Events.GenerateFateEvent.class,
                (GameView view, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_forager);
                    e.blob.desc.add("Playstyle: Resource accumulation");
                    e.blob.desc.add("> Your first unit will have the nature glyph");
                    e.blob.desc.add("> Your units have a 20% chance to generate an extra item while harvesting");
                    e.blob.desc.add("> Your nature glyph units have +1 speed");
                    // TODO add strategicGoals
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_forager, Events.GetInitialGlyphEvent.class,
                (GameView view, Fate receiver, Events.GetInitialGlyphEvent e) -> {
                    e.glyph = Optional.of(Glyph.NATURE);
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_forager, Events.GameStartEvent.class,
                (GameView view, Fate receiver, Events.GameStartEvent e) -> {
                    view.game.events.signals.addListener(Events.HarvestEvent.class, receiver);
                    view.game.events.signals.addListener(Events.UnitMoveDistanceEvent.class, receiver);
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_forager, Events.HarvestEvent.class,
                (GameView view, Fate receiver, Events.HarvestEvent e) -> {
                    if (e.unit.leadership.hasFate(receiver) && !e.unit.haul.isFull() && Lambda.chance(20)) {
                        e.unit.haul.add(view.game.generator.item(e.item.name));
                    }
                    return SideEffect.none;
                });
        events.fate.addEventHandler(Labels.fate_forager, Events.UnitMoveDistanceEvent.class,
                (GameView view, Fate receiver, Events.UnitMoveDistanceEvent e) -> {
                    if (e.unit.leadership.hasFate(receiver) && e.unit.glyphs.has(Glyph.NATURE)) {
                        e.distance++;
                    }
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
        events.unit.addEventHandler(Labels.unit_sir_tlatec, Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.setModelInstance(view.av, "axolotl");
                    e.blob.desc = "Tlatec the Axolotl-man has travelled far from his home in search of worthy opponents";
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_sword_slash);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_swim, Labels.ability_hunt_fish,
                            Labels.ability_plate_mail, Labels.ability_regeneration);
                    e.blob.glyphs.set(Glyph.BATTLE);
                    e.blob.combat.health.setMaxAndValue(40);
                    e.blob.species = Defs.species_salamander;
                    return SideEffect.none;
                });

        // Cenuok the Battle Grue
        // Beetlemoss
        events.unit.addEventHandler(Labels.unit_beetlemoss, Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This nature spirit guards an ancient forest in Eaglehaven";
                    e.blob.setModelInstance(view.av, "beetlemoss");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_fire_cannon,
                            Labels.ability_plant_forest);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_pick_apples,
                            Labels.ability_mine_gems);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.NATURE);
                    e.blob.combat.health.setMaxAndValue(40);
                    e.blob.species = Defs.species_sprite;
                    return SideEffect.none;
                });

        // Gloop the Adventurer
        events.unit.addEventHandler(Labels.unit_gloop_the_adventurer, Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.setModelInstance(view.av, "gloop");
                    e.blob.desc = "This Plasmoid adventurer is eager to prove themself in the dungeons";
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_sword_slash,
                            Labels.ability_dungeon_delve);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_combat_loot,
                            Labels.ability_night_vision, Labels.ability_regeneration);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.MINING);
                    e.blob.combat.health.setMaxAndValue(40);
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
        events.unit.addEventHandler(Labels.unit_golem_of_the_grotto, Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This golem wanders the rocky peaks where it was forged long ago";
                    e.blob.setModelInstance(view.av, "golem-grotto");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_smash, Labels.ability_plant_meadow);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_mountain_strider,
                            Labels.ability_local_defender);
                    e.blob.glyphs.set(Glyph.DEFENSE, Glyph.NATURE);
                    e.blob.combat.health.setMaxAndValue(80);
                    e.blob.species = Defs.species_golem;
                    return SideEffect.none;
                });

        // Puffshroom
        // Lord Tyson
        // Courrier Grog
        // Nizhaad Windwalker
        // Condylure of the Star Nose
        events.unit.addEventHandler(Labels.unit_condylure_of_the_star_nose, Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This Brownie is blind, but traverses the subterranean world with the aid of his nose";
                    e.blob.setModelInstance(view.av, "condylure");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_build_healing_fountain,
                            Labels.ability_dig_mine);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_night_vision,
                            Labels.ability_mine_gems);
                    e.blob.glyphs.set(Glyph.HEALING, Glyph.MINING);
                    e.blob.combat.health.setMaxAndValue(40);
                    e.blob.species = Defs.species_brownie;
                    return SideEffect.none;
                });

        // Huiying the Alchemist
        // Lady Daumia
        events.unit.addEventHandler(Labels.unit_lady_daumia, Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "Elven high missionary to Surgarde";
                    e.blob.setModelInstance(view.av, "daumia");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_heal_wounds,
                            Labels.ability_self_sacrifice);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_night_vision,
                            Labels.ability_life_aura);
                    e.blob.glyphs.set(Glyph.HEALING);
                    e.blob.combat.health.setMaxAndValue(40);
                    e.blob.species = Defs.species_elf;
                    return SideEffect.none;
                });

        // Zen Hito the Kappa
        // Gibrax the Everlasting
        // Passiflor
        // Frogger the Gnome
        events.unit.addEventHandler(Labels.unit_frogger_the_gnome, Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "Just a little Gnome and his frog";
                    e.blob.setModelInstance(view.av, "frog-gnome");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_heal_wounds,
                            Labels.ability_hungry_frog_magic);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_pick_flowers, Labels.ability_swim);
                    e.blob.glyphs.set(Glyph.HEALING);
                    e.blob.combat.health.setMaxAndValue(40);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_gnome;
                    return SideEffect.none;
                });

        // Teragalor
        // Stalagmus
        events.unit.addEventHandler(Labels.unit_stalagmus, Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "Enchanted waters accumulate into this Golem's bowl-shaped body";
                    e.blob.setModelInstance(view.av, "stalagmus");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_dig_mine, Labels.ability_hurl_rock);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_night_vision,
                            Labels.ability_stone_defense, Labels.ability_mine_gems, Labels.ability_mine_gold,
                            Labels.ability_subterranean_potions);
                    e.blob.glyphs.set(Glyph.MINING);
                    e.blob.combat.health.setMaxAndValue(40);
                    e.blob.species = Defs.species_golem;
                    return SideEffect.none;
                });

        // Glimmer
        // Grizzlemane the Mycoweaver
        // Magicad
        // The Druid
        events.unit.addEventHandler(Labels.unit_druid, Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "A mysterious Druid who rarely speaks";
                    e.blob.setModelInstance(view.av, "druid");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_plant_forest,
                            Labels.ability_revenge_of_the_forest);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_pick_apples,
                            Labels.ability_night_vision, Labels.ability_green_fortress);
                    e.blob.glyphs.set(Glyph.NATURE);
                    e.blob.combat.health.setMaxAndValue(40);
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
        events.unit.addEventHandler(Labels.unit_blorp_the_burning, Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "A ravenous Plasmoid with an acidic body";
                    e.blob.setModelInstance(view.av, "blob");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_slime_shot);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_acid_skin,
                            Labels.ability_liquifying_presence);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.DEFENSE);
                    e.blob.combat.health.setMaxAndValue(80);
                    e.blob.species = Defs.species_plasmoid;
                    return SideEffect.none;
                });
        // Sathra the Flame Caster
        // Dendra Ivy
        // Trina the Ettin
        // Prismar
        events.unit.addEventHandler(Labels.unit_prismar, Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.setModelInstance(view.av, Labels.asset_crystal);
                    e.blob.desc = "This Gemstone can focus light into powerful attacks";
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_fire_laser,
                            Labels.ability_collapse_mine);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_crystal_skin,
                            Labels.ability_night_vision, Labels.ability_mine_gems);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.MINING);
                    e.blob.combat.health.setMaxAndValue(40);
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
        events.unit.addEventHandler(Labels.unit_pumpkin_boy, Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "He doesn't say much, he's just a little guy";
                    e.blob.setModelInstance(view.av, "pumpkin-boy");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_plant_meadow, Labels.ability_hug);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_night_vision,
                            Labels.ability_regeneration, Labels.ability_running_through_nature,
                            Labels.ability_sacred_seeds);
                    e.blob.glyphs.set(Glyph.NATURE);
                    e.blob.combat.health.setMaxAndValue(40);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_sprite;
                    return SideEffect.none;
                });

        // Barometz
        events.unit.addEventHandler(Labels.unit_barometz, Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This sheep-like Sprite blooms with delicious fruit";
                    e.blob.setModelInstance(view.av, "barometz");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_bite);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_regeneration, Labels.ability_edible,
                            Labels.ability_deposit_seeds);
                    e.blob.glyphs.set(Glyph.NATURE);
                    e.blob.combat.health.setMaxAndValue(40);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_sprite;
                    return SideEffect.none;
                });

        // Xella the Accursed
        // Svelta Luktegress
        // Al-Fikra
        events.unit.addEventHandler(Labels.unit_al_fikra, Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This being aids the great merchant kings of Eastern Bycidia";
                    e.blob.setModelInstance(view.av, "alfikra");
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_regeneration,
                            Labels.ability_market_indicator);
                    e.blob.glyphs.set(Glyph.TRADE);
                    e.blob.combat.health.setMaxAndValue(40);
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
        events.unit.addEventHandler(Labels.unit_king_gargantos, Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "Warrior-king of the Tortoise Kingdom";
                    e.blob.setModelInstance(view.av, "gargantos");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_smash, Labels.ability_build_vault);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_shell_defense,
                            Labels.ability_market_boom, Labels.ability_swim);
                    e.blob.glyphs.set(Glyph.DEFENSE, Glyph.TRADE);
                    e.blob.combat.health.setMaxAndValue(80);
                    e.blob.hunger.setTimeToHunger(view, 10);
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
        events.ability.addEventHandler(Labels.ability_acid_skin, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Adjacent attackers take damage");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_acid_skin, Events.AttackedEvent.class,
                (GameView view, Ability receiver, Events.AttackedEvent e) -> {
                    if (e.attacker instanceof Unit) {
                        Unit target = (Unit) e.target;
                        Unit attacker = (Unit) e.attacker;
                        return Hexagons.areNeighbors(attacker.getPoint(), target.getPoint())
                                ? attacker.combat.takeDamage(view, new Damage(2), target)
                                : SideEffect.none;
                    }
                    return SideEffect.none;
                });

        // Bite
        events.ability.addEventHandler(Labels.ability_bite, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Basic attack";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_bite, Events.AbilityActivatedEvent.class,
                (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.attack(view,
                        receiver.wielder, new Damage(4), 1));

        // Build Healing Fountain
        events.ability.addEventHandler(Labels.ability_build_healing_fountain, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Constructs a healing fountain";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_build_healing_fountain, Events.AbilityActivatedEvent.class,
                (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.build(view,
                        receiver.wielder, Labels.building_healing_fountain, (Tile t) -> true));

        // Build Vault
        events.ability.addEventHandler(Labels.ability_build_vault, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Builds a vault";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_build_vault, Events.AbilityActivatedEvent.class,
                (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.build(view,
                        receiver.wielder, Labels.building_vault, (Tile t) -> true));

        // Collapse Mine
        events.ability.addEventHandler(Labels.ability_collapse_mine, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format(
                            "Target a mine occupied by an enemy unit. The unit, mine, and any adjacent enemy units all take damage.");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_collapse_mine, Events.AbilityActivatedEvent.class,
                (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                    Set<Point> mines = Lambda.filter(
                            (Point p) -> view.game.world.getTile(p)
                                    .map((Tile t) -> !t.leader.equals(receiver.wielder.getLeader()) && t.building
                                            .map((Building b) -> b.name.equals(Labels.building_mine)).orElse(false)
                                            && t.unit.isPresent())
                                    .orElse(false),
                            Hexagons.getNeighbors(receiver.wielder.getPoint(), 1));
                    return receiver.wielder.getLeader().get().select(view, mines, "No mines in range", (Point p) -> {
                        Set<Entity> targets = new HashSet<>();
                        List<SideEffect> effects = SideEffect.list();
                        targets.add(view.game.world.getTile(p).get().unit.get());
                        targets.add(view.game.world.getTile(p).get().building.get());
                        for (Point p1 : Hexagons.getNeighbors(p, 1)) {
                            Optional<Unit> u = view.game.world.getTile(p1).flatMap((Tile t) -> t.unit);
                            if (u.map((Unit u1) -> !u1.isFriendly(receiver.wielder)).orElse(false)) {
                                targets.add(u.get());
                            }
                        }
                        for (Entity t : targets) {
                            effects.add(receiver.wielder.combat.attack(view, t, new Damage(5)));
                        }
                        return SideEffect.all(effects);
                    });
                });

        // Combat Loot
        events.ability.addEventHandler(Labels.ability_combat_loot, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("+2 damage if this unit has a stored item");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_combat_loot, Events.AttackEvent.class,
                (GameView view, Ability receiver, Events.AttackEvent e) -> {
                    if (receiver.wielder.haul.hasItems()) {
                        e.dmg.base += 2;
                    }
                    return SideEffect.none;
                });

        // Crystal Skin
        events.ability.addEventHandler(Labels.ability_crystal_skin, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Extra defense");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_crystal_skin, Events.TakeDamageEvent.class,
                (GameView view, Ability receiver, Events.TakeDamageEvent e) -> AbilityLogic.defense(e, 2));

        // Deposit Seeds
        events.ability.addEventHandler(Labels.ability_deposit_seeds, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Chance to spawn a meadow when this unit moves");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_deposit_seeds, Events.UnitMovedEvent.class,
                (GameView view, Ability receiver, Events.UnitMovedEvent e) -> {
                    Point p = receiver.wielder.getPoint();
                    return view.game.world.getTile(p).map((Tile t) -> !t.building.isPresent()).orElse(false)
                            && Lambda.chance(10)
                                    ? () -> view.game.generator.building(Labels.building_meadow, p.x, p.y).spawn(view)
                                    : SideEffect.none;
                });

        // Dig Mine
        events.ability.addEventHandler(Labels.ability_dig_mine, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Digs a mine";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_dig_mine, Events.AbilityActivatedEvent.class,
                (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.build(view,
                        receiver.wielder, Labels.building_mine, (Tile t) -> t.name.equals(Labels.tile_rock)));

        // Dungeon Delve
        events.ability.addEventHandler(Labels.ability_dungeon_delve, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String
                            .format("Deals 5 damage and generates loot if targeting a tile with an active building");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_dungeon_delve, Events.AbilityActivatedEvent.class,
                (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.attackAndEffect(view,
                        receiver.wielder, new Damage(5), 1,
                        Optional.of((Point p) -> !receiver.wielder.haul.isFull() && view.game.world.getTile(p)
                                .flatMap((Tile t) -> t.building).map((Building b) -> b.isActive()).orElse(false)
                                        ? () -> receiver.wielder.haul.add(view.game.mechanics.loot.drop(view.game))
                                        : SideEffect.none)));

        // Edible
        events.ability.addEventHandler(Labels.ability_edible, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Generates food");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_edible, Events.SpawnEvent.class, (GameView view, Ability receiver,
                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true));
        events.ability.addEventHandler(Labels.ability_edible, "Tick",
                (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromTile(view,
                        receiver.wielder, Labels.item_apple, (Tile t) -> true));

        // Fire Cannon
        events.ability.addEventHandler(Labels.ability_fire_cannon, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String
                            .format("Deals 8 damage to a building (or 4 damage to a unit) up to 2 tiles away");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_fire_cannon, Events.AbilityActivatedEvent.class,
                (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.dynamicDamageAttack(
                        view, receiver.wielder, 2,
                        (Tile t) -> t.building.isPresent() && !t.unit.isPresent() ? new Damage(8) : new Damage(4)));

        // Fire Laser
        events.ability.addEventHandler(Labels.ability_fire_laser, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Damage up to 3 units in a line");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_fire_laser, Events.AbilityActivatedEvent.class,
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
        events.ability.addEventHandler(Labels.ability_green_fortress, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Extra defense on forests");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_green_fortress, Events.TakeDamageEvent.class,
                (GameView view, Ability receiver, Events.TakeDamageEvent e) -> {
                    boolean isForest = view.game.world.getTile(receiver.wielder.getPoint())
                            .flatMap((Tile t) -> t.building).map((Building b) -> b.name.equals(Labels.building_forest))
                            .orElse(false);
                    return isForest ? AbilityLogic.defense(e, 2) : SideEffect.none;
                });

        // Heal Wounds
        events.ability.addEventHandler(Labels.ability_heal_wounds, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Heals 5 damage";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_heal_wounds, Events.AbilityActivatedEvent.class, (GameView view,
                Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.healUnit(view, receiver.wielder, 5));

        // Hug
        events.ability.addEventHandler(Labels.ability_hug, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Heals the target adjacent unit for a few hit points");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_hug, Events.AbilityActivatedEvent.class, (GameView view,
                Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.healUnit(view, receiver.wielder, 2));

        // Hungry Frog Magic
        events.ability.addEventHandler(Labels.ability_hungry_frog_magic, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Consumes all stored items and heals adjacent friendly units");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_hungry_frog_magic, Events.AbilityActivatedEvent.class,
                (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                    List<SideEffect> effects = SideEffect.list(() -> receiver.wielder.haul.empty());
                    Set<Point> targets = Hexagons.getNeighbors(receiver.wielder.getPoint(), 1);
                    for (Point p : targets) {
                        Optional<Unit> u = view.game.world.getTile(p).flatMap((Tile t) -> t.unit);
                        if (u.map((Unit u1) -> u1.isFriendly(receiver.wielder)).orElse(false)) {
                            effects.add(receiver.wielder.combat.heal(view, u.get(), 10));
                        }
                    }
                    return SideEffect.all(effects);
                });

        // Hunt Fish
        events.ability.addEventHandler(Labels.ability_hunt_fish, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Harvests fish from water tiles");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_hunt_fish, Events.SpawnEvent.class,
                (GameView view, Ability receiver,
                        Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true));
        events.ability.addEventHandler(Labels.ability_hunt_fish, "Tick",
                (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromTile(view,
                        receiver.wielder, Labels.item_fish, (Tile t) -> t.name.equals(Labels.tile_water)));

        // Hurl Rock
        events.ability.addEventHandler(Labels.ability_hurl_rock, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Ranged attack with chance to stun");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_hurl_rock, Events.AbilityActivatedEvent.class,
                (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.attackAndEffect(view,
                        receiver.wielder, new Damage(4), 2, Optional.of((Point p) -> {
                            Optional<Unit> u = view.game.world.getTile(p).flatMap((Tile t) -> t.unit);
                            return u.isPresent()
                                    ? u.get().abilities.addStatusEffect(view, Labels.status_effect_stunned)
                                    : SideEffect.none;
                        })));

        // Life Aura
        events.ability.addEventHandler(Labels.ability_life_aura, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Generates 4 unit points per turn");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_life_aura, Events.SpawnEvent.class,
                (GameView view, Ability receiver,
                        Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 1, true));
        events.ability.addEventHandler(Labels.ability_life_aura, "Tick",
                (GameView view, Ability receiver, Events.RepeatedEvent e) -> () -> receiver.wielder.getLeader()
                        .ifPresent((Player p) -> p.addUnitPoints(view, receiver.wielder.getPoint(), 4)));

        // Liquifying Presence
        events.ability.addEventHandler(Labels.ability_liquifying_presence, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Deals 3 damage each turn to an occupied passive building");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_liquifying_presence, Events.SpawnEvent.class,
                (GameView view, Ability receiver,
                        Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 1, true));
        events.ability.addEventHandler(Labels.ability_liquifying_presence, "Tick",
                (GameView view, Ability receiver, Events.RepeatedEvent e) -> {
                    Optional<Building> b = view.game.world.getTile(receiver.wielder.getPoint())
                            .flatMap((Tile t) -> t.building);
                    return b.map((Building b1) -> !b1.isActive()).orElse(false)
                            ? b.get().combat.takeDamage(view, new Damage(3), receiver.wielder)
                            : SideEffect.none;
                });

        // Local Defender
        events.ability.addEventHandler(Labels.ability_local_defender, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Adjacent buildings have +3 armor");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_local_defender, Events.SpawnEvent.class,
                (GameView view, Ability receiver, Events.SpawnEvent e) -> () -> view.game.events.signals
                        .addListener(Events.AttackedEvent.class, receiver));
        events.ability.addEventHandler(Labels.ability_local_defender, Events.AttackedEvent.class,
                (GameView view, Ability receiver, Events.AttackedEvent e) -> {
                    if (e.target.isEntityType(EntityType.BUILDING)
                            && receiver.wielder.getLeader().equals(e.target.getLeader())
                            && Hexagons.areNeighbors(receiver.wielder.getPoint(), e.target.getPoint())) {
                        e.dmg.base -= 3;
                    }
                    return SideEffect.none;
                });

        // Market Boom
        events.ability.addEventHandler(Labels.ability_market_boom, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Attacks generate 5 auction points");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_market_boom, Events.AttackEvent.class,
                (GameView view, Ability receiver, Events.AttackEvent e) -> AbilityLogic.generateAuctionPoints(view,
                        receiver.wielder, 5));

        // Market Indicator
        events.ability.addEventHandler(Labels.ability_market_indicator, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Generates 1 auction point when adjacent to a vault");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_market_indicator, Events.SpawnEvent.class,
                (GameView view, Ability receiver,
                        Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 1, true));
        events.ability.addEventHandler(Labels.ability_market_indicator, "Tick",
                (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.doWhenAdjacent(view,
                        receiver.wielder,
                        (Tile t) -> t.building.map((Building b) -> b.name.equals(Labels.building_vault)).orElse(false),
                        () -> AbilityLogic.generateAuctionPoints(view, receiver.wielder, 1)));

        // Mine Gems
        events.ability.addEventHandler(Labels.ability_mine_gems, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Harvests gems from mines");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_mine_gems, Events.SpawnEvent.class,
                (GameView view, Ability receiver,
                        Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true));
        events.ability.addEventHandler(Labels.ability_mine_gems, "Tick",
                (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(view,
                        receiver.wielder, Labels.item_emerald, (Building b) -> b.name.equals(Labels.building_mine)));

        // Mine Gold
        events.ability.addEventHandler(Labels.ability_mine_gold, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Harvests gold coins from mines every 4 turns";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_mine_gold, Events.SpawnEvent.class,
                (GameView view, Ability receiver,
                        Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true));
        events.ability.addEventHandler(Labels.ability_mine_gold, "Tick",
                (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(view,
                        receiver.wielder, Labels.item_gold_coin, (Building b) -> b.name.equals(Labels.building_mine)));

        // Mountain Strider
        events.ability.addEventHandler(Labels.ability_mountain_strider, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("This unit can traverse mountains");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_mountain_strider, Events.CanUnitMoveEvent.class,
                (GameView view, Ability receiver, Events.CanUnitMoveEvent e) -> {
                    if (e.tile.building.map((Building b) -> b.name.equals(Labels.building_mountain)).orElse(false)) {
                        e.canWalkOnBuilding = true;
                    }
                    return SideEffect.none;
                });

        // Night Vision
        events.ability.addEventHandler(Labels.ability_night_vision, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("This unit can see normally at night");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_night_vision, Events.GetVisionEvent.class,
                (GameView view, Ability receiver, Events.GetVisionEvent e) -> {
                    e.canSeeAtNight = true;
                    return SideEffect.none;
                });

        // Pick Apples
        events.ability.addEventHandler(Labels.ability_pick_apples, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Harvests apples from forests every 4 turns";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_pick_apples, Events.SpawnEvent.class,
                (GameView view, Ability receiver,
                        Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true));
        events.ability.addEventHandler(Labels.ability_pick_apples, "Tick",
                (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(view,
                        receiver.wielder, Labels.item_apple, (Building b) -> b.name.equals(Labels.building_forest)));

        // Pick Flowers
        events.ability.addEventHandler(Labels.ability_pick_flowers, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Harvests flowers from meadows every 4 turns");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_pick_flowers, Events.SpawnEvent.class,
                (GameView view, Ability receiver,
                        Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true));
        events.ability.addEventHandler(Labels.ability_pick_flowers, "Tick",
                (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(view,
                        receiver.wielder, Labels.item_flower, (Building b) -> b.name.equals(Labels.building_meadow)));

        // Plant Forest
        events.ability.addEventHandler(Labels.ability_plant_forest, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Plants a forest";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_plant_forest, Events.AbilityActivatedEvent.class,
                (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.build(view,
                        receiver.wielder, Labels.building_forest, (Tile t) -> t.name.equals(Labels.tile_grass)));

        // Plant Meadow
        events.ability.addEventHandler(Labels.ability_plant_meadow, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Plants a meadow";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_plant_meadow, Events.AbilityActivatedEvent.class,
                (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.build(view,
                        receiver.wielder, Labels.building_meadow, (Tile t) -> t.name.equals(Labels.tile_grass)));

        // Plate Mail
        events.ability.addEventHandler(Labels.ability_plate_mail, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Extra defense");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_plate_mail, Events.TakeDamageEvent.class,
                (GameView view, Ability receiver, Events.TakeDamageEvent e) -> AbilityLogic.defense(e, 2));

        // Regeneration
        events.ability.addEventHandler(Labels.ability_regeneration, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("This unit heals a little each turn");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_regeneration, Events.SpawnEvent.class,
                (GameView view, Ability receiver,
                        Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 1, true));
        events.ability.addEventHandler(Labels.ability_regeneration, "Tick",
                (GameView view, Ability receiver, Events.RepeatedEvent e) -> receiver.wielder.combat.heal(view, 1));

        // Revenge of the Forest
        events.ability.addEventHandler(Labels.ability_revenge_of_the_forest, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Attack that deals more damage when on a forest");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_revenge_of_the_forest, Events.AbilityActivatedEvent.class,
                (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.dynamicDamageAttack(
                        view, receiver.wielder, 1,
                        (Tile t) -> new Damage(
                                t.building.map((Building b) -> b.name.equals(Labels.building_forest)).orElse(false)
                                        ? 8
                                        : 4)));

        // Running Through Nature
        events.ability.addEventHandler(Labels.ability_running_through_nature, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("This unit is faster on passive buildings");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_running_through_nature, Events.UnitMoveDistanceEvent.class,
                (GameView view, Ability receiver, Events.UnitMoveDistanceEvent e) -> {
                    boolean buildingIsPassive = view.game.world.getTile(e.unit.getPoint())
                            .flatMap((Tile t) -> t.building).map((Building b) -> !b.isActive()).orElse(false);
                    if (buildingIsPassive) {
                        e.distance++;
                    }
                    return SideEffect.none;
                });

        // Self Sacrifice
        events.ability.addEventHandler(Labels.ability_self_sacrifice, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Transfers all their health but 1 to the target unit");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_self_sacrifice, Events.AbilityActivatedEvent.class,
                (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                    final int hitPoints = receiver.wielder.combat.health.get() - 1;
                    return SideEffect.all(AbilityLogic.healUnit(view, receiver.wielder, hitPoints),
                            () -> receiver.wielder.combat.health.set(1));
                });

        // Sacred Seeds
        events.ability.addEventHandler(Labels.ability_sacred_seeds, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Harvests seeds from meadows that can be consumed to generate favor");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_sacred_seeds, Events.SpawnEvent.class,
                (GameView view, Ability receiver,
                        Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true));
        events.ability.addEventHandler(Labels.ability_sacred_seeds, "Tick",
                (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(view,
                        receiver.wielder, Labels.item_sacred_seed,
                        (Building b) -> b.name.equals(Labels.building_meadow)));

        // Shell Defense
        events.ability.addEventHandler(Labels.ability_shell_defense, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Extra defense");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_shell_defense, Events.TakeDamageEvent.class,
                (GameView view, Ability receiver, Events.TakeDamageEvent e) -> AbilityLogic.defense(e, 2));

        // Slime Shot
        events.ability.addEventHandler(Labels.ability_slime_shot, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Ranged attack");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_slime_shot, Events.AbilityActivatedEvent.class,
                (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.attack(view,
                        receiver.wielder, new Damage(4), 3));

        // Smash
        events.ability.addEventHandler(Labels.ability_smash, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Attack with a chance to stun");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_smash, Events.AbilityActivatedEvent.class,
                (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.attackAndEffect(view,
                        receiver.wielder, new Damage(5), 1, Optional.of((Point p) -> {
                            Optional<Unit> u = view.game.world.getTile(p).flatMap((Tile t) -> t.unit);
                            return u.isPresent()
                                    ? u.get().abilities.addStatusEffect(view, Labels.status_effect_stunned)
                                    : SideEffect.none;
                        })));

        // Stone Defense
        events.ability.addEventHandler(Labels.ability_stone_defense, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Extra defense");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_stone_defense, Events.TakeDamageEvent.class,
                (GameView view, Ability receiver, Events.TakeDamageEvent e) -> AbilityLogic.defense(e, 2));

        // Subterranean Potions
        events.ability.addEventHandler(Labels.ability_subterranean_potions, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Generates Health Potions from Mines");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_subterranean_potions, Events.SpawnEvent.class,
                (GameView view, Ability receiver,
                        Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true));
        events.ability.addEventHandler(Labels.ability_subterranean_potions, "Tick",
                (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(view,
                        receiver.wielder, Labels.item_health_potion,
                        (Building b) -> b.name.equals(Labels.building_mine)));

        // Swim
        events.ability.addEventHandler(Labels.ability_swim, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "This unit can swim on water tiles";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_swim, Events.CanUnitMoveEvent.class,
                (GameView view, Ability receiver, Events.CanUnitMoveEvent e) -> {
                    if (!e.canWalkOnTile && e.tile.name.equals(Labels.tile_water)) {
                        e.canWalkOnTile = true;
                    }
                    return SideEffect.none;
                });

        // Sword Slash
        events.ability.addEventHandler(Labels.ability_sword_slash, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    Damage dmg = new Damage(5);
                    e.blob.desc = String.format("Deals %s", dmg);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.ability_sword_slash, Events.AbilityActivatedEvent.class,
                (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.attack(view,
                        receiver.wielder, new Damage(5), 1));

        /**
         * SECTION Status Effects
         */

        // Stunned
        events.ability.addEventHandler(Labels.status_effect_stunned, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("The unit cannot act for 1 turn");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.status_effect_stunned, Events.StatusEffectAddedEvent.class,
                (GameView view, Ability receiver, Events.StatusEffectAddedEvent e) -> {
                    view.game.future.addFutureTick("Tick", receiver, 1, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.status_effect_stunned, "Tick", (GameView view, Ability receiver,
                Events.RepeatedEvent e) -> () -> receiver.wielder.abilities.removeStatusEffect(receiver));
        events.ability.addEventHandler(Labels.status_effect_stunned, Events.IsStunnedEvent.class,
                (GameView view, Ability receiver, Events.IsStunnedEvent e) -> {
                    e.isStunned = true;
                    return SideEffect.none;
                });

        // More Favor
        events.ability.addEventHandler(Labels.status_effect_more_favor, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("+5 favor next time the unit generates it");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.status_effect_more_favor, Events.GenerateFavorEvent.class,
                (GameView view, Ability receiver, Events.GenerateFavorEvent e) -> {
                    receiver.wielder.abilities.removeStatusEffect(receiver);
                    e.favor += 5;
                    return SideEffect.none;
                });

        // Proud Builder
        events.ability.addEventHandler(Labels.status_effect_proud_builder, Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("+3 attack and defense for 2 turns");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.status_effect_proud_builder, Events.StatusEffectAddedEvent.class,
                (GameView view, Ability receiver, Events.StatusEffectAddedEvent e) -> {
                    view.game.future.addFutureTick("Tick", receiver, 2, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.status_effect_proud_builder, "Tick", (GameView view, Ability receiver,
                Events.RepeatedEvent e) -> () -> receiver.wielder.abilities.removeStatusEffect(receiver));
        events.ability.addEventHandler(Labels.status_effect_proud_builder, Events.GenerateFavorEvent.class,
                (GameView view, Ability receiver, Events.GenerateFavorEvent e) -> {
                    e.favor += 5;
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.status_effect_proud_builder, Events.TakeDamageEvent.class,
                (GameView view, Ability receiver, Events.TakeDamageEvent e) -> {
                    e.dmg.base -= 3;
                    return SideEffect.none;
                });
        events.ability.addEventHandler(Labels.status_effect_proud_builder, Events.AttackEvent.class,
                (GameView view, Ability receiver, Events.AttackEvent e) -> {
                    e.dmg.base += 3;
                    return SideEffect.none;
                });

        /**
         * SECTION Items
         */

        // Seeds
        events.item.addEventHandler(Labels.item_sacred_seed, Events.GenerateItemEvent.class,
                (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to generate extra favor";
                    e.blob.icon = Optional.of(Labels.asset_seeds);
                    e.blob.gold = 1;
                    return SideEffect.none;
                });
        events.item.addEventHandler(Labels.item_sacred_seed, Events.ItemConsumedEvent.class,
                (GameView view, Item receiver, Events.ItemConsumedEvent e) -> {
                    return () -> e.consumer.abilities.addStatusEffect(view, Labels.status_effect_more_favor);
                });

        // Flower
        events.item.addEventHandler(Labels.item_flower, Events.GenerateItemEvent.class,
                (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to smell a sweet flower";
                    e.blob.icon = Optional.of(Labels.asset_flower);
                    e.blob.gold = 1;
                    return SideEffect.none;
                });
        events.item.addEventHandler(Labels.item_flower, Events.ItemConsumedEvent.class,
                (GameView view, Item receiver, Events.ItemConsumedEvent e) -> SideEffect.none);

        // Fish
        events.item.addEventHandler(Labels.item_fish, Events.GenerateItemEvent.class,
                (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to stave off hunger";
                    e.blob.icon = Optional.of(Labels.asset_fish);
                    e.blob.gold = 1;
                    return SideEffect.none;
                });
        events.item.addEventHandler(Labels.item_fish, Events.ItemConsumedEvent.class,
                (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.food(view, e));

        // Gold Coin
        events.item.addEventHandler(Labels.item_gold_coin, Events.GenerateItemEvent.class,
                (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to increase your gold";
                    e.blob.icon = Optional.of(Labels.asset_coin);
                    e.blob.gold = 1;
                    return SideEffect.none;
                });
        events.item.addEventHandler(Labels.item_gold_coin, Events.ItemConsumedEvent.class,
                (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.valuable(e));

        // Emerald
        events.item.addEventHandler(Labels.item_emerald, Events.GenerateItemEvent.class,
                (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to increase your gold";
                    e.blob.icon = Optional.of(Labels.asset_crystal);
                    e.blob.gold = 10;
                    return SideEffect.none;
                });
        events.item.addEventHandler(Labels.item_emerald, Events.ItemConsumedEvent.class,
                (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.valuable(e));

        // Apple
        events.item.addEventHandler(Labels.item_apple, Events.GenerateItemEvent.class,
                (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to stave off hunger";
                    e.blob.icon = Optional.of(Labels.asset_apple);
                    e.blob.gold = 1;
                    e.blob.tags.add(Labels.tag_natural).add(Labels.tag_fruit);
                    // TODO eating the apple is broken (does not reset hunger bar)
                    return SideEffect.none;
                });
        events.item.addEventHandler(Labels.item_apple, Events.ItemConsumedEvent.class,
                (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.food(view, e));

        // Health Potion
        events.item.addEventHandler(Labels.item_health_potion, Events.GenerateItemEvent.class,
                (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to heal by 10 hit points";
                    e.blob.icon = Optional.of(Labels.asset_potion);
                    e.blob.gold = 1;
                    return SideEffect.none;
                });
        events.item.addEventHandler(Labels.item_health_potion, Events.ItemConsumedEvent.class,
                (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.potion(view, e, 10));

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
