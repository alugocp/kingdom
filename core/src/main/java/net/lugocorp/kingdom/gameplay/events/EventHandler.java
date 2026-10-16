package net.lugocorp.kingdom.gameplay.events;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;

/**
 * This class represents the basic lambda function that powers the Event
 * handling system
 */
public interface EventHandler<T extends EventReceiver, E extends Event> {
    public SideEffect handle(GameView view, T receiver, E event);
}
