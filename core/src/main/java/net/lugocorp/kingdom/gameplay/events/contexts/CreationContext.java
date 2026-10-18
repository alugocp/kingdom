package net.lugocorp.kingdom.gameplay.events.contexts;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.gameplay.events.AllEventHandlers;
import net.lugocorp.kingdom.gameplay.events.Context;

/**
 * This Context exists as soon as we can use the Event system
 */
public class CreationContext extends Context {
    public final AllEventHandlers events;
    public final Generator generator;

    public CreationContext(AllEventHandlers events, Generator generator) {
        this.generator = generator;
        this.events = events;
    }
}
