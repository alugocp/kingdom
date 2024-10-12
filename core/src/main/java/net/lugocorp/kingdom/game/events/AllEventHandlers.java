package net.lugocorp.kingdom.game.events;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;

/**
 * A neat package to contain all of our EventHandlerBundles
 */
public class AllEventHandlers {
    public final SignalBooster signals = new SignalBooster();
    public final EventHandlerBundle<Building> building;
    public final EventHandlerBundle<Artifact> artifact;
    public final EventHandlerBundle<Ability> ability;
    public final EventHandlerBundle<Item> item;
    public final EventHandlerBundle<Unit> unit;
    public final EventHandlerBundle<Tile> tile;

    public AllEventHandlers() {
        this.building = new EventHandlerBundle<>(this.signals);
        this.artifact = new EventHandlerBundle<>(this.signals);
        this.ability = new EventHandlerBundle<>(this.signals);
        this.item = new EventHandlerBundle<>(this.signals);
        this.unit = new EventHandlerBundle<>(this.signals);
        this.tile = new EventHandlerBundle<>(this.signals);
    }
}
