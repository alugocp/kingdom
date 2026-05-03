package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.ui.views.GameView;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import java.util.function.Supplier;

/**
 * This class is like a factory class for all game objects. It calls the
 * relevant generator Events to instantiate each new object.
 */
public class Generator {
    @FieldSerializer.Optional("view")
    private final GameView view;

    public Generator(GameView view) {
        this.view = view;
    }

    /**
     * Generates a new Tile
     */
    public Tile tile(String name, int x, int y) {
        final Events.GenerateTileEvent e = new Events.GenerateTileEvent(new Tile(name, x, y));
        this.view.game.events.tile.handle(this.view, e.blob, e);
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
        this.view.game.events.unit.handle(this.view, e.blob, e);
        return u;
    }

    /**
     * Generates a new Building
     */
    public Building building(String name, int x, int y) {
        final Supplier<Tile> getTile = () -> this.view.game.world.getTile(x, y).get();
        final Events.GenerateBuildingEvent e = new Events.GenerateBuildingEvent(new Building(name, x, y, getTile));
        this.view.game.events.building.handle(this.view, e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Tower
     */
    public Tower tower(int x, int y) {
        final Supplier<Tile> getTile = () -> this.view.game.world.getTile(x, y).get();
        final Tower t = new Tower(x, y, getTile);
        final Events.GenerateBuildingEvent e = new Events.GenerateBuildingEvent(t);
        this.view.game.events.building.handle(this.view, e.blob, e);
        return t;
    }

    /**
     * Generates a new Patron
     */
    public Patron patron(String name, int x, int y) {
        final Supplier<Tile> getTile = () -> this.view.game.world.getTile(x, y).get();
        final Events.GeneratePatronEvent e = new Events.GeneratePatronEvent(new Patron(name, x, y, getTile));
        this.view.game.events.patron.handle(this.view, e.blob, e);
        this.view.game.mechanics.patronage.addPatron(e.blob);
        return e.blob;
    }

    /**
     * Generates a new Item
     */
    public Item item(String name) {
        final Events.GenerateItemEvent e = new Events.GenerateItemEvent(new Item(name));
        this.view.game.events.item.handle(this.view, e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Ability
     */
    public Ability ability(Unit wielder, String name) {
        final Events.GenerateAbilityEvent e = new Events.GenerateAbilityEvent(new Ability(wielder, name));
        this.view.game.events.ability.handle(this.view, e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Artifact
     */
    public Artifact artifact(String name) {
        final Events.GenerateArtifactEvent e = new Events.GenerateArtifactEvent(new Artifact(name));
        this.view.game.events.artifact.handle(this.view, e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Fate
     */
    public Fate fate(String name) {
        final Events.GenerateFateEvent e = new Events.GenerateFateEvent(new Fate(name));
        this.view.game.events.fate.handle(this.view, e.blob, e);
        return e.blob;
    }
}
