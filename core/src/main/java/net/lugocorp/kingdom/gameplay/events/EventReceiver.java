package net.lugocorp.kingdom.gameplay.events;
import net.lugocorp.kingdom.ai.prediction.CapturedEvents;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;

/**
 * This interface serves as a wrapper so game objects can call their own
 * EventReceivers in OOP fashion
 */
public interface EventReceiver {

    /**
     * API sugar to handle an Event
     */
    public default SideEffect handleEvent(GameView view, Event e) {
        if (CapturedEvents.instance.isActive()) {
            CapturedEvents.instance.capture(e);
        }
        return new SideEffect().add(e).add(this.handleEventWithoutSignalBooster(view, e))
                .add(view.game.events.signals.propagate(view, this, e));
    }

    /**
     * Handles Events without calling the SignalBooster
     */
    public SideEffect handleEventWithoutSignalBooster(GameView view, Event e);

    /**
     * Returns a key that helps determine which EventHandler to use in an
     * EventHandlerBundle
     */
    public String getStratifier();

    /**
     * Call this function when the EventReceiver should no longer listen for Events
     */
    public default void deactivate(GameView view) {
        view.game.future.removeFutureTicks(this);
        view.game.events.signals.deactivateListener(this);
        view.hud.bot.tileMenu.refresh();
    }
}
