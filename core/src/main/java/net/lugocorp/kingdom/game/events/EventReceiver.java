package net.lugocorp.kingdom.game.events;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This interface serves as a wrapper so game objects can call their own
 * EventReceivers in OOP fashion
 */
public interface EventReceiver {

    /**
     * API sugar to handle an Event
     */
    public void handleEvent(GameView g, Event e);

    /**
     * Returns a key that helps determine which EventHandler to use in an
     * EventHandlerBundle
     */
    public String getStratifier();
}
