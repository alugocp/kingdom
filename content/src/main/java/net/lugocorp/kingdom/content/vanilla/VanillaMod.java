package net.lugocorp.kingdom.content.vanilla;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.builtin.logic.AbilityLogic;
import net.lugocorp.kingdom.builtin.logic.BuildingLogic;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.content.Labels;
import net.lugocorp.kingdom.engine.assets.SpriteLoader;
import net.lugocorp.kingdom.game.glyph.Glyph;
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
import net.lugocorp.kingdom.game.properties.InventoryType;
import net.lugocorp.kingdom.gameplay.actions.ActionType;
import net.lugocorp.kingdom.gameplay.combat.Damage;
import net.lugocorp.kingdom.gameplay.events.AllEventHandlers;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.gameplay.events.Stratified;
import net.lugocorp.kingdom.gameplay.events.contexts.GameViewContext;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.menu.game.ArtifactNode;
import net.lugocorp.kingdom.menu.game.FateNode;
import net.lugocorp.kingdom.menu.game.InventoryNode;
import net.lugocorp.kingdom.menu.icon.ActionNode;
import net.lugocorp.kingdom.mods.GameMod;
import net.lugocorp.kingdom.mods.ModProfile;
import net.lugocorp.kingdom.ui.overlay.RisingOverlay;
import net.lugocorp.kingdom.utils.Lambda;
import net.lugocorp.kingdom.utils.SideEffect;
import java.util.Optional;
import java.util.Set;

/**
 * This mod defines all the content for version 1.0 of the base game
 */
public class VanillaMod implements GameMod {

    /** {@inheritdoc} */
    @Override
    public ModProfile getProfile() {
        return new ModProfile("vanilla", "Vanilla", "Contains all content in the initial release of Legends of T'ahn",
                new String[]{"Alex Lugo"});
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
        sprites.register(Labels.asset_equip_frame, Labels.asset_items, InventoryNode.SIDE, InventoryNode.SIDE, 7, 7);

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
        sprites.register(Labels.asset_erupt, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 7, 6);
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
        sprites.register(Labels.asset_blessing_of_natures_hand, Labels.asset_abilities, ActionNode.SIDE,
                ActionNode.SIDE, 7, 8);
        sprites.register(Labels.asset_great_cycle, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 0, 9);
        sprites.register(Labels.asset_harvest_batatas, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 1, 9);
        sprites.register(Labels.asset_harvest_cacao, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 2, 9);
        sprites.register(Labels.asset_harvest_mesquite, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 3, 9);
        sprites.register(Labels.asset_defense_glyph, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 4, 9);
        sprites.register(Labels.asset_shield_bash, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 5, 9);
        sprites.register(Labels.asset_harvest_figs, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 6, 9);
        sprites.register(Labels.asset_harvest_pumpkins, Labels.asset_abilities, ActionNode.SIDE, ActionNode.SIDE, 7, 9);

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
        sprites.register(Labels.asset_sentinel, Labels.asset_fates, FateNode.WIDTH, FateNode.HEIGHT, 0, 1);
        sprites.register(Labels.asset_usurper, Labels.asset_fates, FateNode.WIDTH, FateNode.HEIGHT, 1, 1);
        sprites.register(Labels.asset_forager, Labels.asset_fates, FateNode.WIDTH, FateNode.HEIGHT, 2, 1);
    }

    /** {@inheritdoc} */
    @Override
    public void registerEvents(AllEventHandlers events) {
        VanillaModAbilities.registerEvents(events);
        VanillaModItems.registerEvents(events);
        VanillaModUnits.registerEvents(events);

        /**
         * SECTION Default handlers
         */

        // GetsHungry (TODO find a way to remove this)
        events.unit.setDefaultHandler("GetsHungry", (GameViewContext ctx, Unit receiver,
                Event event) -> new SideEffect().add(() -> receiver.hunger.gotHungry(ctx)));

        /**
         * SECTION Tiles
         */

        // Grass
        new Stratified<Tile>(events.tile, Labels.tile_grass).add(Events.GenerateTileEvent.class,
                (GameViewContext ctx, Tile receiver, Events.GenerateTileEvent e) -> {
                    e.blob.setModelInstance(Labels.asset_grass);
                    e.blob.setMinimapColor(0x2c9965);
                    e.blob.desc = "The seeds to spring new life lay dormant beneath this place";
                    return new SideEffect();
                });

        // Rock
        new Stratified<Tile>(events.tile, Labels.tile_rock).add(Events.GenerateTileEvent.class,
                (GameViewContext ctx, Tile receiver, Events.GenerateTileEvent e) -> {
                    e.blob.setModelInstance(Labels.asset_grass);
                    e.blob.setMinimapColor(0x666666);
                    e.blob.setMaterial(Labels.asset_rock);
                    e.blob.desc = "The rocky mountainscape is home to many creatures";
                    return new SideEffect();
                });

        // Sand
        new Stratified<Tile>(events.tile, Labels.tile_sand).add(Events.GenerateTileEvent.class,
                (GameViewContext ctx, Tile receiver, Events.GenerateTileEvent e) -> {
                    e.blob.setModelInstance(Labels.asset_grass);
                    e.blob.setMinimapColor(0xc7c567);
                    e.blob.setMaterial(Labels.asset_sand);
                    e.blob.desc = "The hot sands seem to stretch on forever";
                    return new SideEffect();
                });

        // Snow
        new Stratified<Tile>(events.tile, Labels.tile_snow).add(Events.GenerateTileEvent.class,
                (GameViewContext ctx, Tile receiver, Events.GenerateTileEvent e) -> {
                    e.blob.setModelInstance(Labels.asset_grass);
                    e.blob.setMinimapColor(0xffffff);
                    e.blob.setMaterial(Labels.asset_snow);
                    e.blob.desc = "Dense and cold";
                    return new SideEffect();
                });

        // Water
        new Stratified<Tile>(events.tile, Labels.tile_water).add(Events.GenerateTileEvent.class,
                (GameViewContext ctx, Tile receiver, Events.GenerateTileEvent e) -> {
                    e.blob.setModelInstance(Labels.asset_water);
                    e.blob.setMinimapColor(0x20c7f7);
                    e.blob.setObstacle(true);
                    e.blob.setWave(true);
                    e.blob.desc = "Only certain units can swim";
                    return new SideEffect();
                });

        // Lava
        new Stratified<Tile>(events.tile, Labels.tile_lava).add(Events.GenerateTileEvent.class,
                (GameViewContext ctx, Tile receiver, Events.GenerateTileEvent e) -> {
                    e.blob.setModelInstance(Labels.asset_water);
                    e.blob.setMinimapColor(0xcf3b23);
                    e.blob.setMaterial(Labels.asset_lava);
                    e.blob.setObstacle(true);
                    e.blob.setWave(true);
                    e.blob.desc = "Watch your step!";
                    return new SideEffect();
                });

        /**
         * SECTION Buildings
         */

        // Tower
        new Stratified<Building>(events.building, Labels.building_tower).add(Events.GenerateBuildingEvent.class,
                (GameViewContext ctx, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance("tower");
                    e.blob.desc = "Towers provide influence over the map and cost gold each turn. Players who have lost control of all their Towers lose the game.";
                    e.blob.items = Optional.of(new Inventory(InventoryType.BUILDING, 5));
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.setMinimapColor(0x000000);
                    return new SideEffect();
                }).add(BuildingLogic.vision(3));

        // Mine
        new Stratified<Building>(events.building, Labels.building_mine).add(Events.GenerateBuildingEvent.class,
                (GameViewContext ctx, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance("mine");
                    e.blob.desc = "Units with mining abilities can generate gold or items when they occupy this building.";
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.setMinimapColor(0x555555);
                    return new SideEffect();
                });

        // Cache
        new Stratified<Building>(events.building, Labels.building_cache).add(Events.GenerateBuildingEvent.class,
                (GameViewContext ctx, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance("cache");
                    e.blob.desc = "This building can store items. But be careful, if the cache is destroyed then so are all the items inside!";
                    e.blob.items = Optional.of(new Inventory(InventoryType.BUILDING, 24));
                    e.blob.combat.health.setMaxAndValue(5);
                    e.blob.setMinimapColor(0x000000);
                    return new SideEffect();
                });

        // Marketplace
        new Stratified<Building>(events.building, Labels.building_marketplace).add(Events.GenerateBuildingEvent.class,
                (GameViewContext ctx, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance("marketplace");
                    e.blob.desc = "Occupying units generate gold and sometimes an item.";
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.setMinimapColor(0x000000);
                    return new SideEffect();
                })
                .add(Events.SpawnEvent.class,
                        (GameViewContext ctx, Building receiver, Events.SpawnEvent e) -> new SideEffect().add(
                                () -> ctx.view.game.future.addFutureTick("Tick", receiver, 1, true, Optional.empty())))
                .add("Tick", (GameViewContext ctx, Building receiver, Events.RepeatedEvent e) -> {
                    final Optional<Unit> u = ctx.view.game.world.getTile(receiver.getPoint())
                            .flatMap((Tile t) -> t.unit);
                    final SideEffect effects = new SideEffect();
                    u.ifPresent((Unit unit) -> {
                        if (Math.random() < 0.05) {
                            effects.add(() -> {
                                final Item item = ctx.view.game.mechanics.loot.drop(ctx.view.game);
                                ctx.view.overlays.entity(unit)
                                        .addRising(new RisingOverlay(ctx.view, unit, ColorScheme.WHITE.hex, item.name));
                                unit.haul.add(item);
                            });
                        } else {
                            effects.add(AbilityLogic.gold(ctx.view, unit, 5));
                        }
                    });
                    return effects;
                });

        // Forest
        new Stratified<Building>(events.building, Labels.building_forest).add(Events.GenerateBuildingEvent.class,
                (GameViewContext ctx, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance("forest");
                    e.blob.desc = "Units with harvest abilities can generate food or other items when they occupy this building.";
                    e.blob.combat.health.setMaxAndValue(5);
                    e.blob.setMinimapColor(0x257d53);
                    return new SideEffect();
                });

        // Dense Forest
        new Stratified<Building>(events.building, Labels.building_dense_forest).add(Events.GenerateBuildingEvent.class,
                (GameViewContext ctx, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance("dense-forest");
                    e.blob.desc = "Most units cannot traverse dense forests. They leave behind regular forests when they are destroyed.";
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.setMinimapColor(0x194D34);
                    e.blob.setObstacle(true);
                    return new SideEffect();
                }).add(Events.ReplaceBuildingEvent.class,
                        (GameViewContext ctx, Building receiver, Events.ReplaceBuildingEvent e) -> {
                            e.replacement = Optional.of(Labels.building_forest);
                            return new SideEffect();
                        });

        // Taiga
        new Stratified<Building>(events.building, Labels.building_taiga).add(Events.GenerateBuildingEvent.class,
                (GameViewContext ctx, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance("forest");
                    e.blob.desc = "Units with harvest abilities can generate food or other items when they occupy this building.";
                    e.blob.combat.health.setMaxAndValue(5);
                    e.blob.setMinimapColor(0xb4c3c7);
                    e.blob.setMaterial(Labels.asset_taiga);
                    return new SideEffect();
                });

        // Meadow
        new Stratified<Building>(events.building, Labels.building_meadow).add(Events.GenerateBuildingEvent.class,
                (GameViewContext ctx, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance("meadow");
                    e.blob.desc = "Units with harvest abilities can generate food or other items when they occupy this building.";
                    e.blob.combat.health.setMaxAndValue(5);
                    e.blob.setMinimapColor(0x4dd349);
                    return new SideEffect();
                });

        // Oasis
        new Stratified<Building>(events.building, Labels.building_oasis).add(Events.GenerateBuildingEvent.class,
                (GameViewContext ctx, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance("oasis");
                    e.blob.desc = "Units with harvest abilities can generate food or other items when they occupy this building.";
                    e.blob.combat.health.setMaxAndValue(5);
                    e.blob.setMinimapColor(0x2c9965);
                    return new SideEffect();
                });

        // Shrubland
        new Stratified<Building>(events.building, Labels.building_shrubland).add(Events.GenerateBuildingEvent.class,
                (GameViewContext ctx, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance("shrubland");
                    e.blob.desc = "Units with harvest abilities can generate food or other items when they occupy this building.";
                    e.blob.combat.health.setMaxAndValue(5);
                    e.blob.setMinimapColor(0x4dd349);
                    return new SideEffect();
                });

        // Mountain
        new Stratified<Building>(events.building, Labels.building_mountain).add(Events.GenerateBuildingEvent.class,
                (GameViewContext ctx, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance("mountain");
                    e.blob.desc = "Most units cannot traverse mountains.";
                    e.blob.combat.health.invulnerable();
                    e.blob.setMinimapColor(0x875f9a);
                    e.blob.setObstacle(true);
                    return new SideEffect();
                });

        // Hill
        new Stratified<Building>(events.building, Labels.building_hill).add(Events.GenerateBuildingEvent.class,
                (GameViewContext ctx, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance("hill");
                    e.blob.desc = "Units move slower on hills.";
                    e.blob.combat.health.invulnerable();
                    e.blob.setMinimapColor(0x2c9965);
                    return new SideEffect();
                }).add(Events.GetSpeedCostEvent.class,
                        (GameViewContext ctx, Building receiver, Events.GetSpeedCostEvent e) -> {
                            e.cost = 2;
                            return new SideEffect();
                        });

        // Bluff
        new Stratified<Building>(events.building, Labels.building_bluff).add(Events.GenerateBuildingEvent.class,
                (GameViewContext ctx, Building receiver, Events.GenerateBuildingEvent e) -> {
                    e.blob.setModelInstance("bluff");
                    e.blob.desc = "Units move slower on bluffs.";
                    e.blob.combat.health.invulnerable();
                    e.blob.setMinimapColor(0x2c9965);
                    return new SideEffect();
                }).add(Events.GetSpeedCostEvent.class,
                        (GameViewContext ctx, Building receiver, Events.GetSpeedCostEvent e) -> {
                            e.cost = 2;
                            return new SideEffect();
                        });

        // Healing Fountain
        new Stratified<Building>(events.building, Labels.building_healing_fountain)
                .add(Events.GenerateBuildingEvent.class,
                        (GameViewContext ctx, Building receiver, Events.GenerateBuildingEvent e) -> {
                            e.blob.setModelInstance("fountain");
                            e.blob.desc = "An occupying unit regains health each turn.";
                            e.blob.combat.health.setMaxAndValue(10);
                            e.blob.setMinimapColor(0x875f9a);
                            return new SideEffect();
                        })
                .add(Events.SpawnEvent.class,
                        (GameViewContext ctx, Building receiver, Events.SpawnEvent e) -> new SideEffect().add(
                                () -> ctx.view.game.future.addFutureTick("Tick", receiver, 1, true, Optional.empty())))
                .add("Tick", (GameViewContext ctx, Building receiver, Events.RepeatedEvent e) -> {
                    Optional<Unit> u = ctx.view.game.world.getTile(receiver.getPoint()).flatMap((Tile t) -> t.unit);
                    return u.isPresent() ? receiver.combat.heal(ctx.view, u.get(), 1) : new SideEffect();
                });

        // Wall
        new Stratified<Building>(events.building, Labels.building_wall).add(Events.GenerateBuildingEvent.class,
                (GameViewContext ctx, Building receiver, Events.GenerateBuildingEvent e) -> {
                    // TODO add new model
                    e.blob.setModelInstance("placeholder1");
                    e.blob.desc = "Units cannot traverse this structure. It crumbles in 6 turns.";
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.setMinimapColor(0x875f9a);
                    e.blob.setObstacle(true);
                    return new SideEffect();
                })
                .add(Events.SpawnEvent.class,
                        (GameViewContext ctx, Building receiver,
                                Events.SpawnEvent e) -> new SideEffect().add(() -> ctx.view.game.future
                                        .addFutureTick("Cleanup", receiver, 6, true, Optional.empty())))
                .add("Cleanup", (GameViewContext ctx, Building receiver, Events.RepeatedEvent e) -> new SideEffect()
                        .add(() -> receiver.deactivate(ctx)));

        // Shield Totem
        new Stratified<Building>(events.building, Labels.building_shield_totem).add(Events.GenerateBuildingEvent.class,
                (GameViewContext ctx, Building receiver, Events.GenerateBuildingEvent e) -> {
                    // TODO add new model
                    e.blob.setModelInstance("placeholder1");
                    e.blob.desc = "All friendly units within a 1 tile radius get +1 defense. This structure will collapse in 4 turns.";
                    e.blob.combat.health.invulnerable();
                    e.blob.setMinimapColor(0x875f9a);
                    return new SideEffect();
                })
                .add(Events.SpawnEvent.class,
                        (GameViewContext ctx, Building receiver, Events.SpawnEvent e) -> new SideEffect()
                                .add(() -> ctx.view.game.future.addFutureTick("Cleanup", receiver, 4, true,
                                        Optional.empty()))
                                .add(() -> ctx.view.game.future.addFutureTick("Tick", receiver, 1, true,
                                        Optional.empty()))
                                .add(() -> ctx.view.game.events.signals.addListener(Events.TakeDamageEvent.class,
                                        receiver)))
                .add("Cleanup",
                        (GameViewContext ctx, Building receiver, Events.RepeatedEvent e) -> new SideEffect()
                                .add(() -> receiver.deactivate(ctx)))
                .add(Events.TakeDamageEvent.class,
                        (GameViewContext ctx, Building receiver, Events.TakeDamageEvent e) -> {
                            if (e.target.isEntityType(EntityType.UNIT) && e.target.leadership.sameLeader(e.target)) {
                                final Set<Point> adjacents = Hexagons.getAdjacents(receiver.getPoint());
                                final Point p = e.target.getPoint();
                                if (p.equals(receiver.getPoint()) || adjacents.contains(p)) {
                                    e.dmg.base--;
                                }
                            }
                            return new SideEffect();
                        });

        /**
         * SECTION Patrons
         */

        // Joyous Reaper
        new Stratified<Patron>(events.patron, Labels.patron_joyous_reaper).add(Events.GeneratePatronEvent.class,
                (GameViewContext ctx, Patron receiver, Events.GeneratePatronEvent e) -> {
                    e.blob.setModelInstance("joyous-reaper");
                    e.blob.desc = "This spirit ferries the souls of warriors to the afterlife. Sometimes it appears before mortals as an omen of encroaching death.";
                    e.blob.effect = "Your battle glyph units generate 5 unit points when they kill or are killed by another unit. The killed unit is reincarnated (returns as a unit recruitment option).";
                    e.blob.preference = "Battle glyph units";
                    e.blob.isPreferredUnitType = (Unit u) -> u.glyphs.has(Glyph.BATTLE);
                    e.blob.setIcons(Labels.asset_rising_spirit, Labels.asset_battle_glyph);
                    return new SideEffect();
                }).add(Events.SpawnEvent.class,
                        (GameViewContext ctx, Patron receiver, Events.SpawnEvent e) -> new SideEffect().add(() -> {
                            ctx.view.game.events.signals.addListener(Events.EntityDiedEvent.class, receiver);
                            ctx.view.game.events.signals.addListener(Events.KilledEntityEvent.class, receiver);
                        }))
                .add(Events.EntityDiedEvent.class, (GameViewContext ctx, Patron receiver, Events.EntityDiedEvent e) -> {
                    if (e.target.isEntityType(EntityType.UNIT) && ((Unit) e.target).glyphs.has(Glyph.BATTLE)
                            && e.target.getLeader().equals(receiver.getFavoritePlayer())) {
                        return new SideEffect().add(() -> {
                            e.target.getLeader().get().addUnitPoints(ctx.view, receiver.getPoint(), 5);
                            ctx.view.game.mechanics.pools.reincarnate((Unit) e.target);
                        });
                    }
                    return new SideEffect();
                }).add(Events.KilledEntityEvent.class,
                        (GameViewContext ctx, Patron receiver, Events.KilledEntityEvent e) -> {
                            if (e.killer.isEntityType(EntityType.UNIT) && ((Unit) e.killer).glyphs.has(Glyph.BATTLE)
                                    && e.killer.getLeader().equals(receiver.getFavoritePlayer())
                                    && e.target.isEntityType(EntityType.UNIT)) {
                                return new SideEffect().add(() -> {
                                    e.killer.getLeader().get().addUnitPoints(ctx.view, receiver.getPoint(), 5);
                                    ctx.view.game.mechanics.pools.reincarnate((Unit) e.target);
                                });
                            }
                            return new SideEffect();
                        });

        // Great Corn Woman
        // Lord Shui, Guardian of the River
        // The Pond Troll
        new Stratified<Patron>(events.patron, Labels.patron_pond_troll).add(Events.GeneratePatronEvent.class,
                (GameViewContext ctx, Patron receiver, Events.GeneratePatronEvent e) -> {
                    e.blob.setModelInstance("pond-troll");
                    e.blob.desc = "This ancient Troll made a pact with water spirits to save her village. In return she was forced to become a spirit herself, and now watches over those who seek to cross bodies of water.";
                    e.blob.effect = "The favorite player's units can traverse water tiles and have a 20% chance to fish when they do";
                    e.blob.preference = "Units that cannot swim";
                    e.blob.isPreferredUnitType = (Unit u) -> !u.abilities.hasPassive(Labels.ability_swim);
                    e.blob.setIcons(Labels.asset_swim, Labels.asset_drown);
                    return new SideEffect();
                }).add(Events.SpawnEvent.class,
                        (GameViewContext ctx, Patron receiver, Events.SpawnEvent e) -> new SideEffect().add(() -> {
                            ctx.view.game.events.signals.addListener(Events.CanUnitMoveEvent.class, receiver);
                            ctx.view.game.events.signals.addListener(Events.UnitMovedEvent.class, receiver);
                        }))
                .add(Events.CanUnitMoveEvent.class,
                        (GameViewContext ctx, Patron receiver, Events.CanUnitMoveEvent e) -> {
                            if (e.unit.getLeader().equals(receiver.getFavoritePlayer())
                                    && e.tile.name.equals(Labels.tile_water)) {
                                e.canWalkOnTile = true;
                            }
                            return new SideEffect();
                        })
                .add(Events.UnitMovedEvent.class, (GameViewContext ctx, Patron receiver, Events.UnitMovedEvent e) -> {
                    if (e.unit.getLeader().equals(receiver.getFavoritePlayer())
                            && ctx.view.game.world.getTile(e.current).get().name.equals(Labels.tile_water)
                            && !e.unit.haul.isFull() && Lambda.chance(20)) {
                        return new SideEffect()
                                .add(() -> e.unit.haul.add(ctx.view.game.generator.item(Labels.item_fish)));
                    }
                    return new SideEffect();
                });

        // The Eternal Guardian
        // Flutterwing
        new Stratified<Patron>(events.patron, Labels.patron_flutterwing).add(Events.GeneratePatronEvent.class,
                (GameViewContext ctx, Patron receiver, Events.GeneratePatronEvent e) -> {
                    e.blob.setModelInstance("flutterwing");
                    e.blob.desc = "This great spirit dances upon the bodies of freshly slain warriors. It recycles their fleeting essence into new life to sustain the great cycle.";
                    e.blob.effect = "Your battle glyph units have +20% critical hit chance and generate 5 unit points when they kill another unit";
                    e.blob.preference = "Battle glyph units";
                    e.blob.isPreferredUnitType = (Unit u) -> u.glyphs.has(Glyph.BATTLE);
                    e.blob.setIcons(Labels.asset_bloodlust, Labels.asset_battle_glyph);
                    return new SideEffect();
                }).add(Events.SpawnEvent.class,
                        (GameViewContext ctx, Patron receiver, Events.SpawnEvent e) -> new SideEffect().add(() -> {
                            ctx.view.game.events.signals.addListener(Events.CheckCriticalHitEvent.class, receiver);
                            ctx.view.game.events.signals.addListener(Events.KilledEntityEvent.class, receiver);
                        }))
                .add(Events.CheckCriticalHitEvent.class,
                        (GameViewContext ctx, Patron receiver, Events.CheckCriticalHitEvent e) -> {
                            if (e.entity.isEntityType(EntityType.UNIT) && ((Unit) e.entity).glyphs.has(Glyph.BATTLE)
                                    && e.entity.getLeader().equals(receiver.getFavoritePlayer())) {
                                e.chance += 10;
                            }
                            return new SideEffect();
                        })
                .add(Events.KilledEntityEvent.class,
                        (GameViewContext ctx, Patron receiver, Events.KilledEntityEvent e) -> {
                            if (e.killer.isEntityType(EntityType.UNIT) && ((Unit) e.killer).glyphs.has(Glyph.BATTLE)
                                    && e.killer.getLeader().equals(receiver.getFavoritePlayer())
                                    && e.target.isEntityType(EntityType.UNIT)) {
                                return new SideEffect().add(() -> e.killer.getLeader().get().addUnitPoints(ctx.view,
                                        receiver.getPoint(), 5));
                            }
                            return new SideEffect();
                        });

        // Wise Mountain
        new Stratified<Patron>(events.patron, Labels.patron_wise_mountain).add(Events.GeneratePatronEvent.class,
                (GameViewContext ctx, Patron receiver, Events.GeneratePatronEvent e) -> {
                    e.blob.setModelInstance("wise-mountain");
                    e.blob.desc = "This mountain has watched over villages at its base for thousands upon thousands of years. It has witnessed the rise and fall of dynasties, the crashing wake of invading armies, and the birth of new forms of magic.";
                    e.blob.effect = "Your mining glyph units' harvest abilities have a 20% chance to generate an additional item";
                    e.blob.preference = "Mining glyph units";
                    e.blob.isPreferredUnitType = (Unit u) -> u.glyphs.has(Glyph.MINING);
                    e.blob.setIcons(Labels.asset_extra_gem, Labels.asset_mining_glyph);
                    return new SideEffect();
                }).add(Events.SpawnEvent.class,
                        (GameViewContext ctx, Patron receiver, Events.SpawnEvent e) -> new SideEffect().add(() -> {
                            ctx.view.game.events.signals.addListener(Events.HarvestEvent.class, receiver);
                        }))
                .add(Events.HarvestEvent.class, (GameViewContext ctx, Patron receiver, Events.HarvestEvent e) -> {
                    if (e.unit.getLeader().equals(receiver.getFavoritePlayer()) && e.unit.glyphs.has(Glyph.MINING)
                            && !e.unit.haul.isFull() && Lambda.chance(20)) {
                        final Item i = ctx.view.game.generator.item(e.item.name);
                        return new SideEffect().add(() -> e.unit.haul.add(i));
                    }
                    return new SideEffect();
                });

        // Wise Oak
        new Stratified<Patron>(events.patron, Labels.patron_wise_oak).add(Events.GeneratePatronEvent.class,
                (GameViewContext ctx, Patron receiver, Events.GeneratePatronEvent e) -> {
                    e.blob.setModelInstance("wise-oak");
                    e.blob.desc = "This elder communes with the surrounding forest through a complex network of underground fungal helpers. It knows the names of all the trees and flowers and shrubs that have existed on the continent for the past several hundred years.";
                    e.blob.effect = "Your nature glyph units' harvest abilities have a 20% chance to generate an additional item";
                    e.blob.preference = "Nature glyph units";
                    e.blob.isPreferredUnitType = (Unit u) -> u.glyphs.has(Glyph.NATURE);
                    e.blob.setIcons(Labels.asset_extra_fruit, Labels.asset_nature_glyph);
                    return new SideEffect();
                }).add(Events.SpawnEvent.class,
                        (GameViewContext ctx, Patron receiver, Events.SpawnEvent e) -> new SideEffect().add(() -> {
                            ctx.view.game.events.signals.addListener(Events.HarvestEvent.class, receiver);
                        }))
                .add(Events.HarvestEvent.class, (GameViewContext ctx, Patron receiver, Events.HarvestEvent e) -> {
                    return new SideEffect().add(() -> {
                        if (e.unit.getLeader().equals(receiver.getFavoritePlayer()) && e.unit.glyphs.has(Glyph.NATURE)
                                && !e.unit.haul.isFull() && Lambda.chance(20)) {
                            final Item i = ctx.view.game.generator.item(e.item.name);
                            e.unit.haul.add(i);
                        }
                    });
                });

        // Ahn-Juné
        // The Shining Eyes
        new Stratified<Patron>(events.patron, Labels.patron_shining_eyes).add(Events.GeneratePatronEvent.class,
                (GameViewContext ctx, Patron receiver, Events.GeneratePatronEvent e) -> {
                    e.blob.setModelInstance("shining-eyes");
                    e.blob.desc = "A mysterious being from some upper realm. The light that irradiates from its otherworldly form nurtures the soul and heals fatal wounds. Many believe its true form is far more strange than how it normally depicts itself.";
                    e.blob.effect = "Heals 4 random units of its favorite player each turn";
                    e.blob.preference = "Support glyph units";
                    e.blob.isPreferredUnitType = (Unit u) -> u.glyphs.has(Glyph.SUPPORT);
                    e.blob.setIcons(Labels.asset_heal_wounds, Labels.asset_regeneration);
                    return new SideEffect();
                })
                .add(Events.SpawnEvent.class,
                        (GameViewContext ctx, Patron receiver, Events.SpawnEvent e) -> new SideEffect().add(
                                () -> ctx.view.game.future.addFutureTick("Tick", receiver, 1, true, Optional.empty())))
                .add("Tick", (GameViewContext ctx, Patron receiver, Events.RepeatedEvent e) -> {
                    final SideEffect effects = new SideEffect();
                    final Optional<Player> favorite = receiver.getFavoritePlayer();
                    favorite.ifPresent((Player p) -> {
                        for (Unit u : Lambda.subset(4, p.units)) {
                            effects.add(u.combat.heal(ctx.view, 3));
                        }
                    });
                    return effects;
                });

        /**
         * SECTION Artifacts
         */

        // Cho's Sigil of Haste
        new Stratified<Artifact>(events.artifact, Labels.artifact_chos_sigil_of_haste)
                .add(Events.GenerateArtifactEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Your support glyph units get +1 movement speed";
                            e.blob.image = Optional.of(Labels.asset_chos_sigil_of_haste);
                            return new SideEffect();
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            ctx.view.game.events.signals.addListener(Events.UnitMoveDistanceEvent.class, e.artifact);
                            return new SideEffect();
                        })
                .add(Events.UnitMoveDistanceEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.UnitMoveDistanceEvent e) -> {
                            if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.SUPPORT)) {
                                e.distance++;
                            }
                            return new SideEffect();
                        });

        // Urdin's Scroll of Agility
        new Stratified<Artifact>(events.artifact, Labels.artifact_urdins_scroll_of_agility)
                .add(Events.GenerateArtifactEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Your defense glyph units get +1 movement speed";
                            e.blob.image = Optional.of(Labels.asset_urdins_scroll_of_agility);
                            return new SideEffect();
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            ctx.view.game.events.signals.addListener(Events.UnitMoveDistanceEvent.class, e.artifact);
                            return new SideEffect();
                        })
                .add(Events.UnitMoveDistanceEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.UnitMoveDistanceEvent e) -> {
                            if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.DEFENSE)) {
                                e.distance++;
                            }
                            return new SideEffect();
                        });

        // Sword of Aesethos
        new Stratified<Artifact>(events.artifact, Labels.artifact_sword_of_aesethos)
                .add(Events.GenerateArtifactEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Your units have +10% critical hit chance";
                            e.blob.image = Optional.of(Labels.asset_sword_of_aesethos);
                            return new SideEffect();
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            ctx.view.game.events.signals.addListener(Events.CheckCriticalHitEvent.class, e.artifact);
                            return new SideEffect();
                        })
                .add(Events.CheckCriticalHitEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.CheckCriticalHitEvent e) -> {
                            if (receiver.isClaimedByLeader(e.entity)) {
                                e.chance += 10;
                            }
                            return new SideEffect();
                        });

        // Kauna's Amulet
        new Stratified<Artifact>(events.artifact, Labels.artifact_kaunas_amulet).add(Events.GenerateArtifactEvent.class,
                (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your units within a patron's domain have extra defense";
                    e.blob.image = Optional.of(Labels.asset_kaunas_amulet);
                    return new SideEffect();
                }).add(Events.ArtifactClaimedEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            ctx.view.game.events.signals.addListener(Events.TakeDamageEvent.class, e.artifact);
                            return new SideEffect();
                        })
                .add(Events.TakeDamageEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.TakeDamageEvent e) -> {
                            if (receiver.isClaimedByLeader(e.target) && e.target.isEntityType(EntityType.UNIT)) {
                                for (Patron p : ctx.view.game.mechanics.patronage) {
                                    if (p.getHostDomain(ctx.view).contains(e.target.getPoint())) {
                                        e.dmg.base -= 2;
                                        break;
                                    }
                                }
                            }
                            return new SideEffect();
                        });

        // Staff of Wurmdel
        new Stratified<Artifact>(events.artifact, Labels.artifact_staff_of_wurmdel)
                .add(Events.GenerateArtifactEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Your healing spells restore +4 more health";
                            e.blob.image = Optional.of(Labels.asset_staff_of_wurmdel);
                            return new SideEffect();
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            ctx.view.game.events.signals.addListener(Events.HealEntityEvent.class, e.artifact);
                            return new SideEffect();
                        })
                .add(Events.HealEntityEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.HealEntityEvent e) -> {
                            if (receiver.isClaimedByLeader(e.healer)) {
                                e.amount += 4;
                            }
                            return new SideEffect();
                        });

        // Tome of Morun
        // TODO completely rework the Tome of Morun
        /*
         * new Stratified<Artifact>(events.artifact,
         * Labels.artifact_tome_of_morun).add(Events.GenerateArtifactEvent.class,
         * (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
         * e.blob.desc = "20% chance to spawn a glyph when your units kill an enemy";
         * e.blob.image = Optional.of(Labels.asset_tome_of_morun); return new
         * SideEffect(); }).add(Events.ArtifactClaimedEvent.class, (GameViewContext ctx,
         * Artifact receiver, Events.ArtifactClaimedEvent e) -> {
         * ctx.view.game.events.signals.addListener(Events.EntityDiedEvent.class,
         * e.artifact); return new SideEffect(); }) .add(Events.EntityDiedEvent.class,
         * (GameViewContext ctx, Artifact receiver, Events.EntityDiedEvent e) -> { if
         * (receiver.isClaimedByLeader(e.killer) &&
         * !receiver.isClaimedByLeader(e.target)) { Tile t =
         * ctx.view.game.world.getTile(e.killer.getPoint()).get(); if
         * (!t.getGlyph().isPresent() && Lambda.chance(20)) {
         * t.setGlyph(Optional.of(Lambda.random(Glyph/Category.class))); } } return new
         * SideEffect(); });
         */

        // Orb of Nerketo
        new Stratified<Artifact>(events.artifact, Labels.artifact_orb_of_nerketo)
                .add(Events.GenerateArtifactEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Your units have +1 vision";
                            e.blob.image = Optional.of(Labels.asset_orb_of_nerketo);
                            return new SideEffect();
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            ctx.view.game.events.signals.addListener(Events.GetVisionEvent.class, e.artifact);
                            for (Unit u : e.player.units) {
                                u.vision.set(ctx.view, e.player, u, u.getPoint());
                            }
                            return new SideEffect();
                        })
                .add(Events.GetVisionEvent.class, (GameViewContext ctx, Artifact receiver, Events.GetVisionEvent e) -> {
                    if (receiver.isClaimedByPlayer(e.player)) {
                        e.radius++;
                    }
                    return new SideEffect();
                });

        // Shada's Flute
        new Stratified<Artifact>(events.artifact, Labels.artifact_shadas_flute).add(Events.GenerateArtifactEvent.class,
                (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your patrons generate 3 unit points per turn";
                    e.blob.image = Optional.of(Labels.asset_shadas_flute);
                    return new SideEffect();
                })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.ArtifactClaimedEvent e) -> new SideEffect().add(
                                () -> ctx.view.game.future.addFutureTick("Tick", receiver, 1, true, Optional.empty())))
                .add("Tick", (GameViewContext ctx, Artifact receiver, Events.RepeatedEvent e) -> {
                    for (Patron patron : ctx.view.game.mechanics.patronage) {
                        if (receiver.getOwner().equals(patron.getFavoritePlayer())) {
                            receiver.getOwner().get().addUnitPoints(ctx.view, 3);
                        }
                    }
                    return new SideEffect();
                });

        // Stones of Thudin
        new Stratified<Artifact>(events.artifact, Labels.artifact_stones_of_thudin)
                .add(Events.GenerateArtifactEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Your towers have +2 defense";
                            e.blob.image = Optional.of(Labels.asset_stones_of_thudin);
                            return new SideEffect();
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            ctx.view.game.events.signals.addListener(Events.TakeDamageEvent.class, e.artifact);
                            return new SideEffect();
                        })
                .add(Events.TakeDamageEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.TakeDamageEvent e) -> {
                            if (e.target.isEntityType(EntityType.BUILDING)) {
                                if (receiver.isClaimedByLeader(e.target)
                                        && e.target.name.equals(Labels.building_tower)) {
                                    e.dmg.base -= 2;
                                }
                            }
                            return new SideEffect();
                        });

        // The Chasi Bones
        new Stratified<Artifact>(events.artifact, Labels.artifact_chasi_bones).add(Events.GenerateArtifactEvent.class,
                (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your nature glyph units have a 20% chance to harvest an additional item";
                    e.blob.image = Optional.of(Labels.asset_chasi_bones);
                    return new SideEffect();
                }).add(Events.ArtifactClaimedEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            ctx.view.game.events.signals.addListener(Events.HarvestEvent.class, e.artifact);
                            return new SideEffect();
                        })
                .add(Events.HarvestEvent.class, (GameViewContext ctx, Artifact receiver, Events.HarvestEvent e) -> {
                    if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.NATURE) && !e.unit.haul.isFull()
                            && Lambda.chance(20)) {
                        e.unit.haul.add(ctx.view.game.generator.item(e.item.getStratifier()));
                    }
                    return new SideEffect();
                });

        // Ucha's Bowl of Plenty
        new Stratified<Artifact>(events.artifact, Labels.artifact_uchas_bowl_of_plenty)
                .add(Events.GenerateArtifactEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "+1 option when selecting a new unit";
                            e.blob.image = Optional.of(Labels.asset_uchas_bowl_of_plenty);
                            e.blob.chips = 2;
                            return new SideEffect();
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            e.player.numRecruitmentOptions++;
                            return new SideEffect();
                        });

        // Nerketo's Helm
        new Stratified<Artifact>(events.artifact, Labels.artifact_nerketos_helm).add(Events.GenerateArtifactEvent.class,
                (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Critical hits against your units are less effective";
                    e.blob.image = Optional.of(Labels.asset_nerketos_helm);
                    e.blob.chips = 2;
                    return new SideEffect();
                }).add(Events.ArtifactClaimedEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            ctx.view.game.events.signals.addListener(Events.CheckCriticalHitEvent.class, e.artifact);
                            return new SideEffect();
                        })
                .add(Events.CheckCriticalHitEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.CheckCriticalHitEvent e) -> {
                            if (e.entity.isEntityType(EntityType.UNIT) && receiver.isClaimedByLeader(e.entity)) {
                                e.multiplier = 1.1f;
                            }
                            return new SideEffect();
                        });

        // Bounty of Ahn-June
        new Stratified<Artifact>(events.artifact, Labels.artifact_bounty_of_ahn_june)
                .add(Events.GenerateArtifactEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Your trade glyph units generate +2 more auction points";
                            e.blob.image = Optional.of(Labels.asset_bounty_of_ahn_june);
                            e.blob.chips = 2;
                            return new SideEffect();
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            ctx.view.game.events.signals.addListener(Events.GenerateAuctionPointsEvent.class,
                                    e.artifact);
                            return new SideEffect();
                        })
                .add(Events.GenerateAuctionPointsEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.GenerateAuctionPointsEvent e) -> {
                            if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.TRADE)) {
                                e.points += 2;
                            }
                            return new SideEffect();
                        });

        // Mark of Kung
        new Stratified<Artifact>(events.artifact, Labels.artifact_mark_of_kung).add(Events.GenerateArtifactEvent.class,
                (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "Your battle glyph units get +1 movement speed";
                    e.blob.image = Optional.of(Labels.asset_mark_of_kung);
                    e.blob.chips = 2;
                    return new SideEffect();
                }).add(Events.ArtifactClaimedEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            ctx.view.game.events.signals.addListener(Events.UnitMoveDistanceEvent.class, e.artifact);
                            return new SideEffect();
                        })
                .add(Events.UnitMoveDistanceEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.UnitMoveDistanceEvent e) -> {
                            if (receiver.isClaimedByLeader(e.unit) && e.unit.glyphs.has(Glyph.BATTLE)) {
                                e.distance++;
                            }
                            return new SideEffect();
                        });

        // Chalco's Seal of Protection
        new Stratified<Artifact>(events.artifact, Labels.artifact_chalcos_seal_of_protection)
                .add(Events.GenerateArtifactEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Your trade glyph units have +2 defense";
                            e.blob.image = Optional.of(Labels.asset_chalcos_seal_of_protection);
                            e.blob.chips = 2;
                            return new SideEffect();
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            ctx.view.game.events.signals.addListener(Events.TakeDamageEvent.class, e.artifact);
                            return new SideEffect();
                        })
                .add(Events.TakeDamageEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.TakeDamageEvent e) -> {
                            if (e.target.isEntityType(EntityType.UNIT)) {
                                Unit u = (Unit) e.target;
                                if (receiver.isClaimedByLeader(u) && u.glyphs.has(Glyph.TRADE)) {
                                    e.dmg.base -= 2;
                                }
                            }
                            return new SideEffect();
                        });

        // Poda's Elixir
        // TODO completely rework Poda's Elixir
        /*
         * new Stratified<Artifact>(events.artifact,
         * Labels.artifact_podas_elixir).add(Events.GenerateArtifactEvent.class,
         * (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
         * e.blob.desc = "15% chance refresh a glyph when you recruit a unit";
         * e.blob.image = Optional.of(Labels.asset_podas_elixir); e.blob.chips = 2;
         * return new SideEffect(); }).add(Events.ArtifactClaimedEvent.class, (GameView
         * view, Artifact receiver, Events.ArtifactClaimedEvent e) -> { return new
         * SideEffect().add( () ->
         * ctx.view.game.events.signals.addListener(Events.SpawnEvent.class,
         * e.artifact)); }) .add(Events.SpawnEvent.class, (GameViewContext ctx, Artifact
         * receiver, Events.SpawnEvent e) -> { if (e.spawned instanceof Unit) { Unit u =
         * (Unit) e.spawned; Tile t = ctx.view.game.world.getTile(u.getPoint()).get();
         * if (receiver.isClaimedByLeader(u) && !t.getGlyph().isPresent() &&
         * Lambda.chance(15)) { return new SideEffect() .add(() ->
         * t.setGlyph(Optional.of(Lambda.random(Glyph/Category.class)))); } } return new
         * SideEffect(); });
         */

        // Gaia's Effigy
        new Stratified<Artifact>(events.artifact, Labels.artifact_gaias_effigy).add(Events.GenerateArtifactEvent.class,
                (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "+8 unit points each turn";
                    e.blob.image = Optional.of(Labels.asset_gaias_effigy);
                    e.blob.chips = 3;
                    return new SideEffect();
                })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.ArtifactClaimedEvent e) -> new SideEffect().add(
                                () -> ctx.view.game.future.addFutureTick("Tick", receiver, 1, true, Optional.empty())))
                .add("Tick", (GameViewContext ctx, Artifact receiver, Events.RepeatedEvent e) -> {
                    receiver.getOwner().get().addUnitPoints(ctx.view, 8);
                    return new SideEffect();
                });

        // Rod of Adelon
        new Stratified<Artifact>(events.artifact, Labels.artifact_rod_of_adelon).add(Events.GenerateArtifactEvent.class,
                (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                    e.blob.desc = "5% chance to recruit an enemy unit when you kill it";
                    e.blob.image = Optional.of(Labels.asset_rod_of_adelon);
                    e.blob.chips = 3;
                    return new SideEffect();
                }).add(Events.ArtifactClaimedEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            ctx.view.game.events.signals.addListener(Events.KilledEntityEvent.class, e.artifact);
                            return new SideEffect();
                        })
                .add(Events.KilledEntityEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.KilledEntityEvent e) -> {
                            if (e.target.isEntityType(EntityType.UNIT) && receiver.isClaimedByLeader(e.killer)
                                    && !e.killer.isFriendly(e.target) && Lambda.chance(5)) {
                                ctx.view.game.generator.unit(e.target.name, e.target.getX(), e.target.getY())
                                        .spawn(ctx.view);
                            }
                            return new SideEffect();
                        });

        // Blade of Sanguinor
        new Stratified<Artifact>(events.artifact, Labels.artifact_blade_of_sanguinor)
                .add(Events.GenerateArtifactEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.GenerateArtifactEvent e) -> {
                            e.blob.desc = "Your battle glyph units deal +2 damage";
                            e.blob.image = Optional.of(Labels.asset_blade_of_sanguinor);
                            e.blob.chips = 3;
                            return new SideEffect();
                        })
                .add(Events.ArtifactClaimedEvent.class,
                        (GameViewContext ctx, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
                            ctx.view.game.events.signals.addListener(Events.AttackEvent.class, e.artifact);
                            return new SideEffect();
                        })
                .add(Events.AttackEvent.class, (GameViewContext ctx, Artifact receiver, Events.AttackEvent e) -> {
                    if (e.target.isEntityType(EntityType.UNIT) && receiver.isClaimedByLeader(e.target)
                            && ((Unit) e.target).glyphs.has(Glyph.BATTLE)) {
                        e.dmg.base += 2;
                    }
                    return new SideEffect();
                });

        // Cask of Amontior
        // TODO completely rework Cask of Amontior
        /*
         * new Stratified<Artifact>(events.artifact, Labels.artifact_cask_of_amontior)
         * .add(Events.GenerateArtifactEvent.class, (GameViewContext ctx, Artifact
         * receiver, Events.GenerateArtifactEvent e) -> { e.blob.desc =
         * "Your unoccupied tiles in a patron's domain provide +1 favor"; e.blob.image =
         * Optional.of(Labels.asset_cask_of_amontior); e.blob.chips = 3; return new
         * SideEffect(); }) .add(Events.ArtifactClaimedEvent.class, (GameViewContext
         * ctx, Artifact receiver, Events.ArtifactClaimedEvent e) -> {
         * ctx.view.game.events.signals.addListener(Events.CalculateFavorEvent.class,
         * e.artifact); return new SideEffect(); })
         * .add(Events.CalculateFavorEvent.class, (GameViewContext ctx, Artifact
         * receiver, Events.CalculateFavorEvent e) -> { if
         * (receiver.isClaimedByPlayer(e.player)) { for (Point p :
         * e.patron.getHostDomain(view).get()) { Tile t =
         * ctx.view.game.world.getTile(p).get(); if (t.getLeader().map((Player p1) ->
         * p1.equals(e.player)).orElse(false) && !t.unit.isPresent()) { e.favor++; } } }
         * return new SideEffect(); });
         */

        /**
         * SECTION Fates
         */

        // The Raider
        new Stratified<Fate>(events.fate, Labels.fate_raider).add(Events.GenerateFateEvent.class,
                (GameViewContext ctx, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_raider);
                    e.blob.desc.add("Playstyle: High-risk aggro");
                    e.blob.desc.add("• Your first unit will have the battle glyph");
                    e.blob.desc.add("• Your units always deal critical hits at or below 25% of their max health");
                    e.blob.desc
                            .add("• 15% chance for your units to fully heal themselves when they kill an enemy unit");
                    return new SideEffect();
                }).add(Events.GetInitialGlyphEvent.class,
                        (GameViewContext ctx, Fate receiver, Events.GetInitialGlyphEvent e) -> {
                            e.glyph = Optional.of(Glyph.BATTLE);
                            return new SideEffect();
                        })
                .add(Events.GameStartEvent.class, (GameViewContext ctx, Fate receiver, Events.GameStartEvent e) -> {
                    ctx.view.game.events.signals.addListener(Events.CheckCriticalHitEvent.class, receiver);
                    ctx.view.game.events.signals.addListener(Events.KilledEntityEvent.class, receiver);
                    return new SideEffect();
                }).add(Events.CheckCriticalHitEvent.class,
                        (GameViewContext ctx, Fate receiver, Events.CheckCriticalHitEvent e) -> {
                            if (e.entity.isEntityType(EntityType.UNIT)) {
                                final Unit u = (Unit) e.entity;
                                if (u.leadership.hasFate(receiver) && u.combat.health.atOrBelowPercent(25)) {
                                    e.chance = 100;
                                }
                            }
                            return new SideEffect();
                        })
                .add(Events.KilledEntityEvent.class,
                        (GameViewContext ctx, Fate receiver, Events.KilledEntityEvent e) -> {
                            if (e.killer.isEntityType(EntityType.UNIT) && e.target.isEntityType(EntityType.UNIT)) {
                                final Unit k = (Unit) e.killer;
                                final Unit t = (Unit) e.target;
                                if (k.leadership.hasFate(receiver) && !k.leadership.sameLeader(t)
                                        && Lambda.chance(15)) {
                                    k.combat.health.set(k.combat.health.getMax());
                                }
                            }
                            return new SideEffect();
                        });

        // The Merchant
        new Stratified<Fate>(events.fate, Labels.fate_merchant).add(Events.GenerateFateEvent.class,
                (GameViewContext ctx, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_merchant);
                    e.blob.desc.add("Playstyle: Market control");
                    e.blob.desc.add("• Your first unit will have the trade glyph");
                    e.blob.desc.add("• Your buildings that can hold item generate 2 unit points each turn");
                    e.blob.desc.add("• Your units generate 150% auction points");
                    return new SideEffect();
                }).add(Events.GetInitialGlyphEvent.class,
                        (GameViewContext ctx, Fate receiver, Events.GetInitialGlyphEvent e) -> {
                            e.glyph = Optional.of(Glyph.TRADE);
                            return new SideEffect();
                        })
                .add(Events.EndOfTurnEvent.class, (GameViewContext ctx, Fate receiver, Events.EndOfTurnEvent e) -> {
                    for (Point p : ctx.view.game.getVaultBuildings(receiver.getPlayer())) {
                        receiver.getPlayer().addUnitPoints(ctx.view, p, 2);
                    }
                    return new SideEffect();
                });
        new Stratified<Fate>(events.fate, Labels.fate_merchant).add(Events.GenerateAuctionPointsEvent.class,
                (GameViewContext ctx, Fate receiver, Events.GenerateAuctionPointsEvent e) -> {
                    if (e.unit.leadership.hasFate(receiver)) {
                        e.points += e.points / 2;
                    }
                    return new SideEffect();
                });

        // The Veteran
        new Stratified<Fate>(events.fate, Labels.fate_veteran).add(Events.GenerateFateEvent.class,
                (GameViewContext ctx, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_veteran);
                    e.blob.desc.add("Playstyle: Military production");
                    e.blob.desc.add("• Your battle glyph units heal for 3 damage when they don't act in a turn");
                    e.blob.desc.add("• Recruiting a battle glyph unit gives you 15 unit points");
                    return new SideEffect();
                }).add(Events.EndOfTurnEvent.class, (GameViewContext ctx, Fate receiver, Events.EndOfTurnEvent e) -> {
                    for (Unit u : receiver.getPlayer().units) {
                        if (ctx.view.game.actions.getUnitActionType(u).map((ActionType at) -> at == ActionType.SKIP)
                                .orElse(false)) {
                            u.combat.heal(ctx.view, 3);
                        }
                    }
                    return new SideEffect();
                }).add(Events.RecruitNewUnitEvent.class,
                        (GameViewContext ctx, Fate receiver, Events.RecruitNewUnitEvent e) -> {
                            if (e.unit.glyphs.has(Glyph.BATTLE)) {
                                receiver.getPlayer().addUnitPoints(ctx.view, e.unit.getPoint(), 15);
                            }
                            return new SideEffect();
                        });

        // The Sentinel
        new Stratified<Fate>(events.fate, Labels.fate_sentinel).add(Events.GenerateFateEvent.class,
                (GameViewContext ctx, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_sentinel);
                    e.blob.desc.add("Playstyle: Defensive expansion");
                    e.blob.desc.add("• Your buildings take 15% less damage");
                    e.blob.desc.add("• When you create a building the occupying unit gains 2 attack and defense");
                    e.blob.desc.add("• Recruiting a defense glyph unit gives you 20 unit points");
                    return new SideEffect();
                }).add(Events.GameStartEvent.class, (GameViewContext ctx, Fate receiver, Events.GameStartEvent e) -> {
                    ctx.view.game.events.signals.addListener(Events.TakeDamageEvent.class, receiver);
                    ctx.view.game.events.signals.addListener(Events.SpawnEvent.class, receiver);
                    return new SideEffect();
                }).add(Events.TakeDamageEvent.class, (GameViewContext ctx, Fate receiver, Events.TakeDamageEvent e) -> {
                    if (e.target.getEntityType() == EntityType.BUILDING
                            && e.target.getLeader().map((Player p) -> p.equals(receiver.getPlayer())).orElse(false)) {
                        e.dmg.base -= (int) (e.dmg.base * 0.15);
                    }
                    return new SideEffect();
                }).add(Events.SpawnEvent.class, (GameViewContext ctx, Fate receiver, Events.SpawnEvent e) -> {
                    if (e.spawned instanceof Building) {
                        final Building b = (Building) e.spawned;
                        if (b.getLeader().map((Player p) -> p.equals(receiver.getPlayer())).orElse(false)) {
                            return new SideEffect().add(() -> ctx.view.game.world.getTile(b.getPoint())
                                    .flatMap((Tile t) -> t.unit).ifPresent((Unit u) -> {
                                        u.abilities.addStatusEffect(ctx.view, Labels.status_effect_proud_builder)
                                                .execute();
                                    }));
                        }
                    }
                    return new SideEffect();
                }).add(Events.RecruitNewUnitEvent.class,
                        (GameViewContext ctx, Fate receiver, Events.RecruitNewUnitEvent e) -> {
                            if (e.unit.glyphs.has(Glyph.DEFENSE)) {
                                receiver.getPlayer().addUnitPoints(ctx.view, e.unit.getPoint(), 20);
                            }
                            return new SideEffect();
                        });

        // The Usurper
        new Stratified<Fate>(events.fate, Labels.fate_usurper).add(Events.GenerateFateEvent.class,
                (GameViewContext ctx, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_usurper);
                    e.blob.desc.add("Playstyle: Early market bonus into unit production");
                    e.blob.desc.add("• Your first unit will have the trade glyph");
                    e.blob.desc.add("• You get a free auction chip at the start of the game");
                    e.blob.desc.add("• You get 15 unit points when you do not win an auction");
                    return new SideEffect();
                }).add(Events.GetInitialGlyphEvent.class,
                        (GameViewContext ctx, Fate receiver, Events.GetInitialGlyphEvent e) -> {
                            e.glyph = Optional.of(Glyph.TRADE);
                            return new SideEffect();
                        })
                .add(Events.GameStartEvent.class, (GameViewContext ctx, Fate receiver, Events.GameStartEvent e) -> {
                    e.player.auctionChips++;
                    return new SideEffect();
                })
                .add(Events.LostAuctionEvent.class, (GameViewContext ctx, Fate receiver, Events.LostAuctionEvent e) -> {
                    e.player.addUnitPoints(ctx.view, 15);
                    return new SideEffect();
                });

        // The Forager
        new Stratified<Fate>(events.fate, Labels.fate_forager).add(Events.GenerateFateEvent.class,
                (GameViewContext ctx, Fate receiver, Events.GenerateFateEvent e) -> {
                    e.blob.image = Optional.of(Labels.asset_forager);
                    e.blob.desc.add("Playstyle: Resource accumulation");
                    e.blob.desc.add("• Your first unit will have the nature glyph");
                    e.blob.desc.add("• Your units have a 20% chance to generate an extra item while harvesting");
                    e.blob.desc.add("• Your nature glyph units have +1 speed");
                    return new SideEffect();
                }).add(Events.GetInitialGlyphEvent.class,
                        (GameViewContext ctx, Fate receiver, Events.GetInitialGlyphEvent e) -> {
                            e.glyph = Optional.of(Glyph.NATURE);
                            return new SideEffect();
                        })
                .add(Events.GameStartEvent.class, (GameViewContext ctx, Fate receiver, Events.GameStartEvent e) -> {
                    ctx.view.game.events.signals.addListener(Events.HarvestEvent.class, receiver);
                    ctx.view.game.events.signals.addListener(Events.UnitMoveDistanceEvent.class, receiver);
                    return new SideEffect();
                }).add(Events.HarvestEvent.class, (GameViewContext ctx, Fate receiver, Events.HarvestEvent e) -> {
                    if (e.unit.leadership.hasFate(receiver) && !e.unit.haul.isFull() && Lambda.chance(20)) {
                        e.unit.haul.add(ctx.view.game.generator.item(e.item.name));
                    }
                    return new SideEffect();
                }).add(Events.UnitMoveDistanceEvent.class,
                        (GameViewContext ctx, Fate receiver, Events.UnitMoveDistanceEvent e) -> {
                            if (e.unit.leadership.hasFate(receiver) && e.unit.glyphs.has(Glyph.NATURE)) {
                                e.distance++;
                            }
                            return new SideEffect();
                        });

        /**
         * SECTION Status Effects
         */

        // Stunned
        new Stratified<Ability>(events.ability, Labels.status_effect_stunned).add(Events.GenerateAbilityEvent.class,
                (GameViewContext ctx, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_stunned);
                    return new SideEffect();
                }).add(AbilityLogic.desc("The unit cannot act for 1 turn"))
                .add(Events.StatusEffectAddedEvent.class,
                        (GameViewContext ctx, Ability receiver,
                                Events.StatusEffectAddedEvent e) -> new SideEffect().add(() -> ctx.view.game.future
                                        .addFutureTick("Tick", receiver, 1, false, e.unit.getLeader())))
                .add("Tick",
                        (GameViewContext ctx, Ability receiver, Events.RepeatedEvent e) -> new SideEffect()
                                .add(() -> receiver.wielder.abilities.removeStatusEffect(ctx.view, receiver)))
                .add(Events.IsStunnedEvent.class, (GameViewContext ctx, Ability receiver, Events.IsStunnedEvent e) -> {
                    e.isStunned = true;
                    return new SideEffect();
                });

        // Proud Builder
        new Stratified<Ability>(events.ability, Labels.status_effect_proud_builder)
                .add(Events.GenerateAbilityEvent.class,
                        (GameViewContext ctx, Ability receiver, Events.GenerateAbilityEvent e) -> {
                            e.blob.setIcon(Labels.asset_proud_builder);
                            return new SideEffect();
                        })
                .add(AbilityLogic.desc("+2 attack and defense for 2 turns"))
                .add(Events.StatusEffectAddedEvent.class,
                        (GameViewContext ctx, Ability receiver,
                                Events.StatusEffectAddedEvent e) -> new SideEffect().add(() -> ctx.view.game.future
                                        .addFutureTick("Tick", receiver, 2, false, e.unit.getLeader())))
                .add("Tick",
                        (GameViewContext ctx, Ability receiver, Events.RepeatedEvent e) -> new SideEffect()
                                .add(() -> receiver.wielder.abilities.removeStatusEffect(ctx.view, receiver)))
                .add(Events.TakeDamageEvent.class,
                        (GameViewContext ctx, Ability receiver, Events.TakeDamageEvent e) -> {
                            e.dmg.base -= 2;
                            return new SideEffect();
                        })
                .add(Events.AttackEvent.class, (GameViewContext ctx, Ability receiver, Events.AttackEvent e) -> {
                    e.dmg.base += 2;
                    return new SideEffect();
                });

        // Extra Defense
        new Stratified<Ability>(events.ability, Labels.status_effect_extra_defense)
                .add(Events.GenerateAbilityEvent.class,
                        (GameViewContext ctx, Ability receiver, Events.GenerateAbilityEvent e) -> {
                            e.blob.setIcon(Labels.asset_shield);
                            return new SideEffect();
                        })
                .add(AbilityLogic.desc("+2 defense for 2 turns"))
                .add(Events.StatusEffectAddedEvent.class,
                        (GameViewContext ctx, Ability receiver,
                                Events.StatusEffectAddedEvent e) -> new SideEffect().add(() -> ctx.view.game.future
                                        .addFutureTick("Tick", receiver, 2, false, e.unit.getLeader())))
                .add("Tick",
                        (GameViewContext ctx, Ability receiver, Events.RepeatedEvent e) -> new SideEffect()
                                .add(() -> receiver.wielder.abilities.removeStatusEffect(ctx.view, receiver)))
                .add(Events.TakeDamageEvent.class,
                        (GameViewContext ctx, Ability receiver, Events.TakeDamageEvent e) -> {
                            e.dmg.base -= 2;
                            return new SideEffect();
                        });

        // Extra Attack
        new Stratified<Ability>(events.ability, Labels.status_effect_extra_attack)
                .add(Events.GenerateAbilityEvent.class,
                        (GameViewContext ctx, Ability receiver, Events.GenerateAbilityEvent e) -> {
                            // TODO add new icon
                            e.blob.setIcon(Labels.asset_apple);
                            return new SideEffect();
                        })
                .add(AbilityLogic.desc("+2 damage for 2 turns"))
                .add(Events.StatusEffectAddedEvent.class,
                        (GameViewContext ctx, Ability receiver,
                                Events.StatusEffectAddedEvent e) -> new SideEffect().add(() -> ctx.view.game.future
                                        .addFutureTick("Tick", receiver, 2, false, e.unit.getLeader())))
                .add("Tick",
                        (GameViewContext ctx, Ability receiver, Events.RepeatedEvent e) -> new SideEffect()
                                .add(() -> receiver.wielder.abilities.removeStatusEffect(ctx.view, receiver)))
                .add(Events.AttackEvent.class, (GameViewContext ctx, Ability receiver, Events.AttackEvent e) -> {
                    e.dmg.base += 2;
                    return new SideEffect();
                });

        // Poisoned
        new Stratified<Ability>(events.ability, Labels.status_effect_poisoned).add(Events.GenerateAbilityEvent.class,
                (GameViewContext ctx, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_poisoned);
                    return new SideEffect();
                }).add(AbilityLogic.desc("The unit takes 1 damage each turn for 4 turns"))
                .add(Events.StatusEffectAddedEvent.class,
                        (GameViewContext ctx, Ability receiver, Events.StatusEffectAddedEvent e) -> new SideEffect()
                                .add(() -> ctx.view.game.future.addFutureTick("Remove", receiver, 4, false,
                                        e.unit.getLeader()))
                                .add(() -> ctx.view.game.future.addFutureTick("Poison", receiver, 1, true,
                                        e.unit.getLeader())))
                .add("Remove",
                        (GameViewContext ctx, Ability receiver, Events.RepeatedEvent e) -> new SideEffect()
                                .add(() -> ctx.view.game.future.removeFutureTicks(receiver, "Poison"))
                                .add(() -> receiver.wielder.abilities.removeStatusEffect(ctx.view, receiver)))
                .add("Poison", (GameViewContext ctx, Ability receiver, Events.RepeatedEvent e) -> {
                    return receiver.wielder.combat.takeDamage(ctx, new Damage(1), receiver.wielder);
                });

        // Swift
        new Stratified<Ability>(events.ability, Labels.status_effect_swift).add(Events.GenerateAbilityEvent.class,
                (GameViewContext ctx, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_swift);
                    return new SideEffect();
                }).add(AbilityLogic.desc("The unit can move an extra space for the next 2 turns"))
                .add(Events.StatusEffectAddedEvent.class,
                        (GameViewContext ctx, Ability receiver,
                                Events.StatusEffectAddedEvent e) -> new SideEffect().add(() -> ctx.view.game.future
                                        .addFutureTick("Tick", receiver, 2, false, e.unit.getLeader())))
                .add("Tick",
                        (GameViewContext ctx, Ability receiver, Events.RepeatedEvent e) -> new SideEffect()
                                .add(() -> receiver.wielder.abilities.removeStatusEffect(ctx.view, receiver)))
                .add(Events.UnitMoveDistanceEvent.class,
                        (GameViewContext ctx, Ability receiver, Events.UnitMoveDistanceEvent e) -> {
                            e.distance++;
                            return new SideEffect();
                        });

        // Rally
        new Stratified<Ability>(events.ability, Labels.status_effect_rally).add(Events.GenerateAbilityEvent.class,
                (GameViewContext ctx, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_bloodlust);
                    return new SideEffect();
                }).add(AbilityLogic.desc("+2 damage and +1 speed for 2 turns"))
                .add(Events.StatusEffectAddedEvent.class,
                        (GameViewContext ctx, Ability receiver,
                                Events.StatusEffectAddedEvent e) -> new SideEffect().add(() -> ctx.view.game.future
                                        .addFutureTick("Tick", receiver, 2, false, e.unit.getLeader())))
                .add("Tick",
                        (GameViewContext ctx, Ability receiver, Events.RepeatedEvent e) -> new SideEffect()
                                .add(() -> receiver.wielder.abilities.removeStatusEffect(ctx.view, receiver)))
                .add(Events.AttackEvent.class, (GameViewContext ctx, Ability receiver, Events.AttackEvent e) -> {
                    e.dmg.base += 2;
                    return new SideEffect();
                }).add(Events.UnitMoveDistanceEvent.class,
                        (GameViewContext ctx, Ability receiver, Events.UnitMoveDistanceEvent e) -> {
                            e.distance++;
                            return new SideEffect();
                        });

        // Stone Form Active
        new Stratified<Ability>(events.ability, Labels.status_effect_stone_form_active)
                .add(Events.GenerateAbilityEvent.class,
                        (GameViewContext ctx, Ability receiver, Events.GenerateAbilityEvent e) -> {
                            e.blob.setIcon(Labels.asset_acid_skin, 0x34a33b, 0x828282);
                            return new SideEffect();
                        })
                .add(AbilityLogic.desc("The unit has returned to its natural rocky form"));

        // Conserved Energy
        new Stratified<Ability>(events.ability, Labels.status_effect_conserved_energy)
                .add(Events.GenerateAbilityEvent.class,
                        (GameViewContext ctx, Ability receiver, Events.GenerateAbilityEvent e) -> {
                            e.blob.setIcon(Labels.asset_regeneration, 0x00ff10, 0x00a9ff);
                            return new SideEffect();
                        })
                .add(AbilityLogic.desc("+1 defense")).add(Events.TakeDamageEvent.class,
                        (GameViewContext ctx, Ability receiver, Events.TakeDamageEvent e) -> {
                            e.dmg.base--;
                            return new SideEffect();
                        });

        // Burned
        new Stratified<Ability>(events.ability, Labels.status_effect_burned).add(Events.GenerateAbilityEvent.class,
                (GameViewContext ctx, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    e.blob.setIcon(Labels.asset_fireball);
                    return new SideEffect();
                }).add(AbilityLogic.desc("The unit takes 1 damage each turn for 4 turns"))
                .add(Events.StatusEffectAddedEvent.class,
                        (GameViewContext ctx, Ability receiver, Events.StatusEffectAddedEvent e) -> new SideEffect()
                                .add(() -> ctx.view.game.future.addFutureTick("Remove", receiver, 4, false,
                                        e.unit.getLeader()))
                                .add(() -> ctx.view.game.future.addFutureTick("Burn", receiver, 1, true,
                                        e.unit.getLeader())))
                .add("Remove",
                        (GameViewContext ctx, Ability receiver, Events.RepeatedEvent e) -> new SideEffect()
                                .add(() -> ctx.view.game.future.removeFutureTicks(receiver, "Burn"))
                                .add(() -> receiver.wielder.abilities.removeStatusEffect(ctx.view, receiver)))
                .add("Burn", (GameViewContext ctx, Ability receiver, Events.RepeatedEvent e) -> {
                    return receiver.wielder.combat.takeDamage(ctx, new Damage(1), receiver.wielder);
                });

        // Bleed
        new Stratified<Ability>(events.ability, Labels.status_effect_bleed).add(Events.GenerateAbilityEvent.class,
                (GameViewContext ctx, Ability receiver, Events.GenerateAbilityEvent e) -> {
                    // TODO add new icon
                    e.blob.setIcon(Labels.asset_apple);
                    return new SideEffect();
                }).add(AbilityLogic.desc("The unit takes 1 damage each turn for 3 turns"))
                .add(Events.StatusEffectAddedEvent.class,
                        (GameViewContext ctx, Ability receiver, Events.StatusEffectAddedEvent e) -> new SideEffect()
                                .add(() -> ctx.view.game.future.addFutureTick("Remove", receiver, 3, false,
                                        e.unit.getLeader()))
                                .add(() -> ctx.view.game.future.addFutureTick("Bleed", receiver, 1, true,
                                        e.unit.getLeader())))
                .add("Remove",
                        (GameViewContext ctx, Ability receiver, Events.RepeatedEvent e) -> new SideEffect()
                                .add(() -> ctx.view.game.future.removeFutureTicks(receiver, "Bleed"))
                                .add(() -> receiver.wielder.abilities.removeStatusEffect(ctx.view, receiver)))
                .add("Bleed", (GameViewContext ctx, Ability receiver, Events.RepeatedEvent e) -> {
                    return receiver.wielder.combat.takeDamage(ctx, new Damage(1), receiver.wielder);
                });
    }
}
