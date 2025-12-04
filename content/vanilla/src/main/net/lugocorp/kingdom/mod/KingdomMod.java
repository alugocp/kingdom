package net.lugocorp.kingdom.mod;
import net.lugocorp.kingdom.ai.goals.AttackEnemy;
import net.lugocorp.kingdom.ai.goals.MineGold;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.builtin.logic.AbilityLogic;
import net.lugocorp.kingdom.builtin.logic.ItemLogic;
import net.lugocorp.kingdom.builtin.logic.UnitLogic;
import net.lugocorp.kingdom.engine.assets.SpriteLoader;
import net.lugocorp.kingdom.game.actions.ActionType;
import net.lugocorp.kingdom.game.combat.Damage;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.Stratified;
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
import net.lugocorp.kingdom.game.properties.Rarity;
import net.lugocorp.kingdom.math.HexSide;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.menu.game.ArtifactNode;
import net.lugocorp.kingdom.menu.game.FateNode;
import net.lugocorp.kingdom.menu.game.InventoryNode;
import net.lugocorp.kingdom.menu.icon.ActionNode;
import net.lugocorp.kingdom.mod.common.Defs;
import net.lugocorp.kingdom.mod.common.Labels;
import net.lugocorp.kingdom.mods.GameMod;
import net.lugocorp.kingdom.mods.ModProfile;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import net.lugocorp.kingdom.utils.Semver;
import net.lugocorp.kingdom.utils.SideEffect;
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
        sprites.register(Labels.asset_placeholder, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 0, 0);
        sprites.register(Labels.asset_potion, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 1, 0);
        sprites.register(Labels.asset_apple, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 2, 0);
        sprites.register(Labels.asset_pouch, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 3, 0);
        sprites.register(Labels.asset_stone, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 4, 0);
        sprites.register(Labels.asset_staff, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 5, 0);
        sprites.register(Labels.asset_beads, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 6, 0);
        sprites.register(Labels.asset_chestplate, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 7, 0);
        sprites.register(Labels.asset_coin, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 0, 1);
        sprites.register(Labels.asset_sword, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 1, 1);
        sprites.register(Labels.asset_shield, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 2, 1);
        sprites.register(Labels.asset_candle, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 3, 1);
        sprites.register(Labels.asset_ring, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 4, 1);
        sprites.register(Labels.asset_robe, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 5, 1);
        sprites.register(Labels.asset_wizard_hat, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 6, 1);
        sprites.register(Labels.asset_pendant, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 7, 1);
        sprites.register(Labels.asset_mushroom, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 0, 2);
        sprites.register(Labels.asset_crystal, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 1, 2);
        sprites.register(Labels.asset_bone, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 2, 2);
        sprites.register(Labels.asset_fish, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 3, 2);
        sprites.register(Labels.asset_pants, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 4, 2);
        sprites.register(Labels.asset_boots, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 5, 2);
        sprites.register(Labels.asset_glove, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 6, 2);
        sprites.register(Labels.asset_belt, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 7, 2);
        sprites.register(Labels.asset_flower, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 0, 3);
        sprites.register(Labels.asset_seeds, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 1, 3);
        sprites.register(Labels.asset_paper, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 2, 3);
        sprites.register(Labels.asset_helmet, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 3, 3);
        sprites.register(Labels.asset_slime, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 4, 3);
        sprites.register(Labels.asset_feather, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 5, 3);
        sprites.register(Labels.asset_rattle, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 6, 3);
        sprites.register(Labels.asset_powder, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 7, 3);
        sprites.register(Labels.asset_spear, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 0, 4);
        sprites.register(Labels.asset_hammer, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 1, 4);
        sprites.register(Labels.asset_doll, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 2, 4);
        sprites.register(Labels.asset_mace, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 3, 4);
        sprites.register(Labels.asset_carving, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 4, 4);
        sprites.register(Labels.asset_slingshot, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 5, 4);
        sprites.register(Labels.asset_net, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 6, 4);
        sprites.register(Labels.asset_axe, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 7, 4);
        sprites.register(Labels.asset_brooch, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 0, 5);
        sprites.register(Labels.asset_dagger, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 1, 5);
        sprites.register(Labels.asset_tankard, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 2, 5);
        sprites.register(Labels.asset_book, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 3, 5);
        sprites.register(Labels.asset_club, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 4, 5);
        sprites.register(Labels.asset_vase, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 5, 5);
        sprites.register(Labels.asset_wand, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 6, 5);
        sprites.register(Labels.asset_pickaxe, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 7, 5);
        sprites.register(Labels.asset_bow, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 0, 6);
        sprites.register(Labels.asset_rune, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 1, 6);
        sprites.register(Labels.asset_shovel, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 2, 6);
        sprites.register(Labels.asset_telescope, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 3, 6);
        sprites.register(Labels.asset_scales, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 4, 6);
        sprites.register(Labels.asset_eye, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 5, 6);

        // Ability sprites
        sprites.register(Labels.asset_bite, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 0, 0);
        sprites.register(Labels.asset_build_healing_fountain, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE,
                1, 0);
        sprites.register(Labels.asset_build_vault, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 2, 0);
        sprites.register(Labels.asset_collapse_mine, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 3, 0);
        sprites.register(Labels.asset_combat_loot, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 4, 0);
        sprites.register(Labels.asset_acid_skin, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 5, 0);
        sprites.register(Labels.asset_green_fortress, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 6, 0);
        sprites.register(Labels.asset_deposit_seeds, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 7, 0);
        sprites.register(Labels.asset_dig_mine, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 0, 1);
        sprites.register(Labels.asset_dungeon_delve, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 1, 1);
        sprites.register(Labels.asset_edible, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 2, 1);
        sprites.register(Labels.asset_fire_cannon, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 3, 1);
        sprites.register(Labels.asset_fire_laser, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 4, 1);
        sprites.register(Labels.asset_defense, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 5, 1);
        sprites.register(Labels.asset_heal_wounds, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 6, 1);
        sprites.register(Labels.asset_hug, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 7, 1);
        sprites.register(Labels.asset_hungry_frog_magic, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 0,
                2);
        sprites.register(Labels.asset_hunt_fish, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 1, 2);
        sprites.register(Labels.asset_life_aura, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 2, 2);
        sprites.register(Labels.asset_liquifying_presence, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 3,
                2);
        sprites.register(Labels.asset_local_defender, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 4, 2);
        sprites.register(Labels.asset_market_boom, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 5, 2);
        sprites.register(Labels.asset_market_indicator, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 6, 2);
        sprites.register(Labels.asset_mine_gems, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 7, 2);
        sprites.register(Labels.asset_mine_gold, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 0, 3);
        sprites.register(Labels.asset_mountain_strider, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 1, 3);
        sprites.register(Labels.asset_night_vision, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 2, 3);
        sprites.register(Labels.asset_pick_apples, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 3, 3);
        sprites.register(Labels.asset_pick_flowers, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 4, 3);
        sprites.register(Labels.asset_plant_forest, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 5, 3);
        sprites.register(Labels.asset_plant_meadow, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 6, 3);
        sprites.register(Labels.asset_regeneration, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 7, 3);
        sprites.register(Labels.asset_running_through_nature, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE,
                0, 4);
        sprites.register(Labels.asset_self_sacrifice, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 1, 4);
        sprites.register(Labels.asset_slime_shot, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 2, 4);
        sprites.register(Labels.asset_smash, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 3, 4);
        sprites.register(Labels.asset_subterranean_potions, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 4,
                4);
        sprites.register(Labels.asset_swim, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 5, 4);
        sprites.register(Labels.asset_sword_slash, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 6, 4);
        sprites.register(Labels.asset_drown, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 7, 4);
        sprites.register(Labels.asset_rising_spirit, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 0, 5);
        sprites.register(Labels.asset_battle_glyph, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 1, 5);
        sprites.register(Labels.asset_bloodlust, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 2, 5);
        sprites.register(Labels.asset_mining_glyph, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 3, 5);
        sprites.register(Labels.asset_extra_gem, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 4, 5);
        sprites.register(Labels.asset_nature_glyph, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 5, 5);
        sprites.register(Labels.asset_extra_fruit, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 6, 5);
        sprites.register(Labels.asset_bash, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 7, 5);
        sprites.register(Labels.asset_golden_spear, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 0, 6);
        sprites.register(Labels.asset_slime_armor, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 1, 6);
        sprites.register(Labels.asset_defensive_blossom, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 2,
                6);
        sprites.register(Labels.asset_economic_activity, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 3,
                6);
        sprites.register(Labels.asset_stomach, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 4, 6);
        sprites.register(Labels.asset_fireball, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 5, 6);
        sprites.register(Labels.asset_raise_undead, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 6, 6);
        sprites.register(Labels.asset_gilded_strike, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 7, 6);
        sprites.register(Labels.asset_harvest_slime, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 0, 7);
        sprites.register(Labels.asset_harvest_mushroom, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 1, 7);
        sprites.register(Labels.asset_gems, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 2, 7);
        sprites.register(Labels.asset_market_value_goo, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 3, 7);
        sprites.register(Labels.asset_eat, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 4, 7);
        sprites.register(Labels.asset_worship_glyph, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 5, 7);
        sprites.register(Labels.asset_spores, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 6, 7);
        sprites.register(Labels.asset_stomp, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 7, 7);
        sprites.register(Labels.asset_axe_swing, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 0, 8);
        sprites.register(Labels.asset_thorny_skin, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 1, 8);
        sprites.register(Labels.asset_trade, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 2, 8);
        sprites.register(Labels.asset_stunned, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 3, 8);
        sprites.register(Labels.asset_proud_builder, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 4, 8);
        sprites.register(Labels.asset_poisoned, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 5, 8);
        sprites.register(Labels.asset_swift, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 6, 8);

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
            // TODO receiver.getLeader().get().isHumanPlayer() is a stop-gap that should be
            // removed before the beta release
            if (receiver.getLeader().isPresent() && receiver.getLeader().get().isHumanPlayer()
                    && !receiver.hunger.autoEatCheck(view)) {
                return () -> receiver.loyalty.decrease(view, 1);
            }
            ((Events.RepeatedEvent) event).repeat = false;
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
        new Stratified<Tile>(events.tile, Labels.tile_grass).add(Events.GenerateTileEvent.class,
                (GameView view, Tile receiver, Events.GenerateTileEvent e) -> {
                    e.blob.setModelInstance(view.av, Labels.asset_grass);
                    e.blob.setMinimapColor(0x2c9965);
                    e.blob.desc = "The seeds to spring new life lay dormant beneath this place";
                    return SideEffect.none;
                });

        // Rock
        new Stratified<Tile>(events.tile, Labels.tile_rock).add(Events.GenerateTileEvent.class,
                (GameView view, Tile receiver, Events.GenerateTileEvent e) -> {
                    e.blob.setModelInstance(view.av, Labels.asset_grass);
                    e.blob.setMinimapColor(0x666666);
                    e.blob.setMaterial(Labels.asset_rock);
                    e.blob.desc = "The rocky mountainscape is home to many creatures";
                    return SideEffect.none;
                });

        // Sand
        new Stratified<Tile>(events.tile, Labels.tile_sand).add(Events.GenerateTileEvent.class,
                (GameView view, Tile receiver, Events.GenerateTileEvent e) -> {
                    e.blob.setModelInstance(view.av, Labels.asset_grass);
                    e.blob.setMinimapColor(0xc7c567);
                    e.blob.setMaterial(Labels.asset_sand);
                    e.blob.desc = "The hot sands seem to stretch on forever";
                    return SideEffect.none;
                });

        // Snow
        new Stratified<Tile>(events.tile, Labels.tile_snow).add(Events.GenerateTileEvent.class,
                (GameView view, Tile receiver, Events.GenerateTileEvent e) -> {
                    e.blob.setModelInstance(view.av, Labels.asset_grass);
                    e.blob.setMinimapColor(0xffffff);
                    e.blob.setMaterial(Labels.asset_snow);
                    e.blob.desc = "Dense and cold";
                    return SideEffect.none;
                });

        // Water
        new Stratified<Tile>(events.tile, Labels.tile_water).add(Events.GenerateTileEvent.class,
                (GameView view, Tile receiver, Events.GenerateTileEvent e) -> {
                    e.blob.setModelInstance(view.av, Labels.asset_water);
                    e.blob.setMinimapColor(0x20c7f7);
                    e.blob.setObstacle(true);
                    e.blob.setWave(true);
                    e.blob.desc = "Only certain units can swim";
                    return SideEffect.none;
                });

        // Lava
        new Stratified<Tile>(events.tile, Labels.tile_lava).add(Events.GenerateTileEvent.class,
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
        new Stratified<Building>(events.building, Labels.building_mine).add(Events.GenerateBuildingEvent.class,
                (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance(view.av, "mine");
                    e.blob.desc = "Units with mining abilities can generate gold or items when they occupy this building";
                    e.blob.combat.health.setMaxAndValue(35);
                    e.blob.setMinimapColor(0x555555);
                    e.blob.setActive();
                    return SideEffect.none;
                });

        // Vault
        new Stratified<Building>(events.building, Labels.building_vault).add(Events.GenerateBuildingEvent.class,
                (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance(view.av, "vault");
                    e.blob.desc = "This building can store items and its contents be wagered in auctions";
                    e.blob.items = Optional.of(new Inventory(InventoryType.BUILDING, 24));
                    e.blob.combat.health.setMaxAndValue(45);
                    e.blob.setMinimapColor(0x000000);
                    e.blob.setActive();
                    return SideEffect.none;
                });

        // Forest
        new Stratified<Building>(events.building, Labels.building_forest).add(Events.GenerateBuildingEvent.class,
                (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance(view.av, "forest");
                    e.blob.desc = "Units with harvest abilities can generate food when they occupy this building";
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.setMinimapColor(0x257d53);
                    return SideEffect.none;
                });

        // Taiga
        new Stratified<Building>(events.building, Labels.building_taiga).add(Events.GenerateBuildingEvent.class,
                (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance(view.av, "forest");
                    e.blob.desc = "Units with harvest abilities can generate food when they occupy this building";
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.setMinimapColor(0xb4c3c7);
                    e.blob.setMaterial(Labels.asset_taiga);
                    return SideEffect.none;
                });

        // Meadow
        new Stratified<Building>(events.building, Labels.building_meadow).add(Events.GenerateBuildingEvent.class,
                (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance(view.av, "meadow");
                    e.blob.desc = "Units with harvest abilities can generate items when they occupy this building";
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.setMinimapColor(0x4dd349);
                    return SideEffect.none;
                });

        // Oasis
        new Stratified<Building>(events.building, Labels.building_oasis).add(Events.GenerateBuildingEvent.class,
                (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance(view.av, "oasis");
                    e.blob.desc = "Units with harvest abilities can generate food when they occupy this building";
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.setMinimapColor(0x2c9965);
                    return SideEffect.none;
                });

        // Shrubland
        new Stratified<Building>(events.building, Labels.building_shrubland).add(Events.GenerateBuildingEvent.class,
                (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance(view.av, "shrubland");
                    e.blob.desc = "Units with harvest abilities can generate items when they occupy this building";
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.setMinimapColor(0x4dd349);
                    return SideEffect.none;
                });

        // Mountain
        new Stratified<Building>(events.building, Labels.building_mountain).add(Events.GenerateBuildingEvent.class,
                (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance(view.av, "mountain");
                    e.blob.desc = "Most units cannot traverse mountains";
                    e.blob.combat.health.invulnerable();
                    e.blob.setMinimapColor(0x875f9a);
                    e.blob.setObstacle(true);
                    return SideEffect.none;
                });

        // Healing Fountain
        new Stratified<Building>(events.building, Labels.building_healing_fountain)
                .add(Events.GenerateBuildingEvent.class,
                        (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                            e.blob.setModelInstance(view.av, "fountain");
                            e.blob.desc = "A unit that occupies this building gets healed a little each turn";
                            e.blob.combat.health.setMaxAndValue(35);
                            e.blob.setMinimapColor(0x875f9a);
                            e.blob.setActive();
                            return SideEffect.none;
                        })
                .add(Events.SpawnEvent.class,
                        (GameView view, Building receiver,
                                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 1, true))
                .add("Tick", (GameView view, Building receiver, Events.RepeatedEvent e) -> {
                    Optional<Unit> u = view.game.world.getTile(receiver.getPoint()).flatMap((Tile t) -> t.unit);
                    return u.isPresent() ? receiver.combat.heal(view, u.get(), 5) : SideEffect.none;
                });

        // Market Value Goo
        new Stratified<Building>(events.building, Labels.building_market_value_goo)
                .add(Events.GenerateBuildingEvent.class,
                        (GameView view, Building receiver, Events.GenerateBuildingEvent e) -> {
                            e.blob.setModelInstance(view.av, "fountain"); // TODO new asset
                            e.blob.desc = "This goo generates 3 auction points each turn for 2 turns";
                            e.blob.combat.health.setMaxAndValue(5);
                            e.blob.setMinimapColor(0x875f9a);
                            return SideEffect.none;
                        })
                .add(Events.SpawnEvent.class, (GameView view, Building receiver, Events.SpawnEvent e) -> () -> {
                    view.game.future.addFutureTick("Tick", receiver, 1, true);
                    view.game.future.addFutureTick("Remove", receiver, 2, false);
                }).add("Tick", (GameView view, Building receiver, Events.RepeatedEvent e) -> {
                    return () -> view.game.mechanics.auction.addPoints(view, receiver.getPoint(), 3);
                }).add("Remove", (GameView view, Building receiver, Events.RepeatedEvent e) -> {
                    return SideEffect.all(() -> view.game.future.removeFutureEvents(receiver, "Tick"),
                            receiver.combat.takeDamage(view, new Damage(receiver.combat.health.get()), receiver));
                });

        /**
         * SECTION Patrons
         */

        // Joyous Reaper
        new Stratified<Patron>(events.patron, Labels.patron_joyous_reaper).add(Events.GeneratePatronEvent.class,
                (GameView view, Patron receiver, Events.GeneratePatronEvent e) -> {
                    e.blob.setModelInstance(view.av, "joyous-reaper");
                    e.blob.desc = "Your battle glyph units generate 5 unit points when they kill or are killed by another unit. The killed unit is reincarnated (returns as a unit recruitment option).";
                    e.blob.preference = "Battle glyph units";
                    e.blob.isPreferredUnitType = (Unit u) -> u.glyphs.has(Glyph.BATTLE);
                    e.blob.setIcons(Labels.asset_rising_spirit, Labels.asset_battle_glyph);
                    return SideEffect.none;
                }).add(Events.SpawnEvent.class, (GameView view, Patron receiver, Events.SpawnEvent e) -> () -> {
                    view.game.events.signals.addListener(Events.EntityDiedEvent.class, receiver);
                    view.game.events.signals.addListener(Events.KilledEntityEvent.class, receiver);
                }).add(Events.EntityDiedEvent.class, (GameView view, Patron receiver, Events.EntityDiedEvent e) -> {
                    if (e.target.isEntityType(EntityType.UNIT) && ((Unit) e.target).glyphs.has(Glyph.BATTLE)
                            && e.target.getLeader().equals(receiver.getFavoritePlayer())) {
                        return () -> {
                            e.target.getLeader().get().addUnitPoints(view, receiver.getPoint(), 5);
                            view.game.mechanics.pools.reincarnate((Unit) e.target);
                        };
                    }
                    return SideEffect.none;
                }).add(Events.KilledEntityEvent.class, (GameView view, Patron receiver, Events.KilledEntityEvent e) -> {
                    if (e.killer.isEntityType(EntityType.UNIT) && ((Unit) e.killer).glyphs.has(Glyph.BATTLE)
                            && e.killer.getLeader().equals(receiver.getFavoritePlayer())
                            && e.target.isEntityType(EntityType.UNIT)) {
                        return () -> {
                            e.killer.getLeader().get().addUnitPoints(view, receiver.getPoint(), 5);
                            view.game.mechanics.pools.reincarnate((Unit) e.target);
                        };
                    }
                    return SideEffect.none;
                });

        // Great Corn Woman
        // Lord Shui, Guardian of the River
        // The Pond Troll
        new Stratified<Patron>(events.patron, Labels.patron_pond_troll).add(Events.GeneratePatronEvent.class,
                (GameView view, Patron receiver, Events.GeneratePatronEvent e) -> {
                    e.blob.setModelInstance(view.av, "pond-troll");
                    e.blob.desc = "The favorite player's units can traverse water tiles and have a 20% chance to fish when they do";
                    e.blob.preference = "Units that cannot swim";
                    e.blob.isPreferredUnitType = (Unit u) -> !u.abilities.hasPassive(Labels.ability_swim);
                    e.blob.setIcons(Labels.asset_swim, Labels.asset_drown);
                    return SideEffect.none;
                }).add(Events.SpawnEvent.class, (GameView view, Patron receiver, Events.SpawnEvent e) -> () -> {
                    view.game.events.signals.addListener(Events.CanUnitMoveEvent.class, receiver);
                    view.game.events.signals.addListener(Events.UnitMovedEvent.class, receiver);
                }).add(Events.CanUnitMoveEvent.class, (GameView view, Patron receiver, Events.CanUnitMoveEvent e) -> {
                    if (e.unit.getLeader().equals(receiver.getFavoritePlayer())
                            && e.tile.name.equals(Labels.tile_water)) {
                        e.canWalkOnTile = true;
                    }
                    return SideEffect.none;
                }).add(Events.UnitMovedEvent.class, (GameView view, Patron receiver, Events.UnitMovedEvent e) -> {
                    if (e.unit.getLeader().equals(receiver.getFavoritePlayer())
                            && view.game.world.getTile(e.current).get().name.equals(Labels.tile_water)
                            && !e.unit.haul.isFull() && Lambda.chance(20)) {
                        return () -> e.unit.haul.add(view.game.generator.item(Labels.item_fish));
                    }
                    return SideEffect.none;
                });

        // The Eternal Guardian
        // Flutterwing
        new Stratified<Patron>(events.patron, Labels.patron_flutterwing).add(Events.GeneratePatronEvent.class,
                (GameView view, Patron receiver, Events.GeneratePatronEvent e) -> {
                    e.blob.setModelInstance(view.av, "flutterwing");
                    e.blob.desc = "Your battle glyph units have +20% critical hit chance and generate 5 unit points when they kill another unit";
                    e.blob.preference = "Battle glyph units";
                    e.blob.isPreferredUnitType = (Unit u) -> u.glyphs.has(Glyph.BATTLE);
                    e.blob.setIcons(Labels.asset_bloodlust, Labels.asset_battle_glyph);
                    return SideEffect.none;
                }).add(Events.SpawnEvent.class, (GameView view, Patron receiver, Events.SpawnEvent e) -> () -> {
                    view.game.events.signals.addListener(Events.CheckCriticalHitEvent.class, receiver);
                    view.game.events.signals.addListener(Events.KilledEntityEvent.class, receiver);
                }).add(Events.CheckCriticalHitEvent.class,
                        (GameView view, Patron receiver, Events.CheckCriticalHitEvent e) -> {
                            if (e.entity.isEntityType(EntityType.UNIT) && ((Unit) e.entity).glyphs.has(Glyph.BATTLE)
                                    && e.entity.getLeader().equals(receiver.getFavoritePlayer())) {
                                e.chance += 10;
                            }
                            return SideEffect.none;
                        })
                .add(Events.KilledEntityEvent.class, (GameView view, Patron receiver, Events.KilledEntityEvent e) -> {
                    if (e.killer.isEntityType(EntityType.UNIT) && ((Unit) e.killer).glyphs.has(Glyph.BATTLE)
                            && e.killer.getLeader().equals(receiver.getFavoritePlayer())
                            && e.target.isEntityType(EntityType.UNIT)) {
                        return () -> e.killer.getLeader().get().addUnitPoints(view, receiver.getPoint(), 5);
                    }
                    return SideEffect.none;
                });

        // Wise Mountain
        new Stratified<Patron>(events.patron, Labels.patron_wise_mountain).add(Events.GeneratePatronEvent.class,
                (GameView view, Patron receiver, Events.GeneratePatronEvent e) -> {
                    e.blob.setModelInstance(view.av, "wise-mountain");
                    e.blob.desc = "Your mining glyph units' harvest abilities have a 20% chance to generate an additional item";
                    e.blob.preference = "Mining glyph units";
                    e.blob.isPreferredUnitType = (Unit u) -> u.glyphs.has(Glyph.MINING);
                    e.blob.setIcons(Labels.asset_extra_gem, Labels.asset_mining_glyph);
                    return SideEffect.none;
                }).add(Events.SpawnEvent.class, (GameView view, Patron receiver, Events.SpawnEvent e) -> () -> {
                    view.game.events.signals.addListener(Events.HarvestEvent.class, receiver);
                }).add(Events.HarvestEvent.class, (GameView view, Patron receiver, Events.HarvestEvent e) -> {
                    if (e.unit.getLeader().equals(receiver.getFavoritePlayer()) && e.unit.glyphs.has(Glyph.MINING)
                            && !e.unit.haul.isFull() && Lambda.chance(20)) {
                        return () -> e.unit.haul.add(e.item);
                    }
                    return SideEffect.none;
                });

        // Wise Oak
        new Stratified<Patron>(events.patron, Labels.patron_wise_oak).add(Events.GeneratePatronEvent.class,
                (GameView view, Patron receiver, Events.GeneratePatronEvent e) -> {
                    e.blob.setModelInstance(view.av, "wise-oak");
                    e.blob.desc = "Your nature glyph units' harvest abilities have a 20% chance to generate an additional item";
                    e.blob.preference = "Nature glyph units";
                    e.blob.isPreferredUnitType = (Unit u) -> u.glyphs.has(Glyph.NATURE);
                    e.blob.setIcons(Labels.asset_extra_fruit, Labels.asset_nature_glyph);
                    return SideEffect.none;
                }).add(Events.SpawnEvent.class, (GameView view, Patron receiver, Events.SpawnEvent e) -> () -> {
                    view.game.events.signals.addListener(Events.HarvestEvent.class, receiver);
                }).add(Events.HarvestEvent.class, (GameView view, Patron receiver, Events.HarvestEvent e) -> {
                    return () -> {
                        if (e.unit.getLeader().equals(receiver.getFavoritePlayer()) && e.unit.glyphs.has(Glyph.NATURE)
                                && !e.unit.haul.isFull() && Lambda.chance(20)) {
                            e.unit.haul.add(e.item);
                        }
                    };
                });

        // Ahn-Juné
        // The Shining Eyes
        new Stratified<Patron>(events.patron, Labels.patron_shining_eyes).add(Events.GeneratePatronEvent.class,
                (GameView view, Patron receiver, Events.GeneratePatronEvent e) -> {
                    e.blob.setModelInstance(view.av, "shining-eyes");
                    e.blob.desc = "Heals 4 random units of its favorite player each turn";
                    e.blob.preference = "Healing glyph units";
                    e.blob.isPreferredUnitType = (Unit u) -> u.glyphs.has(Glyph.HEALING);
                    e.blob.setIcons(Labels.asset_heal_wounds, Labels.asset_regeneration);
                    return SideEffect.none;
                })
                .add(Events.SpawnEvent.class,
                        (GameView view, Patron receiver,
                                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 1, true))
                .add("Tick", (GameView view, Patron receiver, Events.RepeatedEvent e) -> {
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
        new Stratified<Artifact>(events.artifact, Labels.artifact_chos_sigil_of_haste)
                .add(Events.GenerateArtifactEvent.class,
                        (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Your healing glyph units get +1 movement speed";
                            e.blob.image = Optional.of(Labels.asset_chos_sigil_of_haste);
                            return SideEffect.none;
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            view.game.events.signals.addListener(Events.UnitMoveDistanceEvent.class, e.artifact);
                            return SideEffect.none;
                        })
                .add(Events.UnitMoveDistanceEvent.class,
                        (GameView view, Artifact receiver, Events.UnitMoveDistanceEvent e) -> {
                            if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.HEALING)) {
                                e.distance++;
                            }
                            return SideEffect.none;
                        });

        // Urdin's Scroll of Agility
        new Stratified<Artifact>(events.artifact, Labels.artifact_urdins_scroll_of_agility)
                .add(Events.GenerateArtifactEvent.class,
                        (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Your defense glyph units get +1 movement speed";
                            e.blob.image = Optional.of(Labels.asset_urdins_scroll_of_agility);
                            return SideEffect.none;
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            view.game.events.signals.addListener(Events.UnitMoveDistanceEvent.class, e.artifact);
                            return SideEffect.none;
                        })
                .add(Events.UnitMoveDistanceEvent.class,
                        (GameView view, Artifact receiver, Events.UnitMoveDistanceEvent e) -> {
                            if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.DEFENSE)) {
                                e.distance++;
                            }
                            return SideEffect.none;
                        });

        // Sword of Aesethos
        new Stratified<Artifact>(events.artifact, Labels.artifact_sword_of_aesethos)
                .add(Events.GenerateArtifactEvent.class,
                        (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Your units have +10% critical hit chance";
                            e.blob.image = Optional.of(Labels.asset_sword_of_aesethos);
                            return SideEffect.none;
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            view.game.events.signals.addListener(Events.CheckCriticalHitEvent.class, e.artifact);
                            return SideEffect.none;
                        })
                .add(Events.CheckCriticalHitEvent.class,
                        (GameView view, Artifact receiver, Events.CheckCriticalHitEvent e) -> {
                            if (receiver.isClaimedByLeader(e.entity)) {
                                e.chance += 10;
                            }
                            return SideEffect.none;
                        });

        // Kauna's Amulet
        new Stratified<Artifact>(events.artifact, Labels.artifact_kaunas_amulet).add(Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your units within a patron's domain have extra defense";
                    e.blob.image = Optional.of(Labels.asset_kaunas_amulet);
                    return SideEffect.none;
                }).add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            view.game.events.signals.addListener(Events.TakeDamageEvent.class, e.artifact);
                            return SideEffect.none;
                        })
                .add(Events.TakeDamageEvent.class, (GameView view, Artifact receiver, Events.TakeDamageEvent e) -> {
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
        new Stratified<Artifact>(events.artifact, Labels.artifact_staff_of_wurmdel)
                .add(Events.GenerateArtifactEvent.class,
                        (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Your healing spells restore +4 more health";
                            e.blob.image = Optional.of(Labels.asset_staff_of_wurmdel);
                            return SideEffect.none;
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            view.game.events.signals.addListener(Events.HealEntityEvent.class, e.artifact);
                            return SideEffect.none;
                        })
                .add(Events.HealEntityEvent.class, (GameView view, Artifact receiver, Events.HealEntityEvent e) -> {
                    if (receiver.isClaimedByLeader(e.healer)) {
                        e.amount += 4;
                    }
                    return SideEffect.none;
                });

        // Tome of Morun
        new Stratified<Artifact>(events.artifact, Labels.artifact_tome_of_morun).add(Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "20% chance to spawn a glyph when your units kill an enemy";
                    e.blob.image = Optional.of(Labels.asset_tome_of_morun);
                    return SideEffect.none;
                }).add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            view.game.events.signals.addListener(Events.EntityDiedEvent.class, e.artifact);
                            return SideEffect.none;
                        })
                .add(Events.EntityDiedEvent.class, (GameView view, Artifact receiver, Events.EntityDiedEvent e) -> {
                    if (receiver.isClaimedByLeader(e.killer) && !receiver.isClaimedByLeader(e.target)) {
                        Tile t = view.game.world.getTile(e.killer.getPoint()).get();
                        if (!t.getGlyph().isPresent() && Lambda.chance(20)) {
                            t.setGlyph(Optional.of(Lambda.random(GlyphCategory.class)));
                        }
                    }
                    return SideEffect.none;
                });

        // Orb of Nerketo
        new Stratified<Artifact>(events.artifact, Labels.artifact_orb_of_nerketo)
                .add(Events.GenerateArtifactEvent.class,
                        (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Your units have +1 vision";
                            e.blob.image = Optional.of(Labels.asset_orb_of_nerketo);
                            return SideEffect.none;
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            view.game.events.signals.addListener(Events.GetVisionEvent.class, e.artifact);
                            for (Unit u : e.player.units) {
                                u.vision.set(view, e.player, u, u.getPoint());
                            }
                            return SideEffect.none;
                        })
                .add(Events.GetVisionEvent.class, (GameView view, Artifact receiver, Events.GetVisionEvent e) -> {
                    if (receiver.isClaimedByPlayer(e.player)) {
                        e.radius++;
                    }
                    return SideEffect.none;
                });

        // Shada's Flute
        new Stratified<Artifact>(events.artifact, Labels.artifact_shadas_flute).add(Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your patrons generate 3 unit points per turn";
                    e.blob.image = Optional.of(Labels.asset_shadas_flute);
                    return SideEffect.none;
                }).add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            view.game.future.addFutureTick("Tick", receiver, 1, true);
                            return SideEffect.none;
                        })
                .add("Tick", (GameView view, Artifact receiver, Events.RepeatedEvent e) -> {
                    for (Patron patron : view.game.mechanics.patronage) {
                        if (receiver.getOwner().equals(patron.getFavoritePlayer())) {
                            receiver.getOwner().get().addUnitPoints(view, 3);
                        }
                    }
                    return SideEffect.none;
                });

        // Stones of Thudin
        new Stratified<Artifact>(events.artifact, Labels.artifact_stones_of_thudin)
                .add(Events.GenerateArtifactEvent.class,
                        (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Your vaults have +2 defense";
                            e.blob.image = Optional.of(Labels.asset_stones_of_thudin);
                            return SideEffect.none;
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            view.game.events.signals.addListener(Events.TakeDamageEvent.class, e.artifact);
                            return SideEffect.none;
                        })
                .add(Events.TakeDamageEvent.class, (GameView view, Artifact receiver, Events.TakeDamageEvent e) -> {
                    if (e.target.isEntityType(EntityType.BUILDING)) {
                        if (receiver.isClaimedByLeader(e.target) && e.target.name.equals(Labels.building_vault)) {
                            e.dmg.base -= 2;
                        }
                    }
                    return SideEffect.none;
                });

        // The Chasi Bones
        new Stratified<Artifact>(events.artifact, Labels.artifact_chasi_bones).add(Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your nature glyph units have a 20% chance to harvest an additional item";
                    e.blob.image = Optional.of(Labels.asset_chasi_bones);
                    return SideEffect.none;
                }).add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            view.game.events.signals.addListener(Events.HarvestEvent.class, e.artifact);
                            return SideEffect.none;
                        })
                .add(Events.HarvestEvent.class, (GameView view, Artifact receiver, Events.HarvestEvent e) -> {
                    if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.NATURE) && !e.unit.haul.isFull()
                            && Lambda.chance(20)) {
                        e.unit.haul.add(view.game.generator.item(e.item.getStratifier()));
                    }
                    return SideEffect.none;
                });

        // Ucha's Bowl of Plenty
        new Stratified<Artifact>(events.artifact, Labels.artifact_uchas_bowl_of_plenty)
                .add(Events.GenerateArtifactEvent.class,
                        (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "+1 option when selecting a new unit";
                            e.blob.image = Optional.of(Labels.asset_uchas_bowl_of_plenty);
                            e.blob.chips = 2;
                            return SideEffect.none;
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            e.player.numRecruitmentOptions++;
                            return SideEffect.none;
                        });

        // Nerketo's Helm
        new Stratified<Artifact>(events.artifact, Labels.artifact_nerketos_helm).add(Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Critical hits against your units are less effective";
                    e.blob.image = Optional.of(Labels.asset_nerketos_helm);
                    e.blob.chips = 2;
                    return SideEffect.none;
                }).add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            view.game.events.signals.addListener(Events.CheckCriticalHitEvent.class, e.artifact);
                            return SideEffect.none;
                        })
                .add(Events.CheckCriticalHitEvent.class,
                        (GameView view, Artifact receiver, Events.CheckCriticalHitEvent e) -> {
                            if (e.entity.isEntityType(EntityType.UNIT) && receiver.isClaimedByLeader(e.entity)) {
                                e.multiplier = 1.1f;
                            }
                            return SideEffect.none;
                        });

        // Bounty of Ahn-June
        new Stratified<Artifact>(events.artifact, Labels.artifact_bounty_of_ahn_june)
                .add(Events.GenerateArtifactEvent.class,
                        (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Trade glyph units on your vaults generate +2 more auction points";
                            e.blob.image = Optional.of(Labels.asset_bounty_of_ahn_june);
                            e.blob.chips = 2;
                            return SideEffect.none;
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            view.game.events.signals.addListener(Events.GenerateAuctionPointsEvent.class, e.artifact);
                            return SideEffect.none;
                        })
                .add(Events.GenerateAuctionPointsEvent.class,
                        (GameView view, Artifact receiver, Events.GenerateAuctionPointsEvent e) -> {
                            if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.TRADE)
                                    && view.game.world.getTile(e.unit.getPoint()).flatMap((Tile t) -> t.building)
                                            .map((Building b) -> b.name.equals(Labels.building_vault)).orElse(false)) {
                                e.points += 2;
                            }
                            return SideEffect.none;
                        });

        // Mark of Kung
        new Stratified<Artifact>(events.artifact, Labels.artifact_mark_of_kung).add(Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your battle glyph units get +1 movement speed";
                    e.blob.image = Optional.of(Labels.asset_mark_of_kung);
                    e.blob.chips = 2;
                    return SideEffect.none;
                }).add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            view.game.events.signals.addListener(Events.UnitMoveDistanceEvent.class, e.artifact);
                            return SideEffect.none;
                        })
                .add(Events.UnitMoveDistanceEvent.class,
                        (GameView view, Artifact receiver, Events.UnitMoveDistanceEvent e) -> {
                            if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.BATTLE)) {
                                e.distance++;
                            }
                            return SideEffect.none;
                        });

        // Chalco's Seal of Protection
        new Stratified<Artifact>(events.artifact, Labels.artifact_chalcos_seal_of_protection)
                .add(Events.GenerateArtifactEvent.class,
                        (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Your trade glyph units have +2 defense";
                            e.blob.image = Optional.of(Labels.asset_chalcos_seal_of_protection);
                            e.blob.chips = 2;
                            return SideEffect.none;
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            view.game.events.signals.addListener(Events.TakeDamageEvent.class, e.artifact);
                            return SideEffect.none;
                        })
                .add(Events.TakeDamageEvent.class, (GameView view, Artifact receiver, Events.TakeDamageEvent e) -> {
                    if (e.target.isEntityType(EntityType.UNIT)) {
                        Unit u = (Unit) e.target;
                        if (receiver.isClaimedByLeader(u) && u.glyphs.has(Glyph.TRADE)) {
                            e.dmg.base -= 2;
                        }
                    }
                    return SideEffect.none;
                });

        // Poda's Elixir
        new Stratified<Artifact>(events.artifact, Labels.artifact_podas_elixir).add(Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "15% chance refresh a glyph when you recruit a unit";
                    e.blob.image = Optional.of(Labels.asset_podas_elixir);
                    e.blob.chips = 2;
                    return SideEffect.none;
                }).add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            return () -> view.game.events.signals.addListener(Events.SpawnEvent.class, e.artifact);
                        })
                .add(Events.SpawnEvent.class, (GameView view, Artifact receiver, Events.SpawnEvent e) -> {
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
        new Stratified<Artifact>(events.artifact, Labels.artifact_gaias_effigy).add(Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "+8 unit points each turn";
                    e.blob.image = Optional.of(Labels.asset_gaias_effigy);
                    e.blob.chips = 3;
                    return SideEffect.none;
                }).add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            view.game.future.addFutureTick("Tick", receiver, 1, true);
                            return SideEffect.none;
                        })
                .add("Tick", (GameView view, Artifact receiver, Events.RepeatedEvent e) -> {
                    receiver.getOwner().get().addUnitPoints(view, 8);
                    return SideEffect.none;
                });

        // Rod of Adelon
        new Stratified<Artifact>(events.artifact, Labels.artifact_rod_of_adelon).add(Events.GenerateArtifactEvent.class,
                (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "5% chance to recruit an enemy unit when you kill it";
                    e.blob.image = Optional.of(Labels.asset_rod_of_adelon);
                    e.blob.chips = 3;
                    return SideEffect.none;
                }).add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            view.game.events.signals.addListener(Events.KilledEntityEvent.class, e.artifact);
                            return SideEffect.none;
                        })
                .add(Events.KilledEntityEvent.class, (GameView view, Artifact receiver, Events.KilledEntityEvent e) -> {
                    if (e.target.isEntityType(EntityType.UNIT) && receiver.isClaimedByLeader(e.killer)
                            && !e.killer.isFriendly(e.target) && Lambda.chance(5)) {
                        view.game.generator.unit(e.target.name, e.target.getX(), e.target.getY()).spawn(view);
                    }
                    return SideEffect.none;
                });

        // Blade of Sanguinor
        new Stratified<Artifact>(events.artifact, Labels.artifact_blade_of_sanguinor)
                .add(Events.GenerateArtifactEvent.class,
                        (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Your battle glyph units deal +2 damage";
                            e.blob.image = Optional.of(Labels.asset_blade_of_sanguinor);
                            e.blob.chips = 3;
                            return SideEffect.none;
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            view.game.events.signals.addListener(Events.AttackEvent.class, e.artifact);
                            return SideEffect.none;
                        })
                .add(Events.AttackEvent.class, (GameView view, Artifact receiver, Events.AttackEvent e) -> {
                    if (e.target.isEntityType(EntityType.UNIT) && receiver.isClaimedByLeader(e.target)
                            && ((Unit) e.target).glyphs.has(Glyph.BATTLE)) {
                        e.dmg.base += 2;
                    }
                    return SideEffect.none;
                });

        // Cask of Amonitor
        new Stratified<Artifact>(events.artifact, Labels.artifact_cask_of_amontior)
                .add(Events.GenerateArtifactEvent.class,
                        (GameView view, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Your unoccupied tiles in a patron's domain provide +1 favor";
                            e.blob.image = Optional.of(Labels.asset_cask_of_amontior);
                            e.blob.chips = 3;
                            return SideEffect.none;
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameView view, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            view.game.events.signals.addListener(Events.CalculateFavorEvent.class, e.artifact);
                            return SideEffect.none;
                        })
                .add(Events.CalculateFavorEvent.class,
                        (GameView view, Artifact receiver, Events.CalculateFavorEvent e) -> {
                            if (receiver.isClaimedByPlayer(e.player)) {
                                for (Point p : e.patron.getDomain()) {
                                    Tile t = view.game.world.getTile(p).get();
                                    if (t.leader.map((Player p1) -> p1.equals(e.player)).orElse(false)
                                            && !t.unit.isPresent()) {
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
        new Stratified<Fate>(events.fate, Labels.fate_raider)
                .add(Events.GenerateFateEvent.class, (GameView view, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_raider);
                    e.blob.desc.add("Playstyle: High-risk aggro");
                    e.blob.desc.add("• Your first unit will have the battle glyph");
                    e.blob.desc.add("• Your units always deal critical hits at or below 25% of their max health");
                    e.blob.desc
                            .add("• 15% chance for your units to fully heal themselves when they kill an enemy unit");
                    e.blob.strategicGoals.add(new AttackEnemy());
                    return SideEffect.none;
                }).add(Events.GetInitialGlyphEvent.class,
                        (GameView view, Fate receiver, Events.GetInitialGlyphEvent e) -> {
                            e.glyph = Optional.of(Glyph.BATTLE);
                            return SideEffect.none;
                        })
                .add(Events.GameStartEvent.class, (GameView view, Fate receiver, Events.GameStartEvent e) -> {
                    view.game.events.signals.addListener(Events.CheckCriticalHitEvent.class, receiver);
                    view.game.events.signals.addListener(Events.KilledEntityEvent.class, receiver);
                    return SideEffect.none;
                }).add(Events.CheckCriticalHitEvent.class,
                        (GameView view, Fate receiver, Events.CheckCriticalHitEvent e) -> {
                            if (e.entity.isEntityType(EntityType.UNIT)) {
                                final Unit u = (Unit) e.entity;
                                if (u.leadership.hasFate(receiver) && u.combat.health.atOrBelowPercent(25)) {
                                    e.chance = 100;
                                }
                            }
                            return SideEffect.none;
                        })
                .add(Events.KilledEntityEvent.class, (GameView view, Fate receiver, Events.KilledEntityEvent e) -> {
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
        new Stratified<Fate>(events.fate, Labels.fate_merchant)
                .add(Events.GenerateFateEvent.class, (GameView view, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_merchant);
                    e.blob.desc.add("Playstyle: Market control");
                    e.blob.desc.add("• Your first unit will have the trade glyph");
                    e.blob.desc.add("• Your vault buildings generate 2 unit points each turn");
                    e.blob.desc.add("• Your units generate 150% auction points");
                    e.blob.strategicGoals.add(new MineGold());
                    return SideEffect.none;
                }).add(Events.GetInitialGlyphEvent.class,
                        (GameView view, Fate receiver, Events.GetInitialGlyphEvent e) -> {
                            e.glyph = Optional.of(Glyph.TRADE);
                            return SideEffect.none;
                        })
                .add(Events.EndOfTurnEvent.class, (GameView view, Fate receiver, Events.EndOfTurnEvent e) -> {
                    for (Point p : view.game.getVaultBuildings(receiver.getPlayer())) {
                        receiver.getPlayer().addUnitPoints(view, p, 2);
                    }
                    return SideEffect.none;
                });
        new Stratified<Fate>(events.fate, Labels.fate_merchant).add(Events.GenerateAuctionPointsEvent.class,
                (GameView view, Fate receiver, Events.GenerateAuctionPointsEvent e) -> {
                    if (e.unit.leadership.hasFate(receiver)) {
                        e.points += e.points / 2;
                    }
                    return SideEffect.none;
                });

        // The Veteran
        new Stratified<Fate>(events.fate, Labels.fate_veteran)
                .add(Events.GenerateFateEvent.class, (GameView view, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_veteran);
                    e.blob.desc.add("Playstyle: Military production");
                    e.blob.desc.add("• Your battle glyph units heal for 3 damage when they don't act in a turn");
                    e.blob.desc.add("• Recruiting a battle glyph unit gives you 15 unit points");
                    return SideEffect.none;
                }).add(Events.EndOfTurnEvent.class, (GameView view, Fate receiver, Events.EndOfTurnEvent e) -> {
                    for (Unit u : receiver.getPlayer().units) {
                        if (view.game.actions.getUnitActionType(u).map((ActionType at) -> at == ActionType.SKIP)
                                .orElse(false)) {
                            u.combat.heal(view, 3);
                        }
                    }
                    return SideEffect.none;
                })
                .add(Events.RecruitNewUnitEvent.class, (GameView view, Fate receiver, Events.RecruitNewUnitEvent e) -> {
                    if (e.unit.glyphs.has(Glyph.BATTLE)) {
                        receiver.getPlayer().addUnitPoints(view, e.unit.getPoint(), 15);
                    }
                    return SideEffect.none;
                });

        // The Devout
        new Stratified<Fate>(events.fate, Labels.fate_devout)
                .add(Events.GenerateFateEvent.class, (GameView view, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_devout);
                    e.blob.desc.add("Playstyle: Patron collection");
                    e.blob.desc.add("• Your active patrons generate +6 unit points");
                    e.blob.desc.add("• Your units generate +3 favor");
                    return SideEffect.none;
                }).add(Events.GameStartEvent.class, (GameView view, Fate receiver, Events.GameStartEvent e) -> {
                    view.game.events.signals.addListener(Events.GenerateFavorEvent.class, receiver);
                    return SideEffect.none;
                }).add(Events.EndOfTurnEvent.class, (GameView view, Fate receiver, Events.EndOfTurnEvent e) -> {
                    for (Patron p : view.game.mechanics.patronage) {
                        if (p.getFavoritePlayer().map((Player p1) -> receiver.getPlayer().equals(p1)).orElse(false)) {
                            receiver.getPlayer().addUnitPoints(view, p.getPoint(), 6);
                        }
                    }
                    return SideEffect.none;
                });
        new Stratified<Fate>(events.fate, Labels.fate_devout).add(Events.GenerateFavorEvent.class,
                (GameView view, Fate receiver, Events.GenerateFavorEvent e) -> {
                    if (e.unit.leadership.hasFate(receiver)) {
                        e.favor += 3;
                    }
                    return SideEffect.none;
                });

        // The Sentinel
        new Stratified<Fate>(events.fate, Labels.fate_sentinel)
                .add(Events.GenerateFateEvent.class, (GameView view, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_sentinel);
                    e.blob.desc.add("Playstyle: Defensive expansion");
                    e.blob.desc.add("• Your buildings take 15% less damage");
                    e.blob.desc.add("• When you create a building the occupying unit gains 2 attack and defense");
                    e.blob.desc.add("• Recruiting a defense glyph unit gives you 20 unit points");
                    return SideEffect.none;
                }).add(Events.GameStartEvent.class, (GameView view, Fate receiver, Events.GameStartEvent e) -> {
                    view.game.events.signals.addListener(Events.TakeDamageEvent.class, receiver);
                    view.game.events.signals.addListener(Events.SpawnEvent.class, receiver);
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class, (GameView view, Fate receiver, Events.TakeDamageEvent e) -> {
                    if (e.target.getEntityType() == EntityType.BUILDING
                            && e.target.getLeader().map((Player p) -> p.equals(receiver.getPlayer())).orElse(false)) {
                        e.dmg.base -= (int) (e.dmg.base * 0.15);
                    }
                    return SideEffect.none;
                }).add(Events.SpawnEvent.class, (GameView view, Fate receiver, Events.SpawnEvent e) -> {
                    if (e.spawned instanceof Building) {
                        final Building b = (Building) e.spawned;
                        if (b.getLeader().map((Player p) -> p.equals(receiver.getPlayer())).orElse(false)) {
                            return () -> view.game.world.getTile(b.getPoint()).flatMap((Tile t) -> t.unit)
                                    .ifPresent((Unit u) -> {
                                        u.abilities.addStatusEffect(view, Labels.status_effect_proud_builder).execute();
                                    });
                        }
                    }
                    return SideEffect.none;
                })
                .add(Events.RecruitNewUnitEvent.class, (GameView view, Fate receiver, Events.RecruitNewUnitEvent e) -> {
                    if (e.unit.glyphs.has(Glyph.DEFENSE)) {
                        receiver.getPlayer().addUnitPoints(view, e.unit.getPoint(), 20);
                    }
                    return SideEffect.none;
                });

        // The Usurper
        new Stratified<Fate>(events.fate, Labels.fate_usurper)
                .add(Events.GenerateFateEvent.class, (GameView view, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_usurper);
                    e.blob.desc.add("Playstyle: Early market bonus into unit production");
                    e.blob.desc.add("• Your first unit will have the trade glyph");
                    e.blob.desc.add("• You get a free auction chip at the start of the game");
                    e.blob.desc.add("• You get 15 unit points when you do not win an auction");
                    e.blob.strategicGoals.add(new MineGold());
                    return SideEffect.none;
                }).add(Events.GetInitialGlyphEvent.class,
                        (GameView view, Fate receiver, Events.GetInitialGlyphEvent e) -> {
                            e.glyph = Optional.of(Glyph.TRADE);
                            return SideEffect.none;
                        })
                .add(Events.GameStartEvent.class, (GameView view, Fate receiver, Events.GameStartEvent e) -> {
                    e.player.auctionChips++;
                    return SideEffect.none;
                }).add(Events.LostAuctionEvent.class, (GameView view, Fate receiver, Events.LostAuctionEvent e) -> {
                    e.player.addUnitPoints(view, 15);
                    return SideEffect.none;
                });

        // The Forager
        new Stratified<Fate>(events.fate, Labels.fate_forager)
                .add(Events.GenerateFateEvent.class, (GameView view, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_forager);
                    e.blob.desc.add("Playstyle: Resource accumulation");
                    e.blob.desc.add("• Your first unit will have the nature glyph");
                    e.blob.desc.add("• Your units have a 20% chance to generate an extra item while harvesting");
                    e.blob.desc.add("• Your nature glyph units have +1 speed");
                    return SideEffect.none;
                }).add(Events.GetInitialGlyphEvent.class,
                        (GameView view, Fate receiver, Events.GetInitialGlyphEvent e) -> {
                            e.glyph = Optional.of(Glyph.NATURE);
                            return SideEffect.none;
                        })
                .add(Events.GameStartEvent.class, (GameView view, Fate receiver, Events.GameStartEvent e) -> {
                    view.game.events.signals.addListener(Events.HarvestEvent.class, receiver);
                    view.game.events.signals.addListener(Events.UnitMoveDistanceEvent.class, receiver);
                    return SideEffect.none;
                }).add(Events.HarvestEvent.class, (GameView view, Fate receiver, Events.HarvestEvent e) -> {
                    if (e.unit.leadership.hasFate(receiver) && !e.unit.haul.isFull() && Lambda.chance(20)) {
                        e.unit.haul.add(view.game.generator.item(e.item.name));
                    }
                    return SideEffect.none;
                }).add(Events.UnitMoveDistanceEvent.class,
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
        new Stratified<Unit>(events.unit, Labels.unit_knuckleheads).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.setModelInstance(view.av, "knuckleheads");
                    e.blob.desc = "This Ettin roams the Dragonlands and feasts on giant lizard flesh";
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_bash, Labels.ability_stomp);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_shield_defense,
                            Labels.ability_efficient_stomach);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.DEFENSE);
                    e.blob.combat.health.setMaxAndValue(35);
                    e.blob.haul.setMax(2);
                    e.blob.species = Defs.species_ettin;
                    return SideEffect.none;
                });

        // Gorax the Dragon Knight
        // Equinox
        // Elder Chumsa
        // Gemrock
        new Stratified<Unit>(events.unit, Labels.unit_gemrock).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.setModelInstance(view.av, "golem-grotto");
                    e.blob.setMaterial("gemrock");
                    e.blob.desc = "This craggy golem has priceless gems set into its flesh";
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_smash);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_stone_defense,
                            Labels.ability_loose_gems);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.MINING);
                    e.blob.combat.health.setMaxAndValue(35);
                    e.blob.species = Defs.species_golem;
                    return SideEffect.none;
                });

        // Glittersnout
        new Stratified<Unit>(events.unit, Labels.unit_glittersnout).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.setModelInstance(view.av, "glittersnout");
                    e.blob.desc = "She's a skilled goldsmith from the high nation of Urqusuyu";
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_gilded_strike,
                            Labels.ability_craft_golden_spear);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_night_vision,
                            Labels.ability_mine_gems, Labels.ability_mine_gold);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.MINING);
                    e.blob.combat.health.setMaxAndValue(25);
                    e.blob.species = Defs.species_brownie;
                    return SideEffect.none;
                });

        // Sir Tlatec
        new Stratified<Unit>(events.unit, Labels.unit_sir_tlatec).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.setModelInstance(view.av, "axolotl");
                    e.blob.desc = "Tlatec the Axolotl-man has travelled far from his home in search of worthy opponents";
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_sword_slash);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_swim, Labels.ability_hunt_fish,
                            Labels.ability_plate_mail, Labels.ability_regeneration);
                    e.blob.glyphs.set(Glyph.BATTLE);
                    e.blob.combat.health.setMaxAndValue(35);
                    e.blob.species = Defs.species_salamander;
                    return SideEffect.none;
                });

        // Cenuok the Battle Grue
        // Beetlemoss
        new Stratified<Unit>(events.unit, Labels.unit_beetlemoss).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This nature spirit guards an ancient forest in Eaglehaven";
                    e.blob.setModelInstance(view.av, "beetlemoss");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_fire_cannon,
                            Labels.ability_plant_forest);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_pick_apples,
                            Labels.ability_mine_gems);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.NATURE);
                    e.blob.combat.health.setMaxAndValue(35);
                    e.blob.species = Defs.species_sprite;
                    return SideEffect.none;
                });

        // Gloop the Adventurer
        new Stratified<Unit>(events.unit, Labels.unit_gloop_the_adventurer).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.setModelInstance(view.av, "gloop");
                    e.blob.desc = "This Plasmoid adventurer is eager to prove themself in the dungeons";
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_sword_slash,
                            Labels.ability_dungeon_delve);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_combat_loot,
                            Labels.ability_night_vision, Labels.ability_regeneration);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.MINING);
                    e.blob.combat.health.setMaxAndValue(35);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_plasmoid;
                    return SideEffect.none;
                });

        // Dominus the Lich
        // Graymaw
        // Roseris Thorn-hoof
        // Nitu Sodfoot
        // Nebaneba
        new Stratified<Unit>(events.unit, Labels.unit_nebaneba).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.setModelInstance(view.av, "nebaneba");
                    e.blob.desc = "This living goo crafts slime armor for his allies";
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_smash,
                            Labels.ability_craft_slime_armor);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_regeneration,
                            Labels.ability_harvest_goo);
                    e.blob.glyphs.set(Glyph.DEFENSE);
                    e.blob.combat.health.setMaxAndValue(45);
                    e.blob.species = Defs.species_plasmoid;
                    return SideEffect.none;
                });

        // Kamiena
        // Faustus
        // Maekuro the Mighty
        // Garudee
        new Stratified<Unit>(events.unit, Labels.unit_garudee).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This Garuda artilleryman spits pebbles at invaders";
                    e.blob.setModelInstance(view.av, "garudee");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_pebble_shot,
                            Labels.ability_swing_axe);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_entrenched);
                    e.blob.glyphs.set(Glyph.DEFENSE);
                    e.blob.combat.health.setMaxAndValue(45);
                    e.blob.species = Defs.species_garuda;
                    UnitLogic.speed(events, e.blob, 1);
                    return SideEffect.none;
                });

        // Pebbles
        // Magdalena
        // Lost Golem
        // Samara
        // Golem of the Grotto
        new Stratified<Unit>(events.unit, Labels.unit_golem_of_the_grotto).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This Golem wanders the rocky peaks where it was forged long ago";
                    e.blob.setModelInstance(view.av, "golem-grotto");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_smash, Labels.ability_plant_meadow);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_mountain_strider,
                            Labels.ability_local_defender);
                    e.blob.glyphs.set(Glyph.DEFENSE, Glyph.NATURE);
                    e.blob.combat.health.setMaxAndValue(45);
                    e.blob.species = Defs.species_golem;
                    return SideEffect.none;
                });

        // Puffshroom
        new Stratified<Unit>(events.unit, Labels.unit_puffshroom).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This Golem-like fungal being spawns new ecosystems where it roams";
                    e.blob.setModelInstance(view.av, "puffshroom");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_pummel,
                            Labels.ability_protective_spores);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_defensive_bloom,
                            Labels.ability_harvest_mushrooms);
                    e.blob.glyphs.set(Glyph.DEFENSE, Glyph.NATURE);
                    e.blob.combat.health.setMaxAndValue(35);
                    e.blob.species = Defs.species_toadstool;
                    return SideEffect.none;
                });

        // Lord Tyson
        // Courrier Grog
        // Nizhaad Windwalker
        // Condylure of the Star Nose
        new Stratified<Unit>(events.unit, Labels.unit_condylure_of_the_star_nose).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This Brownie is blind, but traverses the subterranean world with the aid of his nose";
                    e.blob.setModelInstance(view.av, "condylure");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_build_healing_fountain,
                            Labels.ability_dig_mine);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_night_vision,
                            Labels.ability_mine_gems);
                    e.blob.glyphs.set(Glyph.HEALING, Glyph.MINING);
                    e.blob.combat.health.setMaxAndValue(35);
                    e.blob.species = Defs.species_brownie;
                    return SideEffect.none;
                });

        // Huiying the Alchemist
        // Lady Daumia
        new Stratified<Unit>(events.unit, Labels.unit_lady_daumia).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "Elven high missionary to Surgarde";
                    e.blob.setModelInstance(view.av, "daumia");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_heal_wounds,
                            Labels.ability_self_sacrifice);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_night_vision,
                            Labels.ability_life_aura);
                    e.blob.glyphs.set(Glyph.HEALING);
                    e.blob.combat.health.setMaxAndValue(35);
                    e.blob.species = Defs.species_elf;
                    return SideEffect.none;
                });

        // Zen Hito the Kappa
        // Gibrax the Everlasting
        // Passiflor
        // Frogger the Gnome
        new Stratified<Unit>(events.unit, Labels.unit_frogger_the_gnome).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "Just a little Gnome and his frog";
                    e.blob.setModelInstance(view.av, "frog-gnome");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_heal_wounds,
                            Labels.ability_hungry_frog_magic);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_forage_in_meadow,
                            Labels.ability_swim);
                    e.blob.glyphs.set(Glyph.HEALING);
                    e.blob.combat.health.setMaxAndValue(25);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_gnome;
                    return SideEffect.none;
                });

        // Teragalor
        // Stalagmus
        new Stratified<Unit>(events.unit, Labels.unit_stalagmus).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "Enchanted waters accumulate into this Golem's bowl-shaped body";
                    e.blob.setModelInstance(view.av, "stalagmus");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_dig_mine, Labels.ability_hurl_rock);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_night_vision,
                            Labels.ability_stone_defense, Labels.ability_mine_gems, Labels.ability_mine_gold,
                            Labels.ability_subterranean_potions);
                    e.blob.glyphs.set(Glyph.MINING);
                    e.blob.combat.health.setMaxAndValue(35);
                    e.blob.species = Defs.species_golem;
                    return SideEffect.none;
                });

        // Glimmer
        // Grizzlemane the Mycoweaver
        // Magicad
        // The Druid
        new Stratified<Unit>(events.unit, Labels.unit_druid).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "A mysterious Druid who rarely speaks";
                    e.blob.setModelInstance(view.av, "druid");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_plant_forest,
                            Labels.ability_revenge_of_the_forest);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_pick_apples,
                            Labels.ability_night_vision, Labels.ability_green_fortress);
                    e.blob.glyphs.set(Glyph.NATURE);
                    e.blob.combat.health.setMaxAndValue(35);
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
        new Stratified<Unit>(events.unit, Labels.unit_blorp_the_burning).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "A ravenous Plasmoid with an acidic body";
                    e.blob.setModelInstance(view.av, "blob");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_slime_shot);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_acid_skin,
                            Labels.ability_liquifying_presence);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.DEFENSE);
                    e.blob.combat.health.setMaxAndValue(45);
                    e.blob.species = Defs.species_plasmoid;
                    return SideEffect.none;
                });

        // Sathra the Flame Caster
        // Dendra Ivy
        // Trina the Ettin
        // Prismar
        new Stratified<Unit>(events.unit, Labels.unit_prismar).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.setModelInstance(view.av, Labels.asset_crystal);
                    e.blob.desc = "This Gemstone can focus light into powerful attacks";
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_fire_laser,
                            Labels.ability_collapse_mine);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_crystal_skin,
                            Labels.ability_night_vision, Labels.ability_mine_gems);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.MINING);
                    e.blob.combat.health.setMaxAndValue(35);
                    e.blob.species = Defs.species_gemstone;
                    return SideEffect.none;
                });

        // Slip
        new Stratified<Unit>(events.unit, Labels.unit_slip).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This Plasmoid stores wares in its gelatinous form and ferries them across trade routes";
                    e.blob.setModelInstance(view.av, "blob");
                    e.blob.setMaterial("slip", 1);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_metabolize);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_regeneration,
                            Labels.ability_market_value_goo, Labels.ability_economic_activity);
                    e.blob.glyphs.set(Glyph.TRADE);
                    e.blob.combat.health.setMaxAndValue(45);
                    e.blob.haul.setMax(12);
                    e.blob.hunger.tags.acceptAll();
                    e.blob.species = Defs.species_plasmoid;
                    return SideEffect.none;
                });

        // Ariala the Mage
        // Therona Rabbitfoot
        //
        // Rezak the Conjurer
        // Halifax
        // Glub Glub
        // Galygos the Juggernaut
        // Defender Cuauhtli
        // Gilded Cho'chal
        // Soothing Gills
        // Pumpkin Boy
        new Stratified<Unit>(events.unit, Labels.unit_pumpkin_boy).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "He doesn't say much, he's just a little guy";
                    e.blob.setModelInstance(view.av, "pumpkin-boy");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_plant_meadow, Labels.ability_hug);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_night_vision,
                            Labels.ability_regeneration, Labels.ability_running_through_nature,
                            Labels.ability_sacred_seeds);
                    e.blob.glyphs.set(Glyph.NATURE);
                    e.blob.combat.health.setMaxAndValue(25);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_sprite;
                    return SideEffect.none;
                });

        // Barometz
        new Stratified<Unit>(events.unit, Labels.unit_barometz).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This sheep-like Sprite blooms with delicious fruit";
                    e.blob.setModelInstance(view.av, "barometz");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_bite);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_regeneration, Labels.ability_edible,
                            Labels.ability_deposit_seeds);
                    e.blob.glyphs.set(Glyph.NATURE);
                    e.blob.combat.health.setMaxAndValue(35);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_sprite;
                    return SideEffect.none;
                });

        // Xella the Accursed
        // Svelta Luktegress
        // Al-Fikra
        new Stratified<Unit>(events.unit, Labels.unit_al_fikra).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This being aids the great merchant kings of Eastern Bycidia";
                    e.blob.setModelInstance(view.av, "alfikra");
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_regeneration,
                            Labels.ability_market_indicator);
                    e.blob.glyphs.set(Glyph.TRADE);
                    e.blob.combat.health.setMaxAndValue(35);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_tulpa;
                    UnitLogic.speed(events, e.blob, 3);
                    UnitLogic.vision(events, e.blob, 4);
                    return SideEffect.none;
                });

        // Goldtooth
        // The Necromancer
        new Stratified<Unit>(events.unit, Labels.unit_necromancer).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This fallen creature now terrorizes its once idyllic home";
                    e.blob.setModelInstance(view.av, "druid");
                    e.blob.setMaterial("necromancer");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_necrotic_blast,
                            Labels.ability_raise_undead);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_regeneration,
                            Labels.ability_night_vision);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.DEFENSE);
                    e.blob.combat.health.setMaxAndValue(45);
                    e.blob.hunger.setTimeToHunger(view, 10);
                    e.blob.species = Defs.species_undead;
                    return SideEffect.none;
                });

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
        new Stratified<Unit>(events.unit, Labels.unit_barbs).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This pufferfish woman can inject foes with poison from her many barbs";
                    e.blob.setModelInstance(view.av, "barbs");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_inject_poison,
                            Labels.ability_remove_poison);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_swim, Labels.ability_thorny_skin,
                            Labels.ability_hunt_fish);
                    e.blob.glyphs.set(Glyph.DEFENSE, Glyph.HEALING);
                    e.blob.combat.health.setMaxAndValue(45);
                    e.blob.species = Defs.species_merfolk;
                    return SideEffect.none;
                });

        // Yalitza
        // Old Man Mosscloak
        //
        //
        // King Gargantos
        new Stratified<Unit>(events.unit, Labels.unit_king_gargantos).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "Warrior-king of the Tortoise Kingdom";
                    e.blob.setModelInstance(view.av, "gargantos");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_smash, Labels.ability_build_vault);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_shell_defense,
                            Labels.ability_market_boom, Labels.ability_swim);
                    e.blob.glyphs.set(Glyph.DEFENSE, Glyph.TRADE);
                    e.blob.combat.health.setMaxAndValue(45);
                    e.blob.hunger.setTimeToHunger(view, 10);
                    e.blob.species = Defs.species_tortugan;
                    return SideEffect.none;
                });

        // Sir Rootbeard
        // Wuraj the Blessed
        // Karina Brightfeather
        // Photali
        // Batatita
        // Razma
        new Stratified<Unit>(events.unit, Labels.unit_razma).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "A wandering mystic and trader";
                    e.blob.setModelInstance(view.av, "alfikra");
                    e.blob.setMaterial("razma");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_fireball,
                            Labels.ability_heal_wounds);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_economic_activity,
                            Labels.ability_pious);
                    e.blob.glyphs.set(Glyph.HEALING, Glyph.TRADE);
                    e.blob.combat.health.setMaxAndValue(35);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_human;
                    UnitLogic.speed(events, e.blob, 3);
                    return SideEffect.none;
                });

        // Theressa the Rover
        //
        // Illapa
        // Disastra
        // Chicao
        // Alaistar and Wurmdel
        // Mi'chalb Lightfoot
        // Ghastly Thrall
        new Stratified<Unit>(events.unit, Labels.unit_ghastly_thrall).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.doNotAddToGlyphPool();
                    e.blob.desc = "A terrifying undead warrior risen by those skilled in the dark arts";
                    e.blob.setModelInstance(view.av, "skeleton");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_sword_slash);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_ghastly_thrall);
                    e.blob.glyphs.set(Glyph.BATTLE);
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.species = Defs.species_undead;
                    UnitLogic.speed(events, e.blob, 100);
                    return SideEffect.none;
                });

        /**
         * SECTION Abilities
         */

        // Acid Skin
        new Stratified<Ability>(events.ability, Labels.ability_acid_skin).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Adjacent attackers take damage");
                    e.blob.setIcon(Labels.asset_acid_skin);
                    return SideEffect.none;
                }).add(Events.AttackedEvent.class, (GameView view, Ability receiver, Events.AttackedEvent e) -> {
                    if (e.attacker instanceof Unit) {
                        Unit target = (Unit) e.target;
                        Unit attacker = (Unit) e.attacker;
                        return Hexagons.areNeighbors(attacker.getPoint(), target.getPoint())
                                ? attacker.combat.takeDamage(view, new Damage(2), target)
                                : SideEffect.none;
                    }
                    return SideEffect.none;
                });

        // Bash
        new Stratified<Ability>(events.ability, Labels.ability_bash).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Deals 12 damage";
                    e.blob.setIcon(Labels.asset_bash);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.attack(view,
                                receiver.wielder, new Damage(12), 1));

        // Bite
        new Stratified<Ability>(events.ability, Labels.ability_bite).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Deals 5 damage";
                    e.blob.setIcon(Labels.asset_bite);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.attack(view,
                                receiver.wielder, new Damage(5), 1));

        // Build Healing Fountain
        new Stratified<Ability>(events.ability, Labels.ability_build_healing_fountain).add(
                Events.GenerateAbilityEvent.class, (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Constructs a healing fountain";
                    e.blob.setIcon(Labels.asset_build_healing_fountain);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.build(view,
                                receiver.wielder, Labels.building_healing_fountain, (Tile t) -> true));

        // Build Vault
        new Stratified<Ability>(events.ability, Labels.ability_build_vault).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Builds a vault";
                    e.blob.setIcon(Labels.asset_build_vault);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.build(view,
                                receiver.wielder, Labels.building_vault, (Tile t) -> true));

        // Collapse Mine
        new Stratified<Ability>(events.ability, Labels.ability_collapse_mine).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format(
                            "Target a mine occupied by an enemy unit. The unit, mine, and any adjacent enemy units all take damage.");
                    e.blob.setIcon(Labels.asset_collapse_mine);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            Set<Point> mines = Lambda.filter((Point p) -> view.game.world.getTile(p)
                                    .map((Tile t) -> !t.leader.equals(receiver.wielder.getLeader()) && t.building
                                            .map((Building b) -> b.name.equals(Labels.building_mine)).orElse(false)
                                            && t.unit.isPresent())
                                    .orElse(false), Hexagons.getNeighbors(receiver.wielder.getPoint(), 2));
                            return receiver.wielder.getLeader().get().select(view, mines, "No mines in range",
                                    (Point p) -> {
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
        new Stratified<Ability>(events.ability, Labels.ability_combat_loot).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("+2 damage if this unit has a stored item");
                    e.blob.setIcon(Labels.asset_combat_loot);
                    return SideEffect.none;
                }).add(Events.AttackEvent.class, (GameView view, Ability receiver, Events.AttackEvent e) -> {
                    if (receiver.wielder.haul.hasItems()) {
                        e.dmg.base += 2;
                    }
                    return SideEffect.none;
                });

        // Craft Golden Spear
        new Stratified<Ability>(events.ability, Labels.ability_craft_golden_spear).add(
                Events.GenerateAbilityEvent.class, (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Gives the target adjacent ally a golden spear (+2 damage)");
                    e.blob.setIcon(Labels.asset_golden_spear);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            final Set<Point> targets = Lambda.filter((Point p) -> view.game.world.getTile(p)
                                    .flatMap((Tile t) -> t.unit)
                                    .map((Unit u) -> u.leadership.sameLeader(receiver.wielder) && !u.haul.isFull())
                                    .orElse(false), Hexagons.getNeighbors(receiver.wielder.getPoint(), 1));
                            return receiver.wielder.getLeader().get().select(view, targets, "No allies in range",
                                    (Point p) -> {
                                        return () -> {
                                            view.game.world.getUnit(p).ifPresent((Unit u) -> u.haul
                                                    .add(view.game.generator.item(Labels.item_golden_spear)));
                                            view.game.actions.unitHasCastSpell(view, receiver.wielder);
                                        };
                                    });
                        });

        // Craft Slime Armor
        new Stratified<Ability>(events.ability, Labels.ability_craft_slime_armor).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String
                            .format("Consumes one goo item and gives the target ally slime armor (+2 defense)");
                    e.blob.setIcon(Labels.asset_slime_armor);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            if (!receiver.wielder.haul.hasItemWithTag(Labels.tag_goo)) {
                                view.hud.logger.error("Cannot craft slime armor without a goo item");
                                return SideEffect.none;
                            }
                            final Set<Point> targets = Lambda.filter((Point p) -> view.game.world.getTile(p)
                                    .flatMap((Tile t) -> t.unit)
                                    .map((Unit u) -> u.leadership.sameLeader(receiver.wielder) && !u.haul.isFull())
                                    .orElse(false), Hexagons.getNeighbors(receiver.wielder.getPoint(), 2));
                            return receiver.wielder.getLeader().get().select(view, targets, "No allies in range",
                                    (Point p) -> {
                                        return () -> {
                                            receiver.wielder.haul.removeItemWithTag(Labels.tag_goo);
                                            view.game.world.getUnit(p).ifPresent((Unit u) -> u.haul
                                                    .add(view.game.generator.item(Labels.item_slime_armor)));
                                            view.game.actions.unitHasCastSpell(view, receiver.wielder);
                                        };
                                    });
                        });

        // Crystal Skin
        new Stratified<Ability>(events.ability, Labels.ability_crystal_skin).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Extra defense");
                    e.blob.setIcon(Labels.asset_acid_skin, 0x34a33b, 0x318ec0);
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class,
                        (GameView view, Ability receiver, Events.TakeDamageEvent e) -> AbilityLogic.defense(e, 2));

        // Defensive Bloom
        new Stratified<Ability>(events.ability, Labels.ability_defensive_bloom).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "15% chance to generate a natural item when the unit is attacked";
                    e.blob.setIcon(Labels.asset_defensive_blossom);
                    return SideEffect.none;
                }).add(Events.AttackedEvent.class, (GameView view, Ability receiver, Events.AttackedEvent e) -> {
                    return !receiver.wielder.haul.isFull() && Lambda.chance(15)
                            ? () -> receiver.wielder.haul
                                    .add(view.game.mechanics.loot.dropByTag(view.game, Labels.tag_natural))
                            : SideEffect.none;
                });

        // Deposit Seeds
        new Stratified<Ability>(events.ability, Labels.ability_deposit_seeds).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Chance to spawn a meadow when this unit moves");
                    e.blob.setIcon(Labels.asset_deposit_seeds);
                    return SideEffect.none;
                }).add(Events.UnitMovedEvent.class, (GameView view, Ability receiver, Events.UnitMovedEvent e) -> {
                    Point p = receiver.wielder.getPoint();
                    return view.game.world.getTile(p).map((Tile t) -> !t.building.isPresent()).orElse(false)
                            && Lambda.chance(10)
                                    ? () -> view.game.generator.building(Labels.building_meadow, p.x, p.y).spawn(view)
                                    : SideEffect.none;
                });

        // Dig Mine
        new Stratified<Ability>(events.ability, Labels.ability_dig_mine).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Digs a mine";
                    e.blob.setIcon(Labels.asset_dig_mine);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.build(view,
                                receiver.wielder, Labels.building_mine, (Tile t) -> t.name.equals(Labels.tile_rock)));

        // Dungeon Delve
        new Stratified<Ability>(events.ability, Labels.ability_dungeon_delve).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String
                            .format("Deals 8 damage and generates loot if targeting a tile with an active building");
                    e.blob.setIcon(Labels.asset_dungeon_delve);
                    return SideEffect.none;
                })
                .add(Events.AbilityActivatedEvent.class, (GameView view, Ability receiver,
                        Events.AbilityActivatedEvent e) -> AbilityLogic.attackAndEffect(view, receiver.wielder,
                                new Damage(8), 1,
                                Optional.of((Point p) -> !receiver.wielder.haul.isFull() && view.game.world.getTile(p)
                                        .flatMap((Tile t) -> t.building).map((Building b) -> b.isActive()).orElse(false)
                                                ? () -> receiver.wielder.haul
                                                        .add(view.game.mechanics.loot.drop(view.game))
                                                : SideEffect.none)));

        // Economic Activity
        new Stratified<Ability>(events.ability, Labels.ability_economic_activity).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Generates 3 auction points when occupying a vault");
                    e.blob.setIcon(Labels.asset_economic_activity);
                    return SideEffect.none;
                })
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver,
                                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 1, true))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.doOnBuilding(view,
                                receiver.wielder, (Building b) -> b.name.equals(Labels.building_vault),
                                () -> AbilityLogic.generateAuctionPoints(view, receiver.wielder, 1)));

        // Edible
        new Stratified<Ability>(events.ability, Labels.ability_edible).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Generates food");
                    e.blob.setIcon(Labels.asset_edible);
                    return SideEffect.none;
                })
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver,
                                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromTile(view,
                                receiver.wielder, view.game.mechanics.loot.getByTag(Labels.tag_fruit),
                                (Tile t) -> true));

        // Efficient Stomach
        new Stratified<Ability>(events.ability, Labels.ability_efficient_stomach).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Can cast a second spell below 2 hunger");
                    e.blob.setIcon(Labels.asset_stomach);
                    return SideEffect.none;
                }).add(Events.GetMaxActivationsEvent.class,
                        (GameView view, Ability receiver, Events.GetMaxActivationsEvent e) -> {
                            if (receiver.wielder.hunger.get(view) < 2) {
                                e.max++;
                            }
                            return SideEffect.none;
                        });

        // Entrenched
        new Stratified<Ability>(events.ability, Labels.ability_entrenched).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("+2 armor when on an active building");
                    e.blob.setIcon(Labels.asset_local_defender);
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class, (GameView view, Ability receiver, Events.TakeDamageEvent e) -> {
                    final boolean isOnActiveBuilding = view.game.world.getTile(receiver.wielder.getPoint())
                            .flatMap((Tile t) -> t.building).map((Building b) -> b.isActive()).orElse(false);
                    return isOnActiveBuilding ? AbilityLogic.defense(e, 2) : SideEffect.none;
                });

        // Fireball
        new Stratified<Ability>(events.ability, Labels.ability_fireball).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Deals 8 damage");
                    e.blob.setIcon(Labels.asset_fireball);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.attack(view,
                                receiver.wielder, new Damage(8), 3));

        // Fire Cannon
        new Stratified<Ability>(events.ability, Labels.ability_fire_cannon).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Deals 5 damage (or 12 damage to a building)");
                    e.blob.setIcon(Labels.asset_fire_cannon);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic
                                .dynamicDamageAttack(view, receiver.wielder, 2,
                                        (Tile t) -> t.building.isPresent() && !t.unit.isPresent()
                                                ? new Damage(12)
                                                : new Damage(5)));

        // Fire Laser
        new Stratified<Ability>(events.ability, Labels.ability_fire_laser).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Damage up to 3 units in a line");
                    e.blob.setIcon(Labels.asset_fire_laser);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
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
                                                    .ifPresent((Unit u) -> effects.add(
                                                            receiver.wielder.combat.attack(view, u, new Damage(4))));
                                        }
                                        effects.add(() -> view.game.actions.unitHasCastSpell(view, receiver.wielder));
                                        if (receiver.wielder.leadership.belongsToHuman()) {
                                            effects.add(() -> view.hud.bot.tileMenu.refresh());
                                        }
                                        return SideEffect.all(effects);
                                    });
                        });

        // Forage in Meadow
        new Stratified<Ability>(events.ability, Labels.ability_forage_in_meadow).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Harvests natural items from meadows every 4 turns");
                    e.blob.setIcon(Labels.asset_pick_flowers);
                    return SideEffect.none;
                })
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver,
                                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(
                                view, receiver.wielder, view.game.mechanics.loot.getByTag(Labels.tag_natural),
                                (Building b) -> b.name.equals(Labels.building_meadow)));

        // Ghastly Thrall
        new Stratified<Ability>(events.ability, Labels.ability_ghastly_thrall).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format(
                            "This unit can only move to tiles adjacent to The Necromancer, and will follow The Necromancer as it moves");
                    e.blob.setIcon(Labels.asset_raise_undead);
                    return SideEffect.none;
                })
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver,
                                Events.SpawnEvent e) -> () -> view.game.events.signals
                                        .addListener(Events.AfterUnitMovedEvent.class, receiver))
                .add(Events.AfterUnitMovedEvent.class,
                        (GameView view, Ability receiver, Events.AfterUnitMovedEvent e) -> {
                            if (e.unit.name.equals(Labels.unit_necromancer)
                                    && e.unit.leadership.sameLeader(receiver.wielder)) {
                                receiver.wielder.movement.turnOffPreCheck();
                                SideEffect se = receiver.wielder.movement.move(view, e.previous, e.parallel);
                                receiver.wielder.movement.turnOnPreCheck();
                                return se;
                            }
                            return SideEffect.none;
                        })
                .add(Events.CanUnitMoveEvent.class, (GameView view, Ability receiver, Events.CanUnitMoveEvent e) -> {
                    final Set<Point> radius = Lambda.filter(
                            (Point p) -> view.game.world.getUnit(p)
                                    .map((Unit u) -> u.name.equals(Labels.unit_necromancer)).orElse(false),
                            Hexagons.getNeighbors(e.tile.getPoint(), 1));
                    if (radius.size() == 0) {
                        e.canWalkOnBuilding = false;
                    }
                    return SideEffect.none;
                });

        // Gilded Strike
        new Stratified<Ability>(events.ability, Labels.ability_gilded_strike).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Deals 12 damage and generates 10 gold");
                    e.blob.setIcon(Labels.asset_gilded_strike);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic
                                .attackAndEffect(view, receiver.wielder, new Damage(12), 1, Optional.of((Point p) -> {
                                    return () -> {
                                        receiver.wielder.getLeader().ifPresent((Player l) -> {
                                            l.gold += 10;
                                        });
                                        view.hud.top.update(view.game);
                                    };
                                })));

        // Green Fortress
        new Stratified<Ability>(events.ability, Labels.ability_green_fortress).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Extra defense on forests");
                    e.blob.setIcon(Labels.asset_green_fortress);
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class, (GameView view, Ability receiver, Events.TakeDamageEvent e) -> {
                    boolean isForest = view.game.world.getTile(receiver.wielder.getPoint())
                            .flatMap((Tile t) -> t.building).map((Building b) -> b.name.equals(Labels.building_forest))
                            .orElse(false);
                    return isForest ? AbilityLogic.defense(e, 2) : SideEffect.none;
                });

        // Harvest Goo
        new Stratified<Ability>(events.ability, Labels.ability_harvest_goo).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Harvests goo from mines every 4 turns";
                    e.blob.setIcon(Labels.asset_harvest_slime);
                    return SideEffect.none;
                })
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver,
                                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(
                                view, receiver.wielder, view.game.mechanics.loot.getByTag(Labels.tag_goo),
                                (Building b) -> b.name.equals(Labels.building_mine)));

        // Harvest Mushrooms
        new Stratified<Ability>(events.ability, Labels.ability_harvest_mushrooms).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Harvests mushrooms from forests and mines every 4 turns";
                    e.blob.setIcon(Labels.asset_harvest_mushroom);
                    return SideEffect.none;
                })
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver,
                                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(
                                view, receiver.wielder, view.game.mechanics.loot.getByTag(Labels.tag_mushroom),
                                (Building b) -> b.name.equals(Labels.building_forest)
                                        || b.name.equals(Labels.building_mine)));

        // Heal Wounds
        new Stratified<Ability>(events.ability, Labels.ability_heal_wounds).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Heals 5 damage";
                    e.blob.setIcon(Labels.asset_heal_wounds);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class, (GameView view, Ability receiver,
                        Events.AbilityActivatedEvent e) -> AbilityLogic.healUnit(view, receiver.wielder, 5));

        // Hug
        new Stratified<Ability>(events.ability, Labels.ability_hug).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Heals the target adjacent unit for a few hit points");
                    e.blob.setIcon(Labels.asset_hug);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class, (GameView view, Ability receiver,
                        Events.AbilityActivatedEvent e) -> AbilityLogic.healUnit(view, receiver.wielder, 2));

        // Hungry Frog Magic
        new Stratified<Ability>(events.ability, Labels.ability_hungry_frog_magic).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Consumes all stored items and heals adjacent friendly units");
                    e.blob.setIcon(Labels.asset_hungry_frog_magic);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            List<SideEffect> effects = SideEffect.list(() -> receiver.wielder.haul.empty());
                            Set<Point> targets = Hexagons.getNeighbors(receiver.wielder.getPoint(), 1);
                            for (Point p : targets) {
                                Optional<Unit> u = view.game.world.getUnit(p);
                                if (u.map((Unit u1) -> u1.isFriendly(receiver.wielder)).orElse(false)) {
                                    effects.add(receiver.wielder.combat.heal(view, u.get(), 10));
                                }
                            }
                            return SideEffect.all(effects);
                        });

        // Hunt Fish
        new Stratified<Ability>(events.ability, Labels.ability_hunt_fish).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Harvests fish from water tiles");
                    e.blob.setIcon(Labels.asset_hunt_fish);
                    return SideEffect.none;
                })
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver,
                                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromTile(view,
                                receiver.wielder, Labels.item_fish, (Tile t) -> t.name.equals(Labels.tile_water)));

        // Hurl Rock
        new Stratified<Ability>(events.ability, Labels.ability_hurl_rock).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Deals 5 damage with a 15% chance to stun";
                    e.blob.setIcon(Labels.asset_fire_cannon);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic
                                .attackAndEffect(view, receiver.wielder, new Damage(5), 2, Optional.of((Point p) -> {
                                    Optional<Unit> u = view.game.world.getUnit(p);
                                    return u.isPresent() && Lambda.chance(15)
                                            ? u.get().abilities.addStatusEffect(view, Labels.status_effect_stunned)
                                            : SideEffect.none;
                                })));

        // Inject Poison
        new Stratified<Ability>(events.ability, Labels.ability_inject_poison).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Deals 5 damage and poisons the target";
                    e.blob.setIcon(Labels.asset_bite, 0xffffff, 0x3dac2a);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic
                                .attackAndEffect(view, receiver.wielder, new Damage(5), 1, Optional.of((Point p) -> {
                                    final Optional<Unit> u = view.game.world.getUnit(p);
                                    return u.isPresent()
                                            ? u.get().abilities.addStatusEffect(view, Labels.status_effect_poisoned)
                                            : SideEffect.none;
                                })));

        // Life Aura
        new Stratified<Ability>(events.ability, Labels.ability_life_aura).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Generates 3 unit points per turn");
                    e.blob.setIcon(Labels.asset_life_aura);
                    return SideEffect.none;
                })
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver,
                                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 1, true))
                .add("Tick", (GameView view, Ability receiver, Events.RepeatedEvent e) -> () -> receiver.wielder
                        .getLeader().ifPresent((Player p) -> p.addUnitPoints(view, receiver.wielder.getPoint(), 3)));

        // Liquifying Presence
        new Stratified<Ability>(events.ability, Labels.ability_liquifying_presence)
                .add(Events.GenerateAbilityEvent.class,
                        (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                            e.blob.desc = String.format("Deals 3 damage each turn to an occupied passive building");
                            e.blob.setIcon(Labels.asset_liquifying_presence);
                            return SideEffect.none;
                        })
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver,
                                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 1, true))
                .add("Tick", (GameView view, Ability receiver, Events.RepeatedEvent e) -> {
                    Optional<Building> b = view.game.world.getTile(receiver.wielder.getPoint())
                            .flatMap((Tile t) -> t.building);
                    return b.map((Building b1) -> !b1.isActive()).orElse(false)
                            ? b.get().combat.takeDamage(view, new Damage(3), receiver.wielder)
                            : SideEffect.none;
                });

        // Local Defender
        new Stratified<Ability>(events.ability, Labels.ability_local_defender).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Adjacent buildings have +3 armor");
                    e.blob.setIcon(Labels.asset_local_defender);
                    return SideEffect.none;
                })
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver,
                                Events.SpawnEvent e) -> () -> view.game.events.signals
                                        .addListener(Events.AttackedEvent.class, receiver))
                .add(Events.AttackedEvent.class, (GameView view, Ability receiver, Events.AttackedEvent e) -> {
                    if (e.target.isEntityType(EntityType.BUILDING)
                            && receiver.wielder.getLeader().equals(e.target.getLeader())
                            && Hexagons.areNeighbors(receiver.wielder.getPoint(), e.target.getPoint())) {
                        e.dmg.base -= 3;
                    }
                    return SideEffect.none;
                });

        // Loose Gems
        new Stratified<Ability>(events.ability, Labels.ability_loose_gems).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "15% chance to generate an emerald when the unit attacks";
                    e.blob.setIcon(Labels.asset_gems);
                    return SideEffect.none;
                }).add(Events.AttackEvent.class, (GameView view, Ability receiver, Events.AttackEvent e) -> {
                    return !receiver.wielder.haul.isFull() && Lambda.chance(15)
                            ? () -> receiver.wielder.haul
                                    .add(view.game.mechanics.loot.dropByTag(view.game, Labels.tag_gem))
                            : SideEffect.none;
                });

        // Market Boom
        new Stratified<Ability>(events.ability, Labels.ability_market_boom).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Attacks generate 4 auction points");
                    e.blob.setIcon(Labels.asset_market_boom);
                    return SideEffect.none;
                }).add(Events.AttackEvent.class, (GameView view, Ability receiver, Events.AttackEvent e) -> AbilityLogic
                        .generateAuctionPoints(view, receiver.wielder, 4));

        // Market Indicator
        new Stratified<Ability>(events.ability, Labels.ability_market_indicator).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Generates 3 auction points when adjacent to a vault");
                    e.blob.setIcon(Labels.asset_market_indicator);
                    return SideEffect.none;
                })
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver,
                                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 1, true))
                .add("Tick", (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.doWhenAdjacent(
                        view, receiver.wielder,
                        (Tile t) -> t.building.map((Building b) -> b.name.equals(Labels.building_vault)).orElse(false),
                        () -> AbilityLogic.generateAuctionPoints(view, receiver.wielder, 1)));

        // Market Value Goo
        new Stratified<Ability>(events.ability, Labels.ability_market_value_goo).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "20% chance to spawn goo when this unit moves. This goo generates auction points.";
                    e.blob.setIcon(Labels.asset_market_value_goo);
                    return SideEffect.none;
                }).add(Events.UnitMovedEvent.class, (GameView view, Ability receiver, Events.UnitMovedEvent e) -> {
                    final Point p = receiver.wielder.getPoint();
                    return view.game.world.getTile(p).map((Tile t) -> !t.building.isPresent()).orElse(false)
                            && Lambda.chance(20)
                                    ? () -> view.game.generator.building(Labels.building_market_value_goo, p.x, p.y)
                                            .spawn(view)
                                    : SideEffect.none;
                });

        // Metabolize
        new Stratified<Ability>(events.ability, Labels.ability_metabolize).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Consumes 1 random hauled item to move faster for the next 2 turns");
                    e.blob.setIcon(Labels.asset_eat);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            if (!receiver.wielder.haul.hasItems()) {
                                if (receiver.wielder.leadership.belongsToHuman()) {
                                    view.hud.logger.error("No items to metabolize");
                                }
                                return SideEffect.none;
                            }
                            return SideEffect.all(() -> receiver.wielder.haul.remove(receiver.wielder.haul.random()),
                                    receiver.wielder.abilities.addStatusEffect(view, Labels.status_effect_swift));
                        });

        // Mine Gems
        new Stratified<Ability>(events.ability, Labels.ability_mine_gems).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Harvests gems from mines");
                    e.blob.setIcon(Labels.asset_mine_gems);
                    return SideEffect.none;
                })
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver,
                                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(
                                view, receiver.wielder, view.game.mechanics.loot.getByTag(Labels.tag_gem),
                                (Building b) -> b.name.equals(Labels.building_mine)));

        // Mine Gold
        new Stratified<Ability>(events.ability, Labels.ability_mine_gold).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Harvests gold coins from mines every 4 turns";
                    e.blob.setIcon(Labels.asset_mine_gold);
                    return SideEffect.none;
                })
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver,
                                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(
                                view, receiver.wielder, Labels.item_gold_coin,
                                (Building b) -> b.name.equals(Labels.building_mine)));

        // Mountain Strider
        new Stratified<Ability>(events.ability, Labels.ability_mountain_strider).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("This unit can traverse mountains");
                    e.blob.setIcon(Labels.asset_mountain_strider);
                    return SideEffect.none;
                }).add(Events.CanUnitMoveEvent.class, (GameView view, Ability receiver, Events.CanUnitMoveEvent e) -> {
                    if (e.tile.building.map((Building b) -> b.name.equals(Labels.building_mountain)).orElse(false)) {
                        e.canWalkOnBuilding = true;
                    }
                    return SideEffect.none;
                });

        // Necrotic Blast
        new Stratified<Ability>(events.ability, Labels.ability_necrotic_blast).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Deals 8 damage");
                    e.blob.setIcon(Labels.asset_fireball, 0xdbc626, 0x53cb51);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.attack(view,
                                receiver.wielder, new Damage(8), 3));

        // Night Vision
        new Stratified<Ability>(events.ability, Labels.ability_night_vision).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("This unit can see normally at night");
                    e.blob.setIcon(Labels.asset_night_vision);
                    return SideEffect.none;
                }).add(Events.GetVisionEvent.class, (GameView view, Ability receiver, Events.GetVisionEvent e) -> {
                    e.canSeeAtNight = true;
                    return SideEffect.none;
                });

        // Pebble Shot
        new Stratified<Ability>(events.ability, Labels.ability_pebble_shot).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Deals 5 damage";
                    e.blob.setIcon(Labels.asset_fire_cannon);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.attack(view,
                                receiver.wielder, new Damage(5), 3));

        // Pick Apples
        new Stratified<Ability>(events.ability, Labels.ability_pick_apples).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Harvests apples from forests every 4 turns";
                    e.blob.setIcon(Labels.asset_pick_apples);
                    return SideEffect.none;
                })
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver,
                                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(
                                view, receiver.wielder, Labels.item_apple,
                                (Building b) -> b.name.equals(Labels.building_forest)));

        // Pious
        new Stratified<Ability>(events.ability, Labels.ability_pious).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("This unit generates +1 favor");
                    e.blob.setIcon(Labels.asset_worship_glyph);
                    return SideEffect.none;
                }).add(Events.GenerateFavorEvent.class,
                        (GameView view, Ability receiver, Events.GenerateFavorEvent e) -> {
                            e.favor += 1;
                            return SideEffect.none;
                        });

        // Plant Forest
        new Stratified<Ability>(events.ability, Labels.ability_plant_forest).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Plants a forest";
                    e.blob.setIcon(Labels.asset_plant_forest);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.build(view,
                                receiver.wielder, Labels.building_forest,
                                (Tile t) -> t.name.equals(Labels.tile_grass)));

        // Plant Meadow
        new Stratified<Ability>(events.ability, Labels.ability_plant_meadow).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Plants a meadow";
                    e.blob.setIcon(Labels.asset_plant_meadow);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.build(view,
                                receiver.wielder, Labels.building_meadow,
                                (Tile t) -> t.name.equals(Labels.tile_grass)));

        // Plate Mail
        new Stratified<Ability>(events.ability, Labels.ability_plate_mail).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Extra defense");
                    e.blob.setIcon(Labels.asset_defense);
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class,
                        (GameView view, Ability receiver, Events.TakeDamageEvent e) -> AbilityLogic.defense(e, 2));

        // Protective Spores
        new Stratified<Ability>(events.ability, Labels.ability_protective_spores).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Grants the target +2 defense for the next 2 turns";
                    e.blob.setIcon(Labels.asset_spores);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            final Set<Point> points = Lambda.filter((Point p) -> view.game.world.getUnit(p).isPresent(),
                                    Hexagons.getNeighbors(receiver.wielder.getPoint(), 1));
                            return receiver.wielder.getLeader().get().select(view, points,
                                    "No valid targets for extra defense", (Point p) -> {
                                        final Unit target = view.game.world.getUnit(p).get();
                                        return SideEffect.all(
                                                target.abilities.addStatusEffect(view,
                                                        Labels.status_effect_extra_defense),
                                                () -> view.game.actions.unitHasCastSpell(view, receiver.wielder));
                                    });
                        });

        // Pummel
        new Stratified<Ability>(events.ability, Labels.ability_pummel).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Deals 8 damage");
                    e.blob.setIcon(Labels.asset_smash);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.attack(view,
                                receiver.wielder, new Damage(8), 1));

        // Raise Undead
        new Stratified<Ability>(events.ability, Labels.ability_raise_undead).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Consumes 20 health to spawn a Ghastly Thrall (max one at a time, remains adjacent to this unit)";
                    e.blob.setIcon(Labels.asset_raise_undead);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            // Check for any existing Ghastly Thrall
                            for (Point p : Hexagons.getNeighbors(receiver.wielder.getPoint(), 1)) {
                                if (view.game.world.getUnit(p)
                                        .map((Unit u) -> u.name.equals(Labels.unit_ghastly_thrall)).orElse(false)) {
                                    if (receiver.wielder.leadership.belongsToHuman()) {
                                        view.hud.logger.error("You can only raise one Ghastly Thrall at a time");
                                    }
                                    return SideEffect.none;
                                }
                            }

                            // Select a Tile to spawn the Unit on
                            final Set<Point> points = Lambda.filter(
                                    (Point p) -> view.game.world.getTile(p)
                                            .map((Tile t) -> !t.unit.isPresent()
                                                    && t.leader.equals(receiver.wielder.getLeader()))
                                            .orElse(false),
                                    Hexagons.getNeighbors(receiver.wielder.getPoint(), 1));
                            return receiver.wielder.getLeader().get().select(view, points, "Nowhere to spawn unit",
                                    (Point p) -> {
                                        return () -> {
                                            view.game.actions.unitHasCastSpell(view, receiver.wielder);
                                            receiver.wielder.combat.takeDamage(view, new Damage(20), receiver.wielder);
                                            final Unit u = view.game.generator.unit(Labels.unit_ghastly_thrall, p.x,
                                                    p.y);
                                            view.game.setLeader(view, u, receiver.wielder.getLeader());
                                            u.spawn(view);
                                        };
                                    });
                        });

        // Regeneration
        new Stratified<Ability>(events.ability, Labels.ability_regeneration).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("This unit heals a little each turn");
                    e.blob.setIcon(Labels.asset_regeneration);
                    return SideEffect.none;
                })
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver,
                                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 1, true))
                .add("Tick", (GameView view, Ability receiver, Events.RepeatedEvent e) -> receiver.wielder.combat
                        .heal(view, 1));

        // Remove Poison
        new Stratified<Ability>(events.ability, Labels.ability_remove_poison).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Removes a Poisoned effect from the target and heals both units");
                    e.blob.setIcon(Labels.asset_heal_wounds, 0xaa2007, 0x3dac2a);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            final Set<Point> targets = Lambda.filter((Point p) -> view.game.world.getUnit(p)
                                    .map((Unit u) -> u.leadership.sameLeader(receiver.wielder)
                                            && u.abilities.hasStatusEffect(Labels.status_effect_poisoned))
                                    .orElse(false), Hexagons.getNeighbors(receiver.wielder.getPoint(), 1));
                            return receiver.wielder.getLeader().get().select(view, targets,
                                    "No poisoned targets in range", (Point p) -> {
                                        return () -> {
                                            view.game.world.getUnit(p).ifPresent((Unit u) -> u.abilities
                                                    .removeStatusEffect(view, Labels.status_effect_poisoned));
                                            view.game.actions.unitHasCastSpell(view, receiver.wielder);
                                        };
                                    });
                        });

        // Revenge of the Forest
        new Stratified<Ability>(events.ability, Labels.ability_revenge_of_the_forest).add(
                Events.GenerateAbilityEvent.class, (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Deals 8 damage (or 12 damage when on a forest)");
                    e.blob.setIcon(Labels.asset_sword_slash, 0xffffff, 0x00ff00);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver,
                                Events.AbilityActivatedEvent e) -> AbilityLogic
                                        .dynamicDamageAttack(view, receiver.wielder, 1,
                                                (Tile t) -> new Damage(t.building
                                                        .map((Building b) -> b.name.equals(Labels.building_forest))
                                                        .orElse(false) ? 12 : 8)));

        // Running Through Nature
        new Stratified<Ability>(events.ability, Labels.ability_running_through_nature).add(
                Events.GenerateAbilityEvent.class, (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("This unit is faster on passive buildings");
                    e.blob.setIcon(Labels.asset_running_through_nature);
                    return SideEffect.none;
                }).add(Events.UnitMoveDistanceEvent.class,
                        (GameView view, Ability receiver, Events.UnitMoveDistanceEvent e) -> {
                            boolean buildingIsPassive = view.game.world.getTile(e.unit.getPoint())
                                    .flatMap((Tile t) -> t.building).map((Building b) -> !b.isActive()).orElse(false);
                            if (buildingIsPassive) {
                                e.distance++;
                            }
                            return SideEffect.none;
                        });

        // Self Sacrifice
        new Stratified<Ability>(events.ability, Labels.ability_self_sacrifice).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Transfers all their health but 1 to the target unit");
                    e.blob.setIcon(Labels.asset_self_sacrifice);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> {
                            final int hitPoints = receiver.wielder.combat.health.get() - 1;
                            return SideEffect.all(AbilityLogic.healUnit(view, receiver.wielder, hitPoints),
                                    () -> receiver.wielder.combat.health.set(1));
                        });

        // Sacred Seeds
        new Stratified<Ability>(events.ability, Labels.ability_sacred_seeds).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Harvests seeds from meadows that can be consumed to generate favor");
                    e.blob.setIcon(Labels.asset_deposit_seeds);
                    return SideEffect.none;
                })
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver,
                                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(
                                view, receiver.wielder, Labels.item_sacred_seed,
                                (Building b) -> b.name.equals(Labels.building_meadow)));

        // Shell Defense
        new Stratified<Ability>(events.ability, Labels.ability_shell_defense).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Extra defense");
                    e.blob.setIcon(Labels.asset_defense);
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class,
                        (GameView view, Ability receiver, Events.TakeDamageEvent e) -> AbilityLogic.defense(e, 2));

        // Shield Defense
        new Stratified<Ability>(events.ability, Labels.ability_shield_defense).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Extra defense");
                    e.blob.setIcon(Labels.asset_defense);
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class,
                        (GameView view, Ability receiver, Events.TakeDamageEvent e) -> AbilityLogic.defense(e, 2));

        // Slime Shot
        new Stratified<Ability>(events.ability, Labels.ability_slime_shot).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Deals 8 damage");
                    e.blob.setIcon(Labels.asset_slime_shot);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.attack(view,
                                receiver.wielder, new Damage(8), 3));

        // Smash
        new Stratified<Ability>(events.ability, Labels.ability_smash).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Deals 8 damage with a 15% chance to stun";
                    e.blob.setIcon(Labels.asset_smash);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic
                                .attackAndEffect(view, receiver.wielder, new Damage(8), 1, Optional.of((Point p) -> {
                                    Optional<Unit> u = view.game.world.getUnit(p);
                                    return u.isPresent() && Lambda.chance(15)
                                            ? u.get().abilities.addStatusEffect(view, Labels.status_effect_stunned)
                                            : SideEffect.none;
                                })));

        // Stomp
        new Stratified<Ability>(events.ability, Labels.ability_stomp).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Deals 5 damage with a 15% chance to stun";
                    e.blob.setIcon(Labels.asset_stomp);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic
                                .attackAndEffect(view, receiver.wielder, new Damage(5), 1, Optional.of((Point p) -> {
                                    Optional<Unit> u = view.game.world.getUnit(p);
                                    return u.isPresent() && Lambda.chance(15)
                                            ? u.get().abilities.addStatusEffect(view, Labels.status_effect_stunned)
                                            : SideEffect.none;
                                })));

        // Stone Defense
        new Stratified<Ability>(events.ability, Labels.ability_stone_defense).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Extra defense");
                    e.blob.setIcon(Labels.asset_defense);
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class,
                        (GameView view, Ability receiver, Events.TakeDamageEvent e) -> AbilityLogic.defense(e, 2));

        // Subterranean Potions
        new Stratified<Ability>(events.ability, Labels.ability_subterranean_potions)
                .add(Events.GenerateAbilityEvent.class,
                        (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                            e.blob.desc = String.format("Generates Health Potions from Mines");
                            e.blob.setIcon(Labels.asset_subterranean_potions);
                            return SideEffect.none;
                        })
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver,
                                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(
                                view, receiver.wielder, Labels.item_health_potion,
                                (Building b) -> b.name.equals(Labels.building_mine)));

        // Swim
        new Stratified<Ability>(events.ability, Labels.ability_swim).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "This unit can swim on water tiles";
                    e.blob.setIcon(Labels.asset_swim);
                    return SideEffect.none;
                }).add(Events.CanUnitMoveEvent.class, (GameView view, Ability receiver, Events.CanUnitMoveEvent e) -> {
                    if (!e.canWalkOnTile && e.tile.name.equals(Labels.tile_water)) {
                        e.canWalkOnTile = true;
                    }
                    return SideEffect.none;
                });

        // Swing Axe
        new Stratified<Ability>(events.ability, Labels.ability_swing_axe).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Deals 12 damage";
                    e.blob.setIcon(Labels.asset_axe_swing);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.attack(view,
                                receiver.wielder, new Damage(12), 1));

        // Sword Slash
        new Stratified<Ability>(events.ability, Labels.ability_sword_slash).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    Damage dmg = new Damage(5);
                    e.blob.desc = String.format("Deals 8 damage", dmg);
                    e.blob.setIcon(Labels.asset_sword_slash);
                    return SideEffect.none;
                }).add(Events.AbilityActivatedEvent.class,
                        (GameView view, Ability receiver, Events.AbilityActivatedEvent e) -> AbilityLogic.attack(view,
                                receiver.wielder, new Damage(8), 1));

        // Thorny Skin
        new Stratified<Ability>(events.ability, Labels.ability_thorny_skin).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("Adjacent attackers take damage");
                    e.blob.setIcon(Labels.asset_thorny_skin);
                    return SideEffect.none;
                }).add(Events.AttackedEvent.class, (GameView view, Ability receiver, Events.AttackedEvent e) -> {
                    if (e.attacker instanceof Unit) {
                        Unit target = (Unit) e.target;
                        Unit attacker = (Unit) e.attacker;
                        return Hexagons.areNeighbors(attacker.getPoint(), target.getPoint())
                                ? attacker.combat.takeDamage(view, new Damage(2), target)
                                : SideEffect.none;
                    }
                    return SideEffect.none;
                });

        // Trade
        new Stratified<Ability>(events.ability, Labels.ability_trade).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = "Harvests gold coins from vaults every 4 turns";
                    e.blob.setIcon(Labels.asset_trade);
                    return SideEffect.none;
                })
                .add(Events.SpawnEvent.class,
                        (GameView view, Ability receiver,
                                Events.SpawnEvent e) -> () -> view.game.future.addFutureTick("Tick", receiver, 4, true))
                .add("Tick",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> AbilityLogic.harvestFromBuilding(
                                view, receiver.wielder, Labels.item_gold_coin,
                                (Building b) -> b.name.equals(Labels.building_vault)));

        /**
         * SECTION Status Effects
         */

        // Stunned
        new Stratified<Ability>(events.ability, Labels.status_effect_stunned).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("The unit cannot act for 1 turn");
                    e.blob.setIcon(Labels.asset_stunned);
                    return SideEffect.none;
                }).add(Events.StatusEffectAddedEvent.class,
                        (GameView view, Ability receiver, Events.StatusEffectAddedEvent e) -> {
                            view.game.future.addFutureTick("Tick", receiver, 1, false);
                            return SideEffect.none;
                        })
                .add("Tick",
                        (GameView view, Ability receiver,
                                Events.RepeatedEvent e) -> () -> receiver.wielder.abilities.removeStatusEffect(view,
                                        receiver))
                .add(Events.IsStunnedEvent.class, (GameView view, Ability receiver, Events.IsStunnedEvent e) -> {
                    e.isStunned = true;
                    return SideEffect.none;
                });

        // More Favor
        new Stratified<Ability>(events.ability, Labels.status_effect_more_favor).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("+1 favor next time the unit generates it");
                    e.blob.setIcon(Labels.asset_worship_glyph);
                    return SideEffect.none;
                }).add(Events.GenerateFavorEvent.class,
                        (GameView view, Ability receiver, Events.GenerateFavorEvent e) -> {
                            receiver.wielder.abilities.removeStatusEffect(view, receiver);
                            e.favor += 1;
                            return SideEffect.none;
                        });

        // Proud Builder
        new Stratified<Ability>(events.ability, Labels.status_effect_proud_builder)
                .add(Events.GenerateAbilityEvent.class,
                        (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                            e.blob.desc = String.format("+2 attack and defense for 2 turns");
                            e.blob.setIcon(Labels.asset_proud_builder);
                            return SideEffect.none;
                        })
                .add(Events.StatusEffectAddedEvent.class,
                        (GameView view, Ability receiver, Events.StatusEffectAddedEvent e) -> {
                            view.game.future.addFutureTick("Tick", receiver, 2, false);
                            return SideEffect.none;
                        })
                .add("Tick",
                        (GameView view, Ability receiver,
                                Events.RepeatedEvent e) -> () -> receiver.wielder.abilities.removeStatusEffect(view,
                                        receiver))
                .add(Events.TakeDamageEvent.class, (GameView view, Ability receiver, Events.TakeDamageEvent e) -> {
                    e.dmg.base -= 2;
                    return SideEffect.none;
                }).add(Events.AttackEvent.class, (GameView view, Ability receiver, Events.AttackEvent e) -> {
                    e.dmg.base += 2;
                    return SideEffect.none;
                });

        // Extra Defense
        new Stratified<Ability>(events.ability, Labels.status_effect_extra_defense)
                .add(Events.GenerateAbilityEvent.class,
                        (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                            e.blob.desc = String.format("+2 defense for 2 turns");
                            e.blob.setIcon(Labels.asset_shield);
                            return SideEffect.none;
                        })
                .add(Events.StatusEffectAddedEvent.class,
                        (GameView view, Ability receiver, Events.StatusEffectAddedEvent e) -> {
                            view.game.future.addFutureTick("Tick", receiver, 2, false);
                            return SideEffect.none;
                        })
                .add("Tick",
                        (GameView view, Ability receiver,
                                Events.RepeatedEvent e) -> () -> receiver.wielder.abilities.removeStatusEffect(view,
                                        receiver))
                .add(Events.TakeDamageEvent.class, (GameView view, Ability receiver, Events.TakeDamageEvent e) -> {
                    e.dmg.base -= 2;
                    return SideEffect.none;
                });

        // Poisoned
        new Stratified<Ability>(events.ability, Labels.status_effect_poisoned).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("The unit takes 2 damage each turn for 4 turns");
                    e.blob.setIcon(Labels.asset_poisoned);
                    return SideEffect.none;
                }).add(Events.StatusEffectAddedEvent.class,
                        (GameView view, Ability receiver, Events.StatusEffectAddedEvent e) -> {
                            view.game.future.addFutureTick("Remove", receiver, 4, false);
                            view.game.future.addFutureTick("Poison", receiver, 1, true);
                            return SideEffect.none;
                        })
                .add("Remove",
                        (GameView view, Ability receiver, Events.RepeatedEvent e) -> SideEffect.all(
                                () -> view.game.future.removeFutureEvents(receiver, "Poison"),
                                () -> receiver.wielder.abilities.removeStatusEffect(view, receiver)))
                .add("Poison", (GameView view, Ability receiver, Events.RepeatedEvent e) -> {
                    return receiver.wielder.combat.takeDamage(view, new Damage(2), receiver.wielder);
                });

        // Swift
        new Stratified<Ability>(events.ability, Labels.status_effect_swift).add(Events.GenerateAbilityEvent.class,
                (GameView view, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.desc = String.format("The unit can move an extra space for the next 2 turns");
                    e.blob.setIcon(Labels.asset_swift);
                    return SideEffect.none;
                }).add(Events.StatusEffectAddedEvent.class,
                        (GameView view, Ability receiver, Events.StatusEffectAddedEvent e) -> {
                            view.game.future.addFutureTick("Tick", receiver, 2, false);
                            return SideEffect.none;
                        })
                .add("Tick",
                        (GameView view, Ability receiver,
                                Events.RepeatedEvent e) -> () -> receiver.wielder.abilities.removeStatusEffect(view,
                                        receiver))
                .add(Events.UnitMoveDistanceEvent.class,
                        (GameView view, Ability receiver, Events.UnitMoveDistanceEvent e) -> {
                            e.distance++;
                            return SideEffect.none;
                        });

        /**
         * SECTION Items
         */

        // Goo
        new Stratified<Item>(events.item, Labels.item_goo)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to squish the goo";
                    e.blob.icon = Optional.of(Labels.asset_slime);
                    e.blob.gold = 1;
                    e.blob.tags.add(Labels.tag_goo);
                    return SideEffect.none;
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> SideEffect.none);

        // Slime Armor
        new Stratified<Item>(events.item, Labels.item_slime_armor)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 defense";
                    e.blob.icon = Optional.of(Labels.asset_chestplate);
                    e.blob.gold = 1;
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    e.dmg.base -= 2;
                    return SideEffect.none;
                });

        // Golden Spear
        new Stratified<Item>(events.item, Labels.item_golden_spear)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+1 damage";
                    e.blob.icon = Optional.of(Labels.asset_spear);
                    e.blob.gold = 10;
                    return SideEffect.none;
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    e.dmg.base += 2;
                    return SideEffect.none;
                });

        // Mushroom
        new Stratified<Item>(events.item, Labels.item_mushroom)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to look at this mushroom";
                    e.blob.icon = Optional.of(Labels.asset_mushroom);
                    e.blob.gold = 1;
                    e.blob.tags.add(Labels.tag_mushroom);
                    e.blob.tags.add(Labels.tag_natural);
                    return SideEffect.none;
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> SideEffect.none);

        // Sacred seed
        new Stratified<Item>(events.item, Labels.item_sacred_seed)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to generate extra favor";
                    e.blob.icon = Optional.of(Labels.asset_seeds);
                    e.blob.gold = 1;
                    e.blob.tags.add(Labels.tag_natural);
                    return SideEffect.none;
                }).add(Events.ItemConsumedEvent.class, (GameView view, Item receiver, Events.ItemConsumedEvent e) -> {
                    return e.consumer.abilities.addStatusEffect(view, Labels.status_effect_more_favor);
                });

        // Flower
        new Stratified<Item>(events.item, Labels.item_flower)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to smell a sweet flower";
                    e.blob.icon = Optional.of(Labels.asset_flower);
                    e.blob.gold = 1;
                    e.blob.tags.add(Labels.tag_natural);
                    return SideEffect.none;
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> SideEffect.none);

        // Fish
        new Stratified<Item>(events.item, Labels.item_fish)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to stave off hunger";
                    e.blob.icon = Optional.of(Labels.asset_fish);
                    e.blob.gold = 1;
                    return SideEffect.none;
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.food(view, e));

        // Gold Coin
        new Stratified<Item>(events.item, Labels.item_gold_coin)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to increase your gold";
                    e.blob.icon = Optional.of(Labels.asset_coin);
                    e.blob.gold = 1;
                    return SideEffect.none;
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.valuable(view, e));

        // Emerald
        new Stratified<Item>(events.item, Labels.item_emerald)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to increase your gold";
                    e.blob.icon = Optional.of(Labels.asset_crystal);
                    e.blob.gold = 10;
                    e.blob.tags.add(Labels.tag_gem);
                    return SideEffect.none;
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.valuable(view, e));

        // Apple
        new Stratified<Item>(events.item, Labels.item_apple)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to stave off hunger";
                    e.blob.icon = Optional.of(Labels.asset_apple);
                    e.blob.gold = 1;
                    e.blob.tags.add(Labels.tag_fruit);
                    e.blob.tags.add(Labels.tag_natural);
                    return SideEffect.none;
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.food(view, e));

        // Health Potion
        new Stratified<Item>(events.item, Labels.item_health_potion)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to heal by 5 hit points";
                    e.blob.icon = Optional.of(Labels.asset_potion);
                    e.blob.gold = 1;
                    return SideEffect.none;
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.potion(view, e, 5));

        // Incense
        // Sack of Gold
        new Stratified<Item>(events.item, Labels.item_sack_of_gold)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to generate 10 gold";
                    e.blob.icon = Optional.of(Labels.asset_pouch);
                    e.blob.gold = 10;
                    return SideEffect.none;
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> () -> {
                            e.consumer.getLeader().ifPresent((Player p) -> p.gold += 10);
                            view.hud.top.update(view.game);
                        });

        // Capital
        new Stratified<Item>(events.item, Labels.item_capital)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to generate 6 auction points";
                    e.blob.icon = Optional.of(Labels.asset_paper);
                    e.blob.gold = 6;
                    return SideEffect.none;
                }).add(Events.ItemConsumedEvent.class, (GameView view, Item receiver,
                        Events.ItemConsumedEvent e) -> AbilityLogic.generateAuctionPoints(view, e.consumer, 10));

        // Shellcap Armor
        // Stones
        // Sword
        new Stratified<Item>(events.item, Labels.item_sword)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+1 damage";
                    e.blob.icon = Optional.of(Labels.asset_sword);
                    e.blob.gold = 1;
                    return SideEffect.none;
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 1, true);
                    return SideEffect.none;
                });

        // Shield
        new Stratified<Item>(events.item, Labels.item_shield)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+1 armor";
                    e.blob.icon = Optional.of(Labels.asset_shield);
                    e.blob.gold = 1;
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 1, true);
                    return SideEffect.none;
                });

        // Staff
        new Stratified<Item>(events.item, Labels.item_staff)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+1 healing";
                    e.blob.icon = Optional.of(Labels.asset_staff);
                    e.blob.gold = 1;
                    return SideEffect.none;
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 1, true);
                    return SideEffect.none;
                });

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
        new Stratified<Item>(events.item, Labels.item_acidic_solute)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 damage for Plasmoids";
                    e.blob.icon = Optional.of(Labels.asset_slime);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 2, ((Unit) e.attacker).species.counts(Defs.species_plasmoid));
                    return SideEffect.none;
                });

        // Binding Solute
        new Stratified<Item>(events.item, Labels.item_binding_solute)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 armor for Plasmoids";
                    e.blob.icon = Optional.of(Labels.asset_slime);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 2, ((Unit) e.target).species.counts(Defs.species_plasmoid));
                    return SideEffect.none;
                });

        // Life-Giving Solute
        new Stratified<Item>(events.item, Labels.item_life_giving_solute)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 healing for Plasmoids";
                    e.blob.icon = Optional.of(Labels.asset_slime);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 2, ((Unit) e.healer).species.counts(Defs.species_plasmoid));
                    return SideEffect.none;
                });

        // Blessed Solute
        // Feather of Bravery
        new Stratified<Item>(events.item, Labels.item_feather_of_bravery)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 damage for Garudas";
                    e.blob.icon = Optional.of(Labels.asset_feather);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 2, ((Unit) e.attacker).species.counts(Defs.species_garuda));
                    return SideEffect.none;
                });

        // Iron Beak Brace
        new Stratified<Item>(events.item, Labels.item_iron_beak_brace)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 armor for Garudas";
                    e.blob.icon = Optional.of(Labels.asset_helmet);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 2, ((Unit) e.target).species.counts(Defs.species_garuda));
                    return SideEffect.none;
                });

        // Hollow Bone Rattle
        new Stratified<Item>(events.item, Labels.item_hollow_bone_rattle)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 healing for Garudas";
                    e.blob.icon = Optional.of(Labels.asset_rattle);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 2, ((Unit) e.healer).species.counts(Defs.species_garuda));
                    return SideEffect.none;
                });

        // Bag of Shiny Pebbles
        // Thorny Rose Staff
        new Stratified<Item>(events.item, Labels.item_thorny_rose_staff)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 damage for Sprites";
                    e.blob.icon = Optional.of(Labels.asset_staff);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 2, ((Unit) e.attacker).species.counts(Defs.species_sprite));
                    return SideEffect.none;
                });

        // Overgrown Shield
        new Stratified<Item>(events.item, Labels.item_overgrown_shield)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 armor for Sprites";
                    e.blob.icon = Optional.of(Labels.asset_shield);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 2, ((Unit) e.target).species.counts(Defs.species_sprite));
                    return SideEffect.none;
                });

        // Sap of Unbreaking
        new Stratified<Item>(events.item, Labels.item_sap_of_unbreaking)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 healing for Sprites";
                    e.blob.icon = Optional.of(Labels.asset_slime);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 2, ((Unit) e.healer).species.counts(Defs.species_sprite));
                    return SideEffect.none;
                });

        // Sacred Pollen
        // Advanced Spear
        new Stratified<Item>(events.item, Labels.item_advanced_spear)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 damage for Elves";
                    e.blob.icon = Optional.of(Labels.asset_spear);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 2, ((Unit) e.attacker).species.counts(Defs.species_elf));
                    return SideEffect.none;
                });

        // Sentinel's Shield
        new Stratified<Item>(events.item, Labels.item_sentinels_shield)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 armor for Elves";
                    e.blob.icon = Optional.of(Labels.asset_shield);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 2, ((Unit) e.target).species.counts(Defs.species_elf));
                    return SideEffect.none;
                });

        // Healing Incantation
        new Stratified<Item>(events.item, Labels.item_healing_incantation)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 healing for Elves";
                    e.blob.icon = Optional.of(Labels.asset_paper);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 2, ((Unit) e.healer).species.counts(Defs.species_elf));
                    return SideEffect.none;
                });

        // Devout Incantation
        // Great Hammer
        new Stratified<Item>(events.item, Labels.item_great_hammer)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 damage for Humans";
                    e.blob.icon = Optional.of(Labels.asset_hammer);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 2, ((Unit) e.attacker).species.counts(Defs.species_human));
                    return SideEffect.none;
                });

        // Battle Armaments
        new Stratified<Item>(events.item, Labels.item_battle_armaments)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 armor for Humans";
                    e.blob.icon = Optional.of(Labels.asset_chestplate);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 2, ((Unit) e.target).species.counts(Defs.species_human));
                    return SideEffect.none;
                });

        // Bandage Kit
        new Stratified<Item>(events.item, Labels.item_bandage_kit)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 healing for Humans";
                    e.blob.icon = Optional.of(Labels.asset_pouch);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 2, ((Unit) e.healer).species.counts(Defs.species_human));
                    return SideEffect.none;
                });

        // Dearly Held Idol
        // Stoneshell Mace
        new Stratified<Item>(events.item, Labels.item_stoneshell_mace)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 damage for Tortugans";
                    e.blob.icon = Optional.of(Labels.asset_mace);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 2, ((Unit) e.attacker).species.counts(Defs.species_tortugan));
                    return SideEffect.none;
                });

        // Shell Salve
        new Stratified<Item>(events.item, Labels.item_shell_salve)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 armor for Tortugans";
                    e.blob.icon = Optional.of(Labels.asset_potion);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 2, ((Unit) e.target).species.counts(Defs.species_tortugan));
                    return SideEffect.none;
                });

        // Shell-Sealing Goo
        new Stratified<Item>(events.item, Labels.item_shell_sealing_goo)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 healing for Tortugans";
                    e.blob.icon = Optional.of(Labels.asset_slime);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 2, ((Unit) e.healer).species.counts(Defs.species_tortugan));
                    return SideEffect.none;
                });

        // Sacred Shell Rattle
        // Blessed Charm
        // Bloody Totem
        // Phoenix Blossom
        // Sling and Stone
        // Life-Giving Elixir
        new Stratified<Item>(events.item, Labels.item_life_giving_elixir)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to generate 6 unit points";
                    e.blob.icon = Optional.of(Labels.asset_potion);
                    e.blob.rarity = Rarity.UNCOMMON;
                    e.blob.gold = 6;
                    return SideEffect.none;
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> () -> e.consumer.getLeader()
                                .ifPresent((Player p) -> p.addUnitPoints(view, e.consumer.getPoint(), 10)));

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
        // Floral Seeds
        new Stratified<Item>(events.item, Labels.item_floral_seeds)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to plant a meadow on a grass tile";
                    e.blob.icon = Optional.of(Labels.asset_seeds);
                    e.blob.rarity = Rarity.RARE;
                    e.blob.gold = 10;
                    e.blob.tags.add(Labels.tag_natural);
                    return SideEffect.none;
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> AbilityLogic.build(view,
                                e.consumer, Labels.building_meadow, (Tile t) -> t.name.equals(Labels.tile_grass)));

        // Arboreal Seeds
        new Stratified<Item>(events.item, Labels.item_arboreal_seeds)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to plant a forest on a grass tile";
                    e.blob.icon = Optional.of(Labels.asset_seeds);
                    e.blob.rarity = Rarity.RARE;
                    e.blob.gold = 10;
                    e.blob.tags.add(Labels.tag_natural);
                    return SideEffect.none;
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> AbilityLogic.build(view,
                                e.consumer, Labels.building_forest, (Tile t) -> t.name.equals(Labels.tile_grass)));

        // Arctic Seeds
        new Stratified<Item>(events.item, Labels.item_arctic_seeds)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to plant a taiga on a snow tile";
                    e.blob.icon = Optional.of(Labels.asset_seeds);
                    e.blob.rarity = Rarity.RARE;
                    e.blob.gold = 10;
                    e.blob.tags.add(Labels.tag_natural);
                    return SideEffect.none;
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> AbilityLogic.build(view,
                                e.consumer, Labels.building_taiga, (Tile t) -> t.name.equals(Labels.tile_snow)));

        // Cactus Seeds
        new Stratified<Item>(events.item, Labels.item_cactus_seeds)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to plant a shrubland on a sand tile";
                    e.blob.icon = Optional.of(Labels.asset_seeds);
                    e.blob.rarity = Rarity.RARE;
                    e.blob.gold = 10;
                    e.blob.tags.add(Labels.tag_natural);
                    return SideEffect.none;
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> AbilityLogic.build(view,
                                e.consumer, Labels.building_shrubland, (Tile t) -> t.name.equals(Labels.tile_sand)));

        // Pioneering Seeds
        new Stratified<Item>(events.item, Labels.item_pioneering_seeds)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to plant an oasis on a sand tile";
                    e.blob.icon = Optional.of(Labels.asset_seeds);
                    e.blob.rarity = Rarity.RARE;
                    e.blob.gold = 10;
                    e.blob.tags.add(Labels.tag_natural);
                    return SideEffect.none;
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> AbilityLogic.build(view,
                                e.consumer, Labels.building_oasis, (Tile t) -> t.name.equals(Labels.tile_sand)));

        // Digging Kit
        new Stratified<Item>(events.item, Labels.item_digging_kit)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to dig a mine";
                    e.blob.icon = Optional.of(Labels.asset_shovel);
                    e.blob.rarity = Rarity.RARE;
                    e.blob.gold = 10;
                    return SideEffect.none;
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> AbilityLogic.build(view,
                                e.consumer, Labels.building_mine, (Tile t) -> true));

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
        new Stratified<Item>(events.item, Labels.item_thoughtform_sword)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 damage for Tulpas";
                    e.blob.icon = Optional.of(Labels.asset_sword);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 2, ((Unit) e.attacker).species.counts(Defs.species_tulpa));
                    return SideEffect.none;
                });

        // Shield of the Subconscious
        new Stratified<Item>(events.item, Labels.item_shield_of_the_subconscious)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 armor for Tulpas";
                    e.blob.icon = Optional.of(Labels.asset_shield);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 2, ((Unit) e.target).species.counts(Defs.species_tulpa));
                    return SideEffect.none;
                });

        // Staff of Inner Peace
        new Stratified<Item>(events.item, Labels.item_staff_of_inner_peace)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 healing for Tulpas";
                    e.blob.icon = Optional.of(Labels.asset_staff);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 2, ((Unit) e.healer).species.counts(Defs.species_tulpa));
                    return SideEffect.none;
                });

        // Rod of Psychic Devotion
        // Tree Trunk Club
        new Stratified<Item>(events.item, Labels.item_tree_trunk_club)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 damage for Giants";
                    e.blob.icon = Optional.of(Labels.asset_club);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 2, ((Unit) e.attacker).species.counts(Defs.species_giant));
                    return SideEffect.none;
                });

        // Castle Gate Aegis
        new Stratified<Item>(events.item, Labels.item_castle_gate_aegis)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 armor for Giants";
                    e.blob.icon = Optional.of(Labels.asset_shield);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 2, ((Unit) e.target).species.counts(Defs.species_giant));
                    return SideEffect.none;
                });

        // Vase of Sacred Waters
        new Stratified<Item>(events.item, Labels.item_vase_of_sacred_waters)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 healing for Giants";
                    e.blob.icon = Optional.of(Labels.asset_vase);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 2, ((Unit) e.healer).species.counts(Defs.species_giant));
                    return SideEffect.none;
                });

        // Amulet of the Progenitors
        // Sharpened Quartz
        new Stratified<Item>(events.item, Labels.item_sharpened_quartz)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 damage for Golems";
                    e.blob.icon = Optional.of(Labels.asset_crystal);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 2, ((Unit) e.attacker).species.counts(Defs.species_golem));
                    return SideEffect.none;
                });

        // Igneous Armaments
        new Stratified<Item>(events.item, Labels.item_igneous_armaments)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 armor for Golems";
                    e.blob.icon = Optional.of(Labels.asset_chestplate);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 2, ((Unit) e.target).species.counts(Defs.species_golem));
                    return SideEffect.none;
                });

        // Moss-covered Stone
        new Stratified<Item>(events.item, Labels.item_moss_covered_stone)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 healing for Golems";
                    e.blob.icon = Optional.of(Labels.asset_stone);
                    e.blob.gold = 3;
                    return SideEffect.none;
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 2, ((Unit) e.healer).species.counts(Defs.species_golem));
                    return SideEffect.none;
                });

        // Glyphic Geode
        // Self-Sustaining Soulstone
        // Hero's Call
    }
}
