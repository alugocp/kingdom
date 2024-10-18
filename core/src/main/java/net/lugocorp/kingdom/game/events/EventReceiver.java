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
    public default void handleEvent(GameView view, Event e) {
        this.handleEventWithoutSignalBooster(view, e);
        view.game.events.signals.propagate(view, this, e);
    }

    /**
     * Handles Events without calling the SignalBooster
     */
    public void handleEventWithoutSignalBooster(GameView view, Event e);

    /**
     * Returns a key that helps determine which EventHandler to use in an
     * EventHandlerBundle
     */
    public String getStratifier();

    /**
     * Call this function when the EventReceiver should no longer listen for Events
     */
    public default void deactivate(GameView view) {
        view.game.events.signals.deactivateListener(this);
        view.menu.refresh(true);
    }
}
