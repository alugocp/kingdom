package net.lugocorp.kingdom.game.events;

/**
 * Contains relevant data for some incoming event
 */
public abstract class Event {
    // Units that handle this Event will pass it onto their sub-entities (Abilities,
    // Items, etc)
    public final boolean propagate;

    // Will throw an error if no handler is found for this Event
    public final boolean panic;

    // Tells the game which handler to use for this Event
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
