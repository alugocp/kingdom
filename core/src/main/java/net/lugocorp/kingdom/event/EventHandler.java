package net.lugocorp.kingdom.event;

public interface EventHandler<E extends Event> {
    public void handle(E event);
}