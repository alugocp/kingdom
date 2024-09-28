package net.lugocorp.kingdom.events;

public interface EventHandler<E extends Event> {
    public void handle(E event);
}
