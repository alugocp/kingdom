package net.lugocorp.kingdom.gameplay.events;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Patron;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.gameplay.events.contexts.GameViewContext;

/**
 * A neat package to contain all of our EventHandlerBundles
 */
public class AllEventHandlers {
    public static final AllEventHandlers instance = new AllEventHandlers();
    public final SignalBooster signals = new SignalBooster();
    public final EventHandlerBundle<Building> building = new EventHandlerBundle<>(() -> this.ctx);
    public final EventHandlerBundle<Artifact> artifact = new EventHandlerBundle<>(() -> this.ctx);
    public final EventHandlerBundle<Ability> ability = new EventHandlerBundle<>(() -> this.ctx);
    public final EventHandlerBundle<Patron> patron = new EventHandlerBundle<>(() -> this.ctx);
    public final EventHandlerBundle<Item> item = new EventHandlerBundle<>(() -> this.ctx);
    public final EventHandlerBundle<Unit> unit = new EventHandlerBundle<>(() -> this.ctx);
    public final EventHandlerBundle<Tile> tile = new EventHandlerBundle<>(() -> this.ctx);
    public final EventHandlerBundle<Fate> fate = new EventHandlerBundle<>(() -> this.ctx);
    private GameViewContext ctx = null;

    public void setContext(GameViewContext ctx) {
        this.ctx = ctx;
    }

    public GameViewContext getContext() {
        return this.ctx;
    }
}
