package net.lugocorp.kingdom.event;

/**
 * Contains relevant data for some incoming event
 */
public class Event {
    public final String channel;

    Event(String channel) {
        this.channel = channel;
    }
}