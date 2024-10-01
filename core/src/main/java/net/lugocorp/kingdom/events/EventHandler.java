package net.lugocorp.kingdom.events;
import net.lugocorp.kingdom.game.Game;

public interface EventHandler<E extends Event> {
    public void handle(Game game, E event);
}
