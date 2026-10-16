package net.lugocorp.kingdom.gameplay.events;
import java.util.Optional;

/**
 * This class makes Stratified.add() calls more flexible
 */
public class StratifiedPayload<T extends EventReceiver, E extends Event> {
    final EventHandler<T, E> handler;
    final Class<E> eventClass;
    Optional<String> overrideChannel = Optional.empty();

    public StratifiedPayload(Class<E> eventClass, EventHandler<T, E> handler) {
        this.eventClass = eventClass;
        this.handler = handler;
    }

    /**
     * Sets an override channel to bind this StratifiedPayload to
     */
    public StratifiedPayload setOverrideChannel(String overrideChannel) {
        this.overrideChannel = Optional.of(overrideChannel);
        return this;
    }
}
