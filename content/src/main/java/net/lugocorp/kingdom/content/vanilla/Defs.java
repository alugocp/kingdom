package net.lugocorp.kingdom.content.vanilla;
import net.lugocorp.kingdom.game.properties.Species;

/**
 * Contains definitions for Unit Defs across all official content mods
 */
public class Defs {

    /**
     * SECTION Species
     */
    public static final Species species_human = new Species("Human", 0xb8a461);
    public static final Species species_elf = new Species("Elf", 0xb5db4d);
    public static final Species species_dwarf = new Species("Dwarf", 0xa6793c);
    public static final Species species_gnome = new Species("Gnome", 0xb8a386);
    public static final Species species_orc = new Species("Orc", 0x6e4b4b);
    public static final Species species_goblin = new Species("Goblin", 0x98c44d);
    public static final Species species_giant = new Species("Giant", 0x6e5e2b);
    public static final Species species_grue = new Species("Grue", 0x6e5e2b, Defs.species_giant);
    public static final Species species_ogre = new Species("Ogre", 0x6e5e2b, Defs.species_giant);
    public static final Species species_ettin = new Species("Ettin", 0x6e6c67, Defs.species_giant);
    public static final Species species_troll = new Species("Troll", 0x65827a, Defs.species_giant);
    public static final Species species_centaur = new Species("Centaur", 0x854d13);
    public static final Species species_salamander = new Species("Salamander", 0x4d51c4);
    public static final Species species_tortugan = new Species("Tortugan", 0x98c44d);
    public static final Species species_kappa = new Species("Kappa", 0x98c44d, Defs.species_tortugan);
    public static final Species species_brownie = new Species("Brownie", 0x854d13);
    public static final Species species_kobold = new Species("Kobold", 0x854d13);
    public static final Species species_firbolg = new Species("Firbolg", 0x854d13, Defs.species_kobold);
    public static final Species species_merfolk = new Species("Merfolk", 0x1677a1);
    public static final Species species_garuda = new Species("Garuda", 0x7da117);
    public static final Species species_dragonkin = new Species("Dragonkin", 0x801313);
    public static final Species species_toadstool = new Species("Toadstool", 0x176b0d);
    public static final Species species_sprite = new Species("Sprite", 0x176b0d);
    public static final Species species_dryad = new Species("Dryad", 0x176b0d, Defs.species_sprite);
    public static final Species species_demon = new Species("Demon", 0x801313);
    public static final Species species_tulpa = new Species("Tulpa", 0xd6892b);
    public static final Species species_golem = new Species("Golem", 0x696969);
    public static final Species species_plasmoid = new Species("Plasmoid", 0x8224d1);
    public static final Species species_undead = new Species("Undead", 0x696969);
    public static final Species species_elemental = new Species("Elemental", 0x696969);
    public static final Species species_gemstone = new Species("Gemstone", 0x8abdbc, Defs.species_elemental);
}
