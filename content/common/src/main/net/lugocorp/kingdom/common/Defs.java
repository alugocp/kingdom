package net.lugocorp.kingdom.common;
import net.lugocorp.kingdom.game.model.fields.Species;

/**
 * Contains definitions for names and labels across all official content mods
 */
public class Defs {
    /**
     * SECTION Races
     */
    public static final Species species_human = new Species("Human");
    public static final Species species_elf = new Species("Elf");
    public static final Species species_dwarf = new Species("Dwarf");
    public static final Species species_gnome = new Species("Gnome");
    public static final Species species_orc = new Species("Orc");
    public static final Species species_goblin = new Species("Goblin");
    public static final Species species_giant = new Species("Giant");
    public static final Species species_grue = new Species("Grue", species_giant);
    public static final Species species_ogre = new Species("Ogre", species_giant);
    public static final Species species_ettin = new Species("Ettin", species_giant);
    public static final Species species_troll = new Species("Troll", species_giant);
    public static final Species species_centaur = new Species("Centaur");
    public static final Species species_salamander = new Species("Salamander");
    public static final Species species_tortugan = new Species("Tortugan");
    public static final Species species_kappa = new Species("Kappa", species_tortugan);
    public static final Species species_brownie = new Species("Brownie");
    public static final Species species_kobold = new Species("Kobold");
    public static final Species species_firbolg = new Species("Firbolg", species_kobold);
    public static final Species species_merfolk = new Species("Merfolk");
    public static final Species species_garuda = new Species("Garuda");
    public static final Species species_dragonkin = new Species("Dragonkin");
    public static final Species species_toadstool = new Species("Toadstool");
    public static final Species species_sprite = new Species("Sprite");
    public static final Species species_dryad = new Species("Dryad", species_sprite);
    public static final Species species_demon = new Species("Demon");
    public static final Species species_tulpa = new Species("Tulpa");
    public static final Species species_golem = new Species("Golem");
    public static final Species species_plasmoid = new Species("Plasmoid");
    public static final Species species_undead = new Species("Undead");
    public static final Species species_elemental = new Species("Elemental");
    public static final Species species_gemstone = new Species("Gemstone", species_elemental);

    /**
     * SECTION Tags
     */
    public static final String tag_natural = "natural";
    public static final String tag_fruit = "fruit";

    /**
     * SECTION Tiles
     */
    public static final String tile_grass = "Grass";
    public static final String tile_rock = "Rock";
    public static final String tile_sand = "Sand";
    public static final String tile_snow = "Snow";
    public static final String tile_water = "Water";
    public static final String tile_lava = "Lava";

    /**
     * SECTION Buildings
     */
    public static final String building_mine = "Mine";
    public static final String building_vault = "Vault";
    public static final String building_forest = "Forest";
    public static final String building_taiga = "Taiga";
    public static final String building_meadow = "Meadow";
    public static final String building_oasis = "Oasis";
    public static final String building_shrubland = "Shrubland";
    public static final String building_mountain = "Mountain";
    public static final String building_healing_fountain = "Healing Fountain";

    /**
     * SECTION Patrons
     */
    public static final String patron_pond_troll = "The Pond Troll";
    public static final String patron_shining_eyes = "The Shining Eyes";

    /**
     * SECTION Artifacts
     */
    public static final String artifact_chos_sigil_of_haste = "Cho's Sigil of Haste";
    public static final String artifact_urdins_scroll_of_agility = "Urdin's Scroll of Agility";
    public static final String artifact_sword_of_aesethos = "Sword of Aesethos";
    public static final String artifact_kaunas_amulet = "Kauna's Amulet";
    public static final String artifact_staff_of_wurmdel = "Staff of Wurmdel";
    public static final String artifact_tome_of_morun = "Tome of Morun";
    public static final String artifact_orb_of_nerketo = "Orb of Nerketo";
    public static final String artifact_shadas_flute = "Shada's Flute";
    public static final String artifact_stones_of_thudin = "Stones of Thudin";
    public static final String artifact_the_chasi_bones = "The Chasi Bones";
    public static final String artifact_uchas_bowl_of_plenty = "Ucha's Bowl of Plenty";
    public static final String artifact_nerketos_helm = "Nerketo's Helm";
    public static final String artifact_bounty_of_ahn_june = "Bounty of Ahn-Juné";
    public static final String artifact_mark_of_kung = "Mark of Kung";
    public static final String artifact_chalcos_seal_of_protection = "Chalco's Seal of Protection";
    public static final String artifact_podas_elixir = "Poda's Elixir";
    public static final String artifact_gaias_effigy = "Gaia's Effigy";
    public static final String artifact_rod_of_adelon = "Rod of Adelon";
    public static final String artifact_blade_of_sanguinor = "Blade of Sanguinor";
    public static final String artifact_cask_of_amonitor = "Cask of Amontior";

    /**
     * SECTION Fates
     */
    public static final String fate_raider = "The Raider";
    public static final String fate_merchant = "The Merchant";
    public static final String fate_veteran = "The Veteran";
    public static final String fate_devout = "The Devout";
    public static final String fate_sentinel = "The Sentinel";
    public static final String fate_usurper = "The Usurper";
    public static final String fate_forager = "The Forager";

    /**
     * SECTION Units
     */
    public static final String unit_sir_tlatec = "Sir Tlatec";
    public static final String unit_beetlemoss = "Beetlemoss";
    public static final String unit_gloop_the_adventurer = "Gloop the Adventurer";
    public static final String unit_golem_of_the_grotto = "Golem of the Grotto";
    public static final String unit_condylure_of_the_star_nose = "Condylure of the Star Nose";
    public static final String unit_lady_daumia = "Lady Daumia";
    public static final String unit_frogger_the_gnome = "Frogger the Gnome";
    public static final String unit_stalagmus = "Stalagmus";
    public static final String unit_the_druid = "The Druid";
    public static final String unit_blorp_the_burning = "Blorp the Burning";
    public static final String unit_prismar = "Prismar";
    public static final String unit_pumpkin_boy = "Pumpkin Boy";
    public static final String unit_barometz = "Barometz";
    public static final String unit_al_fikra = "Al-Fikra";
    public static final String unit_king_gargantos = "King Gargantos";

    /**
     * SECTION Abilities
     */
    public static final String ability_acid_skin = "Acid Skin";
    public static final String ability_bite = "Bite";
    public static final String ability_build_healing_fountain = "Build Healing Fountain";
    public static final String ability_build_vault = "Build Vault";
    public static final String ability_collapse_mine = "Collapse Mine";
    public static final String ability_combat_loot = "Combat Loot";
    public static final String ability_crystal_skin = "Crystal Skin";
    public static final String ability_deposit_seeds = "Deposit Seeds";
    public static final String ability_dig_mine = "Dig Mine";
    public static final String ability_dungeon_delve = "Dungeon Delve";
    public static final String ability_edible = "Edible";
    public static final String ability_fire_cannon = "Fire Cannon";
    public static final String ability_fire_laser = "Fire Laser";
    public static final String ability_green_fortress = "Green Fortress";
    public static final String ability_heal_wounds = "Heal Wounds";
    public static final String ability_hug = "Hug";
    public static final String ability_hungry_frog_magic = "Hungry Frog Magic";
    public static final String ability_hunt_fish = "Hunt Fish";
    public static final String ability_hurl_rock = "Hurl Rock";
    public static final String ability_life_aura = "Life Aura";
    public static final String ability_liquifying_presence = "Liquifying Presence";
    public static final String ability_local_defender = "Local Defender";
    public static final String ability_market_boom = "Market Boom";
    public static final String ability_market_indicator = "Market Indicator";
    public static final String ability_mine_gems = "Mine Gems";
    public static final String ability_mine_gold = "Mine Gold";
    public static final String ability_mountain_strider = "Mountain Strider";
    public static final String ability_night_vision = "Night Vision";
    public static final String ability_pick_apples = "Pick Apples";
    public static final String ability_pick_flowers = "Pick Flowers";
    public static final String ability_plant_forest = "Plant Forest";
    public static final String ability_plant_meadow = "Plant Meadow";
    public static final String ability_plate_mail = "Plate Mail";
    public static final String ability_regeneration = "Regeneration";
    public static final String ability_revenge_of_the_forest = "Revenge of the Forest";
    public static final String ability_running_through_nature = "Running Through Nature";
    public static final String ability_self_sacrifice = "Self Sacrifice";
    public static final String ability_sacred_seeds = "Sacred Seeds";
    public static final String ability_shell_defense = "Shell Defense";
    public static final String ability_slime_shot = "Slime Shot";
    public static final String ability_smash = "Smash";
    public static final String ability_stone_defense = "Stone Defense";
    public static final String ability_subterranean_potions = "Subterranean Potions";
    public static final String ability_swim = "Swim";
    public static final String ability_sword_slash = "Sword Slash";

    /**
     * SECTION Status Effects
     */
    public static final String status_effect_stunned = "Stunned";

    /**
     * SECTION Items
     */
    public static final String item_sacred_seed = "Sacred Seed";
    public static final String item_flower = "Flower";
    public static final String item_fish = "Fish";
    public static final String item_gold_coin = "Gold Coin";
    public static final String item_emerald = "Emerald";
    public static final String item_apple = "Apple";
    public static final String item_health_potion = "Health Potion";
}
