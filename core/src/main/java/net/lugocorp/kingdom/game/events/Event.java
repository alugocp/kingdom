package net.lugocorp.kingdom.game.events;

/**
 * Contains relevant data for some incoming event
 */
public abstract class Event {
    public final String channel;

    public Event(String channel) {
        this.channel = channel;
    }
}
