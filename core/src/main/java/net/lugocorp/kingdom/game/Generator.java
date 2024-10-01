package net.lugocorp.kingdom.game;
import net.lugocorp.kingdom.core.Events;
import net.lugocorp.kingdom.events.EventHandlerBundle;

/**
 * This class is like a factory class for all game objects. It calls the
 * relevant generator Events to instantiate each new object.
 */
public class Generator {
    private final EventHandlerBundle events;

    public Generator(EventHandlerBundle events) {
        this.events = events;
    }

    /**
     * Generates a new Tile
     */
    public Tile tile(String name, int x, int y) {
        Events.GenerateTileEvent e = new Events.GenerateTileEvent(new Tile(name, x, y));
        this.events.tile.handle(name, e);
        return e.blob;
    }

    /**
     * Generates a new Unit
     */
    public Unit unit(String name, int x, int y) {
        Events.GenerateUnitEvent e = new Events.GenerateUnitEvent(new Unit(name, x, y));
        this.events.unit.handle(name, e);
        return e.blob;
    }

    /**
     * Generates a new Building
     */
    public Building building(String name, int x, int y) {
        Events.GenerateBuildingEvent e = new Events.GenerateBuildingEvent(new Building(name, x, y));
        this.events.building.handle(name, e);
        return e.blob;
    }
}
