package net.lugocorp.kingdom.game.events;

/**
 * Contains relevant data for some incoming event
 */
public abstract class Event {
    public final boolean propagate;
    public final boolean panic;
    public final String channel;

    public Event(String channel, boolean propagate, boolean panic) {
        this.propagate = propagate;
        this.channel = channel;
        this.panic = panic;
    }

    public Event(String channel) {
        this(channel, true, false);
    }
}
