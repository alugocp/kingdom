package net.lugocorp.kingdom.content.vanilla;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.builtin.logic.UnitLogic;
import net.lugocorp.kingdom.content.Defs;
import net.lugocorp.kingdom.content.Labels;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.gameplay.events.AllEventHandlers;
import net.lugocorp.kingdom.gameplay.events.Stratified;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;

/**
 * SECTION Units
 */
class VanillaModUnits {

    /**
     * Registers all Units for the Vanilla mod
     */
    static void registerEvents(AllEventHandlers events) {
        // Blorp the Burning
        new Stratified<Unit>(events.unit, Labels.unit_blorp_the_burning).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "A ravenous Plasmoid with an acidic body";
                    e.blob.setModelInstance(view.av, "blob");
                    e.blob.species = Defs.species_plasmoid;
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.DEFENSE);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_slime_shot, Labels.ability_absorb);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_slow,
                            Labels.ability_liquifying_presence, Labels.ability_acid_skin,
                            Labels.ability_total_appetite);
                    UnitLogic.largeHealthPool(e.blob);
                    e.blob.equipped.setMax(0);
                    e.blob.haul.setMax(3);
                    return new SideEffect();
                });

        // The Necromancer
        new Stratified<Unit>(events.unit, Labels.unit_necromancer).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This fallen creature now terrorizes its once idyllic home";
                    e.blob.setModelInstance(view.av, "druid");
                    e.blob.setMaterial("necromancer");
                    e.blob.species = Defs.species_undead;
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.DEFENSE);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_necrotic_blast,
                            Labels.ability_raise_undead);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_night_vision,
                            Labels.ability_regeneration);
                    UnitLogic.hungry(view, e.blob);
                    UnitLogic.largeHealthPool(e.blob);
                    return new SideEffect();
                });

        // Gloop the Adventurer
        new Stratified<Unit>(events.unit, Labels.unit_gloop_the_adventurer).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.setModelInstance(view.av, "gloop");
                    e.blob.desc = "This Plasmoid adventurer is eager to prove themself in the dungeons";
                    e.blob.species = Defs.species_plasmoid;
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.MINING);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_sword_slash,
                            Labels.ability_dungeon_delve);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_night_vision,
                            Labels.ability_combat_loot, Labels.ability_mine_gems, Labels.ability_regeneration);
                    UnitLogic.standardHealthPool(e.blob);
                    UnitLogic.largeInventory(e.blob);
                    return new SideEffect();
                });

        // Prismar
        new Stratified<Unit>(events.unit, Labels.unit_prismar).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.setModelInstance(view.av, Labels.asset_crystal);
                    e.blob.desc = "This Gemstone can focus light into powerful attacks";
                    e.blob.species = Defs.species_gemstone;
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.MINING);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_fire_laser,
                            Labels.ability_collapse_structure);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_night_vision,
                            Labels.ability_crystal_skin, Labels.ability_mine_gems, Labels.ability_rock_appetite);
                    UnitLogic.standardHealthPool(e.blob);
                    return new SideEffect();
                });

        // Viraqa Under the Mountain
        // Grimmsnout
        // Sir Tlatec
        new Stratified<Unit>(events.unit, Labels.unit_sir_tlatec).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.setModelInstance(view.av, "axolotl");
                    e.blob.desc = "Tlatec the Axolotl-man has travelled far from his home in search of worthy opponents";
                    e.blob.species = Defs.species_salamander;
                    e.blob.glyphs.set(Glyph.BATTLE);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_sword_slash,
                            Labels.ability_rallying_cry);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_swim, Labels.ability_hunt_fish,
                            Labels.ability_regeneration, Labels.ability_plate_mail);
                    UnitLogic.standardHealthPool(e.blob);
                    return new SideEffect();
                });

        // Cenuok the Battle Grue
        // Sathra the Flame Caster
        // Beetlemoss
        new Stratified<Unit>(events.unit, Labels.unit_beetlemoss).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This nature spirit guards an ancient forest in Eaglehaven";
                    e.blob.setModelInstance(view.av, "beetlemoss");
                    e.blob.species = Defs.species_sprite;
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.NATURE);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_fire_cannon,
                            Labels.ability_plant_forest);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_pick_apples,
                            Labels.ability_mine_gems);
                    UnitLogic.standardHealthPool(e.blob);
                    return new SideEffect();
                });

        // The Pumpkin King
        // Little Buck
        // Garulax
        // Patagan
        new Stratified<Unit>(events.unit, Labels.unit_patagan).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This battle mage is one of King Gargantos's most loyal subjects";
                    // TODO add a real model
                    e.blob.setModelInstance(view.av, "placeholder1");
                    e.blob.species = Defs.species_tortugan;
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.SUPPORT);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_fireball,
                            Labels.ability_heal_wounds);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_shell_defense);
                    UnitLogic.standardHealthPool(e.blob);
                    UnitLogic.largeInventory(e.blob);
                    return new SideEffect();
                });

        // Iyart
        //
        // Badroch the Pack Grue
        // Lord Sakamoto
        // Pelagma
        new Stratified<Unit>(events.unit, Labels.unit_pelagma).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "She disintegrates minerals for sustenance using her molten body";
                    e.blob.setModelInstance(view.av, "placeholder1");
                    e.blob.species = Defs.species_elemental;
                    e.blob.glyphs.set(Glyph.DEFENSE, Glyph.MINING);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_lava_bubble, Labels.ability_erupt);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_scalding_skin,
                            Labels.ability_mine_gems, Labels.ability_mine_gold, Labels.ability_rock_appetite);
                    UnitLogic.largeHealthPool(e.blob);
                    UnitLogic.hungry(view, e.blob);
                    return new SideEffect();
                });

        // Ushaptimun
        // Garudee
        new Stratified<Unit>(events.unit, Labels.unit_garudee).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This Garuda artilleryman spits pebbles at invaders";
                    e.blob.setModelInstance(view.av, "garudee");
                    e.blob.species = Defs.species_garuda;
                    e.blob.glyphs.set(Glyph.DEFENSE);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_pebble_shot,
                            Labels.ability_swing_axe);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_slow, Labels.ability_entrenched);
                    UnitLogic.largeHealthPool(e.blob);
                    return new SideEffect();
                });

        // Defender Cuauhtli
        // The Hunched Warlock
        //
        // Lost Golem
        new Stratified<Unit>(events.unit, Labels.unit_lost_golem).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This Golem guides those who wind up in the Lost Lands";
                    // TODO add a real model
                    e.blob.setModelInstance(view.av, "placeholder1");
                    e.blob.species = Defs.species_golem;
                    e.blob.glyphs.set(Glyph.DEFENSE, Glyph.NATURE);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_stomp, Labels.ability_plant_forest);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_plant_cushion,
                            Labels.ability_harvest_figs);
                    UnitLogic.largeHealthPool(e.blob);
                    return new SideEffect();
                });

        // Golem of the Grotto
        new Stratified<Unit>(events.unit, Labels.unit_golem_of_the_grotto).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This Golem wanders the rocky peaks where it was forged long ago";
                    e.blob.setModelInstance(view.av, "golem-grotto");
                    e.blob.species = Defs.species_golem;
                    e.blob.glyphs.set(Glyph.DEFENSE, Glyph.NATURE);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_smash, Labels.ability_plant_meadow);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_mountain_strider,
                            Labels.ability_regeneration, Labels.ability_life_finds_a_way);
                    UnitLogic.largeHealthPool(e.blob);
                    return new SideEffect();
                });

        // Puffshroom
        new Stratified<Unit>(events.unit, Labels.unit_puffshroom).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This Golem-like fungal being spawns new ecosystems where it roams";
                    e.blob.setModelInstance(view.av, "puffshroom");
                    e.blob.species = Defs.species_toadstool;
                    e.blob.glyphs.set(Glyph.DEFENSE, Glyph.NATURE);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_smash,
                            Labels.ability_protective_spores);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_defensive_bloom,
                            Labels.ability_harvest_mushrooms);
                    UnitLogic.largeHealthPool(e.blob);
                    return new SideEffect();
                });

        //
        // The Elder
        //
        // King Gargantos
        new Stratified<Unit>(events.unit, Labels.unit_king_gargantos).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "Warrior-king of the Tortoise Kingdom";
                    e.blob.setModelInstance(view.av, "gargantos");
                    e.blob.species = Defs.species_tortugan;
                    e.blob.glyphs.set(Glyph.DEFENSE, Glyph.TRADE);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_smash,
                            Labels.ability_construct_marketplace);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_swim, Labels.ability_shell_defense,
                            Labels.ability_market_boom);
                    UnitLogic.largeHealthPool(e.blob);
                    UnitLogic.hungry(view, e.blob);
                    return new SideEffect();
                });

        // Sir Rootbeard
        new Stratified<Unit>(events.unit, Labels.unit_sir_rootbeard).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "Sir Rootbeard is among the most respected Dwarves in all of Surgarde";
                    e.blob.setModelInstance(view.av, "placeholder1");
                    e.blob.species = Defs.species_dwarf;
                    e.blob.glyphs.set(Glyph.DEFENSE, Glyph.TRADE);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_shield_bash);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_fast,
                            Labels.ability_conservation_of_energy, Labels.ability_investment, Labels.ability_trade,
                            Labels.ability_stone_form, Labels.ability_defensive_stone_form);
                    UnitLogic.standardHealthPool(e.blob);
                    UnitLogic.largeInventory(e.blob);
                    return new SideEffect();
                });

        //
        // Stalagmus
        new Stratified<Unit>(events.unit, Labels.unit_stalagmus).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "Enchanted waters accumulate into this Golem's bowl-shaped body";
                    e.blob.setModelInstance(view.av, "stalagmus");
                    e.blob.species = Defs.species_golem;
                    e.blob.glyphs.set(Glyph.MINING);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_hurl_rock,
                            Labels.ability_construct_mine);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_night_vision,
                            Labels.ability_rock_appetite, Labels.ability_stone_defense, Labels.ability_mine_gems,
                            Labels.ability_mine_gold, Labels.ability_subterranean_potions);
                    UnitLogic.standardHealthPool(e.blob);
                    return new SideEffect();
                });

        // Xella the Accursed
        // Svelta Luktegress
        new Stratified<Unit>(events.unit, Labels.unit_svelta_luktegress).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This Dwarven miner is well known throughout Helligdom";
                    e.blob.setModelInstance(view.av, "placeholder1");
                    e.blob.species = Defs.species_dwarf;
                    e.blob.glyphs.set(Glyph.MINING);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_swing_pickaxe,
                            Labels.ability_construct_mine);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_fast, Labels.ability_night_vision,
                            Labels.ability_mine_gems, Labels.ability_stone_form, Labels.ability_mining_stone_form);
                    UnitLogic.standardHealthPool(e.blob);
                    UnitLogic.largeInventory(e.blob);
                    return new SideEffect();
                });

        // Alaistar and Wurmdel
        // Mi'chalb Lightfoot
        // Illapa (Quechua for lightning)
        //
        // The Druid
        new Stratified<Unit>(events.unit, Labels.unit_druid).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "A mysterious Druid who rarely speaks";
                    e.blob.setModelInstance(view.av, "the-druid");
                    e.blob.species = Defs.species_sprite;
                    e.blob.glyphs.set(Glyph.NATURE);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_revenge_of_the_forest,
                            Labels.ability_plant_forest);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_high_vision,
                            Labels.ability_night_vision, Labels.ability_pick_apples, Labels.ability_green_fortress);
                    UnitLogic.standardHealthPool(e.blob);
                    return new SideEffect();
                });

        // Dendra Ivy
        // Pumpkin Boy
        new Stratified<Unit>(events.unit, Labels.unit_pumpkin_boy).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "He doesn't say much, he's just a little guy";
                    e.blob.setModelInstance(view.av, "pumpkin-boy");
                    e.blob.species = Defs.species_sprite;
                    e.blob.glyphs.set(Glyph.NATURE);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_plant_meadow, Labels.ability_hug);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_night_vision,
                            Labels.ability_regeneration, Labels.ability_running_through_nature,
                            Labels.ability_sacred_seeds);
                    UnitLogic.standardHealthPool(e.blob);
                    UnitLogic.largeInventory(e.blob);
                    return new SideEffect();
                });

        // Barometz
        new Stratified<Unit>(events.unit, Labels.unit_barometz).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This sheep-like Sprite blooms with delicious fruit";
                    e.blob.setModelInstance(view.av, "barometz");
                    e.blob.species = Defs.species_sprite;
                    e.blob.glyphs.set(Glyph.NATURE);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_bite);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_regeneration, Labels.ability_edible,
                            Labels.ability_deposit_seeds);
                    UnitLogic.standardHealthPool(e.blob);
                    UnitLogic.largeInventory(e.blob);
                    return new SideEffect();
                });

        //
        // Condylure of the Star Nose
        new Stratified<Unit>(events.unit, Labels.unit_condylure_of_the_star_nose).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This Brownie is blind, but traverses the subterranean world with the aid of his nose";
                    e.blob.setModelInstance(view.av, "condylure");
                    e.blob.species = Defs.species_brownie;
                    e.blob.glyphs.set(Glyph.SUPPORT, Glyph.MINING);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_construct_healing_fountain,
                            Labels.ability_construct_mine);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_night_vision,
                            Labels.ability_mine_gems);
                    UnitLogic.standardHealthPool(e.blob);
                    return new SideEffect();
                });

        // Photali
        // Batatita
        new Stratified<Unit>(events.unit, Labels.unit_batatita).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This small nature spirit communes with the hearty tubers that grow within caves";
                    // TODO add a real model
                    e.blob.setModelInstance(view.av, "placeholder1");
                    e.blob.species = Defs.species_sprite;
                    e.blob.glyphs.set(Glyph.SUPPORT, Glyph.MINING);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_medicinal_tuber,
                            Labels.ability_raid_mine);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_regeneration,
                            Labels.ability_harvest_batatas, Labels.ability_mine_gold);
                    UnitLogic.standardHealthPool(e.blob);
                    return new SideEffect();
                });

        // Lady Daumia
        new Stratified<Unit>(events.unit, Labels.unit_lady_daumia).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "Elven high missionary to Surgarde";
                    e.blob.setModelInstance(view.av, "daumia");
                    e.blob.species = Defs.species_elf;
                    e.blob.glyphs.set(Glyph.SUPPORT);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_heal_wounds,
                            Labels.ability_self_sacrifice);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_night_vision,
                            Labels.ability_life_aura);
                    UnitLogic.standardHealthPool(e.blob);
                    return new SideEffect();
                });

        // Zen Hito the Kappa
        new Stratified<Unit>(events.unit, Labels.unit_zen_hito_the_kappa).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "A wandering healer who never turns down a request for help";
                    // TODO add a real model
                    e.blob.setModelInstance(view.av, "placeholder1");
                    e.blob.species = Defs.species_kappa;
                    e.blob.glyphs.set(Glyph.SUPPORT);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_scratch,
                            Labels.ability_construct_healing_fountain);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_swim, Labels.ability_great_cycle,
                            Labels.ability_healing_water);
                    UnitLogic.standardHealthPool(e.blob);
                    return new SideEffect();
                });

        // Soothing Gills
        // Melis the Honey Troll
        // Passiflor
        // Oystermane
        // Wuraj the Blessed
        new Stratified<Unit>(events.unit, Labels.unit_wuraj_the_blessed).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This Elven shaman has dedicated her life to the restoration of her ancestral forests";
                    // TODO add a real model
                    e.blob.setModelInstance(view.av, "placeholder1");
                    e.blob.species = Defs.species_elf;
                    e.blob.glyphs.set(Glyph.SUPPORT, Glyph.NATURE);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_power_of_nature,
                            Labels.ability_heal_wounds);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_high_vision,
                            Labels.ability_night_vision, Labels.ability_blessing_of_natures_hand,
                            Labels.ability_harvest_cacao, Labels.ability_harvest_mesquite);
                    UnitLogic.standardHealthPool(e.blob);
                    return new SideEffect();
                });

        // Frogger the Gnome
        new Stratified<Unit>(events.unit, Labels.unit_frogger_the_gnome).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "Just a little Gnome and his frog";
                    e.blob.setModelInstance(view.av, "frog-gnome");
                    e.blob.species = Defs.species_gnome;
                    e.blob.glyphs.set(Glyph.SUPPORT, Glyph.TRADE);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_heal_wounds,
                            Labels.ability_hungry_frog_magic);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_swim,
                            Labels.ability_harvest_truffles, Labels.ability_investment, Labels.ability_trade);
                    UnitLogic.standardHealthPool(e.blob);
                    UnitLogic.largeInventory(e.blob);
                    return new SideEffect();
                });

        // Theressa the Rover
        // Keeper of the Keys
        // Wisp Walker
        // Al-Fikra
        new Stratified<Unit>(events.unit, Labels.unit_al_fikra).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This being aids the great merchant kings of Eastern Bycidia";
                    e.blob.setModelInstance(view.av, "alfikra");
                    e.blob.species = Defs.species_tulpa;
                    e.blob.glyphs.set(Glyph.TRADE);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_fast, Labels.ability_high_vision,
                            Labels.ability_regeneration, Labels.ability_market_indicator);
                    UnitLogic.standardHealthPool(e.blob);
                    UnitLogic.largeInventory(e.blob);
                    return new SideEffect();
                });

        // Ghastly Thrall
        new Stratified<Unit>(events.unit, Labels.unit_ghastly_thrall)
                .add(Events.GenerateUnitEvent.class, (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.doNotAddToGlyphPool();
                    e.blob.desc = "A terrifying undead warrior risen by those skilled in the dark arts";
                    e.blob.setModelInstance(view.av, "skeleton");
                    e.blob.species = Defs.species_undead;
                    e.blob.glyphs.set(Glyph.BATTLE);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_sword_slash);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_ghastly_thrall);
                    e.blob.combat.health.setMaxAndValue(4);
                    return new SideEffect();
                }).add(UnitLogic.speed(100));
    }
}
