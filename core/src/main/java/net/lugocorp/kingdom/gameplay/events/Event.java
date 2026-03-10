package net.lugocorp.kingdom.gameplay.events;

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
        if (channel == null) {
            this.channel = this.getClass().getSimpleName();
        } else {
            this.channel = channel;
        }
        this.propagate = propagate;
        this.panic = panic;
    }

    public Event(boolean propagate, boolean panic) {
        this(null, propagate, panic);
    }

    public Event() {
        this(true, false);
    }
}
