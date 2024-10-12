package net.lugocorp.kingdom.game.events;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles arbitrary incoming Events on arbitrary members of some group
 */
public class EventHandlerBundle<T extends EventReceiver> {
    private Map<String, List<EventHandler<T>>> handlers = new HashMap<>();
    private final SignalBooster signals;

    public EventHandlerBundle(SignalBooster signals) {
        this.signals = signals;
    }

    /**
     * Formats the lookup key used in this.handlers
     */
    private String getKey(String stratifier, String channel) {
        return String.format("%s.%s", stratifier, channel);
    }

    /**
     * Registers a new EventHandler to some name and channel on this EventReceiver
     */
    public void addEventHandler(String stratifier, String channel, EventHandler<T> handler) {
        String key = this.getKey(stratifier, channel);
        if (!this.handlers.containsKey(key)) {
            this.handlers.put(key, new ArrayList<EventHandler<T>>());
        }
        this.handlers.get(key).add(handler);
    }

    /**
     * Runs the relevant EventHandler logic for a given name and Event
     */
    public void handle(GameView view, T receiver, Event e) {
        String key = this.getKey(receiver.getStratifier(), e.channel);
        if (!this.handlers.containsKey(key)) {
            return;
        }
        for (EventHandler<T> handler : this.handlers.get(key)) {
            handler.handle(view, receiver, e);
        }
    }
}
