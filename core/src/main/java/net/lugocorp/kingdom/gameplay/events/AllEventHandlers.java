package net.lugocorp.kingdom.gameplay.events;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Patron;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;

/**
 * A neat package to contain all of our EventHandlerBundles
 */
public class AllEventHandlers {
    public final SignalBooster signals = new SignalBooster();
    public final EventHandlerBundle<Building> building = new EventHandlerBundle<>();
    public final EventHandlerBundle<Artifact> artifact = new EventHandlerBundle<>();
    public final EventHandlerBundle<Ability> ability = new EventHandlerBundle<>();
    public final EventHandlerBundle<Patron> patron = new EventHandlerBundle<>();
    public final EventHandlerBundle<Item> item = new EventHandlerBundle<>();
    public final EventHandlerBundle<Unit> unit = new EventHandlerBundle<>();
    public final EventHandlerBundle<Tile> tile = new EventHandlerBundle<>();
    public final EventHandlerBundle<Fate> fate = new EventHandlerBundle<>();
}
