package net.lugocorp.kingdom.events;
import net.lugocorp.kingdom.game.Game;

/**
 * This interface serves as a wrapper so game objects can call their own
 * EventReceivers in OOP fashion
 */
public interface EventTarget {

    /**
     * Allows implementing classes to call into Game's StratifiedEventReceivers
     * using the correct name value
     */
    public void handleEvent(Game g, Event e);
}
