package net.lugocorp.kingdom.gameplay.events;

/**
 * This class makes Stratified.add() calls more flexible
 */
public class StratifiedPayload<T extends EventReceiver, E extends Event> {
    final SingleEventHandler<T, E> handler;
    final Class<E> eventClass;

    public StratifiedPayload(Class<E> eventClass, SingleEventHandler<T, E> handler) {
        this.eventClass = eventClass;
        this.handler = handler;
    }
}
