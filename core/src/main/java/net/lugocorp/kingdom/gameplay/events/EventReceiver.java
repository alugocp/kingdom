package net.lugocorp.kingdom.gameplay.events;
import net.lugocorp.kingdom.gameplay.events.contexts.GameViewContext;
import net.lugocorp.kingdom.prediction.EventLog;
import net.lugocorp.kingdom.utils.SideEffect;

/**
 * This interface serves as a wrapper so game objects can call their own
 * EventReceivers in OOP fashion
 */
public interface EventReceiver {

    /**
     * API sugar to handle an Event
     */
    public default SideEffect handleEvent(Event e) {
        EventLog.log(e, this);
        return new SideEffect().add(e).add(this.handleEventWithoutSignalBooster(e))
                .add(AllEventHandlers.instance.signals.propagate(this, e));
    }

    /**
     * Handles Events without calling the SignalBooster
     */
    public SideEffect handleEventWithoutSignalBooster(Event e);

    /**
     * Returns a key that helps determine which EventHandler to use in an
     * EventHandlerBundle
     */
    public String getStratifier();

    /**
     * Call this function when the EventReceiver should no longer listen for Events
     */
    public default void deactivate(GameViewContext ctx) {
        ctx.view.game.future.removeFutureTicks(this);
        ctx.view.game.events.signals.deactivateListener(this);
        ctx.view.hud.bot.tileMenu.refresh();
    }
}
