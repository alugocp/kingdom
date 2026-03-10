package net.lugocorp.kingdom.gameplay.events;
import net.lugocorp.kingdom.builtin.Events;

/**
 * This class exposes an API that allows us to define content via decorator
 * pattern
 */
public class Stratified<T extends EventReceiver> {
    private final EventHandlerBundle<T> bundle;
    private final String stratifier;

    public Stratified(EventHandlerBundle<T> bundle, String stratifier) {
        this.stratifier = stratifier;
        this.bundle = bundle;
    }

    /**
     * Adds an EventHandler to this instance's stratifier
     */
    public <E extends Event> Stratified<T> add(Class<E> eventClass, SingleEventHandler<T, E> handler) {
        this.bundle.addEventHandler(this.stratifier, eventClass, handler);
        return this;
    }

    /**
     * Adds an EventHandler to this instance's stratifier (for RepeatedEvents in
     * particular)
     */
    public Stratified<T> add(String channel, SingleEventHandler<T, Events.RepeatedEvent> handler) {
        this.bundle.addEventHandler(this.stratifier, channel, handler);
        return this;
    }
}
