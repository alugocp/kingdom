package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * TODO
 * 1) add a CreationContext for pre-GameView stuff (just support Generate*Events so we can use them in the Deserializer);
 * 2) Move the AllEventHandlers instance to CreationContext or something (GameViewContext should be a subclass of it);
 * 3) Remove the mechanics init stuff / instantiating GameView before game.init() (remove game.init) now that CreationContext can handle Generate*Events;
 * 4) No, make Context globally accessible and move AllEventHandlers into CreationContext
 * 5) Create a NullContext for before we load game content?
 */

/**
 * This class is like a factory class for all game objects. It calls the
 * relevant generator Events to instantiate each new object.
 */
public class Generator {
    // TODO serial remove dependence on GameView
    private GameView view = null;

    public void setGameView(GameView view) {
        this.view = view;
    }

    /**
     * Generates a new Tile
     */
    public Tile tile(String name, int x, int y) {
        final Events.GenerateTileEvent e = new Events.GenerateTileEvent(new Tile(name, x, y));
        this.view.game.events.tile.handle(e.blob, e);
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
        this.view.game.events.unit.handle(e.blob, e);
        return u;
    }

    /**
     * Generates a new Building
     */
    public Building building(String name, int x, int y) {
        final Events.GenerateBuildingEvent e = new Events.GenerateBuildingEvent(new Building(name, x, y));
        this.view.game.events.building.handle(e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Tower
     */
    public Tower tower(int x, int y) {
        final Tower t = new Tower(x, y);
        final Events.GenerateBuildingEvent e = new Events.GenerateBuildingEvent(t);
        this.view.game.events.building.handle(e.blob, e);
        return t;
    }

    /**
     * Generates a new Patron
     */
    public Patron patron(String name, int x, int y) {
        final Patron p = this.patronDryRun(name, x, y);
        this.view.game.mechanics.patronage.addPatron(p);
        return p;
    }

    /**
     * Generates a new Patron without registering them to the Patron index
     */
    public Patron patronDryRun(String name, int x, int y) {
        final Events.GeneratePatronEvent e = new Events.GeneratePatronEvent(new Patron(name, x, y));
        this.view.game.events.patron.handle(e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Item
     */
    public Item item(String name) {
        final Events.GenerateItemEvent e = new Events.GenerateItemEvent(new Item(name));
        this.view.game.events.item.handle(e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Ability
     */
    public Ability ability(Unit wielder, String name) {
        return this.ability(new Ability(wielder, name));
    }

    /**
     * Populates a new Ability
     */
    public Ability ability(Ability ability) {
        final Events.GenerateAbilityEvent e = new Events.GenerateAbilityEvent(ability);
        this.view.game.events.ability.handle(e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Artifact
     */
    public Artifact artifact(String name) {
        final Events.GenerateArtifactEvent e = new Events.GenerateArtifactEvent(new Artifact(name));
        this.view.game.events.artifact.handle(e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Fate
     */
    public Fate fate(String name) {
        final Events.GenerateFateEvent e = new Events.GenerateFateEvent(new Fate(name));
        this.view.game.events.fate.handle(e.blob, e);
        return e.blob;
    }
}
