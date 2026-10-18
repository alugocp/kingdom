package net.lugocorp.kingdom.context;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.gameplay.events.AllEventHandlers;

/**
 * This Context exists throughout the entire lifecycle of the application
 */
public class BaseContext extends Context {
    public final AllEventHandlers events;
    public final Generator generator;

    public BaseContext(AllEventHandlers events, Generator generator) {
        this.generator = generator;
        this.events = events;
    }
}
