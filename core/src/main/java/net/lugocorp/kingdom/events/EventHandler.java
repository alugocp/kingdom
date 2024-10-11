package net.lugocorp.kingdom.events;
import net.lugocorp.kingdom.game.Game;

/**
 * This class represents the basic lambda function that powers the Event
 * handling system
 */
public interface EventHandler<E extends Event> {
    public void handle(Game game, E event);
}
