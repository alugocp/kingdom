package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This class is like a factory class for all game objects. It calls the
 * relevant generator Events to instantiate each new object.
 */
public class Generator {
    private final GameView view;

    public Generator(GameView view) {
        this.view = view;
    }

    /**
     * Generates a new Tile
     */
    public Tile tile(String name, int x, int y) {
        Events.GenerateTileEvent e = new Events.GenerateTileEvent(new Tile(name, x, y));
        this.view.game.events.tile.handle(this.view, e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Unit
     */
    public Unit unit(String name, int x, int y) {
        Events.GenerateUnitEvent e = new Events.GenerateUnitEvent(new Unit(name, x, y));
        this.view.game.events.unit.handle(this.view, e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Building
     */
    public Building building(String name, int x, int y) {
        Events.GenerateBuildingEvent e = new Events.GenerateBuildingEvent(new Building(name, x, y));
        this.view.game.events.building.handle(this.view, e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Item
     */
    public Item item(String name) {
        Events.GenerateItemEvent e = new Events.GenerateItemEvent(new Item(name));
        this.view.game.events.item.handle(this.view, e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Ability
     */
    public Ability ability(String name) {
        Events.GenerateAbilityEvent e = new Events.GenerateAbilityEvent(new Ability(name));
        this.view.game.events.ability.handle(this.view, e.blob, e);
        return e.blob;
    }

    /**
     * Generates a new Artifact
     */
    public Artifact artifact(String name) {
        Events.GenerateArtifactEvent e = new Events.GenerateArtifactEvent(new Artifact(name));
        this.view.game.events.artifact.handle(this.view, e.blob, e);
        return e.blob;
    }
}
