package net.lugocorp.kingdom.gameplay.events;

/**
 * Contains relevant data for some incoming event
 */
public abstract class Event<C extends Context> {
    // Units that handle this Event will pass it onto their sub-entities (Abilities,
    // Items, etc)
    public final boolean propagate;

    // Will throw an error if no handler is found for this Event
    public final boolean panic;

    // Tells the game which handler to use for this Event
    public final String channel;

    // Allows us to check if this Event can be handled in any given Context
    final Class<C> contextClass;

    public Event(String channel, Class<C> contextClass, boolean propagate, boolean panic) {
        if (channel == null) {
            this.channel = this.getClass().getSimpleName();
        } else {
            this.channel = channel;
        }
        this.contextClass = contextClass;
        this.propagate = propagate;
        this.panic = panic;
    }

    public Event(Class<C> contextClass, boolean propagate, boolean panic) {
        this(null, contextClass, propagate, panic);
    }

    public Event(Class<C> contextClass) {
        this(contextClass, true, false);
    }
}
