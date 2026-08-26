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
        // Knuckleheads
        new Stratified<Unit>(events.unit, Labels.unit_knuckleheads).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.setModelInstance(view.av, "knuckleheads");
                    e.blob.desc = "This Ettin roams the Dragonlands and feasts on giant lizard flesh";
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_bash, Labels.ability_stomp);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_shield_defense,
                            Labels.ability_efficient_stomach);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.DEFENSE);
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.haul.setMax(2);
                    e.blob.species = Defs.species_ettin;
                    return new SideEffect();
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
                            Labels.ability_loose_gems, Labels.ability_rock_appetite);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.MINING);
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.species = Defs.species_golem;
                    return new SideEffect();
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
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.species = Defs.species_brownie;
                    return new SideEffect();
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
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.species = Defs.species_salamander;
                    return new SideEffect();
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
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.species = Defs.species_sprite;
                    return new SideEffect();
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
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_plasmoid;
                    return new SideEffect();
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
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.species = Defs.species_plasmoid;
                    return new SideEffect();
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
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_entrenched, Labels.ability_slow);
                    e.blob.glyphs.set(Glyph.DEFENSE);
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.species = Defs.species_garuda;
                    return new SideEffect();
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
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.species = Defs.species_golem;
                    return new SideEffect();
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
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.species = Defs.species_toadstool;
                    return new SideEffect();
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
                    e.blob.glyphs.set(Glyph.SUPPORT, Glyph.MINING);
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.species = Defs.species_brownie;
                    return new SideEffect();
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
                    e.blob.glyphs.set(Glyph.SUPPORT);
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.species = Defs.species_elf;
                    return new SideEffect();
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
                    e.blob.glyphs.set(Glyph.SUPPORT);
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_gnome;
                    return new SideEffect();
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
                            Labels.ability_subterranean_potions, Labels.ability_rock_appetite);
                    e.blob.glyphs.set(Glyph.MINING);
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.species = Defs.species_golem;
                    return new SideEffect();
                });

        // Glimmer
        // Grizzlemane the Mycoweaver
        // Magicad
        // The Druid
        new Stratified<Unit>(events.unit, Labels.unit_druid).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "A mysterious Druid who rarely speaks";
                    e.blob.setModelInstance(view.av, "the-druid");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_plant_forest,
                            Labels.ability_revenge_of_the_forest);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_pick_apples,
                            Labels.ability_night_vision, Labels.ability_green_fortress, Labels.ability_high_vision);
                    e.blob.glyphs.set(Glyph.NATURE);
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.species = Defs.species_sprite;
                    return new SideEffect();
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
                            Labels.ability_liquifying_presence, Labels.ability_total_appetite);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.DEFENSE);
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.species = Defs.species_plasmoid;
                    return new SideEffect();
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
                            Labels.ability_night_vision, Labels.ability_mine_gems, Labels.ability_rock_appetite);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.MINING);
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.species = Defs.species_gemstone;
                    return new SideEffect();
                });

        // Slip
        new Stratified<Unit>(events.unit, Labels.unit_slip).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This Plasmoid stores wares in its gelatinous form and ferries them across trade routes";
                    e.blob.setModelInstance(view.av, "blob");
                    e.blob.setMaterial("slip", 1);
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_metabolize);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_regeneration,
                            Labels.ability_market_value_goo, Labels.ability_economic_activity,
                            Labels.ability_total_appetite);
                    e.blob.glyphs.set(Glyph.TRADE);
                    e.blob.combat.health.setMaxAndValue(1);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_plasmoid;
                    return new SideEffect();
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
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_sprite;
                    return new SideEffect();
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
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_sprite;
                    return new SideEffect();
                });

        // Xella the Accursed
        // Svelta Luktegress
        // Al-Fikra
        new Stratified<Unit>(events.unit, Labels.unit_al_fikra).add(Events.GenerateUnitEvent.class,
                (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.desc = "This being aids the great merchant kings of Eastern Bycidia";
                    e.blob.setModelInstance(view.av, "alfikra");
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_regeneration,
                            Labels.ability_market_indicator, Labels.ability_fast, Labels.ability_high_vision);
                    e.blob.glyphs.set(Glyph.TRADE);
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_tulpa;
                    return new SideEffect();
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
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.species = Defs.species_undead;
                    UnitLogic.hungry(view, e.blob);
                    return new SideEffect();
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
                    e.blob.glyphs.set(Glyph.DEFENSE, Glyph.SUPPORT);
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.species = Defs.species_merfolk;
                    return new SideEffect();
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
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_smash,
                            Labels.ability_build_marketplace);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_shell_defense,
                            Labels.ability_market_boom, Labels.ability_swim);
                    e.blob.glyphs.set(Glyph.DEFENSE, Glyph.TRADE);
                    e.blob.combat.health.setMaxAndValue(20);
                    e.blob.species = Defs.species_tortugan;
                    UnitLogic.hungry(view, e.blob);
                    return new SideEffect();
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
                            Labels.ability_pious, Labels.ability_fast);
                    e.blob.glyphs.set(Glyph.SUPPORT, Glyph.TRADE);
                    e.blob.combat.health.setMaxAndValue(10);
                    e.blob.haul.setMax(12);
                    e.blob.species = Defs.species_human;
                    return new SideEffect();
                });

        // Theressa the Rover
        //
        // Illapa
        // Disastra
        // Chicao
        // Alaistar and Wurmdel
        // Mi'chalb Lightfoot
        // Ghastly Thrall
        new Stratified<Unit>(events.unit, Labels.unit_ghastly_thrall)
                .add(Events.GenerateUnitEvent.class, (GameView view, Unit receiver, Events.GenerateUnitEvent e) -> {
                    e.blob.doNotAddToGlyphPool();
                    e.blob.desc = "A terrifying undead warrior risen by those skilled in the dark arts";
                    e.blob.setModelInstance(view.av, "skeleton");
                    e.blob.abilities.setActive(view.game.generator, Labels.ability_sword_slash);
                    e.blob.abilities.setPassive(view.game.generator, Labels.ability_ghastly_thrall);
                    e.blob.glyphs.set(Glyph.BATTLE);
                    e.blob.combat.health.setMaxAndValue(4);
                    e.blob.species = Defs.species_undead;
                    return new SideEffect();
                }).add(UnitLogic.speed(100));
    }
}
