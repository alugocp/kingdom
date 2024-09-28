package net.lugocorp.kingdom.events;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Handles arbitrary incoming Events
 */
public class EventReceiver {
    private static Random random = new Random();
    private Map<String, List<IdentifiedEventHandler>> handlers = new HashMap<>();

    /**
     * Registers a new EventHandler to some channel on this EventReceiver
     */
    public long addEventHandler(String channel, EventHandler handler) {
        if (!this.handlers.containsKey(channel)) {
            this.handlers.put(channel, new ArrayList<IdentifiedEventHandler>());
        }
        final long id = EventReceiver.random.nextLong();
        this.handlers.get(channel).add(new IdentifiedEventHandler(handler, id));
        return id;
    }

    /**
     * Runs the relevant EventHandler logic for a given Event
     */
    public void handle(Event e) {
        if (!this.handlers.containsKey(e.channel)) {
            return;
        }
        List<IdentifiedEventHandler> handlers = this.handlers.get(e.channel);
        for (IdentifiedEventHandler handler : handlers) {
            handler.handler.handle(e);
        }
    }

    /**
     * Nested class to bind an EventHandler with an identifier
     */
    private static class IdentifiedEventHandler {
        final EventHandler handler;
        final long id;

        IdentifiedEventHandler(EventHandler handler, long id) {
            this.handler = handler;
            this.id = id;
        }
    }
}
