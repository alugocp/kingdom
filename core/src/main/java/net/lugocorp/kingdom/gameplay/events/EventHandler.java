package net.lugocorp.kingdom.gameplay.events;
import net.lugocorp.kingdom.gameplay.events.contexts.GameViewContext;
import net.lugocorp.kingdom.utils.SideEffect;

/**
 * This class represents the basic lambda function that powers the Event
 * handling system
 */
public interface EventHandler<T extends EventReceiver, E extends Event> {
    public SideEffect handle(GameViewContext ctx, T receiver, E event);
}
