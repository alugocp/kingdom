package net.lugocorp.kingdom.gameplay.events;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.context.Context;
import net.lugocorp.kingdom.context.GameViewContext;

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
     * Adds an EventHandler to this instance's stratifier using multiple
     * StratifiedPayloads
     */
    public Stratified<T> add(StratifiedPayload[] payloads) {
        for (StratifiedPayload payload : payloads) {
            if (payload.overrideChannel.isPresent()) {
                final StratifiedPayload<GameViewContext, T, Events.RepeatedEvent> p = (StratifiedPayload<GameViewContext, T, Events.RepeatedEvent>) payload;
                this.bundle.addEventHandler(this.stratifier, p.overrideChannel.get(), p.handler);
            } else {
                this.bundle.addEventHandler(this.stratifier, payload.eventClass.getSimpleName(), payload.handler);
            }
        }
        return this;
    }

    /**
     * Adds an EventHandler to this instance's stratifier using a StratifiedPayload
     */
    public <C extends Context, E extends Event<C>> Stratified<T> add(StratifiedPayload<C, T, E> payload) {
        this.bundle.addEventHandler(this.stratifier, payload.overrideChannel.orElse(payload.eventClass.getSimpleName()),
                payload.handler);
        return this;
    }

    /**
     * Adds an EventHandler to this instance's stratifier
     */
    public <C extends Context, E extends Event<C>> Stratified<T> add(Class<E> eventClass,
            EventHandler<C, T, E> handler) {
        this.bundle.addEventHandler(this.stratifier, eventClass.getSimpleName(), handler);
        return this;
    }

    /**
     * Adds an EventHandler to this instance's stratifier (for RepeatedEvents in
     * particular)
     */
    public Stratified<T> add(String channel, EventHandler<GameViewContext, T, Events.RepeatedEvent> handler) {
        this.bundle.addEventHandler(this.stratifier, channel, handler);
        return this;
    }
}
