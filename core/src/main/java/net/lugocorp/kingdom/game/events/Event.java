package net.lugocorp.kingdom.game.events;

/**
 * Contains relevant data for some incoming event
 */
public abstract class Event {
    public final String channel;
    public final boolean panic;

    public Event(String channel, boolean panic) {
        this.channel = channel;
        this.panic = panic;
    }

    public Event(String channel) {
        this(channel, false);
    }
}
