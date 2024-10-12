package net.lugocorp.kingdom.game.events;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This class represents the basic lambda function that powers the Event
 * handling system
 */
public interface EventHandler<T extends EventReceiver> {
    public void handle(GameView view, T receiver, Event event);
}
