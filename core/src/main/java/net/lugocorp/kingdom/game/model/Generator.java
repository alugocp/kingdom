package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.context.Context;

/**
 * This class is like a factory class for all game objects. It calls the
 * relevant generator Events to instantiate each new object.
 */
public class Generator {

    /**
     * Generates a new Tile
     */
    public Tile tile(String name, int x, int y) {
        final Events.GenerateTileEvent e = new Events.GenerateTileEvent(new Tile(name, x, y));
        Context.getContext().events.tile.handle(e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Unit
     */
    public Unit unit(String name, int x, int y) {
        return this.unitOptimal(new Unit(name, x, y));
    }

    /**
     * Calls a generation Event on the given Unit. This is here to avoid
     * instantiating a new Unit for repetitive Unit generation (like for GlyphPools)
     * but note that these Units should not be spawned unless a fresh Unit object is
     * passed in every time.
     */
    public Unit unitOptimal(Unit u) {
        final Events.GenerateUnitEvent e = new Events.GenerateUnitEvent(u);
        Context.getContext().events.unit.handle(e.blob, e);
        return u;
    }

    /**
     * Generates a new Building
     */
    public Building building(String name, int x, int y) {
        final Events.GenerateBuildingEvent e = new Events.GenerateBuildingEvent(new Building(name, x, y));
        Context.getContext().events.building.handle(e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Tower
     */
    public Tower tower(int x, int y) {
        final Tower t = new Tower(x, y);
        final Events.GenerateBuildingEvent e = new Events.GenerateBuildingEvent(t);
        Context.getContext().events.building.handle(e.blob, e);
        return t;
    }

    /**
     * Generates a new Patron
     */
    public Patron patron(String name, int x, int y) {
        final Events.GeneratePatronEvent e = new Events.GeneratePatronEvent(new Patron(name, x, y));
        Context.getContext().events.patron.handle(e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Item
     */
    public Item item(String name) {
        final Events.GenerateItemEvent e = new Events.GenerateItemEvent(new Item(name));
        Context.getContext().events.item.handle(e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Ability
     */
    public Ability ability(Unit wielder, String name) {
        final Events.GenerateAbilityEvent e = new Events.GenerateAbilityEvent(new Ability(wielder, name));
        Context.getContext().events.ability.handle(e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Artifact
     */
    public Artifact artifact(String name) {
        final Events.GenerateArtifactEvent e = new Events.GenerateArtifactEvent(new Artifact(name));
        Context.getContext().events.artifact.handle(e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Fate
     */
    public Fate fate(String name) {
        final Events.GenerateFateEvent e = new Events.GenerateFateEvent(new Fate(name));
        Context.getContext().events.fate.handle(e.blob, e);
        return e.blob;
    }
}
