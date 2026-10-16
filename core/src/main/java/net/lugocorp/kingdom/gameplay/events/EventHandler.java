package net.lugocorp.kingdom.gameplay.events;
import net.lugocorp.kingdom.utils.SideEffect;

/**
 * This class represents the basic lambda function that powers the Event
 * handling system
 */
public interface EventHandler<C extends Context, T extends EventReceiver, E extends Event<C>> {
    public SideEffect handle(C ctx, T receiver, E event);
}
